package betix.core;

import betix.bet365.Bet365;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.schedule.Scheduler;
import betix.core.sikuli.RetakeImageCapture;
import betix.core.sikuli.SikuliRobot;
import org.quartz.SchedulerException;
import org.sikuli.script.FindFailed;

public abstract class BettingMachine {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    protected static final Configuration config = Configuration.getDefaultConfig();

    public final SikuliRobot sikuli = new SikuliRobot();

    public static void main(String[] args) {
        if (args.length > 0) {
            RetakeImageCapture.main(args);
        }

        try {
            Scheduler.startSchedule();
        } catch (SchedulerException e) {
            logger.error("Scheduler not started!");
        }

        startBetProcess();
    }

    public static void startBetProcess() {
        Bet365 betka = new Bet365();

        betka.openSite();
        betka.stopTV();

        if (!betka.login()) {
            return;
        }

        betka.collectInfo();

        betka.openMyTeamsPage();
        betka.placeBets();
    }

    public void openSite() {
        sikuli.focusBrowser();

        try {
            sikuli.messageBox.showMessage("searching for <br>site logo ...", logger);
            sikuli.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            logger.info("site already opened");

        } catch (FindFailed e) {
            sikuli.openBrowser();

            try {
                sikuli.messageBox.showMessage("searching for <br>site logo ...", logger);
                sikuli.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open", ee);
            }
        }
    }

    public abstract boolean login();

    public abstract void collectInfo();

    public abstract Configuration getAccountConfig();

    public abstract void openMyTeamsPage();

    public abstract void placeBets();
}
