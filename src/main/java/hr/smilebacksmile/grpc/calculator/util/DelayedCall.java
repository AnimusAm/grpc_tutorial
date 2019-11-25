package hr.smilebacksmile.grpc.calculator.util;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedCall {

    private static Logger LOGGER = LoggerFactory.getLogger(DelayedCall.class);

    final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static <T> ScheduledFuture<T> doWithDelay(final long millisecondsDelay, final Callable<T> call) {
        LOGGER.info("received task {}", scheduler);
        final ScheduledFuture<T> scheduledFuture = scheduler.schedule(call, millisecondsDelay, TimeUnit.MILLISECONDS);
        LOGGER.info("invoked task {}", scheduler);
        return scheduledFuture;
    }

    public static void shutdown() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            LOGGER.info("shutting down {}", scheduler);
            try{
                if(scheduler.awaitTermination(15000, TimeUnit.MILLISECONDS)) {
                    LOGGER.info("Executor terminated regularly {}", scheduler);
                } else {
                    LOGGER.info("Executor timeout elapsed before termination {}", scheduler);
                }
            }catch (InterruptedException e){
                LOGGER.error("Interrupted exception occurred: {}", e);
            }
        }
    }
}
