package betix.bet365;


import betix.core.AccountInfo;
import betix.core.ConfigKey;
import betix.core.Configuration;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
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
public class Bet365 {

    private Logger logger = LoggerFactory.getLogger(Bet365.class);
    private final Configuration config = new Configuration();

    static Screen screen = new Screen();
    static AccountInfo accountInfo = new AccountInfo();
    private final String DIR_PATTERN = config.getConfigAsString(ConfigKey.imageDir) + File.separator + config.getConfigAsString(ConfigKey.siteName) + File.separator;
    private final Pattern PATTERN_LOGO = new Pattern(DIR_PATTERN + "logo.png");
    private final Pattern PATTERN_HISTORY_BALANCE = new Pattern(DIR_PATTERN + "historyBalance.png");
    private final Pattern PATTERN_HISTORY_LINK = new Pattern(DIR_PATTERN + "historyLink.png");
    private final Pattern PATTERN_HISTORY_DATE = new Pattern(DIR_PATTERN + "historyDate.png");
    private final Pattern PATTERN_RADIO_BUTTON = new Pattern(DIR_PATTERN + "radioButton.png");
    private final Pattern PATTERN_HISTORY_MATCH_LINK = new Pattern(DIR_PATTERN + "historyMatchLink.png");
    private final Pattern PATTERN_HISTORY_PENDING_DROPDOWN = new Pattern(DIR_PATTERN + "historyPendingDropdown.png");
    private final Pattern PATTERN_HISTORY_PENDING = new Pattern(DIR_PATTERN + "historyPending.png");
    private final Pattern PATTERN_HISTORY_FINISHED_DROPDOWN = new Pattern(DIR_PATTERN + "historyFinishedDropdown.png");
    private final Pattern PATTERN_HISTORY_FINISHED = new Pattern(DIR_PATTERN + "historyFinished.png");
    private final Pattern PATTERN_HISTORY_SELLECTION_TYPE_DRAW = new Pattern(DIR_PATTERN + "historySellectionTypeDraw.png");
    private final Pattern PATTERN_HISTORY_BUTTON_FIND = new Pattern(DIR_PATTERN + "historyButtonFind.png");
    private final Pattern PATTERN_VIEW_PAGE_SOURCE = new Pattern(DIR_PATTERN + "viewPageSource.png");
    private final Pattern PATTERN_FOOTBALL_LINK = new Pattern(DIR_PATTERN + "football.png").similar(0.5f);
    private final Pattern PATTERN_LOGOUT_LINK = new Pattern(DIR_PATTERN + "logoutLink.png");
    private final Pattern PATTERN_LOGIN_FIELD = new Pattern(DIR_PATTERN + "loginField.png");
    private final Pattern PATTERN_PASSWORD_FIELD = new Pattern(DIR_PATTERN + "passwordField.png");
    private final Pattern PATTERN_LOGIN_OK_BUTTON = new Pattern(DIR_PATTERN + "loginOKbutton.png");

    public static void main(String[] args) {
        //Settings.ActionLogs = false;

        Bet365 betka = new Bet365();
        betka.openSite();

        if (!betka.login()) {
            System.exit(1);
        }

        betka.collectInfo();

        //betka.openFootbalPage();
    }

    private void openSite() {
        App.focus(config.getConfigAsString(ConfigKey.browser));

        try {
            wait(3);
            screen.wait(PATTERN_LOGO, 10);
            logger.info("site already opened");
        } catch (FindFailed e) {
            App.open(config.getConfigAsString(ConfigKey.browser) + " " + config.getConfigAsString(ConfigKey.siteUrl));

            try {
                wait(3);
                screen.wait(PATTERN_LOGO, 10);
            } catch (FindFailed ee) {
                logger.error("can't find logo, probably site didn't open");
            }
        }
    }

