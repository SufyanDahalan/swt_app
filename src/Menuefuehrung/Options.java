package Menuefuehrung;

import Spielbereitstellug.Netzwerksteuerung;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.awt.Font;

import static java.awt.Toolkit.getDefaultToolkit;


public class Options extends JPanel implements ActionListener, Filesystem {

    MediaPlayer clip, sound;
    int soundVolume = 10;
    int musicVolume = 10;
    boolean music = true;
    JDialog digger;

    Options(MainFrame babaFrame){
        com.sun.javafx.application.PlatformImpl.startup(()->{});

        try {
            clip = Music();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
            e1.printStackTrace();
        }

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


        ImageIcon icon = new ImageIcon(imageDir+"VolumeIcon.png");
        b8.setIcon(icon);
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

        CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();

        b4.addActionListener(e -> {
            babaFrame.prepareMap(true, false, null);
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
            {
                b2.setEnabled(false);
                JDialog settings = new JDialog();
                settings.setUndecorated(false);
                JSlider soundSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, soundVolume);
                JSlider musicSlider = new JSlider(JSlider.HORIZONTAL,0, 10, musicVolume);
                JButton save = new JButton("save");
                save.addActionListener(e2->playSound());


                save.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        settings.setVisible(false);
                        b2.setEnabled(true);
                    }
                });

                settings.addWindowListener(new WindowAdapter(){
                    @Override
                    public void windowClosing(WindowEvent e){
                        settings.setVisible(false);
                        b2.setEnabled(true);
                    }
                });
                musicSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        musicVolume = musicSlider.getValue();
                        clip.setVolume((musicSlider.getValue()/10.0));
                    }
                });

                soundSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        soundVolume = soundSlider.getValue();
                        sound.setVolume((soundSlider.getValue()/10.0));
                    }
                });



                JLabel soundLabel = new JLabel("Sound");
                soundLabel.setForeground(Color.red);
                settings.add(soundLabel);
                settings.add(soundSlider);
                JLabel musicLabel = new JLabel("Music");
                musicLabel.setForeground(Color.red);
                settings.add(musicLabel);
                settings.add(musicSlider);
                settings.add(save);
                settings.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                settings.setResizable(false);
                settings.setSize(new Dimension(500,200));
                settings.setLocationRelativeTo(null);
                settings.setAlwaysOnTop(true);
                settings.setVisible(true);
                settings.getContentPane().setBackground(Color.black);
                settings.setLayout(new BoxLayout(settings.getContentPane(), BoxLayout.PAGE_AXIS));
            }
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

                        // Nutze im folgendem den 2. Netzwerksteuerungs Konstruktor
                        // zum testen mit einer localhost Adresse.
                        Netzwerksteuerung netCont = null;
                        try {
                            netCont = new Netzwerksteuerung(true, InetAddress.getByName("127.0.0.1")); // zuvor war hier der 1. Konstruktor
                        } catch (UnknownHostException unknownHostException) {
                            unknownHostException.printStackTrace();
                        }

                        babaFrame.prepareMap(true, true, netCont);
                    }
                    else{
                        JOptionPane.showConfirmDialog(null, "Keine Verb. mögl.", "Host", JOptionPane.DEFAULT_OPTION);
                    }
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

                    babaFrame.prepareMap(false, true, netCont);
                }
                layout.show(babaFrame.getContentPane(), "multiplayer");
            }
        });

        b6.addActionListener(e -> {
            editorButton(b6, babaFrame);

        });
        b3.addActionListener(e -> System.exit(0));

        b7.addActionListener(e-> {
            JFrame helpme = new JFrame("Help me");
            helpme.setSize(500, 600);
            helpme.getContentPane().setBackground(Color.black);
            helpme.setVisible(true);
            helpme.setLocationRelativeTo(null);

            JLabel text = new JLabel("");
            text.setFont(new Font("Serif", Font.PLAIN, 18));
            text.setText("<html><font color = red size=7> Tastaturbelegung <br> <br>  " +
                    "<font color = white size = 5>" +
                    " nach oben gehen ------------------------------------------ △         <br> <br>" +
                    " nach unten gehen ----------------------------------------- ▽         <br> <br>" +
                    " nach links laufen ----------------------------------------- ◁         <br> <br>" +
                    " nach rechts laufen ---------------------------------------- ▷         <br> <br>" +
                    " feuerball abfeuern --------------------------------------- Leertaste <br> <br>" +
                    "Vollbild-/Fenstermodus ---------------------------------F11       <br> <br>" +
                    "Spiel pausiereen ------------------------------------------ esc       <br> <br> <br><br><br><br><br><br><br><br><br>" +
                    " </b></html> ");


            text.setFont(text.getFont().deriveFont(50f));
            text.setVisible(true);
            helpme.add(text);
        });

       /* b7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Desktop desktop = Desktop.getDesktop();
                if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(new File("bin/Benutzerhandbuch .pdf"));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });*/

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
            LevelEditor editor = new LevelEditor();
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

    public MediaPlayer Music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String bip = musicDir+"Popcorn01.wav";
        Media hit = new Media(new File(bip).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setCycleCount(50000000);
        mediaPlayer.play();
        return mediaPlayer;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(music && clip != null) {
            clip.stop();
            music = false;
        }
        else if(!music && clip != null) {
            clip.play();
            music = true;
        }
    }
}
