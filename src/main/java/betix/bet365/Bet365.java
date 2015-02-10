package betix.bet365;

import betix.core.BettingMachine;
import betix.core.ConfigKey;
import betix.core.Configuration;
import betix.core.data.ImagePattern;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.FindFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * login page https://members.788-sb.com/MEMBERS/Login/
 * <p>
 * https://members.788-sb.com/members/Authenticated/KYC/default.aspx?pageId=8936&prdid=1&ibpid=0&rUrl=&dlru=
 * <p>
 * settings (history page )
 * https://members.788-sb.com/MEMBERS/Authenticated/History/Sports/Default.aspx
 * <p>
 * <p>
 * to eval scala scripts may be used
 * https://github.com/twitter/util/blob/master/util-eval/src/main/scala/com/twitter/util/Eval.scala
 * or
 * https://github.com/matthild/serverpages/blob/master/serverpages/source/scala/com/mh/serverpages/scala_/ScalaCompiler.scala#preparePage()
 */
public class Bet365 extends BettingMachine {

    private final Logger logger = LoggerFactory.getLogger(Bet365.class);
    private final Configuration accountConfig = new Configuration(Configuration.CONFIG_ACCOUNT_SPECIFIC_FILE);


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

            enableSikuliLog(false);
            screen.type(plainText);
            screen.type("\n");

            enableSikuliLog(true);

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
            focusBrowser();
            screen.wait(ImagePattern.PATTERN_LOGO_IN_TAB.pattern, 5);
            screen.click(ImagePattern.PATTERN_LOGO_IN_TAB.pattern);
            wait(1);
            screen.find(ImagePattern.PATTERN_LOGOUT_LINK.pattern);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

    public void collectInfo() {
        new AccountInfoManager(this).collectInfo();
    }

    public void openFootbalPage() {

        try {
            screen.hover(ImagePattern.PATTERN_LOGO.pattern);
            screen.wheel(1, 1);
            wait(1);
            screen.click(ImagePattern.PATTERN_FOOTBALL_LINK.pattern, 0);
        } catch (FindFailed e) {
            e.printStackTrace();
        }
    }

}
