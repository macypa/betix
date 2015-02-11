package betix.core;

import com.sun.awt.AWTUtilities;
import org.sikuli.script.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MessageBoxFrame extends JFrame implements MouseListener {

    private final JPanel panel = new JPanel();

    public MessageBoxFrame() {
        if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
            AWTUtilities.setWindowOpacity(this, 0.3f);
        }

        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel.add(new JLabel("Starting..."));
        panel.addMouseListener(this);
        panel.setBackground(Color.ORANGE);
        add(panel);

        pack();
        setVisible(true);
    }

    public void showMessage(String string, Location location) {
        setLocation(location.getX(), location.getY());
        panel.removeAll();
        panel.add(new JLabel("<html>" + string + "</html>"));
        pack();
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        setVisible(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setVisible(false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setVisible(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setVisible(false);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setVisible(false);
    }

}
