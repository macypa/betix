package betix.core;

import betix.bet365.Bet365;
import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.sikuli.basics.HotkeyManager;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class EntryPoint {

    protected static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);
    public final Configuration config = new Configuration();

    public Pattern PATTERN_UNMAXIMIZE = new Pattern("unmaximize.png");

    public Screen screen = new Screen();
    public MessageBoxFrame messageBox = new MessageBoxFrame();

    public static void main(String[] args) {

        Bet365 betka = new Bet365();

        betka.exitListener();

        betka.openSite(betka);
        if (!betka.login()) {
            System.exit(1);
        }

        betka.collectInfo();

        betka.openFootbalPage();

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

    public void openSite(Bet365 betka) {
        focusBrowser();

        try {
            wait(3);
            messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
            screen.wait(betka.PATTERN_LOGO, 10);
            logger.info("site already opened");
        } catch (FindFailed e) {
            messageBox.showMessage("opening site ...", screen.getCenter());
            App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));

            try {
                wait(3);
                messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
                screen.wait(betka.PATTERN_LOGO, 10);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open", ee);
            }
        }
    }

    public void setSikuliLog(boolean enableLogs) {
        Settings.ActionLogs = enableLogs;
    }

    public void focusBrowser() {
        messageBox.showMessage("focusing Browser ...", screen.getCenter());
        App.focus(config.getConfigAsString(ConfigKey.browser));
    }

    public void maximisePage() {
        screen.type(Key.SPACE, KeyModifier.ALT);
        wait(1);
        try {
            screen.find(PATTERN_UNMAXIMIZE);
        } catch (FindFailed f) {
            screen.type("x");
            wait(1);
            return;
        }

        screen.type(Key.ESC);
    }

    public void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
