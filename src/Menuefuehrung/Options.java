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
        button b1 = new button("Start Game", 20);
        button b2 = new button("Options", 20);
        button b3 = new button("Quit", 20);
        b1.addActionListener((event) ->{
            button b4 = new button("Singleplayer", 20);
            button b5 = new button("Multiplayer", 20);
            Container frame = getParent().getParent();
            CardLayout layout = (CardLayout) frame.getLayout();
            b4.addActionListener(e->{
                layout.show(frame, "game");//Singleplayer mode
            });
            b5.addActionListener(e->{
                layout.show(frame, "game");
            });
            remove(b1);
            remove(b2);
            remove(b3);
            add(b4);
            add(b5);
            add(b2);
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
