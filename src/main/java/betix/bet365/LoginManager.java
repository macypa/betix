package betix.bet365;

import betix.core.BettingMachine;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.schedule.RetryTask;
import betix.core.sikuli.SikuliRobot;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

class LoginManager extends RetryTask {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    private final Configuration accountConfig;

    private final BettingMachine betingMachine;
    private final SikuliRobot sikuli;

    private boolean loggedIn = false;

    LoginManager(Bet365 bet365) {
        betingMachine = bet365;
        sikuli = bet365.sikuli;
        accountConfig = bet365.getAccountConfig();
    }

    public boolean login() throws Exception {

        loggedIn = false;
        if (doLogin()) {
            loggedIn = true;
            return true;
        }

        logger.info("closing tab with failed login attempt ...");
        sikuli.type(Key.F4, KeyModifier.CTRL);
        throw new RuntimeException("can't login");
    }

    private boolean doLogin() {
        if (checkLogin()) {
            return true;
        }

        try {
            logger.info("Trying to log in ...");
            sikuli.click(ImagePattern.PATTERN_LOGIN_FIELD.pattern);

            String username = typeInUsername();
            String plainText = typeInPassword();

            sikuli.type(Key.ENTER);

            if (checkLogin()) {
                if (username != null && !username.isEmpty()) {
                    accountConfig.addConfig(ConfigKey.username, username);
                    accountConfig.saveConfig();
                }
                if (plainText != null && !plainText.isEmpty()) {
                    accountConfig.addConfig(ConfigKey.password, encodePass(plainText));
                    accountConfig.saveConfig();
                }
                return true;
            } else {
                return false;
            }
        } catch (FindFailed e) {
            logger.error("Not logged in!", e);
            return false;
        }
    }

    private String typeInUsername() {
        String updateUser = "";
        String username = accountConfig.getConfigAsString(ConfigKey.username);
        if (username == null || username.trim().isEmpty()) {
            username = sikuli.input("Type your username");
            updateUser = username;
        }

        sikuli.click();
        sikuli.click();
        sikuli.type(username);
        sikuli.type("\t");

        return updateUser;
    }

    private String typeInPassword() throws FindFailed {
        String updatePass = "";
        String plainText = decodePass(accountConfig.getConfigAsString(ConfigKey.password));
        if (plainText == null || plainText.trim().isEmpty()) {
            plainText = sikuli.input("enter pass to encript and store");
            updatePass = plainText;

            sikuli.click(ImagePattern.PATTERN_PASSWORD_FIELD.pattern);
            sikuli.click();
        }

        sikuli.enableSikuliLog(false);
        sikuli.type(plainText);
        sikuli.enableSikuliLog(true);

        return updatePass;
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
            logger.info("checking login");
            betingMachine.openSite();

            checkNewMessages();

            sikuli.wait(ImagePattern.PATTERN_HISTORY_LINK.pattern, 5);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

    private void checkNewMessages() {
        try {
            sikuli.click(ImagePattern.PATTERN_NEW_MESSAGE_CLOSE_BUTTON.pattern);
        } catch (FindFailed findFailed) {
            logger.info("new message... closing message window.");
        }
    }

    @Override
    public void exeuteTask() throws Exception {
        login();
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return loggedIn;
    }
}
