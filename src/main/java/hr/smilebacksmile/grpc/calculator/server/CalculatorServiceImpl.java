package hr.smilebacksmile.grpc.calculator.server;

import hr.smilebacksmile.calculator.*;
import hr.smilebacksmile.fsm.state.impl.MachineState;
import hr.smilebacksmile.grpc.calculator.util.IntegerStatisticsCalculator;
import hr.smilebacksmile.grpc.calculator.util.PrimeNumberIncrementalDecomposer;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        // Extract from request
        Long sum = 0L;
        final List<Integer> inputs = request.getOperands().getOperandsList();

        System.out.println("UNARY REQUEST received from CLIENT side: " + request);
        for (Integer input : inputs) {
            sum += input;
        }
        System.out.println("Prepared UNARY response on SERVER side: " + sum);

        // Form the response
        final SumResponse response = SumResponse.newBuilder().setResult(sum).build();

        // Send response
        responseObserver.onNext(response);

        // Finalize RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void calculatePrimeFactors(PrimeFactorsRequest request, StreamObserver<PrimeFactorsResponse> responseObserver) {

        System.out.println("UNARY REQUEST received from CLIENT side: " + request);
        // get the number from the request
        final Long number = Optional.ofNullable(request.getNumber())
                .map(LargeWholeNumber::getNumber)
                .orElse(0L);

        // get factors for that number
        final List<Long> factors = new PrimeNumberIncrementalDecomposer(number)
                .until(MachineState::isFinal)
                .unwrap()
                .collect(Collectors.toList());

        System.out.println("Server here - I've found following factors: " + factors.toString());

        for (Long factor : factors) {

            // Form the response
            final PrimeFactorsResponse response = PrimeFactorsResponse
                    .newBuilder()
                    .setFactor(
                            LargeWholeNumber.newBuilder().setNumber(factor).build()
                    ).build();

            System.out.println("Prepared UNARY response on SERVER side: " + response);

            // Send response
            responseObserver.onNext(response);
        }
        // Finalize RPC call
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<WholeNumberRequest> calculateAverage(StreamObserver<AverageResponse> responseObserver) {

        final StreamObserver<WholeNumberRequest> requestStreamObserver = new StreamObserver<WholeNumberRequest>() {

            final IntegerStatisticsCalculator calculator = new IntegerStatisticsCalculator();

            @Override
            public void onNext(WholeNumberRequest value) {
                // when client sends each of it's request, process it (add into calculator)
                calculator.progress(value.getNumber());

                System.out.println("STREAMING REQUEST received from CLIENT side: " + value);

            }

            @Override
            public void onError(Throwable t) {
                // what happens when client request results in error
            }

            @Override
            public void onCompleted() {
                // when Client is done, Server will send response
                //  -> here we do what we need when Client is done transmitting - and we communicate with Client using response observer
                System.out.println("STREAMING REQUEST received from CLIENT side - requesting average and end of transmission" );

                final Double average = calculator.avg();
                System.out.println("Prepared UNARY response on SERVER side: " + average);
                responseObserver.onNext(AverageResponse.newBuilder().setAverage(average).build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public StreamObserver<WholeNumberRequest> globalMaximum(StreamObserver<GlobalMaxResponse> responseObserver) {

        final StreamObserver<WholeNumberRequest> requestStreamObserver = new StreamObserver<WholeNumberRequest>() {

            final IntegerStatisticsCalculator calculator = new IntegerStatisticsCalculator();
            Integer globalMaximum = calculator.currentMaximum();

            @Override
            public void onNext(WholeNumberRequest value) {
                // Server will respond only if global maximum changed

                System.out.println("STREAMING REQUEST received from CLIENT side: " + value);

                calculator.progress(value.getNumber());
                final Integer currentMaximum = calculator.currentMaximum();
                if (calculator.currentMaximum() > globalMaximum) {
                    globalMaximum = currentMaximum;

                    final GlobalMaxResponse response = GlobalMaxResponse.newBuilder().setMax(currentMaximum).build();

                    System.out.println("STREAMING RESPONSE sent from SERVER side: " + response);
                    responseObserver.onNext(response);
                }
            }

            @Override
            public void onError(Throwable t) {
                // reset the calculator
                calculator.reset();
            }

            @Override
            public void onCompleted() {
                System.out.println("END TRANSMISSION received from CLIENT side");

                // send one last maximum before terminating if maximum changed in the mean time
                final Integer currentMaximum = calculator.currentMaximum();
                if (calculator.currentMaximum() > globalMaximum) {

                    final GlobalMaxResponse response = GlobalMaxResponse.newBuilder().setMax(currentMaximum).build();
                    System.out.println("STREAMING RESPONSE sent from SERVER side: " + response);
                }

                System.out.println("ENDING TRANSMISSION on SERVER side");
                // we want every client session to start from beginning (not to remember maximum previous call)
                calculator.reset();
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public void squareRoot(WholeNumberRequest request, StreamObserver<SquareRootResponse> responseObserver) {

        System.out.println("UNARY REQUEST received from CLIENT side: " + request);
        final Integer number = request.getNumber();

        if (number >= 0) {

            final double squareRoot = Math.sqrt(number);

            final SquareRootResponse response = SquareRootResponse.newBuilder().setRoot(squareRoot).build();
            System.out.println("UNARY RESPONSE prepared on SERVER side: " + response);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            System.out.println("ERROR on SERVER side - received negative number");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("ERR: Received number is negative")
                            .augmentDescription("Passed argument: " + number)
                            .asRuntimeException()
            );
        }
    }
}
