package hr.smilebacksmile.grpc.calculator.client;


import hr.smilebacksmile.calculator.*;
import hr.smilebacksmile.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import hr.smilebacksmile.calculator.CalculatorServiceGrpc.CalculatorServiceStub;
import hr.smilebacksmile.grpc.calculator.util.DelayedCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CalculatorClient {

    private ManagedChannel managedChannel;

    private void run(final Consumer<ManagedChannel> run) {
        // Create chanel
       managedChannel =
                ManagedChannelBuilder.forAddress("localhost", 50052)
                        .usePlaintext() // needed not to invoke SSL usage
                        .build();


        run.accept(managedChannel);

        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }

    private static void doSumOfNumbersCall(final CalculatorServiceBlockingStub unaryClient) {

        // Make the Operands to send in request
        final Operands operands = Operands.newBuilder()
                .addAllOperands(Arrays.asList(3, 10))
                .build();

        // Make the Request containing the prepared Operands
        final SumRequest request = SumRequest.newBuilder().setOperands(operands).build();

        System.out.println("UNARY request prepared on CLIENT side: " + request);

        // Call method on server using custom service <- RPC:
        final SumResponse response = unaryClient.sum(request);
        System.out.println("UNARY response received from SERVER side: " + response);
    }

    private static void doSquareRootCallThatProducesError(final CalculatorServiceBlockingStub unaryClient) {

        // Make the Request containing negative number
        final WholeNumberRequest request = WholeNumberRequest.newBuilder().setNumber(-1).build();

        System.out.println("UNARY request prepared on CLIENT side: " + request);

        // Call method on server using custom service <- RPC:
        try {
            final SquareRootResponse response = unaryClient.squareRoot(request);
            System.out.println("UNARY response received from SERVER side: " + response);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void doPrimeFactorsCall(final CalculatorServiceBlockingStub unaryClient) {

        // Make the LargeWholeNumber to send in request
        final LargeWholeNumber number = LargeWholeNumber.newBuilder().setNumber(478294039271719L).build();

        // Make the Request containing the prepared Number
        final PrimeFactorsRequest request = PrimeFactorsRequest.newBuilder().setNumber(number).build();
        System.out.println("UNARY request prepared on CLIENT side: " + request);

        // Call method on server using custom service <- RPC:
        final List<Long> receivedFactors = new LinkedList<>();
        // RPC to server to get the PrimeFactorsResponse - StreamingServer
        unaryClient.calculatePrimeFactors(request).forEachRemaining(
                primeFactorsResponse -> {
                    receivedFactors.add(primeFactorsResponse.getFactor().getNumber());
                    System.out.println("STREAMING response received from SERVER side: " + primeFactorsResponse.getFactor());
                }
        );
        System.out.println("Received all factors: " + receivedFactors.toString());
    }

    private static void doAverageCall(final CalculatorServiceStub streamingClient) {

        final CountDownLatch latch = new CountDownLatch(1);

        // First define what happens when response is received -> Response Observer is something SERVER will invoke
        final StreamObserver<WholeNumberRequest> requestStreamObserver =
                streamingClient.calculateAverage(new StreamObserver<AverageResponse>() {

                    @Override
                    public void onNext(AverageResponse value) {
                        // whenever we get response from the server, what do we do
                        // in this current case, will be called only once - when average is calculated (that is, when client announced it ends it's transmission
                        System.out.println("UNARY response received from SERVER side - average is " + value);
                    }

                    @Override
                    public void onError(Throwable t) {
                        // when server reports an error
                    }

                    @Override
                    public void onCompleted() {
                        // when server is done transmitting
                        // in this current case will be called right after one and only 'onNext' call
                        System.out.println("UNARY transmission from SERVER side ended" );
                        latch.countDown();
                    }
                });

        // Generate numbers that will be used in requests - whose average we are looking for
        final WholeNumberRequestOrBuilder averageRequestOrBuilder = WholeNumberRequest.newBuilder();

        final Random random = new Random();
        int minLimit = 38;
        int maxLimit = 8210567;
        int upperLimit = random.ints(1, minLimit, maxLimit).findFirst().orElse(maxLimit);

        int numberOfRandoms = random.ints(1, 1, 10).findFirst().orElse(10);

        random.ints(numberOfRandoms, minLimit, upperLimit).forEach( i ->
                {
                    final WholeNumberRequest request = ((WholeNumberRequest.Builder) averageRequestOrBuilder).setNumber(i).build();

                    System.out.println("STREAMING request prepared on CLIENT side: " + request);
                    requestStreamObserver.onNext(request);
                }
        );

        // We notify Server that client is done with sending data
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void doGlobalMaxCall(final CalculatorServiceStub streamingClient) {

        final CountDownLatch latch = new CountDownLatch(1);

        // First define what happens when response is received -> Response Observer is something SERVER will invoke
        final StreamObserver<WholeNumberRequest> requestStreamObserver =
                streamingClient.globalMaximum(new StreamObserver<GlobalMaxResponse>() {

                    @Override
                    public void onNext(GlobalMaxResponse value) {
                        System.out.println("STREAMING response received from SERVER side: " + value);
                    }

                    @Override
                    public void onError(Throwable t) {
                        // don't react to errors reported from server side
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("STREAMING transmission from SERVER side ended");
                        latch.countDown();
                    }
                });

        final WholeNumberRequestOrBuilder WholeNumberRequestOrBuilder = WholeNumberRequest.newBuilder();

        // altogether, 5 times requests will be generated
        IntStream.rangeClosed(1, 5).forEach( i -> {
                    final Random random = new Random();

                    // how many times request will be generated in this attempt
                    final Integer repeateance = random.ints(1, 1, 3).findFirst().orElse(1);

                    random.ints(repeateance, 1, 12).forEach(
                            randomNumber -> {
                                final Integer delay = random.ints(1, 100, 1000).findFirst().orElse(1000);

                                final WholeNumberRequest request = ((WholeNumberRequest.Builder) WholeNumberRequestOrBuilder).setNumber(randomNumber).build();

                                System.out.println("STREAMING request prepared on CLIENT side: " + request + " with delay: " + delay);

                                try {
                                    requestStreamObserver.onNext(DelayedCall.doWithDelay(delay, () -> request ).get());
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                }
        );

        // We notify Server that client is done with sending data
        requestStreamObserver.onCompleted();
        // !!! We have to shut it down otherwise it will halt JVM although all it's treads have finished execution
        DelayedCall.shutdown();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

     private static void doUnaryCall(final ManagedChannel managedChannel, final Consumer<CalculatorServiceBlockingStub> serverCall) {

        // Create clientStub - blocking, synchronous
        final CalculatorServiceBlockingStub synchronousCalculatorClient =
                CalculatorServiceGrpc.newBlockingStub(managedChannel);

        System.out.println("Preparing UNARY call on CLIENT side");
        // Call method on server using custom service <- RPC:
        serverCall.accept(synchronousCalculatorClient);
    }

    private static void doStreamingCall(final ManagedChannel managedChannel, final Consumer<CalculatorServiceStub> serverCall) {

        // Create Sum client Stub - blocking, synchronous
        final CalculatorServiceStub asynchronousCalculatorClient =
                CalculatorServiceGrpc.newStub(managedChannel);

        System.out.println("Preparing STREAMING call on STREAMING CLIENT side");
        // Call method on server using custom service <- RPC:
        serverCall.accept(asynchronousCalculatorClient);
    }

    public static void main(String[] args) {
        System.out.println("Calculator client side says hello");

        final CalculatorClient client = new CalculatorClient();

        // client.run(mc -> doUnaryCall(mc, CalculatorClient::doSumOfNumbersCall));
        // client.run(mc -> doUnaryCall(mc, CalculatorClient::doPrimeFactorsCall));
        // client.run(mc -> doStreamingCall(mc, CalculatorClient::doAverageCall));
        // client.run(mc -> doStreamingCall(mc, CalculatorClient::doGlobalMaxCall));
        client.run(mc -> doUnaryCall(mc, CalculatorClient::doSquareRootCallThatProducesError));


    }
}
