package betix.core.schedule;

import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import com.google.common.util.concurrent.ListenableFuture;
import com.nurkiewicz.asyncretry.AsyncRetryExecutor;
import com.nurkiewicz.asyncretry.RetryContext;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.nurkiewicz.asyncretry.function.RetryRunnable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class RetryTask {

    private static final Logger logger = LoggerFactory.getLogger(RetryTask.class);

    public abstract void exeuteTask() throws Exception;

    public abstract boolean isFinishedWithoutErrors();

    public boolean exeuteWithRetry() {

        final RetryTask task = this;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler).
                retryOn(Exception.class).
                retryOn(RuntimeException.class).
                withExponentialBackoff(5000, 2).     //500ms times 2 after each retry
                withMaxDelay(10_000).               //10 seconds
                withUniformJitter().                //add between +/- 100 ms randomly
                withMaxRetries(2);

        ListenableFuture<Void> future = executor.doWithRetry(new RetryRunnable() {
            @Override
            public void run(RetryContext context) throws Exception {
                task.exeuteTask();
            }
        });

        try {
            future.get();
        } catch (Exception e) {
            logger.error("error in retry task: " + e.getMessage(), e);
        }
        return task.isFinishedWithoutErrors();
    }
}
