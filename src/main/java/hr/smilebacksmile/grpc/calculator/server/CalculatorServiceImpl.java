package hr.smilebacksmile.grpc.calculator.server;

import hr.smilebacksmile.calculator.*;
import hr.smilebacksmile.fsm.state.impl.MachineState;
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
}
