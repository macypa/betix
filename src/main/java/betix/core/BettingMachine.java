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

    private static RandomAccessFile randomAccessFile;
    private static FileLock fileLock;
    private static File lockedfile;

    public final SikuliRobot sikuli = new SikuliRobot();

    public static void main(String[] args) {
        if (args.length > 0) {
            RetakeImageCapture.main(args);
        }

        logger.debug("locking program instance...");
        lockInstance(config.getConfigAsString(ConfigKey.lockFile));

        if (config.getConfigAsBoolean(ConfigKey.asDaemon)) {
            try {
                Scheduler.startSchedule();
            } catch (SchedulerException e) {
                logger.error("Scheduler not started!");
            }
        }

        startBetProcess();

        if (!config.getConfigAsBoolean(ConfigKey.asDaemon)) {
            shutdown(0);
        }
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
            lockedfile = new File(lockFile);
            if (lockedfile.exists()) {
                logger.error("Lock file exists: " + lockFile + " Other instance is running or program was shutdown without deleting file.");
                shutdown(0);
                return false;
            }
            randomAccessFile = new RandomAccessFile(lockedfile, "rw");
            fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        unlockInstance();
                    }
                });
                return true;
            }
        } catch (Exception e) {
            logger.error("Unable to create and/or lock file: " + lockFile, e);
        }
        return false;
    }

    private static void unlockInstance() {

        try {
            if (fileLock != null) fileLock.release();
            if (randomAccessFile != null) randomAccessFile.close();
        } catch (Exception e) {
            logger.error("Unable to remove lock on file: " + lockedfile, e);
        }

        try {
            if (lockedfile != null) lockedfile.delete();
        } catch (Exception e) {
            logger.error("Unable to delete lock file: " + lockedfile, e);
        }

    }

    public static void shutdown(int status) {
        System.exit(status);
        Runtime.getRuntime().exit(status);

        unlockInstance();

        Runtime.getRuntime().halt(status);
    }

    public static Configuration getConfig() {
        return config;
    }

    public abstract boolean login();

    public abstract boolean collectInfo();

    public abstract void openMyTeamsPage();

    public abstract void placeBets();
}
