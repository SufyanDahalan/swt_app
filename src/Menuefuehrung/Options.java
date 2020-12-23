package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;

public class Options extends JPanel{
    Options(){
        setLayout(new FlowLayout(FlowLayout.CENTER, 500, 50));
        setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width/3, (Height/4)*3));

        setOpaque(false);
        button b1 = new button("Start Game", 20);
        button b2 = new button("Options", 20);
        button b3 = new button("Quit", 20);
        b1.addActionListener((event) ->{
            new Dialog(getParent().getParent());//sends a reference of MainFrame to Dialog
        });
        b3.addActionListener(e -> System.exit(0));

        b2.setPreferredSize(new Dimension(200,200));
        add(b1);
        add(b2);
        add(b3);
    }


}
