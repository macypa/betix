package betix.core;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import com.sun.awt.AWTUtilities;
import org.sikuli.script.Location;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MessageBoxFrame extends JFrame implements MouseListener {

    private final JPanel panel = new JPanel();

    public MessageBoxFrame() {
        if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
            Double messageBoxOpacity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.messageBoxOpacity);
            AWTUtilities.setWindowOpacity(this, messageBoxOpacity.floatValue());
        }

        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel.add(new JLabel("Starting..."));
        panel.addMouseListener(this);
        panel.setBackground(Color.orange);
        add(panel);

        pack();
        setVisible(true);
    }

    public void showMessage(String string) {
        showMessage(string, null);
    }

    public void showMessage(String string, Logger logger) {
        showMessage(string, logger, new Location(0, 0));
    }

    private void showMessage(String string, Logger logger, Location location) {
        if (logger != null) {
            logger.info(string);
        }
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
