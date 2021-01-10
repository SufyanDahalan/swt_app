package Menuefuehrung;

import Spielverlauf.Skin;

import javax.swing.*;
import java.io.File;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;


public class button extends JButton {

    private final String skinName = "original_skin";
    private final String skinfolder_name = "bin/skins/";
    private Skin current_skin;

    button(String Name, int Size){
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
        setPreferredSize(new Dimension(Width/8,Height/13));
            }


    }
