package Menuefuehrung;

import Spielbereitstellug.Lokalsteuerung;
import Spielbereitstellug.Netzwerksteuerung;
import Spielbereitstellug.Spiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MainFrame extends JFrame {

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    boolean fullscreen = false;

    public static void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }

    public static void main(String[] args) throws Exception {

        SwingUtilities.invokeLater(MainFrame::new);

    }

    public MainFrame() {
        getContentPane().setLayout(new CardLayout());
        setTitle("Digger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyBinding(this.getRootPane(), "F11", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FullScreen();
            }
        });
        MainPanel Panel = new MainPanel(this);
        getContentPane().add(Panel, "panel");
        



        setIconImage(new ImageIcon("bin/images/Logo.png").getImage());
        setUndecorated(true);
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(this.getContentPane(), "panel");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);
    }


    private void FullScreen(){
        if (fullscreen) {
            setVisible(false);
            fullscreen = false;
            device.setFullScreenWindow(null);
            setVisible(true);
        }else{
            setVisible(false);
            device.setFullScreenWindow(this);
            fullscreen = true;
            setVisible(true);
        }
    }


    public void prepareMap(boolean isHost, boolean isMultiplayer, Netzwerksteuerung netCont){//copied from Test.java, should be adjusted later

        int height = getContentPane().getPreferredSize().height;
        int width = getContentPane().getPreferredSize().width;

        Spiel spiel = new Spiel(isHost, isMultiplayer, netCont);

        // Naiv-Testing Area:
        Lokalsteuerung lok = new Lokalsteuerung(spiel);

        addKeyBinding(spiel, "DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.down();
            }
        });
        addKeyBinding(spiel, "UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.up();
            }
        });
        addKeyBinding(spiel, "LEFT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.left();
            }
        });
        addKeyBinding(spiel, "RIGHT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.right();
            }
        });
        addKeyBinding(spiel, "SPACE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.shoot();
            }
        });

        if(isMultiplayer)
            getContentPane().add(spiel, "multiplayer");
        else
            getContentPane().add(spiel, "singleplayer");

        spiel.spawnSpieler();

        spiel.start();

    }

}
