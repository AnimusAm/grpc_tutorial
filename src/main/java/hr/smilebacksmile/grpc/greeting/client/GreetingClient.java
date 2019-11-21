package hr.smilebacksmile.grpc.greeting.client;

import hr.smilebacksmile.greet.GreetRequest;
import hr.smilebacksmile.greet.GreetResponse;
import hr.smilebacksmile.greet.GreetServiceGrpc;
import hr.smilebacksmile.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello gRPC from Client side");

        final ManagedChannel managedChannel =
                ManagedChannelBuilder.forAddress("localhost", 50051)
                        .usePlaintext() // needed to invoke SSL usage
                        .build();

        System.out.println("Creating Stub");
        /* generic Stub - w e don't actually want to use since we made our custom service which provides it's stub
        final DummyServiceGrpc.DummyServiceBlockingStub synchronousClient =
                DummyServiceGrpc.newBlockingStub(managedChannel);
        */
        // Created GreetService client - blocking, synchronous
        final GreetServiceGrpc.GreetServiceBlockingStub synchronousGreetClient =
                GreetServiceGrpc.newBlockingStub(managedChannel);

        // Call method on server using custom service <- RPC:

        // Make the Greeting
        final Greeting greeting = Greeting.newBuilder()
                .setFirstName("MyName")
                .build();

        // Make the Request containing the Greeting
        final GreetRequest request = GreetRequest.newBuilder().setGreeting(greeting).build();

        // RPC to server to get the Response - Unary
        final GreetResponse response = synchronousGreetClient.greet(request);
        System.out.println(response.getResult());

        // RPC to server to get the Response - StreamingServer
        synchronousGreetClient.greetManyTimes(request).forEachRemaining(
                greetManyTimeResponse -> {
                    System.out.println("Received streaming result: " + greetManyTimeResponse.getResult());
                }
        );



        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }
}
