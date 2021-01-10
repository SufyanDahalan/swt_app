package Menuefuehrung;

import Spielbereitstellug.Lokalsteuerung;
import Spielbereitstellug.Spiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static Menuefuehrung.Song.play;


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

        play ("bin/music/Popcorn01.mp3") ;
    }
    public MainFrame() {
        getContentPane().setLayout(new CardLayout());
        setTitle("Digger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyBinding(this.getRootPane(), "F11", new AbstractAction() {
                    //TODO: changing size in game does not extend the game correctly, should be corrected in Map.java
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FullScreen();
                    }
                }
        );
        MainPanel Panel = new MainPanel();
        getContentPane().add(Panel, "panel");


        LevelEditor editor = new LevelEditor();
        getContentPane().add(editor, "editor");//add the LevelEditor to the cardboard layout

        prepareMap();

        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("bin/Images/Logo.png")));
        setUndecorated(true);
        CardLayout layout = (CardLayout)getContentPane().getLayout();
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
    public void prepareMap(){//copied from Test.java, should be adjusted later

        int height = getContentPane().getPreferredSize().height;
        int width = getContentPane().getPreferredSize().width;

        int[] size = {width,height};

        Spiel spiel = new Spiel(size);

        spiel.spawnSpieler(true);

        Runnable runnable = (Runnable)spiel; // or an anonymous class, or lambda...

        Thread thread = new Thread(runnable);
        thread.start();

        // Naiv-Testing Area:
        Lokalsteuerung lok = new Lokalsteuerung(spiel);
        //implemented swing keyBinding instead of awt keyListener because keyListener is a heavyweight awt component and does not work
        //with a swing cardLayout.
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
        getContentPane().add(spiel, "game");
    }


}
