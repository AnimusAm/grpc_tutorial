package hr.smilebacksmile.grpc.greeting.server;

import hr.smilebacksmile.greet.*;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // super.greet(request, responseObserver);


        // Extract from request
        String result = "Hello " +
                Optional.ofNullable( request.getGreeting())
                        .map(Greeting::getFirstName)
                        .orElse("No NAME");

        // Form the response
        final GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        // Send response
        responseObserver.onNext(response);

        // Finalize RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetRequest request, StreamObserver<GreetManyTimeResponse> responseObserver) {
        final String name =  Optional.ofNullable( request.getGreeting())
                .map(Greeting::getFirstName)
                .orElse("No NAME");

        try {
            for (int i = 0; i < 5; i++) {
                String result = "Hello " + name + " greeting you " + (i + 1) + ". time.";
                // Form the response
                final GreetManyTimeResponse response = GreetManyTimeResponse.newBuilder().setResult(result).build();

                // Send response
                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Finalize RPC call
            responseObserver.onCompleted();
        }
    }
}
