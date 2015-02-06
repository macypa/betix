package betix.bet365;


import betix.core.Configuration;
import betix.scala.Login;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

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
public class Bet365 {

    private static Logger logger = LoggerFactory.getLogger(Bet365.class);
    private static final Configuration config = new Configuration();

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
        new Login().checkLogin(config, s);
    }

    private static void wait(int sec) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
