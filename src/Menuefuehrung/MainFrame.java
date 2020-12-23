package Menuefuehrung;

import Spielbereitstellug.*;
import Spielverlauf.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MainFrame extends JFrame {
    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    boolean fullscreen = false;
    public static void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
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
        prepareMap();
        getContentPane().add(Panel, "panel");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bin/Images/Logo.png")));
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
        File skin_graphic_file = new File("bin/skins");
        Skin sk = new Skin(skin_graphic_file, "original_skin");
        String[] levels;
        File f = new File("bin/level/");
        levels = f.list();
        JSONObject obj = null;
        try {
            obj =  new JSONObject(new String(Files.readAllBytes(Paths.get("bin/level/"+levels[1]))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] size = {1000,1000};
        Map map = new Map(obj, size ,sk);
        // Beispielcontent
        map.spawnMonster();
        JSONArray geld_pos = new JSONArray("[3,10]");
        map.addGeld(new Geld(geld_pos));
        map.spawnSpieler(new Spieler(map.getSpawnSP1()));
        int[] fb_pos = map.getSP1().getPosition();
        DIRECTION dir = DIRECTION.RIGHT;
        map.addFeuerball(new Feuerball(fb_pos, dir));
        // Naiv-Testing Area:
        Lokalsteuerung lok = new Lokalsteuerung(map.getSP1().getPosition()[0], map.getSP1().getPosition()[1], map);
        //implemented swing keyBinding instead of awt keyListener because keyListener is a heavyweight awt component and does not work
        //with a swing cardLayout.
        addKeyBinding(map, "DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.down();
            }
        });
        addKeyBinding(this.getRootPane(), "UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.up();
            }
        });
        addKeyBinding(this.getRootPane(), "LEFT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.left();
            }
        });
        addKeyBinding(this.getRootPane(), "RIGHT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lok.right();
            }
        });
        getContentPane().add(map, "game");
    }
}
