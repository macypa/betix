package betix.core.schedule;

import betix.core.BettingMachine;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("starting scheduled task");
        BettingMachine.startBetProcess();
    }

}