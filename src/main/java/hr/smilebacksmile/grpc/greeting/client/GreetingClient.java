package hr.smilebacksmile.grpc.greeting.client;

import hr.smilebacksmile.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    private static boolean CALL_UNARY = false;
    private static boolean CALL_SERVER_STREAMING = false;
    private static boolean CALL_CLIENT_STREAMING = true;

    ManagedChannel managedChannel;

    private void run() {
        managedChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // needed to invoke SSL usage
                .build();

        doUnaryCall(CALL_UNARY);
        doCallForStreamingServer(CALL_SERVER_STREAMING);
        doCallForStreamingClient(CALL_CLIENT_STREAMING);

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

    private void doCallForStreamingClient(final boolean doCall) {

        if (doCall) {
            // Created GreetService client - streaming, asynchronous
            final GreetServiceGrpc.GreetServiceStub asynchronousGreetClient =
                    GreetServiceGrpc.newStub(managedChannel);

            System.out.println("Preparing STREAMING call on STREAMING CLIENT side");
            // Call method on server using custom service <- RPC:
            clientStreamingCall(asynchronousGreetClient);
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

    private void clientStreamingCall(final GreetServiceGrpc.GreetServiceStub streamingClient) {

        final CountDownLatch latch = new CountDownLatch(1);

        // First define what happens when response is received -> Response Observer is something SERVER will invoke
        final StreamObserver<GreetRequest> requestStreamObserver =
                streamingClient.longGreet(new StreamObserver<GreetResponse>() {

                    // Notice the following:
                    //      On SERVER side we defined RequestObserver in which we said - when that Observer emits onComplete, we will call OnNext, and then onComplete on ResponseObserver
                    //          -> this here is definition of that ResponseObserver
                    //          - meaning: when client is done transmitting, it's subscriber (in this case the SERVER) will call onCompleted for the requestObserversStream
                    //              - that onCompleted will then call onNext and onCompleted defined here notifying CLIENT that it's response is ready and after that terminate transmission

                    @Override
                    public void onNext(GreetResponse value) {
                        // whenever we get response from the server, what do we do
                        // in this current case, will be called only once
                        System.out.println("UNARY response received from SERVER side: " + value + "{inside onNext of ResponseObserver - the one that Server calls when done}");
                    }

                    @Override
                    public void onError(Throwable t) {
                        // when server reports an error
                    }

                    @Override
                    public void onCompleted() {
                        // when server is done transmitting
                        // in this current case will be called right after one and only 'onNext' call
                        System.out.println("UNARY transmission from SERVER side ended {inside onCompleted of ResponseObserver - the one that Server calls when done}" );
                        latch.countDown();
                    }
                });

        // Then define requests
        final GreetRequestOrBuilder greetRequestOrBuilder = GreetRequest.newBuilder();
        for (int i = 1; i < 4 ; i++) {

            final Greeting greeting = Greeting.newBuilder().setFirstName("whatsoever" + i).build();
            final GreetRequest greetRequest =
                    ((GreetRequest.Builder) greetRequestOrBuilder).setGreeting(greeting)
                    .build();

            System.out.println("STREAMING request prepared on CLIENT side: " + greetRequest + "{calling onNext of RequestObserver - the one that Server will process in RequestObserver defined inside him}");
            requestStreamObserver.onNext(greetRequest);
        }

        // We notify Server that client is done with sending data
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        System.out.println("Hello gRPC from Client side");

        final GreetingClient client = new GreetingClient();
        client.run();

    }
}
