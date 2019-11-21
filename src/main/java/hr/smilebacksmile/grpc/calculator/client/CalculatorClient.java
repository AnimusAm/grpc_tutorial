package hr.smilebacksmile.grpc.calculator.client;


import hr.smilebacksmile.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Calculator client side says hello");

        // Create chanel
        final ManagedChannel managedChannel =
                ManagedChannelBuilder.forAddress("localhost", 50052)
                        .usePlaintext() // needed to invoke SSL usage
                        .build();

        // Create Sum client Stub - blocking, synchronous
        final CalculatorServiceGrpc.CalculatorServiceBlockingStub synchronousCalculatorClient =
                CalculatorServiceGrpc.newBlockingStub(managedChannel);
        System.out.println("Calculator client side - up and running");

        // Call method on server using custom service <- RPC:

        System.out.println("Requesting the sum of numbers");
        // Make the Operands to send in request
        final Operands operands = Operands.newBuilder()
                .addAllOperands(Arrays.asList(3, 10))
                .build();
        System.out.println("I have prepared operands: " + operands.getOperandsList().toString());

        // Make the Request containing the prepared Operands
        final SumRequest request = SumRequest.newBuilder().setOperands(operands).build();

        // RPC to server to get the SumResponse
        final SumResponse response = synchronousCalculatorClient.sum(request);

        System.out.println("Received response about sum: " + response.getResult());

        System.out.println("Requesting the prime factors of a number");
        // Make the LargeWholeNumber to send in request
        final LargeWholeNumber number = LargeWholeNumber.newBuilder().setNumber(478294039271719L).build();

        System.out.println("I have prepared the number: " + number.getNumber());

        // Make the Request containing the prepared Number
        final PrimeFactorsRequest requestFactors = PrimeFactorsRequest.newBuilder().setNumber(number).build();

        final List<Long> receivedFactors = new LinkedList<>();
        // RPC to server to get the PrimeFactorsResponse - StreamingServer
        synchronousCalculatorClient.calculatePrimeFactors(requestFactors).forEachRemaining(
                primeFactorsResponse -> {
                    receivedFactors.add(primeFactorsResponse.getFactor().getNumber());
                    System.out.println("Received streaming result: " + primeFactorsResponse.getFactor());
                }
        );

        System.out.println("Received all factors: " + receivedFactors.toString());

        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }
}
