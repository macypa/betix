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
        return screen.mouseMove(target);
    }

    public <PatternOrString> Match wait(PatternOrString pattern) throws FindFailed {
        return screen.wait(pattern, screen.getAutoWaitTimeout());
    }

    public <PatternOrString> Match wait(PatternOrString pattern, double timeout) throws FindFailed {
        return screen.wait(pattern, 5);
    }

    public <PatternOrString> Match find(PatternOrString target) throws FindFailed {
        screen.wait(target, 5);
        return screen.find(target);
    }

    public <PatternFilenameRegionMatchLocation> int hover(PatternFilenameRegionMatchLocation pattern) throws FindFailed {
        screen.wait(pattern, 5);
        return screen.hover(pattern);
    }

    public int click() throws FindFailed {
        return screen.click();
    }

    public <PatternFilenameRegionMatchLocation> int click(PatternFilenameRegionMatchLocation pattern) throws FindFailed {
        screen.wait(pattern, 5);
        return screen.click(pattern);
    }

    public <PatternFilenameRegionMatchLocation> int click(Region region, PatternFilenameRegionMatchLocation pattern) throws FindFailed {
        region.wait(pattern, 5);
        return region.click(pattern);
    }

    public int doubleClick(Pattern pattern) throws FindFailed {
        screen.wait(pattern, 5);
        return screen.doubleClick(pattern);
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
}
