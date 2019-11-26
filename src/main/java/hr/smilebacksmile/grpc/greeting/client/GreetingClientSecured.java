package hr.smilebacksmile.grpc.greeting.client;

import hr.smilebacksmile.greet.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GreetingClientSecured {

    private static boolean CALL_UNARY = true;


    ManagedChannel managedChannel;

    private void run() throws SSLException {

        /*
        If we would use plain text for the channel, and send request to the secured server, we would get: StatusRuntimeException: UNAVAILABLE: Network closed for unknown reason
        managedChannel = ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext() // needed to invoke SSL usage
                .build();
        */

        managedChannel = NettyChannelBuilder.forAddress("localhost", 50053)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("cert/ca.crt")).build())
                .build();

        doUnaryCallOverSecuredChannel(CALL_UNARY, this::unaryCall);

        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }

    private void doUnaryCallOverSecuredChannel(final boolean doCall, final Consumer<GreetServiceGrpc.GreetServiceBlockingStub> call) {

        if (doCall) {
            // Created GreetService client - blocking, synchronous
            final GreetServiceGrpc.GreetServiceBlockingStub synchronousGreetClient =
                    GreetServiceGrpc.newBlockingStub(managedChannel);

            System.out.println("Preparing UNARY call on CLIENT side");
            // Call method on server using custom service <- RPC:
            call.accept(synchronousGreetClient);
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



    public static void main(String[] args) throws SSLException {
        System.out.println("Hello gRPC from Client side");

        final GreetingClientSecured client = new GreetingClientSecured();
        client.run();

    }
}
