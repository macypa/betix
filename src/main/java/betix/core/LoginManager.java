package betix.core;

import betix.core.schedule.RetryTaskInterface;

public interface LoginManager extends RetryTaskInterface {
    boolean login() throws Exception;

    void logout();
}
