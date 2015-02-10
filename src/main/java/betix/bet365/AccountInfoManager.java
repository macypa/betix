package betix.bet365;

import betix.core.BettingMachine;
import betix.core.ConfigKey;
import betix.core.Configuration;
import betix.core.MessageBoxFrame;
import betix.core.data.*;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.regex.Matcher;

public class AccountInfoManager {

    protected static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    private final AccountInfo accountInfo;
    private final Configuration accountConfig;

    public BettingMachine betingMachine;
    public Screen screen;
    public MessageBoxFrame messageBox;

    AccountInfoManager(Bet365 bet365) {
        betingMachine = bet365;
        screen = bet365.screen;
        accountConfig = bet365.getAccountConfig();
        Object accInfo = accountConfig.getConfig(ConfigKey.accountInfo);
        if (accInfo != null && accInfo instanceof AccountInfo) {
            accountInfo = (AccountInfo) accInfo;
        } else {
            accountInfo = new AccountInfo();
        }
        messageBox = bet365.messageBox;
    }

    public void collectInfo() {

        try {

            screen.click(ImagePattern.PATTERN_HISTORY_LINK.pattern);
            betingMachine.wait(2);

            screen.hover(ImagePattern.PATTERN_HISTORY_TITLE.pattern);

            getBalanceInfo();

            collectPendingMatchesInfo();
            collectFinishedMatchesInfo();

        } catch (FindFailed e) {
            e.printStackTrace();
        } finally {
            try {
                screen.mouseMove(screen.getCenter());
            } catch (FindFailed f) {
                logger.error("can't move mouse to center of the screen...probably can't close the histor page");
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

        String price = searchRegEx(balanceInfo, "(.*?)\\s*BGN");
        accountInfo.setBalance(Double.valueOf(price.replaceAll(",", ".")));
    }

    private void collectPendingMatchesInfo() throws FindFailed {

        screen.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.UP);
        screen.type(Key.TAB);
        screen.type(Key.RIGHT);
        betingMachine.wait(1);
        screen.type(Key.ENTER);
        betingMachine.wait(2);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        getMatchInfo();
        messageBox.setVisible(false);
    }

    private void collectFinishedMatchesInfo() throws FindFailed {

        screen.doubleClick(ImagePattern.PATTERN_HISTORY_TITLE.pattern);
        screen.type(Key.TAB);
        screen.type(Key.TAB);

        screen.type(Key.DOWN);
        screen.type(Key.TAB);
        screen.type(Key.RIGHT);
        betingMachine.wait(1);
        screen.type(Key.ENTER);
        betingMachine.wait(2);
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
            if (!matchInfo.contains("Равен @ ")) {
                logger.info("end of matchInfo, last is : " + matchInfo, screen.getCenter());
                return;
            }

            screen.type(Key.ENTER);
            betingMachine.waitMilisec(500);

            screen.type(Key.DOWN, KeyModifier.SHIFT);
            screen.type(Key.UP, KeyModifier.SHIFT);
            screen.type("c", KeyModifier.CTRL);
            matchInfo = Env.getClipboard();
            logger.info("found matchInfo = " + matchInfo);

            addToAccountInfo(matchInfo);

            screen.type(Key.ENTER);
        }
    }

    private void addToAccountInfo(String matchInfoString) {
        MatchInfo matchInfo = new MatchInfo();

        if (matchInfoString.contains("Текущ")) {
            matchInfo.setState(MatchState.pending);
        } else if (matchInfoString.contains("Загубен")) {
            matchInfo.setState(MatchState.losing);
        } else if (matchInfoString.contains("Печеливш")) {
            matchInfo.setState(MatchState.winning);
        }

        matchInfo.setStake(Double.valueOf(searchRegEx(matchInfoString, "Залог:\\s*(.*?)\\s").replaceAll(",", ".")));
        matchInfo.setCoefficient(Double.valueOf(searchRegEx(matchInfoString, "Никой\\s*(.*?)\\s").replaceAll(",", ".")));
        matchInfo.setWining(Double.valueOf(searchRegEx(matchInfoString, "Залог:\\s*.*?\\sПеч.*?:\\s*(.*?)\\s").replaceAll(",", ".")));

        matchInfo.setEvent(new EventPair(searchRegEx(matchInfoString, "Равен\\s*(.*?)\\s*\\(Краен Резултат")));
        matchInfo.setDate(new Date(searchRegEx(matchInfoString, "Краен Резултат\\)\\s*(.*?)\\s*Никой")));

        accountInfo.getMatchInfo().add(matchInfo);
        accountConfig.addConfig(ConfigKey.accountInfo, accountInfo);
        accountConfig.saveConfig();

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
