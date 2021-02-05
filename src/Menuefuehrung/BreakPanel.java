package Menuefuehrung;

import Spielbereitstellug.Spiel;

import javax.swing.*;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;

/***
 * Klasse für das Pausen Menü, was im Spiel mit Esc angezeigt wird
 */
public class BreakPanel extends JPanel {

    BreakPanel(Spiel s, MainFrame babaFrame, boolean isAdmin) {

        setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width / 3, (Height / 4) * 3));

        setBackground(Color.BLACK);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


        Button leave_btn = new Button("leave game", 20);
        Button quit_btn = new Button("quit completely", 20);

        leave_btn.setAlignmentX(CENTER_ALIGNMENT);
        quit_btn.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(leave_btn);

        if (isAdmin) {
            Button resume_btn = new Button("resume game", 20);
            resume_btn.setAlignmentX(CENTER_ALIGNMENT);
            add(resume_btn);
            resume_btn.addActionListener((event) -> {
                s.resume();
                CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();
                if (s.getMultiplayer())
                    layout.show(babaFrame.getContentPane(), "multiplayer");
                else
                    layout.show(babaFrame.getContentPane(), "singleplayer");
            });
        }
        add(quit_btn);
        add(Box.createVerticalGlue());

        leave_btn.addActionListener((event) -> {
            s.beenden();
            babaFrame.getContentPane().remove(s);
            babaFrame.getContentPane().remove(this);
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();
            layout.show(babaFrame.getContentPane(), "panel");
        });
        quit_btn.addActionListener((event) -> {
            System.exit(0);
        });
    }
}
