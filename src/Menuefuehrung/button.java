package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

public class button extends JButton {
    button(String Name, int Size){
                super(Name);
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setForeground(Color.RED);
                setFocusable(false);
                setFont(new Font("Serif", Font.PLAIN, Size));
            }
    }
