package betix.bet365;

import betix.core.BettingMachine;
import betix.core.MessageBoxFrame;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.data.AccountInfo;
import betix.core.data.Event;
import betix.core.data.MatchInfo;
import betix.core.data.MatchState;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.sikuli.script.*;

import java.util.Date;
import java.util.regex.Matcher;

class AccountInfoManager {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);


    private static final String balanceRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.balanceRegEx);
    private static final String historyMatchInfoLinkRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.historyMatchInfoLinkRegEx);
    private static final String matchInfoPendingState = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoPendingState);
    private static final String matchInfoLoseState = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoLoseState);
    private static final String matchInfoWinState = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoWinState);
    private static final String matchInfoStakeRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoStakeRegEx);
    private static final String matchInfoCoefficientRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoCoefficientRegEx);
    private static final String matchInfoWiningRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoWiningRegEx);
    private static final String matchInfoEventRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoEventRegEx);
    private static final String matchInfoDateRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoDateRegEx);

    private final AccountInfo accountInfo;
    private final Configuration accountConfig;

    private final BettingMachine betingMachine;
    private final Screen screen;
    private final MessageBoxFrame messageBox;

    AccountInfoManager(Bet365 bet365) {
        betingMachine = bet365;
        screen = bet365.screen;
        accountConfig = bet365.getAccountConfig();
        accountInfo = accountConfig.getAccountInfo();
        messageBox = bet365.messageBox;
    }

    public void collectInfo() {

        try {

            betingMachine.click(ImagePattern.PATTERN_HISTORY_LINK.pattern);
            screen.wait(ImagePattern.PATTERN_HISTORY_TITLE.pattern, 5);
            screen.hover(ImagePattern.PATTERN_HISTORY_TITLE.pattern);

            getBalanceInfo();

            collectFinishedMatchesInfo();
            collectPendingMatchesInfo();

        } catch (FindFailed e) {
            e.printStackTrace();
        } finally {
            try {
                screen.mouseMove(screen.getCenter());
            } catch (FindFailed f) {
                logger.error("can't move mouse to center of the screen...probably can't close the history page");
            }
            messageBox.setVisible(false);
            screen.type(Key.F4, KeyModifier.CTRL);
        }
    }

    private void getBalanceInfo() {

        screen.type(Key.TAB, KeyModifier.SHIFT);

        screen.type(Key.DOWN, KeyModifier.SHIFT);
        screen.type("c", KeyModifier.CTRL);
        String balanceInfo = Env.getClipboard();
        logger.info("found balanceInfo = " + balanceInfo);

        String price = searchRegEx(balanceInfo, balanceRegEx);
        accountInfo.setBalance(Double.valueOf(price.replaceAll(",", ".")));
    }

    private void collectPendingMatchesInfo() throws FindFailed {

        betingMachine.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.UP);
        screen.type(Key.TAB);
        screen.type(Key.RIGHT);
        betingMachine.wait(1);

        screen.type(Key.ENTER);
        betingMachine.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.TAB);
        screen.type(Key.TAB);

        getMatchInfo();
        messageBox.setVisible(false);
    }

    private void collectFinishedMatchesInfo() throws FindFailed {

        betingMachine.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.DOWN);
        screen.type(Key.TAB);
        screen.type(Key.RIGHT);
        betingMachine.wait(1);

        screen.type(Key.ENTER);
        betingMachine.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.TAB);
        screen.type(Key.TAB);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        getMatchInfo();
        messageBox.setVisible(false);
    }

    private void getMatchInfo() {
        while (true) {
            screen.type(Key.TAB);
            screen.type(Key.DOWN, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);

            String matchInfo = Env.getClipboard();
            if (!matchInfo.contains(historyMatchInfoLinkRegEx)) {
                logger.info("end of matchInfo, last is : " + matchInfo, screen.getCenter());
                accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
                accountConfig.saveConfig();
                return;
            }

            screen.type(Key.ENTER);
            betingMachine.waitMilisec(500);

            screen.type(Key.DOWN, KeyModifier.SHIFT);
            screen.type(Key.UP, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);
            matchInfo = Env.getClipboard();
            logger.trace("found matchInfo string = " + matchInfo);

            screen.type(Key.ENTER);

            try {
                MatchInfo info = parseMatchInfo(matchInfo);
                logger.info("found MatchInfo = " + info);
                if (MatchState.pending.equals(info.getState()) && !accountInfo.getMatchInfoPending().contains(info)) {
                    accountInfo.getMatchInfoPending().add(info);
                    accountInfo.getMatchInfoFinished().remove(info);
                } else if (!MatchState.pending.equals(info.getState()) && !accountInfo.getMatchInfoFinished().contains(info)) {
                    accountInfo.getMatchInfoFinished().add(info);
                } else {
                    accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
                    accountConfig.saveConfig();
                    return;
                }
            } catch (Exception e) {
                messageBox.showMessage("can't parse info: " + e.getLocalizedMessage(), logger);
            }
        }
    }

    private MatchInfo parseMatchInfo(String matchInfoString) {
        MatchInfo matchInfo = new MatchInfo();

        if (matchInfoString.contains(matchInfoPendingState)) {
            matchInfo.setState(MatchState.pending);
        } else if (matchInfoString.contains(matchInfoLoseState)) {
            matchInfo.setState(MatchState.losing);
        } else if (matchInfoString.contains(matchInfoWinState)) {
            matchInfo.setState(MatchState.winning);
        }

        matchInfo.setStake(Double.valueOf(searchRegEx(matchInfoString, matchInfoStakeRegEx).replaceAll(",", ".")));
        matchInfo.setCoefficient(Double.valueOf(searchRegEx(matchInfoString, matchInfoCoefficientRegEx).replaceAll(",", ".")));
        matchInfo.setWining(Double.valueOf(searchRegEx(matchInfoString, matchInfoWiningRegEx).replaceAll(",", ".")));

        matchInfo.setEvent(new Event(searchRegEx(matchInfoString, matchInfoEventRegEx)));
        matchInfo.setDate(new Date(searchRegEx(matchInfoString, matchInfoDateRegEx)));

        return matchInfo;
    }

    private String searchRegEx(String balanceInfo, String regex) {
        return searchRegEx(balanceInfo, regex, 1);
    }

    private String searchRegEx(String info, String regex, int matchPlace) {
        java.util.regex.Pattern MY_PATTERN = java.util.regex.Pattern.compile(regex);
        Matcher m = MY_PATTERN.matcher(info);
        if (m.find()) {
            return m.group(matchPlace);
        } else {
            logger.error("can't get regex {} from info {}", regex, info);
        }
        return "";
    }

}
