package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;


public class button extends JButton {
    button(String Name, int Size){
                super(Name);
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setForeground(Color.red);
                setFocusable(false);
                setFont(new Font("Serif", Font.PLAIN, Size));
        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width/10,Height/13));
            }


    }
