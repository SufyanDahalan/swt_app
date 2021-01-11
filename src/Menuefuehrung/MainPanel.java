package Menuefuehrung;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    MainPanel(){
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
            Scoreboard scoreboard = new Scoreboard();
            Options options = new Options();
            add(scoreboard);
            add(options);



    }
}
