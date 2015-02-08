package betix.bet365;


import betix.core.ConfigKey;
import betix.core.Configuration;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

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
    private static final String DIR_PATTERN = config.getConfigAsString(ConfigKey.imageDir) + File.separator + config.getConfigAsString(ConfigKey.siteName) + File.separator;
    private static final Pattern PATTERN_LOGO = new Pattern(DIR_PATTERN + "logo.png");
    private static final Pattern PATTERN_HISTORY_LINK = new Pattern(DIR_PATTERN + "historyLink.png");
    private static final Pattern PATTERN_HISTORY_DATE = new Pattern(DIR_PATTERN + "historyDate.png");
    private static final Pattern PATTERN_RADIO_BUTTON = new Pattern(DIR_PATTERN + "radioButton.png");
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

        cloectInfo();

        openFootbalPage();
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

    private static void cloectInfo() {

        try {
            screen.click(PATTERN_HISTORY_LINK);
            wait(1);
            screen.find(PATTERN_HISTORY_DATE).click(PATTERN_RADIO_BUTTON);
            wait(1);
            screen.click(PATTERN_HISTORY_BUTTON_FIND);
            wait(1);
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

        } catch (FindFailed e) {
            e.printStackTrace();
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
