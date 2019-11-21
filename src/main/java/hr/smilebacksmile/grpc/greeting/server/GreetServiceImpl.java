package hr.smilebacksmile.grpc.greeting.server;

import hr.smilebacksmile.greet.*;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        System.out.println("UNARY request received on SERVER side: " + request);

        // Extract from request
        String result = "Hello " +
                Optional.ofNullable( request.getGreeting())
                        .map(Greeting::getFirstName)
                        .orElse("No NAME");

        // Form the response
        final GreetResponse response = GreetResponse.newBuilder().setResult(result).build();
        System.out.println("Preparing UNARY response on SERVER side " + response);

        // Send response
        responseObserver.onNext(response);

        // Finalize RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetRequest request, StreamObserver<GreetManyTimeResponse> responseObserver) {

        System.out.println("UNARY request received on SERVER side: " + request);

        final String name =  Optional.ofNullable(request.getGreeting())
                .map(Greeting::getFirstName)
                .orElse("No NAME");

        System.out.println("Preparing STREAMING response on SERVER side:");
        try {
            for (int i = 0; i < 5; i++) {
                String result = "Hello " + name + " greeting you " + (i + 1) + ". time.";


                // Form the response
                final GreetManyTimeResponse response = GreetManyTimeResponse.newBuilder().setResult(result).build();
                System.out.println("[" + (i+1) + "] chunk of STREAMING response on SERVER side: " + response);

                // Send response
                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Finalize RPC call
            System.out.println("Ending STREAMING transmission on SERVER side:");
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<GreetRequest> longGreet(StreamObserver<GreetResponse> responseObserver) {
        final StreamObserver<GreetRequest> requestStreamObserver = new StreamObserver<GreetRequest>() {

            final StringBuilder sb = new StringBuilder();

            @Override
            public void onNext(GreetRequest value) {
                // when client sends each of it's requests, we will process each one of them on server side (by putting all received requests into one long message)
                if (sb.length() > 0) {
                    sb.append("\\n");
                }
                sb.append("Hello server from :").append(value.getGreeting().getFirstName());

                System.out.println("STREAMING REQUEST received from CLIENT side: " + value + "{inside onNext of RequestObserver - the one that Client calls for each request}" );

            }

            @Override
            public void onError(Throwable t) {
                // what happens when client request results in error
            }

            @Override
            public void onCompleted() {
                // when Client is done, Server will send response
                //  -> here we do what we need when Client is done transmitting - and we communicate with Client using response observer
                System.out.println("STREAMING REQUEST received from CLIENT side {inside onCompleted of RequestObserver - the one that Client calls when it's done transmitting}" );

                final String response = sb.toString();
                System.out.println("Prepared UNARY response on SERVER side: " + response);
                responseObserver.onNext(GreetResponse.newBuilder().setResult(response).build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }
}
