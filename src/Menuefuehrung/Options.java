package Menuefuehrung;

import Spielbereitstellug.Chat;
import Spielbereitstellug.Netzwerksteuerung;
import Spielverlauf.Skin;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import static java.awt.Toolkit.getDefaultToolkit;

/***
 * Klasse für Auswahlmöglichkeiten im Hauptmenü,
 * Optionen sind: Start, Options (für Einstellungen), Quit, Singleplayer, Multiplayer, Level Editor, Help me
 */
public class Options extends JPanel implements ActionListener, Filesystem {

    MediaPlayer clip, sound;
    int soundVolume = 10;
    int musicVolume = 10;
    boolean music = true;
    JDialog digger;
    int mutedVolume = 10;
    Skin skin;

    /***
     * blendet die Optionen im übergebenen JPanel des JFrames ein, samt Buttons im Digger-Look,
     * einige Optionen, werden erst nach klicken auf einen Button ausgeklappt z.B. sind Single- und Multiplayer unter einem Punkt Spielen vereint.
     * @param babaFrame JFrame
     * @param menu JPanel
     * @param skin Skin
     */
    Options(MainFrame babaFrame, JPanel menu, Skin skin){
        com.sun.javafx.application.PlatformImpl.startup(()->{});

        try {
            clip = Music();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
            e1.printStackTrace();
        }

        this.skin = skin;

        setLayout(new FlowLayout(FlowLayout.CENTER, 500, 0));
        setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width/3, (Height/4)*3));

        setOpaque(false);
        Button b1 = new Button("Start", 20);
        Button b2 = new Button("Options", 20);
        Button b3 = new Button("Quit", 20);
        Button b4 = new Button("Singleplayer", 17);
        Button b5 = new Button("Multiplayer", 17);
        Button b6 = new Button("Level Editor", 20);
        Button b7 = new Button("Help me", 17);
        Button b8 = new Button("", 17);




        b1.addActionListener(e -> playSound());
        b2.addActionListener(e -> playSound());
        b3.addActionListener(e -> playSound());
        b4.addActionListener(e -> playSound());
        b5.addActionListener(e -> playSound());
        b6.addActionListener(e -> playSound());
        b7.addActionListener(e -> playSound());
        b8.addActionListener(e -> playSound());
        add(b1);
        add(b6);
        add(b2);
        add(b7);
        add(b3);
        add(b8);


        b8.setIcon(new ImageIcon(skin.getImage("mute_btn")));
        b8.addActionListener(this);

        JPanel sigleplayer = new JPanel();
        sigleplayer.setBackground(Color.black);
        b4.setForeground(Color.orange);

        JPanel multiplayer = new JPanel();
        multiplayer.setBackground(Color.black);
        b5.setForeground(Color.orange);


        Box box1 = Box.createVerticalBox();
        sigleplayer.add(b4);
        multiplayer.add(b5);

        box1.add(sigleplayer);
        box1.add(multiplayer);


        b4.addActionListener(e -> {
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();
            babaFrame.prepareMap(true, false, null, null);
            layout.show(babaFrame.getContentPane(), "singleplayer");//Singleplayer mode
        });

        b1.addActionListener((event) -> {
            if(b1.getForeground() == Color.green){
                b1.setForeground(Color.darkGray);
                remove(b6);
                remove(b8);
                remove(b7);

                add(box1);

                add(b6);
                add(b2);
                add(b7);
                add(b3);
                add(b8);
            }else{
                b1.setForeground(Color.green);
                remove(box1);
            }


            babaFrame.repaint();
            babaFrame.revalidate();
        });


        b2.addActionListener(e -> {

            CardLayout layout = (CardLayout) menu.getLayout();
            layout.show(menu, "Setup");
        });


        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showOptionDialog(null ,"Host or Client ?", "choose a on", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Host", "Client"},  "Host");

                CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();

                InetAddress ipv4 = null;

                try{
                    URL ipstring = new URL("http://checkip.amazonaws.com");
                    BufferedReader in = new BufferedReader(new InputStreamReader(ipstring.openStream()));
                    ipv4 = InetAddress.getByName(in.readLine()); // you get the IP as a String
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.out.println("Konnte IP-Adresse nicht herausfinden");
                }

                if(choice == 0) {
                    // Host ausgewählt

                    if(ipv4 != null) {
                        // Auskommentiert, da irreführend; Blockt den Aufbau einer Verbindung; Muss entweder in eigenem Thread laufen oder z.B. als Panel implentiert werden
                        //JOptionPane.showConfirmDialog(null, "Your IP Address: "+ipv4+" \n" + "Wait for a connation...", "Host", JOptionPane.DEFAULT_OPTION);
                    }
                    else{
                        JOptionPane.showConfirmDialog(null, "Keine Verb. zum Inet.", "Host", JOptionPane.DEFAULT_OPTION);
                    }

                    // Nutze im folgendem den 2. Netzwerksteuerungs Konstruktor
                    // zum testen mit einer localhost Adresse.
                    Netzwerksteuerung netCont = null;
                    try {
                        netCont = new Netzwerksteuerung(true, InetAddress.getByName("127.0.0.1")); // zuvor war hier der 1. Konstruktor
                    } catch (UnknownHostException unknownHostException) {
                        unknownHostException.printStackTrace();
                    }
                    Chat chat = new Chat(netCont);
                    babaFrame.prepareMap(true, true, netCont, chat);
                }
                else {
                    // Client ausgewählt
                    String ipstring = JOptionPane.showInputDialog(digger, "enter the Host_IP: ", "localhost");

                    InetAddress ipImp = null;

                    try { //Auch hier zum testen eine localhost Adresse.
                        ipImp = InetAddress.getByName(ipstring); // zuvor dieses
                        // braucht man auch später wieder, damit die Eingabe genommen wird.
                        //ipImp = InetAddress.getByName("127.0.0.1");
                    } catch (UnknownHostException unknownHostException) {
                        unknownHostException.printStackTrace();
                    }

                    //Netzwerksteuerung netCont = new Netzwerksteuerung(ipImp);
                    //
                    // Auch hier nehmen wir jetzt mal den 2. Konstruktor:

                    Netzwerksteuerung netCont = new Netzwerksteuerung(false, ipImp);
                    Chat chat = new Chat(netCont);
                    babaFrame.prepareMap(false, true, netCont, chat);
                }
                layout.show(babaFrame.getContentPane(), "multiplayer");
            }
        });

        b6.addActionListener(e -> {
            editorButton(b6, babaFrame);

        });
        b3.addActionListener(e -> System.exit(0));

        b7.addActionListener(e-> {

            CardLayout layout = (CardLayout) menu.getLayout();
            layout.show(menu, "Manual");

        });
    }


    public void editorButton(Button b, MainFrame babaFrame){

        if(LevelEditor.assertMaxMap()){
            JDialog ErrorDialog = new JDialog();
            ErrorDialog.setTitle("Error!");
            JLabel text = new JLabel("<html><body>Cannot create more than 100 levels!<br>please delete them and try again later</body></html>", SwingConstants.CENTER);
            text.setVerticalAlignment(SwingConstants.CENTER);
            ErrorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            ErrorDialog.setResizable(false);
            ErrorDialog.setSize(new Dimension(500,200));
            ErrorDialog.setLocationRelativeTo(null);


            text.setForeground(Color.red);

            ErrorDialog.getContentPane().setBackground(Color.BLACK);

            ErrorDialog.add(text);
            ErrorDialog.setVisible(true);
        }else {
            LevelEditor editor = new LevelEditor(skin);
            babaFrame.getContentPane().add(editor, "editor");// adds the LevelEditor to the cardboard layout
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();
            MainFrame.addKeyBinding(editor, "ESCAPE", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    layout.show(babaFrame.getContentPane(), "panel");
                }});
            layout.show(babaFrame.getContentPane(), "editor");
        }
    }

    /***
     * Spielt einen Ton bei Betätigung eines Menü-Buttons ab
     */
    public void playSound()
    {
        if(sound == null) {
            String bip = musicDir+"button-09.wav";
            Media hit = new Media(new File(bip).toURI().toString());
            sound = new MediaPlayer(hit);
        }else{
            sound.play();
            sound.seek(Duration.ZERO);
        }
    }

    /***
     * Spielt Hintergrundmusik im Hauptmenü ab
     * @return Mediaplayer
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    public MediaPlayer Music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String bip = musicDir+"Popcorn01.wav";
        Media hit = new Media(new File(bip).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setCycleCount(50000000);
        mediaPlayer.play();
        return mediaPlayer;
    }

    /***
     * Methode zum Stummschalten der Hintergrundmusik per Schaltfläche mit Piktogramm
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(music && clip != null) {
            mutedVolume = musicVolume;
            musicVolume = 0;
            clip.stop();
            music = false;
        }
        else if(!music && clip != null) {
            musicVolume = mutedVolume;
            clip.play();
            music = true;
        }
    }
}
