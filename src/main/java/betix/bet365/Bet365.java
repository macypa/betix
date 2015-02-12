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
        try {
            click(ImagePattern.PATTERN_FOOTBALL_LINK.pattern);

            click(ImagePattern.PATTERN_FOOTBALL_TEAM_LINK.pattern);

            click(ImagePattern.PATTERN_FOOTBALL_MY_TEAMS_LINK.pattern);

        } catch (FindFailed e) {
            logger.error("error in openMyTeamsPage() ", e);
        }
    }

    public void placeBets() {
        File teamDir = new File(ImagePattern.TEAM_DIR_NAME);
        for (File file : teamDir.listFiles()) {
            if (file.isDirectory())
                continue;

            Team team = new Team(file.getName());
            try {
                click(team.getPattern());

                placeBet(team);

            } catch (FindFailed e) {
                logger.error("error selecting team {} in placeBets() for image {}", team.getName(), file.getName());
            }
        }
    }

    private void placeBet(Team team) throws FindFailed {
        if (isAlreadyPlaced(team)) {
            return;
        }

        screen.wait(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern, 5);
        screen.find(ImagePattern.PATTERN_FOOTBALL_END_RESULT_COLUMN.pattern).
                below(50).click(ImagePattern.PATTERN_FOOTBALL_DRAW_BET_LINK.pattern);

        click(ImagePattern.PATTERN_FOOTBALL_STAKE_FIELD.pattern);

        screen.type(String.valueOf(calculateStake(team)));

        logger.info("placing the bet");
        if (config.getConfigAsBoolean(ConfigKey.placeBet)) {
            click(ImagePattern.PATTERN_PLACE_BET_BUTTON.pattern);
        } else {
            messageBox.showMessage("click", logger, screen.find(ImagePattern.PATTERN_PLACE_BET_BUTTON.pattern).getTarget());
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
