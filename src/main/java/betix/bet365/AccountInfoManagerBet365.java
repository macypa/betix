package betix.bet365;

import betix.core.BettingMachine;
import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.config.Stake;
import betix.core.data.*;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.schedule.RetryTask;
import betix.core.sikuli.SikuliRobot;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

class AccountInfoManagerBet365 extends RetryTask implements betix.core.AccountInfoManager {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);

    private final Configuration accountConfig = new Configuration(Configuration.CONFIG_ACCOUNT_SPECIFIC_FILE);
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
    private static final String matchInfoDateTimeRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoDateTimeRegEx);
    private static final String matchInfoIsNotLastRegEx = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.matchInfoIsNotLastRegEx);

    private final AccountInfo accountInfo;

    private final BettingMachine betingMachine;
    private final SikuliRobot sikuli;

    private boolean dataIsCollected;

    AccountInfoManagerBet365(BettingMachine bettingMachine) {
        betingMachine = bettingMachine;
        sikuli = bettingMachine.sikuli;
        accountInfo = accountConfig.getAccountInfo();

        refreshTeams();
    }

    @Override
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    private void refreshTeams() {
        File teamDir = new File(ImagePattern.TEAM_DIR_NAME);
        for (File file : teamDir.listFiles()) {
            if (file.isDirectory())
                continue;

            Team team = accountInfo.getTeam(file.getName());
            accountInfo.getTeams().add(team);
        }
    }

    @Override
    public void collectInfo() {
        dataIsCollected = false;

        if (!betingMachine.login()) {
            throw new RuntimeException("not logged in");
        }

        try {
            sikuli.openHistoryPage();
            sikuli.wait(ImagePattern.PATTERN_HISTORY_TITLE.pattern);

            collectFinishedMatchesInfo();

            refreshHistoryPage();

            collectPendingMatchesInfo();

            getBalanceInfo();

            dataIsCollected = true;
        } catch (Exception e) {
            logger.error("can't parse info ", e);
            throw new RuntimeException(e);
        } finally {
            logger.hideMessageBox();
            sikuli.type(Key.F4, KeyModifier.CTRL);
        }
    }

    private void refreshHistoryPage() throws FindFailed {
        logger.info("refresh history page");
        sikuli.doubleClick(ImagePattern.PATTERN_LOGO.pattern);
        for (int i = 0; i < 7; i++) {
            sikuli.type(Key.TAB);
        }
        sikuli.type(Key.ENTER);
    }

    private void setDatesInSearchForm(boolean selectFinishedMatches) throws FindFailed {
        logger.info("setting dates in search form");

        sikuli.doubleClick(ImagePattern.PATTERN_LOGO.pattern);
        sikuli.type(Key.TAB, KeyModifier.SHIFT);
        sikuli.type(Key.TAB, KeyModifier.SHIFT);

        sikuli.type(Key.TAB, KeyModifier.SHIFT);
        if (selectFinishedMatches) {
            sikuli.type(Key.UP);
            sikuli.type(Key.DOWN);
        } else {
            sikuli.type(Key.UP);
        }

        sikuli.type(Key.TAB);
        sikuli.type(Key.RIGHT);
        sikuli.type(Key.RIGHT);

        sikuli.type(Key.TAB);
        sikuli.type(getFromDate(new SimpleDateFormat(dateFormat), selectFinishedMatches));

        sikuli.type(Key.TAB);
        sikuli.type(getToDate(new SimpleDateFormat(dateFormat)));

    }

    private void getBalanceInfo() throws FindFailed {
        logger.info("getting balance info");
        sikuli.doubleClick(ImagePattern.PATTERN_LOGO.pattern);
        String balanceInfo = copyAllText();
        String price = searchRegEx(balanceInfo, balanceRegEx);
        accountInfo.setBalance(Double.valueOf(price.replaceAll(",", ".")));

        saveAccountInfo();
    }

    private void collectFinishedMatchesInfo() throws FindFailed, ParseException {
        logger.info("collect finished matches info");

        setDatesInSearchForm(true);
        getMatchInfo();
    }

    private void collectPendingMatchesInfo() throws FindFailed, ParseException {
        logger.info("collect pending matches info");

        setDatesInSearchForm(false);
        getMatchInfo();
    }

    private void getMatchInfo() throws ParseException, FindFailed {
        logger.info("getting match infos");
        sikuli.type(Key.ENTER);

        sikuli.doubleClick(ImagePattern.PATTERN_LOGO.pattern);
        String allText = copyAllText();
        if (!allText.contains(historyMatchInfoLinkRegEx)) {
            logger.info("no matches info found");
            return;
        }

        logger.info("navigate to first match info");
        sikuli.doubleClick(ImagePattern.PATTERN_LOGO.pattern);
        for (int i = 0; i < 19; i++) {
            sikuli.type(Key.TAB);
        }

        while (true) {

            sikuli.type(Key.ENTER);
            sikuli.waitMilisec(500);

            String matchInfo = copyAllText();

            sikuli.type(Key.ENTER);
            sikuli.type(Key.TAB);

            if (!findRegEx(matchInfo, matchInfoEventRegEx)) {
                logger.info("matchInfo is not for football draw bet");
                continue;
            }

            MatchInfo info = parseMatchInfo(matchInfo);
            if (matchInfo.contains("Тотнъм v Уест Хям")) {
                System.out.println("matchInfo = " + matchInfo);
            }
            logger.info("found MatchInfo = {} ", info);
            logger.info("matchInfo contains in finished matches {} ", accountInfo.getMatchInfoFinished().contains(info));
            logger.info("matchInfo contains in pending matches {} ", accountInfo.getMatchInfoPending().contains(info));
            if (MatchState.pending.equals(info.getState())
                    && !accountInfo.getMatchInfoPending().contains(info)) {

                logger.info("adding info to pending and removing it from finished {} ", info);
                accountInfo.addPending(info);
            } else if (!MatchState.pending.equals(info.getState())
                    && !accountInfo.getMatchInfoFinished().contains(info)) {

                logger.info("adding info to finished and removing it from pending {} ", info);
                accountInfo.addFinished(info);
            } else {
                logger.info("next info should be already saved {}", info);
                saveAccountInfo();
                return;
            }

            if (!findRegEx(matchInfo, matchInfoIsNotLastRegEx)) {
                logger.info("end of matchInfo");
                saveAccountInfo();
                return;
            }
        }
    }

    @Override
    public void saveAccountInfo() {
        accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
        accountConfig.saveConfig();
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

        String stakeString = searchRegEx(matchInfoString, matchInfoStakeRegEx).replaceAll(",", ".");
        logger.debug("found stakeString {}", stakeString);
        matchInfo.setStake(Stake.get(stakeString).value);
        matchInfo.setCoefficient(Double.valueOf(searchRegEx(matchInfoString, matchInfoCoefficientRegEx).replaceAll(",", ".")));
        matchInfo.setWining(Double.valueOf(searchRegEx(matchInfoString, matchInfoWiningRegEx).replaceAll(",", ".")));

        matchInfo.setEvent(new Event(searchRegEx(matchInfoString, matchInfoEventRegEx)));

        String dateString = searchRegEx(matchInfoString, matchInfoDateRegEx);
        logger.debug("found date {}", dateString);
        matchInfo.setDate(dateString);

        String dateTimeString = searchRegEx(matchInfoString, matchInfoDateTimeRegEx);
        logger.debug("found date of bet {}", dateTimeString);
        matchInfo.setDateOfBet(dateTimeString);

        return matchInfo;
    }

    private String getFromDate(SimpleDateFormat format, boolean selectFinishedMatches) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date fromDate = calendar.getTime();

        if (selectFinishedMatches) {
            for (MatchInfo info : accountInfo.getMatchInfoFinished()) {
                try {
                    Date date = format.parse(info.getDateOfBet());
                    if (fromDate.after(date)) {
                        fromDate = date;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (MatchInfo info : accountInfo.getMatchInfoPending()) {
                try {
                    Date date = format.parse(info.getDateOfBet());
                    if (fromDate.after(date)) {
                        fromDate = date;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return format.format(fromDate);
    }

    private String getToDate(SimpleDateFormat format) {
        Calendar calendar = Calendar.getInstance();
        return format.format(calendar.getTime());
    }

    private String copyAllText() {
        sikuli.type("a", KeyModifier.CTRL);
        sikuli.type("c", KeyModifier.CTRL);
        String text = sikuli.getClipboard();
        return text;
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

    private boolean findRegEx(String info, String regex) {
        java.util.regex.Pattern MY_PATTERN = java.util.regex.Pattern.compile(regex);
        Matcher m = MY_PATTERN.matcher(info);
        return m.find();
    }

    @Override
    public void executeTask() {
        collectInfo();
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return dataIsCollected;
    }
}
