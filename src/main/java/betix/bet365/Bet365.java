package betix.bet365;


import betix.core.AccountInfo;
import betix.core.ConfigKey;
import betix.core.Configuration;
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

    private static Logger logger = LoggerFactory.getLogger(Bet365.class);
    private static final Configuration config = new Configuration();

    static Screen screen = new Screen();
    static AccountInfo accountInfo = new AccountInfo();
    private static final String DIR_PATTERN = config.getConfigAsString(ConfigKey.imageDir) + File.separator + config.getConfigAsString(ConfigKey.siteName) + File.separator;
    private static final Pattern PATTERN_LOGO = new Pattern(DIR_PATTERN + "logo.png");
    private static final Pattern PATTERN_HISTORY_BALANCE = new Pattern(DIR_PATTERN + "historyBalance.png");
    private static final Pattern PATTERN_HISTORY_LINK = new Pattern(DIR_PATTERN + "historyLink.png");
    private static final Pattern PATTERN_HISTORY_DATE = new Pattern(DIR_PATTERN + "historyDate.png");
    private static final Pattern PATTERN_RADIO_BUTTON = new Pattern(DIR_PATTERN + "radioButton.png");
    private static final Pattern PATTERN_HISTORY_MATCH_LINK = new Pattern(DIR_PATTERN + "historyMatchLink.png");
    private static final Pattern PATTERN_HISTORY_PENDING_DROPDOWN = new Pattern(DIR_PATTERN + "historyPendingDropdown.png");
    private static final Pattern PATTERN_HISTORY_PENDING = new Pattern(DIR_PATTERN + "historyPending.png");
    private static final Pattern PATTERN_HISTORY_FINISHED_DROPDOWN = new Pattern(DIR_PATTERN + "historyFinishedDropdown.png");
    private static final Pattern PATTERN_HISTORY_FINISHED = new Pattern(DIR_PATTERN + "historyFinished.png");
    private static final Pattern PATTERN_HISTORY_SELLECTION_TYPE_DRAW = new Pattern(DIR_PATTERN + "historySellectionTypeDraw.png");
    private static final Pattern PATTERN_HISTORY_BUTTON_FIND = new Pattern(DIR_PATTERN + "historyButtonFind.png");
    private static final Pattern PATTERN_VIEW_PAGE_SOURCE = new Pattern(DIR_PATTERN + "viewPageSource.png");
    private static final Pattern PATTERN_FOOTBALL_LINK = new Pattern(DIR_PATTERN + "football.png").similar(0.5f);
    private static final Pattern PATTERN_LOGOUT_LINK = new Pattern(DIR_PATTERN + "logoutLink.png");
    private static final Pattern PATTERN_LOGIN_FIELD = new Pattern(DIR_PATTERN + "loginField.png");
    private static final Pattern PATTERN_LOGIN_OK_BUTTON = new Pattern(DIR_PATTERN + "loginOKbutton.png");

    public static void main(String[] args) {

        openSite();

        if (!login()) {
            System.exit(1);
        }

        collectInfo();

        //openFootbalPage();
    }

    private static void openSite() {
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

    private static boolean login() {
        if (checkLogin()) {
            return true;
        }

        try {
            logger.info("Trying to log in ...");

            String username = config.getConfigAsString(ConfigKey.username);
            if (username == null || username.trim().isEmpty()) {
                username = SikuliScript.input("Type your username");
            }

            screen.click(PATTERN_LOGIN_FIELD);
            screen.click();
            screen.type(username);
            screen.type("\t");

            String pass = config.getConfigAsString(ConfigKey.password);
            if (pass == null || pass.trim().isEmpty()) {
                SikuliScript.popup("Click ok only when you've typed your password.");
                App.focus(config.getConfigAsString(ConfigKey.browser));
                wait(3);
                try {
                    screen.click(PATTERN_LOGIN_OK_BUTTON);
                } catch (FindFailed e) {
                    logger.error("can't find login ok button. but maybe user already clicked it...");
                }

            } else {
                screen.type(pass);
                screen.type("\n");
            }

            if (checkLogin()) {
                config.addConfig(ConfigKey.username, username);
                config.saveConfig();
            }
        } catch (FindFailed e) {
            SikuliScript.popup("Could NOT log in.");
            logger.error("Not logged in!");
            return false;
        }
        return true;
    }

    private static boolean checkLogin() {
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

    private static void collectInfo() {

        try {

            screen.click(PATTERN_HISTORY_LINK);
            wait(1);

            getBalanceInfo();

            screen.find(PATTERN_HISTORY_DATE).click(PATTERN_RADIO_BUTTON);
            wait(1);
            screen.click(PATTERN_HISTORY_BUTTON_FIND);
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

    private static void getBalanceInfo() {

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
    private static void collectPendingMatchesInfo() throws FindFailed {
        Iterator<Match> matches = screen.findAll(PATTERN_HISTORY_MATCH_LINK);
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

    private static void collectFinishedMatchesInfo() throws FindFailed {
        Match pendingDropdown = screen.find(PATTERN_HISTORY_PENDING_DROPDOWN);
        pendingDropdown.click();
        wait(1);
        pendingDropdown.click();
        wait(1);

        screen.click(PATTERN_HISTORY_FINISHED);
        wait(1);

        Iterator<Match> matches = screen.findAll(PATTERN_HISTORY_MATCH_LINK);
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

    private static void openFootbalPage() {

        try {
            screen.hover(PATTERN_LOGO);
            screen.wheel(1, 1);
            wait(1);
            screen.click(PATTERN_FOOTBALL_LINK, 0);
        } catch (FindFailed e) {
            e.printStackTrace();
        }
    }

    private static void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
