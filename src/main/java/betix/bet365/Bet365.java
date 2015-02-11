package betix.bet365;

import betix.core.BettingMachine;
import betix.core.Configuration;
import betix.core.ImagePattern;
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
    private final Configuration
            accountConfig = new Configuration(Configuration.CONFIG_ACCOUNT_SPECIFIC_FILE);

    public boolean login() {
//        return new LoginManager(this).login();
        return true;
    }

    public void collectInfo() {
//        new AccountInfoManager(this).collectInfo();
    }

    public void openMyTeamsPage() {
        try {
            screen.click(ImagePattern.PATTERN_FOOTBALL_LINK.pattern);

            screen.wait(ImagePattern.PATTERN_FOOTBALL_TEAM_LINK.pattern, 5);
            screen.click(ImagePattern.PATTERN_FOOTBALL_TEAM_LINK.pattern);

            screen.wait(ImagePattern.PATTERN_FOOTBALL_MY_TEAMS_LINK.pattern, 5);
            screen.click(ImagePattern.PATTERN_FOOTBALL_MY_TEAMS_LINK.pattern);

        } catch (FindFailed e) {
            logger.error("error in openMyTeamsPage() ", e);
        }
    }

    public void placeBets() {
        try {

            placeBet();

        } catch (FindFailed e) {
            logger.error("error in placeBets() ", e);
        }
    }

    private void placeBet() throws FindFailed {
        screen.wait(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern, 5);
        screen.find(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern).
                below(50).click(ImagePattern.PATTERN_FOOTBALL_DRAW_BET_LINK.pattern);

        screen.wait(ImagePattern.PATTERN_FOOTBALL_STAKE_FIELD.pattern, 5);
        screen.click(ImagePattern.PATTERN_FOOTBALL_STAKE_FIELD.pattern);

        screen.type("0.50");

        logger.info("placing the bet");
//            screen.type(Key.ENTER);
    }

    public Configuration getAccountConfig() {
        return accountConfig;
    }
}
