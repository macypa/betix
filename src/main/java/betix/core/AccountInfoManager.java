package betix.core;

import betix.core.data.AccountInfo;
import betix.core.schedule.RetryTaskInterface;

public interface AccountInfoManager extends RetryTaskInterface {
    AccountInfo getAccountInfo();

    void collectInfo();

    void saveAccountInfo();
}
