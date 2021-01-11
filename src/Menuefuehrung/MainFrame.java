package Menuefuehrung;

import Spielbereitstellug.Lokalsteuerung;
import Spielbereitstellug.Spiel;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;

public class MainFrame extends JFrame implements ActionListener {

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

        MainPanel Panel = new MainPanel();
        getContentPane().add(Panel, "panel");


        LevelEditor editor = new LevelEditor();
        getContentPane().add(editor, "editor");// adds the LevelEditor to the cardboard layout

        prepareMap();

        setUndecorated(true);
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(this.getContentPane(), "panel");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }


    public Clip Music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream as1 = AudioSystem.getAudioInputStream(new BufferedInputStream(new java.io.FileInputStream("bin/music/Popcorn01.wav")));
               AudioFormat af = as1.getFormat();
               Clip clip1 = AudioSystem.getClip();
               DataLine.Info info = new DataLine.Info(Clip.class, af);
               Line line1 = AudioSystem.getLine(info);
               if (!line1.isOpen()){
                clip1.open(as1);
                clip1.loop(Clip.LOOP_CONTINUOUSLY);
                clip1.start();
               }
        return clip1;
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

    private void Mute() {}


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

        getContentPane().add(spiel, "multiplayer");
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
