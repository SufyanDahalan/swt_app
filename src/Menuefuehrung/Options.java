package Menuefuehrung;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import Spielbereitstellug.Netzwerksteuerung;
import javafx.scene.control.DialogPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static java.awt.Toolkit.getDefaultToolkit;


public class Options extends JPanel implements ActionListener {

    MediaPlayer clip;
    boolean music = true;
    MainFrame digger;

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
        button b1 = new button("Start", 20);
        button b2 = new button("Options", 20);
        button b3 = new button("Quit", 20);
        button b4 = new button("Singleplayer", 17);
        button b5 = new button("Multiplayer", 17);
        button b6 = new button("Level Editor", 20);
        button b7 = new button("About", 17);
        button b8 = new button("", 17);


        b1.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b2.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b3.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b4.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b5.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b6.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b7.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        b8.addActionListener(e -> this.playSound("bin/music/button-09.wav"));
        add(b1);
        add(b6);
        add(b2);
        add(b7);
        add(b3);
        add(b8);


        ImageIcon icon = new ImageIcon("bin/images/VolumeIcon.png");
        b8.setIcon(icon);
        b8.addActionListener(this);


        b1.addActionListener((event) -> {
            b1.setEnabled(false);
            remove(b6);
            remove(b8);
            remove(b7);

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

            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();

            b4.addActionListener(e -> {
                babaFrame.prepareMap(true, false, null);
                layout.show(frame, "singleplayer");//Singleplayer mode
            });

            add(box1);
            add(b6);
            add(b2);
            add(b7);
            add(b3);
            add(b8);

            frame.repaint();
            frame.revalidate();
        });


        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String name = JOptionPane.showInputDialog(digger, "Option", null);
                //JOptionPane
            }
        });

        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField name = new JTextField(8);
                JTextField age = new JTextField(2);
                //JTextField date = new JTextField(8);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Your name :"));
                myPanel.add(name);
                myPanel.add(new JLabel("Your Age :"));
                myPanel.add(age);
               // myPanel.add(new JLabel("the date"));
                //myPanel.add(date);
                myPanel.add(Box.createHorizontalStrut(10));

                int result = JOptionPane.showConfirmDialog(null, myPanel, "please enter ..", JOptionPane.OK_CANCEL_OPTION);

                // Click on OK
                if(result == JOptionPane.OK_OPTION) {
                    // der Name und der Alter werden dann im Scoreboard eingespeichert.
                    // Fraglich, ob bei Abbruch des Spieles der Eintrag tz. eingespeichert bleibt.

                }
            }
        });



        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showOptionDialog(null ,"Host or Client ?", "choose a on", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Host", "Client"},  "Host");

                CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();/* frame.getLayout(); */

                if(choice == 0) {
                    // Host ausgewählt
                    //System.out.println("Your IP Address: xxx.xxx.xxx.xxx \n" + "Wait for a connation..." );
                    int Host = JOptionPane.showConfirmDialog(null,"Your IP Address: xxx.xxx.xxx.xxx \n" + "Wait for a connation...", "Host", JOptionPane.DEFAULT_OPTION );

                    Netzwerksteuerung netCont = new Netzwerksteuerung(true);

                    babaFrame.prepareMap(true, true, netCont);
                }
                else {
                    // Local ausgewählt
                    String name = JOptionPane.showInputDialog(digger, "enter the Host_IP: ", null);

                    Netzwerksteuerung netCont = new Netzwerksteuerung(false);

                    babaFrame.prepareMap(false, true, netCont);
                }
                layout.show(babaFrame.getContentPane(), "multiplayer");
            }
        });

        b6.addActionListener(e -> {
            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();
            layout.show(frame, "editor");
        });

        b3.addActionListener(e -> System.exit(0));

        editorButton(b6, babaFrame);
    }


    public void editorButton(button b, MainFrame babaFrame){

        LevelEditor editor = new LevelEditor();
        babaFrame.getContentPane().add(editor, "editor");// adds the LevelEditor to the cardboard layout
        CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();/* frame.getLayout(); */

        layout.show(babaFrame.getContentPane(), "editor");

    }

    public void playSound(String soundName)
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception ex)
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace( );
        }


    }

        public MediaPlayer Music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            String bip = "bin/music/Popcorn01.wav";
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
