package betix.core.logger;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import org.sikuli.script.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MessageBoxFrame extends JFrame implements MouseListener {

    public static final MessageBoxFrame msgBox = new MessageBoxFrame();

    private final JPanel panel = new JPanel();
    private final Location location = new Location(Configuration.getDefaultConfig().getConfigAsInteger(ConfigKey.messageBoxX),
            Configuration.getDefaultConfig().getConfigAsInteger(ConfigKey.messageBoxY));

    public static MessageBoxFrame getMessageBox() {
        return msgBox;
    }

    private MessageBoxFrame() {
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
        showMessageBox();
    }

    public void showErrorMessage(String string) {
        showMessage(string, location, Color.magenta);
    }

    public void showMessage(String string) {
        showMessage(string, location, Color.orange);
    }

    public void showMessage(String string, Location location, Color color) {
        panel.removeAll();
        panel.add(new JLabel("<html>" + string + "</html>"));
        panel.setBackground(color);
        setLocation(location.getX(), location.getY());
        pack();
        showMessageBox();
    }

    private void showMessageBox() {
        if (Configuration.getDefaultConfig().getConfigAsBoolean(ConfigKey.showMessageBox)) {
            setVisible(true);
        }
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