    private boolean login() {
        if (checkLogin()) {
            return true;
        }

        try {
            logger.info("Trying to log in ...");

            String username = config.getConfigAsString(ConfigKey.username, true);
            if (username == null || username.trim().isEmpty()) {
                username = SikuliScript.input("Type your username");
            }

            screen.click(PATTERN_LOGIN_FIELD);
            screen.click();
            screen.type(username);
            screen.type("\t");

            String plainText = decodePass(config.getConfigAsString(ConfigKey.password, true));
            if (plainText == null || plainText.trim().isEmpty()) {
                plainText = SikuliScript.input("enter pass to encript and store");

                screen.click(PATTERN_PASSWORD_FIELD);
                screen.click();
            }

            screen.type(plainText);
            screen.type("\n");

            if (checkLogin()) {
                config.addConfig(ConfigKey.username, username, false, true);
                config.addConfig(ConfigKey.password, encodePass(plainText), false, true);
                config.saveConfig();
            } else {
                return false;
            }
        } catch (FindFailed e) {
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
            App.focus(config.getConfigAsString(ConfigKey.browser));
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

    private void collectInfo() {

        try {

            screen.click(PATTERN_HISTORY_LINK);
            wait(1);

            getBalanceInfo();

            screen.click(PATTERN_HISTORY_DATE);
            wait(1);

            collectPendingMatchesInfo();
            collectFinishedMatchesInfo();

            // click on all "Равен @" img in loop
            // search for  "Равен" img as black text
            // type shift+Dwon
            // copy clipborad
            // parse text
            // save to config

                    /*

Равен 	Фулъм v Съндърланд
(Краен Резултат) 	03/02/2015 	Никой 	3.40 	Загубен
Залог:  0,50   Печалби:  0,00

                     */


                    /*

Равен 	Клермон Фуут v Ниор
(Краен Резултат) 	06/02/2015 	Никой 	3.10 	Печеливш
Залог:  0,50   Печалби:  1,55

                     */

//            screen.rightClick();
//            wait(1);
//            screen.click(PATTERN_VIEW_PAGE_SOURCE);
//            wait(1);
//            screen.type("a", KeyModifier.CTRL);
//            wait(1);
//            screen.type("c", KeyModifier.CTRL);
//            wait(1);
//            String source = Env.getClipboard();
//            System.out.println("source = " + source);
        } catch (FindFailed e) {
            e.printStackTrace();
        } finally {
            screen.type(Key.F4, KeyModifier.CTRL);
        }
    }

    private void maximisePage() {
        screen.type(Key.SPACE, KeyModifier.ALT);
        wait(1);
        screen.type("x");
        wait(1);
    }

    private void getBalanceInfo() {

        // double click on Баланс img
            /*
            Спорт История (BGN)
                    198,55 BGN
                    Баланс
            */

        try {
            screen.doubleClick(PATTERN_HISTORY_BALANCE);
            screen.type(Key.UP, KeyModifier.SHIFT);
            wait(1);
            screen.type("c", KeyModifier.CTRL);
            wait(1);
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

    /**
     * // click on all "Равен @" img in loop
     * // search for  "Равен" img as black text
     * // type shift+Dwon
     * // copy clipborad
     * // parse text
     * // save to config
     * <p>
     * <p>
     * Равен 	Фулъм v Съндърланд
     * (Краен Резултат) 	03/02/2015 	Никой 	3.40 	Загубен
     * Залог:  0,50   Печалби:  0,00
     * <p>
     * <p>
     * <p>
     * Равен 	Клермон Фуут v Ниор
     * (Краен Резултат) 	06/02/2015 	Никой 	3.10 	Печеливш
     * Залог:  0,50   Печалби:  1,55
     *
     * @throws FindFailed
     */
    private void collectPendingMatchesInfo() throws FindFailed {

        screen.click(PATTERN_HISTORY_BUTTON_FIND);
        wait(2);
        maximisePage();

        Iterator<Match> matches;
        try {
            matches = screen.findAll(PATTERN_HISTORY_MATCH_LINK);
        } catch (FindFailed f) {
            logger.info("no matches pending found.");
            return;
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            match.click();
            wait(1);

            screen.doubleClick(PATTERN_HISTORY_SELLECTION_TYPE_DRAW);

            screen.type(Key.DOWN, KeyModifier.SHIFT);
            wait(1);
            screen.type("c", KeyModifier.CTRL);
            wait(1);
            String matchInfo = Env.getClipboard();
            logger.info("found matchInfo = " + matchInfo);

            //colapse match info
            match.click();
            wait(1);
        }
    }

    private void collectFinishedMatchesInfo() throws FindFailed {
        screen.click(PATTERN_HISTORY_PENDING_DROPDOWN);
        wait(1);
        screen.type(Key.DOWN);
        screen.type(Key.ENTER);
        wait(1);

        screen.click(PATTERN_HISTORY_BUTTON_FIND);
        wait(1);
        maximisePage();

        Iterator<Match> matches;
        try {
            matches = screen.findAll(PATTERN_HISTORY_MATCH_LINK);
        } catch (FindFailed f) {
            logger.info("no matches finished found.");
            return;
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            match.click();
            wait(1);

            screen.doubleClick(PATTERN_HISTORY_SELLECTION_TYPE_DRAW);

            screen.type(Key.DOWN, KeyModifier.SHIFT);
            wait(1);
            screen.type("c", KeyModifier.CTRL);
            wait(1);
            String matchInfo = Env.getClipboard();
            logger.info("found matchInfo = " + matchInfo);

            //colapse match info
            match.click();
            wait(1);
        }
    }

    private void openFootbalPage() {

        try {
            screen.hover(PATTERN_LOGO);
            screen.wheel(1, 1);
            wait(1);
            screen.click(PATTERN_FOOTBALL_LINK, 0);
        } catch (FindFailed e) {
            e.printStackTrace();
        }
    }

    private void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
