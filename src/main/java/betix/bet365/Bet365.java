package betix.bet365;

import betix.core.AccountInfo;
import betix.core.ConfigKey;
import betix.core.Configuration;
import betix.core.EntryPoint;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;

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
public class Bet365 extends EntryPoint {

    private final Logger logger = LoggerFactory.getLogger(Bet365.class);
    private final Configuration accountConfig = new Configuration(Configuration.CONFIG_ACCOUNT_SPECIFIC_FILE);

    AccountInfo accountInfo = new AccountInfo();

    private final String DIR_PATTERN = config.getConfigAsString(ConfigKey.imageDir)
            + File.separator + config.getConfigAsString(ConfigKey.siteName) + File.separator;

    public final Pattern PATTERN_FOOTBALL_LINK = new Pattern(DIR_PATTERN + "football.png").similar(similarity);
    public final Pattern PATTERN_HISTORY_BALANCE = new Pattern(DIR_PATTERN + "historyBalance.png");
    public final Pattern PATTERN_HISTORY_LINK = new Pattern(DIR_PATTERN + "historyLink.png");
    public final Pattern PATTERN_LOGIN_FIELD = new Pattern(DIR_PATTERN + "loginField.png").similar(similarity);
    public final Pattern PATTERN_LOGO = new Pattern(DIR_PATTERN + "logo.png").similar(similarity);
    public final Pattern PATTERN_LOGOUT_LINK = new Pattern(DIR_PATTERN + "logoutLink.png");
    public final Pattern PATTERN_PASSWORD_FIELD = new Pattern(DIR_PATTERN + "passwordField.png").similar(similarity);

    public boolean login() {
        if (checkLogin()) {
            return true;
        }

        try {
            messageBox.showMessage("searching for <br>site logo ...", screen.getCenter());
            logger.info("Trying to log in ...");

            screen.click(PATTERN_LOGIN_FIELD);
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

                screen.click(PATTERN_PASSWORD_FIELD);
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
            wait(3);
            screen.wait(PATTERN_LOGO, 10);
            screen.find(PATTERN_LOGOUT_LINK);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

    public void collectInfo() {

        try {

            screen.click(PATTERN_HISTORY_LINK);
            wait(1);

            getBalanceInfo();

            collectPendingMatchesInfo();
            collectFinishedMatchesInfo();
            screen.doubleClick(PATTERN_HISTORY_BALANCE);

        } catch (FindFailed e) {
            e.printStackTrace();
        } finally {
//            screen.type(Key.SPACE, KeyModifier.ALT);
//            wait(1);
//            screen.type("c");
            screen.type(Key.F4, KeyModifier.CTRL);
        }
    }

    private void getBalanceInfo() {
        try {
            screen.doubleClick(PATTERN_HISTORY_BALANCE);
            screen.type(Key.UP, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);
            String balanceInfo = Env.getClipboard();
            logger.info("found balanceInfo = " + balanceInfo);

            java.util.regex.Pattern MY_PATTERN = java.util.regex.Pattern.compile(".*?\\(BGN\\)\\s*(.*?)\\s*BGN");
            Matcher m = MY_PATTERN.matcher(balanceInfo);
            while (m.find()) {
                String s = m.group(1);
                Double balance = Double.valueOf(s.replaceAll(",", "."));
                logger.info("found balance = " + balance);
                accountInfo.setBalance(balance);
            }
        } catch (FindFailed findFailed) {
            logger.error("can't get balance .... no biggy.");
        }
    }

    private void collectPendingMatchesInfo() throws FindFailed {
        messageBox.showMessage("searching balance pattern", screen.getCenter());
        screen.doubleClick(PATTERN_HISTORY_BALANCE);
        screen.type(Key.TAB);
        screen.type(Key.UP);
        screen.type(Key.TAB);
        wait(1);
        screen.type(Key.RIGHT);
        wait(1);
        screen.type(Key.ENTER);
        wait(2);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        getMatchInfo();
    }

    private void collectFinishedMatchesInfo() throws FindFailed {
        messageBox.showMessage("searching balance pattern", screen.getCenter());
        screen.doubleClick(PATTERN_HISTORY_BALANCE);
        screen.type(Key.TAB);
        screen.type(Key.DOWN);
        screen.type(Key.TAB);
        wait(1);
        screen.type(Key.RIGHT);
        wait(1);
        screen.type(Key.ENTER);
        wait(2);
        screen.type(Key.TAB);
        screen.type(Key.TAB);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        getMatchInfo();
    }

    private void getMatchInfo() {
        while (true) {
            screen.type(Key.TAB);
            screen.type(Key.DOWN, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);

            String matchInfo = Env.getClipboard();
            if (!matchInfo.contains("Равен ")) {
                messageBox.showMessage("matchInfo : " + matchInfo, screen.getCenter());
                screen.type(Key.TAB, KeyModifier.SHIFT);
                screen.type(Key.TAB, KeyModifier.SHIFT);
                screen.type(Key.TAB, KeyModifier.SHIFT);
                wait(1);
                return;
            }

            screen.type(Key.ENTER);
            wait(1);

            screen.type(Key.DOWN, KeyModifier.SHIFT);
            screen.type(Key.UP, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);
            matchInfo = Env.getClipboard();
            logger.info("found matchInfo = " + matchInfo);

            screen.type(Key.ENTER);
            wait(1);

        }
    }

    public void openFootbalPage() {

        try {
            screen.hover(PATTERN_LOGO);
            screen.wheel(1, 1);
            wait(1);
            screen.click(PATTERN_FOOTBALL_LINK, 0);
        } catch (FindFailed e) {
            e.printStackTrace();
        }
    }

}
