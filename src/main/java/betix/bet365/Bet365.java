package betix.bet365;

import betix.core.BettingMachine;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.data.MatchInfo;
import betix.core.data.Team;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.awt.*;
import java.io.File;

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
        return new LoginManager(this).login();
    }

    public void collectInfo() {
        new AccountInfoManager(this).collectInfo();
    }

    public void openMyTeamsPage() {

        if (!login()) {
            return;
        }

        try {
            logger.error("opening football page");
            sikuli.click(ImagePattern.PATTERN_FOOTBALL_LINK.pattern);
            stopTV();

            logger.error("opening football teams page");
            sikuli.click(ImagePattern.PATTERN_FOOTBALL_TEAM_LINK.pattern);
            logger.error("opening football my teams page");
            sikuli.click(ImagePattern.PATTERN_FOOTBALL_MY_TEAMS_LINK.pattern);
        } catch (FindFailed e) {
            logger.error("error in openMyTeamsPage() ", e);
        }
    }

    public void stopTV() {
        try {
            logger.error("searching tv region");
            Region region = sikuli.find(ImagePattern.PATTERN_LIVE_TV_TITLE.pattern).below(200);
            region.hover();
            logger.error("try stopping tv");
            sikuli.click(region, ImagePattern.PATTERN_LIVE_TV_STOP_BUTTON.pattern);

        } catch (FindFailed e) {
            logger.error("error in stopTV() ", e);
        }
    }

    public void placeBets() {

        openMyTeamsPage();

        collectInfo();

        File teamDir = new File(ImagePattern.TEAM_DIR_NAME);
        for (File file : teamDir.listFiles()) {
            if (file.isDirectory())
                continue;

            Team team = new Team(file.getName());
            if (isAlreadyPlaced(team)) {
                logger.info("bet already placed for {}", team);
                continue;
            }
            try {
                logger.hideMessageBox();
                sikuli.click(team.getPattern());

                placeBet(team);

            } catch (FindFailed e) {
                logger.error("error selecting team {} in placeBets() for image {}", team.getName(), file.getName());
                logger.error("trying open again the my teams page");
                openMyTeamsPage();
            }
        }
    }

    private void placeBet(Team team) throws FindFailed {

        logger.error("try clicking on the draw link button to place the bet");
        sikuli.click(sikuli.find(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern),
                ImagePattern.PATTERN_FOOTBALL_DRAW_BET_LINK.pattern);

        logger.error("finding the field to set the stake");
        sikuli.click(ImagePattern.PATTERN_FOOTBALL_STAKE_FIELD.pattern);

        new Screen().type(String.valueOf(calculateStake(team)));

        logger.info("placing the bet");
        if (config.getConfigAsBoolean(ConfigKey.placeBet)) {
            sikuli.click(ImagePattern.PATTERN_PLACE_BET_BUTTON.pattern);
        } else {
            logger.showMessage("click", sikuli.find(ImagePattern.PATTERN_PLACE_BET_BUTTON.pattern).getTarget(), Color.green);
        }
    }

    private Double calculateStake(Team team) {
        for (MatchInfo matchInfo : accountConfig.getAccountInfo().getMatchInfoFinished()) {
            if (matchInfo.getEvent().isParticipant(team.getName())) {
                return getNextStake(matchInfo.getStake());
            }
        }

        return Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.minBetStake);
    }

    private Double getNextStake(double stake) {
        if (!Configuration.getDefaultConfig().getConfigAsBoolean(ConfigKey.useFibonacciForStakes)) {
            return stake * 2;
        }

        double minBetStake = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.minBetStake);
        double secondBetStake = minBetStake * 2;
        if (stake == minBetStake) return secondBetStake;

        double fibo1 = minBetStake, fibo2 = secondBetStake, nextStake = secondBetStake;
        while (stake >= nextStake) {
            nextStake = fibo1 + fibo2;
            fibo1 = fibo2;
            fibo2 = nextStake;
        }
        return nextStake;
    }

    private boolean isAlreadyPlaced(Team team) {
        for (MatchInfo matchInfo : accountConfig.getAccountInfo().getMatchInfoPending()) {
            if (matchInfo.getEvent().isParticipant(team.getName())) {
                return true;
            }
        }
        return false;
    }

    public Configuration getAccountConfig() {
        return accountConfig;
    }
}
