package com.bet365;


import betix.core.Configuration;
import org.sikuli.basics.SikuliScript;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Bet365 {

    private static Logger logger = LoggerFactory.getLogger(Bet365.class);

    public static void main(String[] args) {

        Screen s = new Screen();
        checkLogin(s);

        try {
            s.hover(new Pattern("img/logo.png"));
            s.wheel(1, 1);
            wait(1);
            s.click(new Pattern("img/football.png"), 0);
//            s.click(new Pattern("img/addNE.png"), 0);
//            s.type(new Pattern("img/addNeIP.png"), args[0] + "\n", 0);
//            s.click(new Pattern("img/cliUsername.png"), 0);
//            s.type("a", KeyModifier.CTRL);
//            s.type(args[1] +"\n");
//            s.click(new Pattern("img/cliPassword.png"), 0);
//            s.type("a", KeyModifier.CTRL);
//            s.type(args[2] + "\n");
//            s.click(new Pattern("img/readCommunity.png"), 0);
//            s.type("a", KeyModifier.CTRL);
//            s.type(args[3] + "\n");
//            s.click(new Pattern("img/writeCommunity.png"), 0);
//            s.type("a", KeyModifier.CTRL);
//            s.type(args[4] + "\n");
//            s.click(new Pattern("img/createNE.png"), 0);
//            s.click(new Pattern("img/closeNePanel.png"), 0);
//            s.hover(new Pattern("img/hoverInventory.png"));
//            s.click(new Pattern("img/neInventory.png"));
//            s.click(new Pattern("img/neFilter.png"));
//            s.type(args[0] + "\n");
//            if(s.hover(new Pattern("img/checkForAvailability.png"))==1){
//                System.out.println("Wooooow");
//
//            }
//              /*  s.type(new Pattern("img/cliPassword.png"), args[0]+"\n", 0);
//                s.click(new Pattern("img/addNE.png"), 0);
//                s.type(new Pattern("img/addNeIP.png"), args[0]+"\n", 0);*/
        } catch (FindFailed e) {
            e.printStackTrace();
        }
    }

    private static void checkLogin(Screen s) {
        App.focus(Configuration.getConfigAsString("browser"));

        try {
            s.find(new Pattern("img/logout.png"));
            logger.info("You're logged in.");
        } catch (FindFailed e) {
            SikuliScript.popup("You're NOT logged in.");
            logger.error("Not logged in!");
            System.exit(1);
        }
    }

    private static void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
