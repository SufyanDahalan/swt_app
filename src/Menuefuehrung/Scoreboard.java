package Menuefuehrung;

import Spielverlauf.Skin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;

import static java.awt.Toolkit.getDefaultToolkit;

public class Scoreboard extends JPanel {
    private final String skinName = "original_skin"; // Skinnname
    private final String skinfolder_name = "bin/skins/"; // ./skin/sink_original.json,...
    private Skin current_skin;
        Scoreboard(){
            current_skin = new Skin(new File(skinfolder_name), skinName); // Loades original_skin.png and original.json from skins/
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));

            setOpaque(false);
            DefaultTableModel dtm = new DefaultTableModel(0,0) {
                public boolean isCellEditable(int rowIndex, int mColIndex) {
                    return false;
                }
            };
            String[] CHeader = {"Name", "Age", "Date", "Score"};
            dtm.setColumnIdentifiers(CHeader);

            for (int count = 1; count <= 10; count++) {
                dtm.addRow(new Object[]{"Name " + count, "Age " + count, "Date " + count, "Score " + count});
            }
            JTable Table = new JTable (dtm);
            Table.setModel(dtm);
            Table.setFillsViewportHeight(true);
            Table.setForeground(Color.white);
            Table.setFont(current_skin.getFont().deriveFont(Font.PLAIN, 15));



            Dimension screenSize = getDefaultToolkit().getScreenSize();
            int Height = (int) screenSize.getHeight(), Width = (int) screenSize.getWidth();
            setPreferredSize(new Dimension(Width / 3, (Height / 4) * 3));
            Table.setRowHeight(((Height / 4) * 3)/11);

            Table.setOpaque(false);
            Table.setFillsViewportHeight(true);
            Table.setShowGrid(false);
            ((DefaultTableCellRenderer)Table.getDefaultRenderer(Object.class)).setOpaque(false);
            ((DefaultTableCellRenderer)Table.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.CENTER );
            JTableHeader Header = Table.getTableHeader();
            Header.setOpaque(false);
            Header.setBackground(new Color(0,0,0,255));
            Header.setForeground(Color.RED);
            Header.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,255)));
            Header.setResizingAllowed(false);
            Header.setReorderingAllowed(false);
            Header.setFont(current_skin.getFont().deriveFont(Font.PLAIN, 20));
            Table.setFocusable(false);
            Table.setRowSelectionAllowed(false);

            add(Header);
            add(Table);
        }

}