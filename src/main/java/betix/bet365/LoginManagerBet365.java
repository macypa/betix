package betix.bet365;

import betix.core.AccountInfoManager;
import betix.core.BettingMachine;
import betix.core.config.ImagePattern;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import betix.core.schedule.RetryTask;
import betix.core.sikuli.SikuliRobot;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

import javax.swing.*;

class LoginManagerBet365 extends RetryTask implements betix.core.LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(BettingMachine.class);
    private final AccountInfoManager accountInfoManager;

    private final BettingMachine betingMachine;
    private final SikuliRobot sikuli;

    private boolean loggedIn = false;

    LoginManagerBet365(BettingMachine bettingMachine) {
        betingMachine = bettingMachine;
        sikuli = bettingMachine.sikuli;
        this.accountInfoManager = bettingMachine.getAccountInfoManager();
    }

    @Override
    public boolean login() throws Exception {

        loggedIn = false;
        if (doLogin()) {
            loggedIn = true;
            return true;
        }

        logger.info("closing tab with failed login attempt ...");
        sikuli.type(Key.F4, KeyModifier.CTRL);
        throw new RuntimeException("can't login");
    }

    private boolean doLogin() {
        if (checkLogin()) {
            return true;
        }

        try {
            logger.info("Trying to log in ...");
            sikuli.click(ImagePattern.PATTERN_LOGIN_FIELD.pattern);

            String username = typeInUsername();
            String plainText = typeInPassword();

            sikuli.type(Key.ENTER);

            if (checkLogin()) {
                if (username != null && !username.isEmpty()) {
                    accountInfoManager.getAccountInfo().setUsername(username);
                    accountInfoManager.saveAccountInfo();
                }
                if (plainText != null && !plainText.isEmpty()) {
                    accountInfoManager.getAccountInfo().setPassword(encodePass(plainText));
                    accountInfoManager.saveAccountInfo();
                }
                return true;
            } else {
                return false;
            }
        } catch (FindFailed e) {
            logger.error("Not logged in!", e);
            return false;
        }
    }

    private String typeInUsername() {
        String updateUser = "";
        String username = accountInfoManager.getAccountInfo().getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = input(false);
            updateUser = username;
        }

        sikuli.click();
        sikuli.click();
        sikuli.type(username);
        sikuli.type("\t");

        return updateUser;
    }

    private String typeInPassword() throws FindFailed {
        String updatePass = "";
        String plainText = decodePass(accountInfoManager.getAccountInfo().getPassword());
        if (plainText == null || plainText.trim().isEmpty()) {
//            plainText = sikuli.input("enter pass to encript and store");
            plainText = input(true);

            updatePass = plainText;

            sikuli.click(ImagePattern.PATTERN_PASSWORD_FIELD.pattern);
            sikuli.click();
        }

        sikuli.enableSikuliLog(false);
        sikuli.type(plainText);
        sikuli.enableSikuliLog(true);

        return updatePass;
    }

    public static String input(boolean isPassword) {
        JPanel panel = new JPanel();
        final JTextField field;
        if (isPassword) {
            panel.add(new JLabel("Password: "));
            field = new JPasswordField(15);
        } else {
            panel.add(new JLabel("Username: "));
            field = new JTextField(15);
        }

        panel.add(field);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new String[]{"OK"}) {
            @Override
            public void selectInitialValue() {
                field.requestFocusInWindow();
            }
        };
        pane.createDialog(null, "Enter password: ").setVisible(true);
        return field.getText();
    }

    private String encodePass(String clearText) {
        if (clearText == null) return null;
        return new String(Base64.encodeBase64(clearText.getBytes()));
    }

    private String decodePass(String encodedPass) {
        if (encodedPass == null) return null;
        return new String(Base64.decodeBase64(encodedPass.getBytes()));

    }

    private boolean checkLogin() {
        try {
            logger.info("checking login");
            betingMachine.openSite();

            checkNewMessages();

            sikuli.wait(ImagePattern.PATTERN_HISTORY_LINK.pattern, 5);
            logger.info("You're logged in.");
            return true;
        } catch (FindFailed e) {
            logger.error("Not logged in!");
        }
        return false;
    }

    @Override
    public void logout() {
        logger.info("Trying to log in ...");

        try {
            sikuli.click(ImagePattern.PATTERN_LOGOUT_LINK.pattern);
        } catch (FindFailed findFailed) {
            logger.error("can't logout", findFailed);
        }
    }

    private void checkNewMessages() {
        try {
            sikuli.find(ImagePattern.PATTERN_NEW_MESSAGE_CLOSE_BUTTON.pattern, false);
            sikuli.click(ImagePattern.PATTERN_NEW_MESSAGE_CLOSE_BUTTON.pattern);
        } catch (FindFailed findFailed) {
            logger.info("new message... closing message window.");
        }
    }

    @Override
    public void executeTask() throws Exception {
        login();
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return loggedIn;
    }
}
