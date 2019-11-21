package hr.smilebacksmile.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Calculator server side says hello");

        final Server server = ServerBuilder
                .forPort(50052)
                .addService(new CalculatorServiceImpl())
                .build();
        server.start();
        System.out.println("Calculator server side - up & running");

        Runtime.getRuntime().addShutdownHook(new Thread(
            () -> {
                System.out.println("Requested server shutdown");
                server.shutdown();
                System.out.println("Server successfully shut down");
            })
        );
        server.awaitTermination();
    }
}
