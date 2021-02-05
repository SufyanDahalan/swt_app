package Menuefuehrung;

import Spielverlauf.Skin;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/***
 * Klasse für das Panel des Hauptmenüs, wird vom MainFrame genutzt
 */
public class MainPanel extends JPanel {

    JPanel menu;
    Skin skin;

    /***
     * Panel mit Menü, wird im übergebenen Frame erstellt,
     * zeigt Auswahlmöglichkeiten der Options-Klasse an.
     * @param babaFrame JFrame
     * @param skin Skin
     */
    MainPanel(MainFrame babaFrame, Skin skin){

        this.skin = skin;

        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        Scoreboard scoreboard = new Scoreboard(skin);

        menu = new JPanel(new CardLayout());
        menu.setBackground(Color.black);

        Options options = new Options(babaFrame, menu, skin);
        SetupPanel setup = new SetupPanel(menu, options);
        ManualPanel manual = new ManualPanel(menu);
        MPPanel multiplayer = new MPPanel(menu);

        menu.add(options, "Hauptmenu");
        menu.add(setup, "Setup");
        menu.add(manual, "Manual");
        menu.add(multiplayer, "Multiplayer");

        add(scoreboard);
        add(menu);

    }

    private abstract class SubMenu extends JPanel {

        public SubMenu(JPanel mainmenu) {
            setLayout(new BorderLayout());
            setBackground(Color.black);
            setBorder(new CompoundBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED), BorderFactory.createEmptyBorder(getHeight()/20,getHeight()/20,getHeight()/20,getHeight()/20)) );
            Button backBtn = new Button("back", 17);

            backBtn.addActionListener(e -> {
                CardLayout layout = (CardLayout) mainmenu.getLayout();
                layout.show(mainmenu, "Hauptmenu");
            });

            add(backBtn,BorderLayout.SOUTH);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            setBorder(new CompoundBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED), BorderFactory.createEmptyBorder(getHeight()/20,getHeight()/20,getHeight()/20,getHeight()/20)) );        }
    }

    private class SetupPanel extends SubMenu {
        public SetupPanel(JPanel mainmenu, Options options) {
            super(mainmenu);

            JLabel header = new JLabel("Setup", SwingConstants.CENTER);
            header.setFont(skin.getFont().deriveFont(Font.PLAIN, 34));
            header.setForeground(Color.red);
            add(header, BorderLayout.NORTH);

            JPanel controlles = new JPanel();

            //--------------------------------

            JSlider soundSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, options.soundVolume);
            JSlider musicSlider = new JSlider(JSlider.HORIZONTAL,0, 10, options.musicVolume);

            soundSlider.setUI(new SliderDigger(soundSlider, skin));
            musicSlider.setUI(new SliderDigger(musicSlider, skin));

            JLabel soundLabel = new JLabel("Sound", SwingConstants.CENTER);
            soundLabel.setForeground(Color.white);
            soundLabel.setAlignmentX(CENTER_ALIGNMENT);
            soundLabel.setFont(skin.getFont().deriveFont(Font.PLAIN, 20));

            JLabel musicLabel = new JLabel("Music", SwingConstants.CENTER);
            musicLabel.setForeground(Color.white);
            musicLabel.setAlignmentX(CENTER_ALIGNMENT);
            musicLabel.setFont(skin.getFont().deriveFont(Font.PLAIN, 20));


            controlles.setLayout(new BoxLayout(controlles, BoxLayout.PAGE_AXIS));
            controlles.setBackground(Color.black);

            controlles.add(Box.createVerticalGlue());
            controlles.add(soundLabel);
            controlles.add(Box.createRigidArea(new Dimension(0, 10)));
            controlles.add(soundSlider);
            controlles.add(Box.createRigidArea(new Dimension(0, 20)));
            controlles.add(musicLabel);
            controlles.add(Box.createRigidArea(new Dimension(0, 10)));
            controlles.add(musicSlider);
            controlles.add(Box.createVerticalGlue());

            setBackground(Color.black);

            musicSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    options.musicVolume = musicSlider.getValue();
                    options.clip.setVolume((musicSlider.getValue()/10.0));
                }
            });

            soundSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    options.soundVolume = soundSlider.getValue();
                    options.sound.setVolume((soundSlider.getValue()/10.0));
                }
            });


            //--------------------------------

            add(controlles, BorderLayout.CENTER);

        }
    }

    private class ManualPanel extends SubMenu {

        JTable tabelle;

        public ManualPanel(JPanel mainmenu) {
            super(mainmenu);

            JLabel header = new JLabel("keyboard input\n", SwingConstants.CENTER);
            header.setFont(skin.getFont().deriveFont(Font.PLAIN, 34));
            header.setForeground(Color.red);
            add(header, BorderLayout.NORTH);

            DefaultTableModel dtm = new DefaultTableModel(0,0) {
                public boolean isCellEditable(int rowIndex, int mColIndex) {
                    return false;
                }
            };
            String[] CHeader = {"function", "key"};
            dtm.setColumnIdentifiers(CHeader);


            dtm.addRow(new Object[]{"",""});
            dtm.addRow(new Object[]{"move up","UP"});
            dtm.addRow(new Object[]{"move down","DOWN"});
            dtm.addRow(new Object[]{"move left","LEFT"});
            dtm.addRow(new Object[]{"move rigth","RIGHT"});
            dtm.addRow(new Object[]{"fire","SPACE"});
            dtm.addRow(new Object[]{"fullscreen / windowed","F11"});
            dtm.addRow(new Object[]{"game pause","ESC"});

            tabelle = new JTable(dtm);

            setOpaque(false);
            tabelle.setFillsViewportHeight(true);
            tabelle.setShowGrid(false);
            tabelle.setFocusable(false);
            tabelle.setRowSelectionAllowed(false);
            tabelle.setFillsViewportHeight(true);
            tabelle.setForeground(Color.white);
            tabelle.setBackground(Color.black);
            tabelle.setFont(skin.getFont().deriveFont(Font.PLAIN, 20));

            add(tabelle, BorderLayout.CENTER);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if(getHeight()/20 > 0)
                tabelle.setRowHeight(getHeight()/20);
        }
    }

    private class MPPanel extends SubMenu {
        public MPPanel(JPanel mainmenu) {
            super(mainmenu);
            add(new JLabel("Das ist Multiplayer"));
        }
    }
}
