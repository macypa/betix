package betix.core;

import betix.bet365.Bet365;
import betix.core.data.ImagePattern;
import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.sikuli.basics.HotkeyManager;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class BettingMachine {

    protected static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    public final Configuration config = new Configuration();


    public Screen screen = new Screen();
    public MessageBoxFrame messageBox = new MessageBoxFrame();

    public static void main(String[] args) {
        Bet365 betka = new Bet365();
        betka.setSikuliMinSimilarity(0.5);

        betka.exitListener();

        betka.openSite();
        if (!betka.login()) {
            System.exit(1);
        }

        betka.collectInfo();

        betka.openMyTeamsPage();

        System.exit(1);
    }

    protected void exitListener() {
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

    public void openSite() {
        focusBrowser();

        try {
            messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
            screen.wait(ImagePattern.PATTERN_LOGO_IN_TAB.pattern, 5);
            screen.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            logger.info("site already opened");
        } catch (FindFailed e) {
            messageBox.showMessage("opening site ...", screen.getCenter());
            App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));

            try {
                wait(3);
                messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
                screen.wait(ImagePattern.PATTERN_LOGO_IN_TAB.pattern, 5);
                screen.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open", ee);
            }
        }
    }

    public void enableSikuliLog(boolean enableLogs) {
        Settings.ActionLogs = enableLogs;
    }

    public void setSikuliMinSimilarity(double minSimilarity) {
        Settings.MinSimilarity = minSimilarity;
    }

    public void focusBrowser() {
        messageBox.showMessage("focusing Browser ...", screen.getCenter());
        App.focus(config.getConfigAsString(ConfigKey.browser));
    }

    public void maximisePage() {
        screen.type(Key.SPACE, KeyModifier.ALT);
        wait(1);
        try {
            screen.find(ImagePattern.PATTERN_UNMAXIMIZE.pattern);
        } catch (FindFailed f) {
            screen.type("x");
            wait(1);
            return;
        }

        screen.type(Key.ESC);
    }

    public void wait(int sec) {
        waitMilisec(TimeUnit.SECONDS.toMillis(sec));
    }

    public void waitMilisec(long milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean login();

    public abstract void collectInfo();

    public abstract Configuration getAccountConfig();

    public abstract void openMyTeamsPage();
}
