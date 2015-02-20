package betix.core.schedule;

public interface RetryTaskInterface {

    public boolean executeWithRetry();

    public void executeTask() throws Exception;

    public boolean isFinishedWithoutErrors();

}
