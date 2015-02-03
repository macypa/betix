package com.bet365;


import org.sikuli.basics.*;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bet365 {

    private static Logger logger = LoggerFactory.getLogger(Bet365.class);

    public static void main(String[] args) {
        App.focus("firefox");
        Screen s = new Screen();

        try{
            s.find(new Pattern("img/logout.png"));
            SikuliScript.popup("You're logged in.");
            logger.info("You're logged in.");
        } catch(FindFailed e){
            SikuliScript.popup("You're NOT logged in.");
            logger.error("Not logged in!");
            System.exit(1);
        }
//
//
//
//            s.click(new Pattern("img/eg.png"), 0);
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
//        }    catch(FindFailed e){
//            e.printStackTrace();
//        }
    }
}
