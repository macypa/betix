package betix.core;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.logger.Logger;
import org.sikuli.script.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

public class MessageBoxFrame extends JFrame implements MouseListener {

    private final JPanel panel = new JPanel();

    public MessageBoxFrame() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        if (ge.getDefaultScreenDevice().isWindowTranslucencySupported(TRANSLUCENT)) {
            Double messageBoxOpacity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.messageBoxOpacity);
            setOpacity(messageBoxOpacity.floatValue());
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
        showMessage(string, null, new Location(0, 0));
    }

    public void showMessage(String string, Logger logger) {
        showMessage(string, logger, new Location(0, 0));
    }

    public void showMessage(String string, Location location) {
        showMessage(string, null, location);
    }

    public void showMessage(String string, Logger logger, Location location) {
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
