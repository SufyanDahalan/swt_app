package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;

public class Options extends JPanel{
    Options(){

        setLayout(new FlowLayout(FlowLayout.CENTER, 500, 0));
        setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width/3, (Height/4)*3));

        setOpaque(false);
        button b1 = new button("Start", 20);
        b1.setForeground(Color.green);
        button b2 = new button("Options", 20);
        b2.setForeground(Color.green);
        button b3 = new button("Quit", 20);
        b3.setForeground(Color.green);
        button b8 = new button("music", 20);
        b8.setForeground(Color.yellow);
        //b8.setHorizontalAlignment(SwingConstants.LEFT);
        b1.addActionListener((event) ->{
            button b4 = new button("Singleplayer", 17);
            button b5 = new button("Multiplayer", 17);
            Box box1 = Box.createHorizontalBox();
            box1.add(b4);
            box1.add(b5);
            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();
            b4.addActionListener(e->{
                layout.show(frame, "game");//Singleplayer mode
            });
            b5.addActionListener(e->{
                layout.show(frame, "game");
            });


            //add(b1);

            add(box1);
            add(b2);
            add(b3);

            frame.repaint();
            frame.revalidate();
        });

        b2.addActionListener((event)-> {
            button b6 = new button("Level Editor", 17);
            button b7 = new button("Sound", 17);
            //button b8 = new button("Music", 17);
            Box box = Box.createHorizontalBox();
            box.add(b6);
            box.add(b7);



            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();
            b6.addActionListener(e->{

                layout.show(frame, "editor");
            });

            //add(b1);
            //add(b2);
            add(box);
            add(b3);

            frame.repaint();
            frame.revalidate();

        });


        b3.addActionListener(e -> System.exit(0));
        add(b1);
        add(b2);
        add(b3);


    }




}
