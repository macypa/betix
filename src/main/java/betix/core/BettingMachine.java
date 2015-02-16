package betix.core;

import betix.bet365.Bet365;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.schedule.RetryTask;
import betix.core.schedule.Scheduler;
import betix.core.sikuli.RetakeImageCapture;
import betix.core.sikuli.SikuliRobot;
import org.quartz.SchedulerException;
import org.sikuli.script.FindFailed;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public abstract class BettingMachine extends RetryTask {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    protected static final Configuration config = Configuration.getDefaultConfig();

    public final SikuliRobot sikuli = new SikuliRobot();

    public static void main(String[] args) {
        if (args.length > 0) {
            RetakeImageCapture.main(args);
        }

        logger.debug("locking program instance...");
        lockInstance(config.getConfigAsString(ConfigKey.lockFile));

        try {
            Scheduler.startSchedule();
        } catch (SchedulerException e) {
            logger.error("Scheduler not started!");
        }

        startBetProcess();
    }

    public static synchronized void startBetProcess() {
        logger.debug("starting the betting process");
        new Bet365().placeBets();
    }

    public void openSite() {
        sikuli.focusBrowser();

        try {
            logger.info("searching for site logo in tab ...");
            sikuli.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            logger.info("site already opened");

        } catch (FindFailed e) {
            logger.info("browser not opened, opening ...");
            sikuli.openBrowser();

            try {
                logger.info("searching for site logo in tab ...");
                sikuli.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open", ee);
            }
        }
    }

    private static boolean lockInstance(final String lockFile) {
        try {
            final File file = new File(lockFile);
            if (file.exists()) {
                logger.error("Lock file exists: " + lockFile + " Other instance is running or program was shutdown without deleting file.");
                Runtime.getRuntime().halt(1);
                return false;
            }
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                        } catch (Exception e) {
                            logger.error("Unable to remove lock file: " + lockFile, e);
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {
            logger.error("Unable to create and/or lock file: " + lockFile, e);
        }
        return false;
    }

    public abstract boolean login();

    public abstract boolean collectInfo();

    public abstract Configuration getAccountConfig();

    public abstract void openMyTeamsPage();

    public abstract void placeBets();
}
