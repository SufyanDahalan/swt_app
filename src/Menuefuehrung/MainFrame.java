package Menuefuehrung;

import Spielbereitstellug.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;


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

        final Spiel spiel = new Spiel(isHost, isMultiplayer, netCont);

        EndListener el = spielstand -> {

            JTextField name = new JTextField(8);
            JTextField age = new JTextField(2);

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("Your name :"));
            myPanel.add(name);
            myPanel.add(new JLabel("Your Age :"));
            myPanel.add(age);
            myPanel.add(Box.createHorizontalStrut(10));

            int result = JOptionPane.showConfirmDialog(null, myPanel, "please enter ..", JOptionPane.OK_CANCEL_OPTION);

            // Click on OK
            if(result == JOptionPane.OK_OPTION) {
                // der Name und der Alter werden dann im Scoreboard eingespeichert.
                // Fraglich, ob bei Abbruch des Spieles der Eintrag tz. eingespeichert bleibt.

                GregorianCalendar now = new GregorianCalendar();
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);      // 14. April 2012
                //DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM)   // 14.04.2012

                final String _date = df.format(now.getTime());
                final int _endspielstand = spielstand;
                final String _name = name.getText();
                final String _age = age.getText();

                // Auf jeden Fall Exceptions einbauen: Alter keine Zahl ect...

                JSONObject score = new JSONObject();

                score.put("name", _name);
                score.put("age", _age);
                score.put("date", _date);
                score.put("score", _endspielstand);


                // Save score
                String rootfolder_name = "bin/";

                JSONObject obj = null;
                try {
                    obj = new JSONObject(new String(Files.readAllBytes(Paths.get(rootfolder_name + "scores.json"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                JSONArray scores;

                if(obj.has("data")) {
                    scores = obj.getJSONArray("data");

                    if (scores.length() > 10)
                        scores.remove(0);
                }
                else
                    scores = new JSONArray("data");

                scores.put(score);

                try {
                    Files.write(Paths.get(rootfolder_name + "scores.json"), obj.toString(4).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }



                // go back to Main
                getContentPane().remove(spiel);
                CardLayout layout = (CardLayout) getContentPane().getLayout();
                layout.show(getContentPane(), "panel");

            }


        };


        spiel.addListener(el);

        // Naiv-Testing Area:
        Lokalsteuerung lok = new Lokalsteuerung(spiel, isHost);

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

        addKeyBinding(spiel, "ESC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spiel.pause();
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
