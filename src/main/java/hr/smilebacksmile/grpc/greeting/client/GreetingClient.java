package hr.smilebacksmile.grpc.greeting.client;

import hr.smilebacksmile.greet.GreetRequest;
import hr.smilebacksmile.greet.GreetResponse;
import hr.smilebacksmile.greet.GreetServiceGrpc;
import hr.smilebacksmile.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    private static boolean CALL_UNARY = true;
    private static boolean CALL_SERVER_STREAMING = true;
    private static boolean CALL_CLIENT_STREAMING = true;

    ManagedChannel managedChannel;

    private void run() {
        managedChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // needed to invoke SSL usage
                .build();

        doUnaryCall(CALL_UNARY);
        doCallForStreamingServer(CALL_SERVER_STREAMING);

        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }

    private void doUnaryCall(final boolean doCall) {

        if (doCall) {
            // Created GreetService client - blocking, synchronous
            final GreetServiceGrpc.GreetServiceBlockingStub synchronousGreetClient =
                    GreetServiceGrpc.newBlockingStub(managedChannel);

            System.out.println("Preparing UNARY call on CLIENT side");
            // Call method on server using custom service <- RPC:
            unaryCall(synchronousGreetClient);

        }
    }

    private void doCallForStreamingServer(final boolean doCall) {

        if (doCall) {
            // Created GreetService client - blocking, synchronous
            final GreetServiceGrpc.GreetServiceBlockingStub synchronousGreetClient =
                    GreetServiceGrpc.newBlockingStub(managedChannel);

            System.out.println("Preparing SERVER STREAMING call on CLIENT side");
            // Call method on server using custom service <- RPC:
            serverStreamingCall(synchronousGreetClient);

        }
    }

    private void unaryCall(final GreetServiceGrpc.GreetServiceBlockingStub unaryClient) {

        // Make the Greeting
        final Greeting greeting = Greeting.newBuilder()
                .setFirstName("SomeName for UnaryService")
                .build();

        // Make the Request containing the Greeting
        final GreetRequest request = GreetRequest.newBuilder().setGreeting(greeting).build();
        System.out.println("UNARY request prepared on CLIENT side: " + request);

        // RPC to server to get the Response - Unary
        final GreetResponse response = unaryClient.greet(request);
        System.out.println("UNARY response received from SERVER side: " + response.getResult());

    }

    private void serverStreamingCall(final GreetServiceGrpc.GreetServiceBlockingStub unaryClient) {

        // Make the Greeting
        final Greeting greeting = Greeting.newBuilder()
                .setFirstName("SomeName for UnaryService")
                .build();

        // Make the Request containing the Greeting
        final GreetRequest request = GreetRequest.newBuilder().setGreeting(greeting).build();
        System.out.println("UNARY request prepared on CLIENT side: " + request);

        // RPC to server to get the Response - StreamingServer
        unaryClient.greetManyTimes(request).forEachRemaining(
                greetManyTimeResponse -> {
                    System.out.println("STREAMING response received from SERVER side: " + greetManyTimeResponse.getResult());
                }
        );

    }

    public static void main(String[] args) {
        System.out.println("Hello gRPC from Client side");

        final GreetingClient client = new GreetingClient();
        client.run();

    }
}
