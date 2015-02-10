package betix.bet365;

import betix.core.BettingMachine;
import betix.core.MessageBoxFrame;
import betix.core.data.ImagePattern;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

public class AccountInfoManager {

    protected static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);

    public BettingMachine betingMachine;
    public Screen screen;
    public MessageBoxFrame messageBox;

    AccountInfoManager(Bet365 bet365) {
        betingMachine = bet365;
        screen = bet365.screen;
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

        java.util.regex.Pattern MY_PATTERN = java.util.regex.Pattern.compile("(.*?)\\s*BGN");
        Matcher m = MY_PATTERN.matcher(balanceInfo);
        if (m.find()) {
            String s = m.group(1);
            Double balance = Double.valueOf(s.replaceAll(",", "."));
            logger.info("found balance = " + balance);
            betingMachine.getAccountInfo().setBalance(balance);
        } else {
            messageBox.showMessage("can't get balance .... no biggy.", screen.getCenter());
            logger.error("can't get balance .... no biggy.");
        }
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

            screen.type(Key.ENTER);
        }
    }

}
