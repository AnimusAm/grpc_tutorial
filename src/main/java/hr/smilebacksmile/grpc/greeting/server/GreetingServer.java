package hr.smilebacksmile.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC from Server side");

        final Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetServiceImpl())
                .build();
        server.start();

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
