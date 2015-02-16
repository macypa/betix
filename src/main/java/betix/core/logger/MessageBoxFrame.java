package betix.core.logger;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import org.sikuli.script.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

class MessageBoxFrame extends JFrame implements MouseListener {

    public static final MessageBoxFrame msgBox = new MessageBoxFrame();

    private final JPanel panel = new JPanel();

    public static MessageBoxFrame getMessageBox() {
        return msgBox;
    }

    private MessageBoxFrame() {
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

        setFocusable(false);
        setFocusableWindowState(false);
        toBack();

        setAlwaysOnTop(true);
        pack();
        setVisible(true);
    }

    public void showErrorMessage(String string) {
        showMessage(string, new Location(0, 0), Color.magenta);
    }

    public void showMessage(String string) {
        showMessage(string, new Location(0, 0), Color.orange);
    }

    public void showMessage(String string, Location location, Color color) {
        setLocation(location.getX(), location.getY());
        panel.setBackground(color);
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
