package Menuefuehrung;

import Spielbereitstellug.*;
import Spielverlauf.Skin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.GregorianCalendar;


public class MainFrame extends JFrame implements Filesystem, MouseListener {
    Skin skin;

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    boolean fullscreen = false;

    public static void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }


    public static void main(String[] args) throws Exception {

       SwingUtilities.invokeLater(MainFrame::new);
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
         */
    }

    /***
     * Konstruktor der ersten Klasse, die von der Main-Methode aufgerufen wird:
     * Erstellt Panel, in dem Menü und später Spiel angezeigt werden,
     * lädt den Skin ein und setzt die Tastenbelegung für den Vollbildmodus (F11)
     */
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
        skin = new Skin(new File(skinfolder_name), skinName);
        MainPanel Panel = new MainPanel(this, skin);
        getContentPane().add(Panel, "panel");

        setIconImage(new ImageIcon(imageDir+"Logo.png").getImage());
        setUndecorated(false);
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(this.getContentPane(), "panel");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);
    }

    /***
     * Funktion für den Vollbildmous, kann im Menü und Spiel mit eigener Taste ausgelöst werden.
     */
    private void FullScreen(){
        if (fullscreen) {
            dispose();
            setUndecorated(false);
            fullscreen = false;
            pack();
            setVisible(true);
        }else{
            dispose();
            setUndecorated(true);
            fullscreen = true;
            device.setFullScreenWindow(this);
        }
    }

    /***
     * Methode trifft Vorbereitungen für den Spielablauf,
     * setzt Tastenbelegung der Pfeiltasten für die Steuerung,
     * bereitet Ablauf bei Spielende vor: Speicherung von Initialien und Alter des Spielers zusammen mit seinem Score
     * @param isHost Boolean, Unterscheidet Client/Host
     * @param isMultiplayer Boolean, Unterscheidet Single- von Multiplayermodus
     * @param netCont Netzerksteuerung
     * @param chat Chat im Multiplayermodus
     */
    public void prepareMap(boolean isHost, boolean isMultiplayer, Netzwerksteuerung netCont, Chat chat){//copied from Test.java, should be adjusted later

        final Spiel spiel = new Spiel(isHost, isMultiplayer, netCont, chat, skin);


        EndListener el = spielstand -> {

            JTextField name = new JTextField(3);
            JTextField age = new JTextField(2);

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("Your name :"));
            myPanel.add(name);
            myPanel.add(new JLabel("Your Age :"));
            myPanel.add(age);
            myPanel.add(Box.createHorizontalStrut(10));

            name.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {}

                @Override
                public void keyReleased(KeyEvent e) {
                    int pos = name.getCaretPosition();
                    String text = name.getText();
                    if(text.length() > 3){
                        text = text.substring(0,3);
                        pos = 3;
                    }
                    name.setText(text.toUpperCase());
                    name.setCaretPosition(pos);
                }
            });

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

                JSONObject obj = null;
                try {
                    obj = new JSONObject(new String(Files.readAllBytes(Paths.get(rootDir + "scores.json"))));
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
                    Files.write(Paths.get(rootDir + "scores.json"), obj.toString(4).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // go back to Main, update Scoreboard
                Scoreboard.refeshScores();
                getContentPane().remove(spiel);
                CardLayout layout = (CardLayout) getContentPane().getLayout();
                layout.show(getContentPane(), "panel");
            }
        };


        spiel.addListener(el);

        Lokalsteuerung lok = new Lokalsteuerung(spiel, isHost, chat);

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

        if(isMultiplayer) {
            addKeyBinding(spiel, "ENTER", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chat.senden();
                    spiel.requestFocus();
                }
            });
            addKeyBinding(spiel, "TAB", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!chat.isVisible())
                        chat.setVisible(true);
                    else
                        chat.setVisible(false);
                }
            });
        }

        final MainFrame b =this;

        addKeyBinding(spiel, "ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pausiere Spiel
                spiel.pausieren();
                // zeige Pausemenue
                CardLayout layout = (CardLayout) getContentPane().getLayout();
                // erstelle PausePanel

                BreakPanel bp = new BreakPanel(spiel, b, true);
                getContentPane().add(bp, "spielpause");

                layout.show(getContentPane(), "spielpause");
            }
        });



        if(isMultiplayer)
            getContentPane().add(spiel, "multiplayer");
        else
            getContentPane().add(spiel, "singleplayer");
        spiel.spawnSpieler();
        spiel.start();

    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
