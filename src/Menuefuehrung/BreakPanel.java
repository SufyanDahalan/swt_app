package Menuefuehrung;

import Spielbereitstellug.Spiel;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static java.awt.Toolkit.getDefaultToolkit;

public class BreakPanel extends JPanel {

    BreakPanel(Spiel s, MainFrame babaFrame){
        System.out.println("BP erstellt");
        setLayout(new FlowLayout(FlowLayout.CENTER, 500, 0));
        setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
        setPreferredSize(new Dimension(Width/3, (Height/4)*3));

        setBackground(Color.BLACK);

        button resume_btn = new button("resume game", 20);
        button leave_btn = new button("leave game", 20);
        button quit_btn = new button("quit completely", 15);


        add(resume_btn);
        add(leave_btn);
        add(quit_btn);

        resume_btn.addActionListener((event) -> {
            s.resume();
            CardLayout layout = (CardLayout) babaFrame.getContentPane().getLayout();
            if (s.getMultiplayer())
                layout.show(babaFrame.getContentPane(), "multiplayer");
            else
                layout.show(babaFrame.getContentPane(), "singleplayer");
        });
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
