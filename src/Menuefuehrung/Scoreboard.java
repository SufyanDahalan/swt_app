package Menuefuehrung;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

import static java.awt.Toolkit.getDefaultToolkit;

public class Scoreboard extends JPanel {
        Scoreboard(){
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
            Table.setForeground(Color.RED);



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
            Header.setFont(new Font("SansSerif", Font.BOLD, 16));
            Table.setFocusable(false);
            Table.setRowSelectionAllowed(false);

            add(Header);
            add(Table);
        }

}
