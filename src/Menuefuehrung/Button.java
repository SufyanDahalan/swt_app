package Menuefuehrung;

import Spielverlauf.Skin;

import javax.swing.*;
import java.io.File;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;


public class Button extends JButton implements Filesystem {

    private Skin current_skin;

    Button(String Name, int Size){
                super(Name);
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setForeground(Color.green);
                setFocusable(false);
                current_skin = new Skin(new File(skinfolder_name), skinName);
                setFont(current_skin.getFont().deriveFont(Font.PLAIN, 22));
                Dimension screenSize = getDefaultToolkit().getScreenSize();
                int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
                setPreferredSize(new Dimension(Width/7,Height/13));
            }


    }
