package betix.core;

import betix.bet365.Bet365;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.sikuli.basics.HotkeyManager;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.util.concurrent.TimeUnit;

public abstract class BettingMachine {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    protected static final Configuration config = Configuration.getDefaultConfig();


    public final Screen screen = new Screen();
    public final MessageBoxFrame messageBox = new MessageBoxFrame();

    public static void main(String[] args) {
        Bet365 betka = new Bet365();
        Double sikuliMinSimilarity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.sikuliMinSimilarity);
        betka.setSikuliMinSimilarity(sikuliMinSimilarity.floatValue());

        betka.exitListener();

        betka.openSite();
        if (!betka.login()) {
            System.exit(1);
        }

        betka.collectInfo();

        betka.openMyTeamsPage();
        betka.placeBets();

        System.exit(1);
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

    public void openSite() {
        focusBrowser();

        try {
            messageBox.showMessage("searching for <br>site logo ...", logger);
            screen.wait(ImagePattern.PATTERN_LOGO_IN_TAB.pattern, 5);
            screen.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            logger.info("site already opened");
        } catch (FindFailed e) {
            messageBox.showMessage("opening site ...", logger);
            App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));

            try {
                wait(3);
                messageBox.showMessage("searching for <br>site logo ...", logger);
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
        messageBox.showMessage("focusing Browser ...", logger);
        App.focus(config.getConfigAsString(ConfigKey.browser));
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

    public void click(Pattern pattern) throws FindFailed {
        screen.wait(pattern, 5);
        screen.click(pattern);
    }

    public void doubleClick(Pattern pattern) throws FindFailed {
        screen.wait(pattern, 5);
        screen.doubleClick(pattern);
    }

    public abstract boolean login();

    public abstract void collectInfo();

    public abstract Configuration getAccountConfig();

    public abstract void openMyTeamsPage();

    public abstract void placeBets();
}
