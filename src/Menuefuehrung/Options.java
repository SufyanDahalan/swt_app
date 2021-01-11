package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;

public class Options extends JPanel{
    Options(MainFrame babaFrame){
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
        button b6 = new button("Level Editor", 17);
        button b7 = new button("Sound", 17);
        button b8 = new button("Graphic", 17);
        add(b1);
        add(b2);
        add(b3);
        b1.addActionListener((event) -> {
            b1.setEnabled(false);
            JPanel sigleplayer = new JPanel();
            sigleplayer.setBackground(Color.black);
            b4.setForeground(Color.orange);

            JPanel multiplayer = new JPanel();
            multiplayer.setBackground(Color.black);
            b5.setForeground(Color.orange);

            JPanel levereditor = new JPanel();
            levereditor.setBackground(Color.black);
            b6.setForeground(Color.orange);

            Box box1 = Box.createVerticalBox();
            sigleplayer.add(b4);
            multiplayer.add(b5);
            levereditor.add(b6);
            box1.add(sigleplayer);
            box1.add(multiplayer);
            box1.add(levereditor);
            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();

            b4.addActionListener(e -> {
                
                layout.show(frame, "singleplayer");//Singleplayer mode
            });
            b6.addActionListener(e -> {

                layout.show(frame, "editor");
            });

            add(box1);
            add(b2);
            add(b3);

            frame.repaint();
            frame.revalidate();
        });

        b2.addActionListener((event) -> {
            b2.setEnabled(false);
            JPanel sound = new JPanel();
            sound.setBackground(Color.black);
            b7.setForeground(Color.orange);

            JPanel music = new JPanel();
            music.setBackground(Color.black);
            b8.setForeground(Color.orange);
            Box box = Box.createVerticalBox();
            sound.add(b7);
            music.add(b8);
            box.add(sound);
            box.add(music);

            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();
            // b6.addActionListener(e->{

            //     layout.show(frame, "editor");
            // });

            //add(b1);
            //add(b2);
            add(box);
            add(b3);

            frame.repaint();
            frame.revalidate();

        });

        b7.addActionListener((event) -> {});
        b8.addActionListener((event) -> {});

        b3.addActionListener(e -> System.exit(0));
        speilButton(b5, babaFrame);
        editorButton(b6, babaFrame);
        }

        public void speilButton(button b, MainFrame babaFrame){
            b.addActionListener(e -> {
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();/* frame.getLayout(); */
            babaFrame.prepareMap();
            
            layout.show(babaFrame.getContentPane(), "multiplayer");
        });}

        public void editorButton(button b, MainFrame babaFrame){

            LevelEditor editor = new LevelEditor();
            babaFrame.getContentPane().add(editor, "editor");// adds the LevelEditor to the cardboard layout
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();/* frame.getLayout(); */
            
            layout.show(babaFrame.getContentPane(), "editor");

        }



    }
