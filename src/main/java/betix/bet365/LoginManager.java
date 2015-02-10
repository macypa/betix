package betix.bet365;

import betix.core.BettingMachine;
import betix.core.ConfigKey;
import betix.core.Configuration;
import betix.core.MessageBoxFrame;
import betix.core.data.ImagePattern;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginManager {

    protected static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    private final Configuration accountConfig;

    public BettingMachine betingMachine;
    public Screen screen;
    public MessageBoxFrame messageBox;

    LoginManager(Bet365 bet365) {
        betingMachine = bet365;
        screen = bet365.screen;
        accountConfig = bet365.getAccountConfig();
        messageBox = bet365.messageBox;
    }

    public boolean login() {
        if (checkLogin()) {
            return true;
        }

        try {
            messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
            logger.info("Trying to log in ...");

            screen.click(ImagePattern.PATTERN_LOGIN_FIELD.pattern);
            String username = accountConfig.getConfigAsString(ConfigKey.username);
            if (username == null || username.trim().isEmpty()) {
                messageBox.setVisible(false);
                username = SikuliScript.input("Type your username");
            }

            screen.click();
            screen.type(username);
            screen.type("\t");

            String plainText = decodePass(accountConfig.getConfigAsString(ConfigKey.password));
            if (plainText == null || plainText.trim().isEmpty()) {
                messageBox.setVisible(false);
                plainText = SikuliScript.input("enter pass to encript and store");

                screen.click(ImagePattern.PATTERN_PASSWORD_FIELD.pattern);
                screen.click();
            }

            betingMachine.enableSikuliLog(false);
            screen.type(plainText);
            screen.type("\n");

            betingMachine.enableSikuliLog(true);

            if (checkLogin()) {
                accountConfig.addConfig(ConfigKey.username, username);
                accountConfig.addConfig(ConfigKey.password, encodePass(plainText));
                accountConfig.saveConfig();
            } else {
                return false;
            }
        } catch (FindFailed e) {
            messageBox.setVisible(false);
            SikuliScript.popup("Could NOT log in.");
            logger.error("Not logged in!", e);
            return false;
        }
        return true;
    }

    private String encodePass(String clearText) {
        if (clearText == null) return null;
        return new String(Base64.encodeBase64(clearText.getBytes()));
    }

    private String decodePass(String encodedPass) {
        if (encodedPass == null) return null;
        return new String(Base64.decodeBase64(encodedPass.getBytes()));

    }

    private boolean checkLogin() {
        try {
            betingMachine.focusBrowser();
            screen.wait(ImagePattern.PATTERN_LOGO_IN_TAB.pattern, 5);
            screen.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            betingMachine.wait(1);
            screen.find(ImagePattern.PATTERN_LOGOUT_LINK.pattern);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

}
