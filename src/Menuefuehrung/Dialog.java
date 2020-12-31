package Menuefuehrung;


import javax.swing.*;
import java.awt.*;

public class Dialog extends JDialog{
    Dialog(Container frame){
        setTitle("Digger");
        setResizable(false);
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bin/Images/Logo.png")));
        ImageIcon image = new ImageIcon("bin/Images/Logo.png");//TODO: fixing the Logo
        setIconImage(image.getImage());
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        button b1 = new button("Multiplayer", 25);
        JButton b2 = new button("Singleplayer", 25);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        add(b1);
        add(b2);
        pack();
        setLocationRelativeTo(null);
        setVisible(false);
        CardLayout layout = (CardLayout) frame.getLayout();
        b1.addActionListener(e->{
            layout.show(frame, "game");//Singleplayer mode
            dispose();
        });
        b2.addActionListener(e->{
            layout.show(frame, "game");//should be used for Multiplayer mode
            dispose();
        });
        setVisible(true);
    }
}
