package betix.core.sikuli;

import betix.core.MessageBoxFrame;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.sikuli.basics.*;
import org.sikuli.script.*;

import java.util.concurrent.TimeUnit;

public class SikuliRobot {

    protected static final Configuration config = Configuration.getDefaultConfig();
    private static final Logger logger = LoggerFactory.getLogger(SikuliRobot.class);
    public final Screen screen = new Screen();
    public final MessageBoxFrame messageBox = new MessageBoxFrame();

    public SikuliRobot() {

        Double sikuliMinSimilarity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.sikuliMinSimilarity);
        setSikuliMinSimilarity(sikuliMinSimilarity.floatValue());

        exitListener();
    }

    public Screen getScreen() {
        return screen;
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
                System.exit(1);
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

    public Location getCenter() {
        return screen.getCenter();
    }

    public void enableSikuliLog(boolean enableLogs) {
        Settings.ActionLogs = enableLogs;
    }

    public void setSikuliMinSimilarity(double minSimilarity) {
        Settings.MinSimilarity = minSimilarity;
    }

    public <PatternFilenameRegionMatchLocation> int mouseMove(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            return screen.mouseMove(target);
        } catch (FindFailed findFailed) {
            logger.error("error in mouseMove on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match wait(PatternOrString target) throws FindFailed {
        try {
            return screen.wait(target, screen.getAutoWaitTimeout());
        } catch (FindFailed findFailed) {
            logger.error("error in wait on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match wait(PatternOrString target, double timeout) throws FindFailed {
        try {
            return screen.wait(target, 5);
        } catch (FindFailed findFailed) {
            logger.error("error in wait on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> Match find(PatternOrString target) throws FindFailed {
        try {
            screen.wait(target, 5);
            return screen.find(target);
        } catch (FindFailed findFailed) {
            logger.error("error in find on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternOrString> boolean isPresent(PatternOrString target) throws FindFailed {
        try {
            screen.wait(target, 5);
            screen.find(target);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }

    public <PatternFilenameRegionMatchLocation> int hover(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            screen.wait(target, 5);
            return screen.hover(target);
        } catch (FindFailed findFailed) {
            logger.error("error in hover on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternFilenameRegionMatchLocation> int hover(Region region, PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            region.wait(target, 5);
            return region.hover(target);
        } catch (FindFailed findFailed) {
            logger.error("error in hover on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public int click() {
        return screen.click();
    }

    public <PatternFilenameRegionMatchLocation> int click(PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            screen.wait(target, 5);
            return screen.click(target);
        } catch (FindFailed findFailed) {
            logger.error("error in click on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public <PatternFilenameRegionMatchLocation> int click(Region region, PatternFilenameRegionMatchLocation target) throws FindFailed {
        try {
            region.wait(target, 5);
            return region.click(target);
        } catch (FindFailed findFailed) {
            logger.error("error in click on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public int doubleClick(Pattern target) throws FindFailed {
        try {
            screen.wait(target, 5);
            return screen.doubleClick(target);
        } catch (FindFailed findFailed) {
            logger.error("error in doubleClick on target {} ", target, findFailed);
            throw findFailed;
        }
    }

    public int type(String text) {
        return screen.type(text);
    }

    public int type(String text, int modifiers) {
        return screen.type(text, modifiers);
    }

    public void popup(String text) {
        SikuliScript.popup(text);
    }

    public String input(String text) {
        return SikuliScript.input(text);
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

    public String getClipboard() {
        return Env.getClipboard();
    }
}
