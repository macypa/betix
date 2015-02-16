package betix.bet365;

import betix.core.BettingMachine;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.data.AccountInfo;
import betix.core.data.Event;
import betix.core.data.MatchInfo;
import betix.core.data.MatchState;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.sikuli.SikuliRobot;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

class AccountInfoManager {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);

    private static final String dateFormat = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.dateFormat);

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
    private final SikuliRobot sikuli;

    AccountInfoManager(Bet365 bet365) {
        betingMachine = bet365;
        sikuli = bet365.sikuli;
        accountConfig = bet365.getAccountConfig();
        accountInfo = accountConfig.getAccountInfo();
    }

    public void collectInfo() {

        try {

            sikuli.click(ImagePattern.PATTERN_HISTORY_LINK.pattern);
            sikuli.hover(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
            sikuli.hover(ImagePattern.PATTERN_HISTORY_TITLE.pattern);

            getBalanceInfo();

            setDatesInSearchForm();
            collectFinishedMatchesInfo();
            collectPendingMatchesInfo();

        } catch (FindFailed e) {
            logger.error("can't open history page");
        } finally {
            logger.hideMessageBox();
            try {
                sikuli.mouseMove(sikuli.getCenter());
                sikuli.click();
            } catch (FindFailed f) {
                logger.error("can't move mouse to center of the screen...probably can't close the history page");
            }
            sikuli.type(Key.F4, KeyModifier.CTRL);
        }
    }

    private void getBalanceInfo() {

        sikuli.type(Key.TAB, KeyModifier.SHIFT);

        sikuli.type(Key.DOWN, KeyModifier.SHIFT);
        sikuli.type("c", KeyModifier.CTRL);
        String balanceInfo = sikuli.getClipboard();
        logger.info("found balanceInfo = " + balanceInfo);

        String price = searchRegEx(balanceInfo, balanceRegEx);
        accountInfo.setBalance(Double.valueOf(price.replaceAll(",", ".")));
    }

    private void setDatesInSearchForm() throws FindFailed {

        sikuli.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.TAB);
        sikuli.type(Key.RIGHT);
        sikuli.type(Key.RIGHT);

        sikuli.type(Key.TAB);
        sikuli.type(accountInfo.getFromDate(new SimpleDateFormat(dateFormat)));

        sikuli.type(Key.TAB);
        sikuli.type(accountInfo.getToDate(new SimpleDateFormat(dateFormat)));

    }

    private void collectFinishedMatchesInfo() throws FindFailed {

        sikuli.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.DOWN);
        sikuli.type(Key.TAB);

        sikuli.type(Key.ENTER);
        sikuli.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        getMatchInfo();
    }

    private void collectPendingMatchesInfo() throws FindFailed {

        sikuli.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.UP);
        sikuli.type(Key.TAB);

        sikuli.type(Key.ENTER);
        sikuli.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        sikuli.type(Key.TAB);
        sikuli.type(Key.TAB);

        getMatchInfo();
    }

    private boolean getMatchInfo() {
        while (true) {
            sikuli.type(Key.TAB);
            sikuli.type(Key.DOWN, KeyModifier.SHIFT);
            sikuli.type("c", KeyModifier.CTRL);

            String matchInfo = sikuli.getClipboard();
            if (!matchInfo.contains(historyMatchInfoLinkRegEx)) {
                logger.info("end of matchInfo, last is : " + matchInfo, sikuli.getCenter());
                accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
                accountConfig.saveConfig();
                return true;
            }

            sikuli.type(Key.ENTER);
            sikuli.waitMilisec(500);

            sikuli.type(Key.DOWN, KeyModifier.SHIFT);
            sikuli.type(Key.UP, KeyModifier.SHIFT);
            sikuli.type("c", KeyModifier.CTRL);

            matchInfo = sikuli.getClipboard();
            logger.trace("found matchInfo string = " + matchInfo);

            sikuli.type(Key.ENTER);

            try {
                MatchInfo info = parseMatchInfo(matchInfo);
                logger.info("found MatchInfo = " + info);
                if (MatchState.pending.equals(info.getState())
                        && !accountInfo.getMatchInfoPending().contains(info)) {
                    accountInfo.getMatchInfoPending().add(info);
                    accountInfo.getMatchInfoFinished().remove(info);
                } else if (!MatchState.pending.equals(info.getState())
                        && !accountInfo.getMatchInfoFinished().contains(info)) {
                    accountInfo.getMatchInfoFinished().add(info);
                    accountInfo.getMatchInfoPending().remove(info);
                } else {
                    accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
                    accountConfig.saveConfig();
                    return true;
                }
            } catch (Exception e) {
                logger.info("can't parse info: " + e.getLocalizedMessage(), logger);
                return false;
            }
        }
    }

    private MatchInfo parseMatchInfo(String matchInfoString) throws ParseException {
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

        String dateString = searchRegEx(matchInfoString, matchInfoDateRegEx);
        logger.debug("found date {}", dateString);
        matchInfo.setDate(dateString);

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
