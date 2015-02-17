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

    private boolean betPlaced;

    public boolean login() {
        return new LoginManager(this).executeWithRetry();
    }

    public boolean collectInfo() {
        return new AccountInfoManager(this).executeWithRetry();
    }

    public void openMyTeamsPage() {

        if (!login()) {
            throw new RuntimeException("not logged in");
        }

        try {
            openFootballPage();
            stopTV();

            logger.info("opening football teams page");
            sikuli.click(ImagePattern.PATTERN_FOOTBALL_TEAM_LINK.pattern);
            logger.info("opening football my teams page");
            sikuli.click(ImagePattern.PATTERN_FOOTBALL_MY_TEAMS_LINK.pattern);
        } catch (FindFailed e) {
            logger.error("error in openMyTeamsPage() ", e);
        }
    }

    public void openFootballPage() {
        for (int i = 0; i < 5; i++) {
            try {
                logger.info("opening football page");
                sikuli.click(ImagePattern.PATTERN_FOOTBALL_LINK.pattern);
                return;
            } catch (FindFailed e) {
                logger.warn("error in openMyTeamsPage() ");
                logger.info("scrolling down and try to find the link again...");
                try {
                    sikuli.mouseMove(sikuli.getCenter());
                } catch (FindFailed findFailed) {
                    logger.warn("can't move mouse to center");
                }
                sikuli.wheel(1, 1);
            }
        }
    }

    public void stopTV() {
        try {
            logger.info("searching tv region");
            Region region = sikuli.find(ImagePattern.PATTERN_LIVE_TV_TITLE.pattern, false).below(200);
            region.hover();
            logger.info("try stopping tv");
            sikuli.click(region, ImagePattern.PATTERN_LIVE_TV_STOP_BUTTON.pattern, false);

        } catch (FindFailed e) {
            logger.warn("error in stopTV() ");
        }
    }

    public void placeBets() {
        betPlaced = false;
        openMyTeamsPage();

        if (!collectInfo()) {
            throw new RuntimeException("can't get history info");
        }

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
            }
        }
        betPlaced = true;
        logger.hideMessageBox();

        new LoginManager(this).logout();
    }

    private void placeBet(Team team) throws FindFailed {

        logger.info("try clicking on the draw link button to place the bet");
        sikuli.click(sikuli.find(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern),
                ImagePattern.PATTERN_FOOTBALL_DRAW_BET_LINK.pattern);

        logger.info("finding the field to set the stake");
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

    @Override
    public void executeTask() {
        placeBets();
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return betPlaced;
    }
}
