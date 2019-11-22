package hr.smilebacksmile.grpc.calculator.server;

import hr.smilebacksmile.calculator.*;
import hr.smilebacksmile.fsm.state.impl.MachineState;
import hr.smilebacksmile.greet.GreetResponse;
import hr.smilebacksmile.grpc.calculator.util.IntegerStatisticsCalculator;
import hr.smilebacksmile.grpc.calculator.util.PrimeNumberIncrementalDecomposer;
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

        for (Integer input : inputs) {
            System.out.println("Input: " + input);
            sum += input;
        }
        System.out.println("Got result: " + sum);

        // Form the response
        final SumResponse response = SumResponse.newBuilder().setResult(sum).build();

        // Send response
        responseObserver.onNext(response);

        // Finalize RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void calculatePrimeFactors(PrimeFactorsRequest request, StreamObserver<PrimeFactorsResponse> responseObserver) {

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
        try {
            for (Long factor : factors) {

                System.out.println("Preparing response for factor: " + factor);

                // Form the response
                final PrimeFactorsResponse response = PrimeFactorsResponse
                        .newBuilder()
                        .setFactor(
                                LargeWholeNumber.newBuilder().setNumber(factor).build()
                        ).build();

                // Send response
                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Finalize RPC call
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<IntStatisticsRequest> calculateAverage(StreamObserver<AverageResponse> responseObserver) {

        final StreamObserver<IntStatisticsRequest> requestStreamObserver = new StreamObserver<IntStatisticsRequest>() {

            final IntegerStatisticsCalculator calculator = new IntegerStatisticsCalculator();

            @Override
            public void onNext(IntStatisticsRequest value) {
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
    public StreamObserver<IntStatisticsRequest> globalMaximum(StreamObserver<GlobalMaxResponse> responseObserver) {

        final StreamObserver<IntStatisticsRequest> requestStreamObserver = new StreamObserver<IntStatisticsRequest>() {

            final IntegerStatisticsCalculator calculator = new IntegerStatisticsCalculator();
            Integer globalMaximum = calculator.currentMaximum();

            @Override
            public void onNext(IntStatisticsRequest value) {
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
}
