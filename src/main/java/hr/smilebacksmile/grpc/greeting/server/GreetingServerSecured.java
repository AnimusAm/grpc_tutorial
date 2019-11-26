package hr.smilebacksmile.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServerSecured {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC from Server side");

        final Server server = ServerBuilder
                .forPort(50053)
                .addService(new GreetServiceImpl())
                .useTransportSecurity(new File("cert/server.crt"), // server's CA signed certificate
                        new File("cert/server.pem")   // server's private key in
                )
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
