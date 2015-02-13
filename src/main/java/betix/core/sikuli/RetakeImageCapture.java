package betix.core.sikuli;

import org.sikuli.basics.SikuliScript;
import org.sikuli.script.Screen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class RetakeImageCapture {

    public static void main(String[] args) {
        String imgName;
        if (args.length > 0) {
            imgName = args[1];
        } else {
            imgName = SikuliScript.input("Enter relative Image filename");
        }

        File imageFile = new File(imgName);
        try {
            ImageIO.write(new Screen().userCapture().getImage(), "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(1);
    }

}
