package betix.core.sikuli;

import betix.core.MessageBoxFrame;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.quartz.SchedulerException;
import org.sikuli.basics.*;
import org.sikuli.script.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SikuliRobot extends Screen {

    protected static final Configuration config = Configuration.getDefaultConfig();
    private static final Logger logger = LoggerFactory.getLogger(SikuliRobot.class);
    public final MessageBoxFrame messageBox = new MessageBoxFrame();

    public SikuliRobot() {
        Double sikuliMinSimilarity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.sikuliMinSimilarity);
        setSikuliMinSimilarity(sikuliMinSimilarity.floatValue());

        exitListener();
    }

    public MessageBoxFrame getMessageBox() {
        return messageBox;
    }

    public void exitListener() {
        String key = "c"; // take care: US-QWERTY keyboard layout based !!!
        int modifiers = KeyModifier.ALT + KeyModifier.CTRL;

        HotkeyListener c_ALT_CTRL = new HotkeyListener() {
            @Override
            public void hotkeyPressed(HotkeyEvent e) {
                System.out.println("c_ALT_CTRL detected! exiting...");
                try {
                    betix.core.schedule.Scheduler.stopSchedule();
                } catch (SchedulerException e1) {
                    logger.error("can't stop cron scheduled job", e);
                }
                System.exit(0);
                Runtime.getRuntime().halt(0);
            }
        };

        HotkeyManager.getInstance().addHotkey(key, modifiers, c_ALT_CTRL);
    }

    public App focusBrowser() {
        messageBox.showMessage("focusing Browser ...", logger);
        return App.focus(config.getConfigAsString(ConfigKey.browser));
    }

    public App openBrowser() {
        messageBox.showMessage("opening site ...", logger);
        return App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));
    }

    public void enableSikuliLog(boolean enableLogs) {
        Settings.ActionLogs = enableLogs;
    }

    public void setSikuliMinSimilarity(double minSimilarity) {
        Settings.MinSimilarity = minSimilarity;
    }

    public <PatternFilenameRegionMatchLocation> int mouseMove(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            return super.mouseMove(target);
        } catch (FindFailed findFailed) {
            logger.error("error in mouseMove on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match wait(PatternOrString target) throws FindFailed {
        try {
            return super.wait(target, getAutoWaitTimeout());
        } catch (FindFailed findFailed) {
            logger.error("error in wait on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match wait(PatternOrString target, double timeout) throws FindFailed {
        try {
            return super.wait(target, 5);
        } catch (FindFailed findFailed) {
            logger.error("error in wait on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match find(PatternOrString target) throws FindFailed {
        try {
            super.wait(target, 5);
            return super.find(target);
        } catch (FindFailed findFailed) {
            logger.error("error in find on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> boolean isPresent(PatternOrString target) throws FindFailed {
        try {
            super.wait(target, 5);
            super.find(target);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }

    public <PatternFilenameRegionMatchLocation> int hover(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            super.wait(target, 5);
            return super.hover(target);
        } catch (FindFailed findFailed) {
            logger.error("error in hover on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternFilenameRegionMatchLocation> int hover(Region region, PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            region.wait(target, 5);
            return region.hover(target);
        } catch (FindFailed findFailed) {
            logger.error("error in hover on target {} - Screenshot filename {}, region filename {}", target, takeSnapshot(), takeSnapshot(region), findFailed);
            throw findFailed;
        }
    }

    public <PatternFilenameRegionMatchLocation> int click(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            super.wait(target, 5);
            return super.click(target);
        } catch (FindFailed findFailed) {
            logger.error("error in click on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public <PatternFilenameRegionMatchLocation> int click(Region region, PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            region.wait(target, 5);
            return region.click(target);
        } catch (FindFailed findFailed) {
            logger.error("error in click on target {} - Screenshot filename {}, region filename {}", target, takeSnapshot(), takeSnapshot(region), findFailed);
            throw findFailed;
        }
    }

    public int doubleClick(Pattern target) throws FindFailed {
        try {
            super.wait(target, 5);
            return super.doubleClick(target);
        } catch (FindFailed findFailed) {
            logger.error("error in doubleClick on target {} - Screenshot filename {}", target, takeSnapshot(), findFailed);
            throw findFailed;
        }
    }

    public void wait(int sec) {
        waitMilisec(TimeUnit.SECONDS.toMillis(sec));
    }

    public void waitMilisec(long milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            logger.error("error in waitMilisec() ", e);
        }
    }

    public void popup(String text) {
        SikuliScript.popup(text);
    }

    public String input(String text) {
        return SikuliScript.input(text);
    }

    public String getClipboard() {
        return Env.getClipboard();
    }

    public String takeSnapshot() {
        return takeSnapshot(this);
    }

    public String takeSnapshot(Region reg) {
        return takeSnapshot(reg.getRect());
    }

    public String takeSnapshot(Rectangle rectangle) {
        String name = String.valueOf(System.currentTimeMillis());
        try {
            ImageIO.write(super.capture(rectangle).getImage(), "png", new File(Logger.LOG_DIR, name + ".png"));
        } catch (IOException e) {
            logger.debug("can't write screenshot image to file", e);
        }
        return name + ".png";
    }
}
