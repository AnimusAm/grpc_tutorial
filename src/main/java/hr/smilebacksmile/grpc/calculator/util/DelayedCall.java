package hr.smilebacksmile.grpc.calculator.util;

import java.util.concurrent.*;

public class DelayedCall {

    final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static <T> ScheduledFuture<T> doWithDelay(final long millisecondsDelay, final Callable<T> call) {
        return scheduler.schedule(call, millisecondsDelay, TimeUnit.MILLISECONDS);
    }
}
