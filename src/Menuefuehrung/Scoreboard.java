package Menuefuehrung;

import Spielverlauf.Skin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.awt.Toolkit.getDefaultToolkit;

/***
 * Klasse für das Scoreboard, welches im Hauptmenü angezeigt und beim Ende einer Spielrunde befüllt wird
 */
public class Scoreboard extends JPanel implements Filesystem {

    private Skin current_skin;
    static private JTable Table;
    static private DefaultTableModel dtm;

    /***
     * Konstruktor bereitet die Score-Tabelle vor, berücksichtigt dabei die Schriftart aus dem Skin
     * @param skin Skin
     */
    Scoreboard(Skin skin){
        Table = new JTable();
        current_skin = skin; // Loads original_skin.png and original.json from skins/
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        setOpaque(false);
        dtm = new DefaultTableModel(0,0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        String[] CHeader = {"Name", "Age", "Date", "Score"};
        dtm.setColumnIdentifiers(CHeader);

        refeshScores();

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
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED), BorderFactory.createEmptyBorder(getHeight()/20,getHeight()/20,getHeight()/20,getHeight()/20)) );
    }

    /***
     * Methode überprüft ob es neue Scores für die Tabelle gibt, Quelle: scores.json
     */
    public static void refeshScores(){

        JSONObject obj = null;
        try {
            obj = new JSONObject(new String(Files.readAllBytes(Paths.get(rootDir + "scores.json"))));
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONArray scores = obj.getJSONArray("data");


        int rowCount = dtm.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--)
            dtm.removeRow(i);
        ArrayList<JSONObject> temp = new ArrayList();
        for (int i = 0; i < scores.length(); i++){
            temp.add(scores.getJSONObject(i));
        }
        Collections.sort(temp, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        return ((Integer)o1.get("score")).compareTo((Integer) o2.get("score"));
                    }
                }
        );
        for (int i = scores.length() - 1; i >=0 && i > scores.length() - 10; i--) {
            JSONObject s = (JSONObject) scores.remove(i);
            dtm.addRow(new Object[]{temp.get(i).get("name"), temp.get(i).get("age"), temp.get(i).get("date"), temp.get(i).get("score")});
        }
        Table = new JTable (dtm);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED), BorderFactory.createEmptyBorder(getHeight()/20,getHeight()/20,getHeight()/20,getHeight()/20)) );
    }


}
