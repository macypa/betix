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

public class EntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);
    public static final Configuration config = new Configuration();

    public static final Pattern PATTERN_UNMAXIMIZE = new Pattern("unmaximize.png");

    static Screen screen = new Screen();

    public static void main(String[] args) {
        Settings.ActionLogs = false;

        exitListener();

        Bet365 betka = new Bet365();
        openSite(betka);

        if (!betka.login()) {
            System.exit(1);
        }

        betka.collectInfo();

        betka.openFootbalPage();

        System.exit(1);
    }

    private static void exitListener() {
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

    public static void openSite(Bet365 betka) {
        focusBrowser();

        try {
            wait(3);
            screen.wait(betka.PATTERN_LOGO, 10);
            logger.info("site already opened");
        } catch (FindFailed e) {
            App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));

            try {
                wait(3);
                screen.wait(betka.PATTERN_LOGO, 10);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open");
            }
        }
    }

    public static void focusBrowser() {
        App.focus(config.getConfigAsString(ConfigKey.browser));
    }

    public static void maximisePage() {
        screen.type(Key.SPACE, KeyModifier.ALT);
        EntryPoint.wait(1);
        try {
            screen.find(PATTERN_UNMAXIMIZE);
        } catch (FindFailed f) {
            screen.type("x");
            EntryPoint.wait(1);
            return;
        }

        screen.type(Key.ESC);
    }

    public static void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
