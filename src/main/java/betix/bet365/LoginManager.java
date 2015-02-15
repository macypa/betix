package betix.bet365;

import betix.core.BettingMachine;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.sikuli.SikuliRobot;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;

class LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    private final Configuration accountConfig;

    private final BettingMachine betingMachine;
    private final SikuliRobot sikuli;

    LoginManager(Bet365 bet365) {
        betingMachine = bet365;
        sikuli = bet365.sikuli;
        accountConfig = bet365.getAccountConfig();
    }

    public boolean login() {
        if (checkLogin()) {
            return true;
        }

        try {
            logger.info("searching for <br>site logo ...");
            logger.info("Trying to log in ...");

            sikuli.click(ImagePattern.PATTERN_LOGIN_FIELD.pattern);
            boolean updateUserPass = false;
            String username = accountConfig.getConfigAsString(ConfigKey.username);
            if (username == null || username.trim().isEmpty()) {
                username = sikuli.input("Type your username");
                updateUserPass = true;
            }

            sikuli.click();
            sikuli.click();
            sikuli.type(username);
            sikuli.type("\t");

            String plainText = decodePass(accountConfig.getConfigAsString(ConfigKey.password));
            if (plainText == null || plainText.trim().isEmpty()) {
                plainText = sikuli.input("enter pass to encript and store");
                updateUserPass = true;

                sikuli.click(ImagePattern.PATTERN_PASSWORD_FIELD.pattern);
                sikuli.click();
            }

            sikuli.enableSikuliLog(false);
            sikuli.type(plainText);
            sikuli.enableSikuliLog(true);
            sikuli.type(Key.ENTER);

            if (checkLogin()) {
                if (updateUserPass) {
                    accountConfig.addConfig(ConfigKey.username, username);
                    accountConfig.addConfig(ConfigKey.password, encodePass(plainText));
                    accountConfig.saveConfig();
                }
                return true;
            }
        } catch (FindFailed e) {
            sikuli.popup("Could NOT log in.");
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
            betingMachine.openSite();
            sikuli.wait(ImagePattern.PATTERN_HISTORY_LINK.pattern, 5);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

}
