package hr.smilebacksmile.grpc.calculator;

import hr.smilebacksmile.grpc.calculator.util.DelayedCall;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;

public class DummyAsynchronousBehaviourTest {

    private static Logger LOGGER = LoggerFactory.getLogger(DummyAsynchronousBehaviourTest.class);

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<Integer> generateAfterRandMilliseconds(final long millisecondsDelay, final Integer number) {

        LOGGER.info("Received request to generate {} after {} ms - I'm gonna sleep now a bit", number, millisecondsDelay);
        Callable<Integer> callable = () -> number;
        return scheduler.schedule(callable, millisecondsDelay, TimeUnit.MILLISECONDS);
    }

    @Test
    public void dummyExampleTest() {

        final Random random = new Random();
        final int sleepTime = random.ints(1, 1000, 5000).findFirst().orElse(1000);
        final Integer randomNumber = random.ints(1, 10, 28).findFirst().orElse(5);


        final ScheduledFuture<Integer> scheduledFuture = generateAfterRandMilliseconds(sleepTime, randomNumber);

        try {
            LOGGER.info("Received random number {}",  scheduledFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void delayedExecutionCallTest() {

        final int[] randomDelays = new Random().ints(5, 1000, 5000).toArray();

        for (int i = 0; i < randomDelays.length; i++) {
            LOGGER.info("Calling {}. with delay {}", i, randomDelays[i]);
            final int number = i;
            ScheduledFuture<Integer> result = DelayedCall.doWithDelay(randomDelays[i], () -> number);
            try {
                LOGGER.info("Received {} with result {}", i, result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


    }

    @Test
    public void delayedExecutionCallTestWithRelevantDelay() {

        LOGGER.info("Calling 1. with delay 10000");
        ScheduledFuture<Integer> firstDelayed = DelayedCall.doWithDelay(10000, () -> 1);
        LOGGER.info("Calling 2. with delay 100");
        ScheduledFuture<Integer> secondDelayed = DelayedCall.doWithDelay(100, () -> 2);

        try {
            LOGGER.info("Received 1. with result {}", firstDelayed.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            LOGGER.info("Received 2. with result {}", secondDelayed.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delayedExecutionCallTestWithRelevantDelayAsync() {


        CompletableFuture<Integer> completableFutureFirst = CompletableFuture.supplyAsync(() -> {
                    LOGGER.info("Calling 1. with delay 10000");
                    return DelayedCall.doWithDelay(10000, () -> 1);
                }
        ).thenCompose(sf -> {
                Integer result = null;
                LOGGER.info("Prepared to receive result from 1. future");
                try {
                    result = sf.get();
                    LOGGER.info("Received: {} in 1. future", result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return CompletableFuture.completedFuture(result);
            }
            );

        CompletableFuture<Integer> completableFutureSecond = CompletableFuture.supplyAsync(() -> {
                    LOGGER.info("Calling 2. with delay 100");
                    return DelayedCall.doWithDelay(100, () -> 2);
                }
        ).thenCompose(sf -> {
                    Integer result = null;
                    LOGGER.info("Prepared to receive result from 2. future");
                    try {
                        result = sf.get();
                        LOGGER.info("Received: {} in 2. future", result);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return CompletableFuture.completedFuture(result);
                }
        );


        try {
            LOGGER.info("Outside received 1. with result {}", completableFutureFirst.get());
            LOGGER.info("Outside received 2. with result {}", completableFutureSecond.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        /*

        CompletableFuture.supplyAsync(() -> {
                    LOGGER.info("Calling 2. with delay 100");
                    return DelayedCall.doWithDelay(100, () -> 2);
                }
        ).thenCompose(sf -> {
            try {
                LOGGER.info("Received 2. with result {}", sf.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });
        */
    }
}
