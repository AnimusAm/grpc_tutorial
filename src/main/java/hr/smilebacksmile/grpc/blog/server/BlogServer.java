package hr.smilebacksmile.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Blog server side says hello");

        final Server server = ServerBuilder
                .forPort(50054)
                .addService(new BlogServerServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build();
        server.start();
        System.out.println("Blog server side - up & running");

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
