package Menuefuehrung;

import Spielbereitstellug.Render;
import Spielverlauf.Animation;
import Spielverlauf.Skin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/***
 * Diese Klasse soll das erstellen neuer Level ermöglichen,
 * erstellte Level können im .json Format gespeichert werden.
 * Ein neu erstelltes Level wird standarmäßig als Letztes Level der spielbaren Level angehängt.
 * Das Feature richtet sich an fortgeschrittene Spieler, denen die Standardlevel zu langweilig sind.
 */
public class LevelEditor extends Render implements MouseListener, Filesystem {

    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    MouseAdapter globalMouseAdapter;

    JSONArray playground_size = new JSONArray(2);
    private int[] spawn_monster;
    private int[] spawn_sp1;
    private int[] spawn_sp2;

    /***
     * um Tastenbelegung zu erstellen
     * @param c JComponent
     * @param key Taste
     * @param action Action
     */
    public static void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);// add new keybinding
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }

    /**
     * Prüft ob es schon mehr mehr als 99 Maps gibt
     * @return boolean
     */
    static public boolean assertMaxMap(){
        ArrayList<Integer> mapNumber = new ArrayList<>();
        String[] maps = new File(levelfolder_name).list();
        return maps.length > 99;
    }

    /***
     * Konstruktor des LevelEditors
     */
    public LevelEditor(Skin skin) {
        super(skin);
        playground_size.put(0, 18);
        playground_size.put(1, 10);
        super.obj.put("pg_size", playground_size);
        keybindings();

        Name();

        super.refreshSizing();
    }

    /***
     * speichert das erstellte Level, aber nur wenn alle für das Spiel notwendigen Elemente gesetzt wurden
     */
    public void validateAndSave() {// can be called with ctrl+s
        if (super.obj.has("name") && super.obj.has("pg_size") && super.obj.has("spawn_p1") && super.obj.has("spawn_p2") && super.obj.has("spawn_mon")
                && super.obj.has("spawn_cherry") && super.obj.has("pos_diam") && super.obj.has("pos_money") && super.obj.has("pos_tun")) {

            ArrayList<Integer> mapNumber = new ArrayList<>();
            String[] maps = new File(levelfolder_name).list();
            for(String map : maps){
                map = map.substring(0, 8);

                Matcher matcher = lastIntPattern.matcher(map);
                if (matcher.find()) {String someNumberStr = matcher.group(1);
                    mapNumber.add(Integer.parseInt(someNumberStr));}
            }
            String name;
            DecimalFormat formatter = new DecimalFormat("00");

            if(!mapNumber.isEmpty()){
                int a = Collections.max(mapNumber)+1;
                String aFormatted = formatter.format(a);
                name = "level_"+aFormatted+".json";
            }else{
                name = "level_01.json";
            }
            try {
                    System.out.println("name: "+levelfolder_name+"+"+name);
                Files.write(Paths.get(levelfolder_name + name), LevelEditor.super.obj.toString(4).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Soll bestehende Level in den Editor laden können
     */
    public void loadSavedLevel() { // Soll zum laden bestehender Level genutzt werden
        System.out.println("STRG + O Wurde gedrückt!!!");

        JFileChooser fileChooser = new JFileChooser();
        // Erstelle "File-Filter", so dass nur .json Dateien ausgewählt werden können
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()) return true;
                else{
                    String name = file.getName().toLowerCase();
                    return name.endsWith(".json");
                }
            }

            @Override
            public String getDescription() {
                return "Level Dateien im .json Format";
            }
        });


        int response = fileChooser.showOpenDialog(null);
        if(response != 0) return; // d.h.: Das Fenster wurde geschlossen, ohne, dass eine Datei ausgewählt wurde.

        String filename = fileChooser.getSelectedFile().getAbsolutePath();
        System.out.println(filename); // filename enthölt den absoluten Dateipfad zur json Datei


        // Hier wird die zuvor ausgewählte Json-Datei geladen
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));

            LevelEditor.super.obj = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /***
     * zeigt das Spielfeld an und animiert Monster und Spieler Figuren
     * @param g Graphics-Object
     */
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // Zeichne Spieler
        if(spawn_sp1 != null) {
            Animation ani_right = current_skin.getAnimation("digger_red_right");
            BufferedImage sp1Img;
            sp1Img = ani_right.nextFrame(super.field_size);
            int[] field = super.getCenterOf(spawn_sp1);
            int x_pixel = field[0] - (sp1Img.getWidth() / 2);
            int y_pixel = field[1] - (sp1Img.getHeight() / 2);
            g.drawImage(sp1Img, x_pixel, y_pixel, null);
        }
        if(spawn_sp2 != null) {
            Animation ani_left = current_skin.getAnimation("digger_gre_left");
            BufferedImage spImg;
            spImg = ani_left.nextFrame(super.field_size);
            int[] field = super.getCenterOf(spawn_sp2);
            int x_pixel = field[0] - (spImg.getWidth() / 2);
            int y_pixel = field[1] - (spImg.getHeight() / 2);
            g.drawImage(spImg, x_pixel, y_pixel, null);
        }

        // Monster
        Animation ani_nobbin = current_skin.getAnimation("nobbin");
        BufferedImage nobbinImg = ani_nobbin.nextFrame(super.field_size);
        if (spawn_monster != null) {
            int[] field = super.getCenterOf(spawn_monster);
            int x_pixel = field[0] - (nobbinImg.getWidth() / 2);
            int y_pixel = field[1] - (nobbinImg.getHeight() / 2);

            g.drawImage(nobbinImg, x_pixel, y_pixel, null);
        }

        // Zeichne Anleitung
        final int margin_y = field_size / 4;
        int margin_x = field_size / 2;

        int fontSize = (int) ((double) field_size / 3.5);
        g.setFont(current_skin.getFont().deriveFont(Font.PLAIN, fontSize));
        g.setColor(Color.white);

        BufferedImage pic;
        String text;
        int off_left = field_size / 3;
        int off_right = field_size / 3;

        int pic_size = (int) ((double) field_size / 1.2);

        text = "D:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("diamond", pic_size);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "H:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("tunnel_hori", pic_size / 2);
        pic = current_skin.invertImage(pic);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "V:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("tunnel_vert", pic_size / 2);
        pic = current_skin.invertImage(pic);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "S:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("tunnel_space", pic_size / 2);
        pic = current_skin.invertImage(pic);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "P:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("statusbar_digger_MP_red", pic_size);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "B:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("statusbar_digger_MP_gre", pic_size);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "G:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("money_static", pic_size);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "K:";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;
        pic = current_skin.getImage("cherry", pic_size);
        g.drawImage(pic, margin_x, margin_y, null);
        margin_x += pic.getWidth() + off_left;

        text = "R: Remove";
        g.drawString(text, margin_x, margin_y + fontSize);
        margin_x += g.getFontMetrics().stringWidth(text) + off_right;

        text = "Str+S: Save";
        g.drawString(text, margin_x, margin_y + fontSize);

        text = "Str+O: Open";
        g.drawString(text, margin_x, margin_y + 2 * fontSize);

        repaint();
    }

    /***
     * untersucht welche Level-Dateien es im Level-Ordner bereits gibt
     * inkrementiert die Letzte Ziffer im Dateinamen
     */
    void Name(){
        String[] maps = new File(levelfolder_name).list();
        ArrayList<Integer> mapNumber = new ArrayList<>();

        for (String map : maps) {
            // read Level-File
            JSONObject _obj = null;
            try {
                _obj = new JSONObject(new String(Files.readAllBytes(Paths.get(levelfolder_name + map))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(_obj.getString("name").charAt(0) == 'U'){//value of key "name" does not start with 'U', implying that the map is a user map, not an "Original_L*" map
                String latestName = _obj.getString("name");//Name of latest user created map
                Matcher matcher = lastIntPattern.matcher(latestName);
                if (matcher.find()) {
                    String someNumberStr = matcher.group(1);
                    mapNumber.add(Integer.parseInt(someNumberStr));
                }
            }
        }
        if(!mapNumber.isEmpty()){
            super.obj.put("name", "User_L"+(Collections.max(mapNumber)+1));
        }else if(mapNumber.isEmpty()){
            super.obj.put("name", "User_L"+1);
        }
    }//this function defines the name of the JSON key "name", not the name under which
    //the JSON file is to be saved

    /***
     * Hier werden die Belegungen der Tasten definiert:
     *
     * STRG + O - Öffnen einer bestehenden Leveldatei
     *
     * STRG + S - Speichern eines erstellten Levels (falls relevante Elemente platziert)
     *
     * P - Spawnpoint Spieler 1 platzieren
     *
     * B - Spawnpoint Spieler 2 platzieren
     *
     * M - Spawnpoint Monster platzieren
     *
     * K - Spawnpoint der Kirsche platzieren
     *
     * D - Diamanten platzieren
     *
     * G - Geldsack platzieren
     *
     * V - vertikalen Tunnel setzten
     *
     * H - horizontalen Tunnel setzten
     *
     * S - Tunnelknoten (Space) setzten
     *
     * R - gesetztes Element entfernen
     */
    void keybindings(){

        KeyStroke ctrlO = KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( ctrlO, ctrlO);// add new keybinding
        getActionMap().put(ctrlO, new AbstractAction() {//ctrl+s for saving the map at the end
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavedLevel();
            }
        });

        KeyStroke ctrlS = KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( ctrlS, ctrlS);// add new keybinding
        getActionMap().put(ctrlS, new AbstractAction() {//ctrl+s for saving the map at the end
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndSave();
            }
        });
        setFocusable(true);

        addKeyBinding(this, "P", new AbstractAction() {//Spieler1 Spawn point
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        ArrayList<Integer> coordinations = new ArrayList<>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(P1[0] >= 0 && P1[1] >= 0){
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if(LevelEditor.super.obj.has("pos_tun") && ((LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") && duplicate( LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its not on a tunnel
                            LevelEditor.super.obj.put("spawn_p1", coordinations);
                            spawn_sp1 = new int[]{P1[0], P1[1]};
                        }}
                    }
                };
                addMouseListener(globalMouseAdapter);
            }
        });


        addKeyBinding(this, "B", new AbstractAction() {//Spieler2 Spawn point
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        ArrayList<Integer> coordinations = new ArrayList<>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = LevelEditor.super.getFieldOf(new int[]{x, y});
                            if(P1[0] >= 0 && P1[1] >= 0){
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if(LevelEditor.super.obj.has("pos_tun") && ((LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") && duplicate( LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its not on a tunnel
                            LevelEditor.super.obj.put("spawn_p2", coordinations);
                            spawn_sp2 = new int[]{P1[0], P1[1]};
                        }}
                    }
                };
                addMouseListener((globalMouseAdapter));
            }});

        addKeyBinding(this, "M", new AbstractAction() {//Monster Spawn point
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        ArrayList<Integer> coordinations = new ArrayList<>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(P1[0] >= 0 && P1[1] >= 0){
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);

                        if(LevelEditor.super.obj.has("pos_tun") && (LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") &&  (duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) ||
                                (LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && duplicate(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its not on a tunnel
                            LevelEditor.super.obj.put("spawn_mon", coordinations);
                            spawn_monster = new int[]{P1[0], P1[1]};
                        }}
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});

        addKeyBinding(this, "K", new AbstractAction() {//Kirsche Spawn Point
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        ArrayList<Integer> coordinations = new ArrayList<>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(P1[0] >= 0 && P1[1] >= 0){
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if((!LevelEditor.super.obj.has("pos_money") || !duplicate(LevelEditor.super.obj.getJSONArray("pos_money"), new JSONArray(coordinations)) ) && (!LevelEditor.super.obj.has("pos_diam") || !duplicate(LevelEditor.super.obj.getJSONArray("pos_diam"),
                                new JSONArray(coordinations)))){//make sure its not overlapping with another item
                            LevelEditor.super.obj.put("spawn_cherry", new JSONArray(coordinations));
                        }}
                    }
                };
                addMouseListener(globalMouseAdapter);
            }
        });


        addKeyBinding(this, "D", new AbstractAction() {//Diamant
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();
                        int[] field = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(field[0] >= 0 && field[1] >= 0){
                        JSONArray D = new JSONArray();
                        D.put(new JSONArray(field));

                        if((!LevelEditor.super.obj.has("pos_money") || !duplicate(LevelEditor.super.obj.getJSONArray("pos_money"),  (JSONArray)D.get(0)) ) && (!LevelEditor.super.obj.has("spawn_cherry") ||
                                ((int)LevelEditor.super.obj.getJSONArray("spawn_cherry").get(0) != LevelEditor.super.getFieldOf(new int[]{x, y})[0] || (int)LevelEditor.super.obj.getJSONArray("spawn_cherry").get(1) != LevelEditor.super.getFieldOf(new int[]{x, y})[1])
                        )){//make sure its not overlapping with another item

                            if(LevelEditor.super.obj.has("pos_diam")){
                                JSONArray temp = LevelEditor.super.obj.getJSONArray("pos_diam");
                                for(int i = 0; i < LevelEditor.super.obj.getJSONArray("pos_diam").length(); i++){
                                    D.put(temp.get(i));
                                }
                                LevelEditor.super.obj.remove("pos_diam");
                            }
                            LevelEditor.super.obj.put("pos_diam", D);
                        }}
                    }
                };
                addMouseListener(globalMouseAdapter);
            }
        });

        addKeyBinding(this, "G", new AbstractAction() {//Geldsack
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();
                        int[] field = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(field[0] >= 0 && field[1] >= 0){
                        JSONArray D = new JSONArray();
                        D.put(new JSONArray(field));
                        if((!LevelEditor.super.obj.has("pos_diam") || !duplicate(LevelEditor.super.obj.getJSONArray("pos_diam"), (JSONArray)D.get(0)) ) && (!LevelEditor.super.obj.has("spawn_cherry") ||
                                ((int)LevelEditor.super.obj.getJSONArray("spawn_cherry").get(0) != LevelEditor.super.getFieldOf(new int[]{x, y})[0] || (int)LevelEditor.super.obj.getJSONArray("spawn_cherry").get(1) != LevelEditor.super.getFieldOf(new int[]{x, y})[1])
                        ))//make sure its not overlapping with another item
                        {
                            if(LevelEditor.super.obj.has("pos_money")){
                                JSONArray temp = LevelEditor.super.obj.getJSONArray("pos_money");
                                for(int i = 0; i < LevelEditor.super.obj.getJSONArray("pos_money").length(); i++){
                                    D.put(temp.get(i));
                                }
                                LevelEditor.super.obj.remove("pos_money");
                            }
                            LevelEditor.super.obj.put("pos_money", D);
                        }}
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});

        addKeyBinding(this, "V", new AbstractAction() {//Tunnel Vertikal
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();
                        JSONObject temp = new JSONObject();
                        JSONArray tempV = new JSONArray();
                        JSONArray tempH = new JSONArray();
                        JSONArray tempS = new JSONArray();
                        int[] field = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(field[0] >= 0 && field[1] >= 0){
                        tempV.put(new JSONArray(field));
                        if(LevelEditor.super.obj.has("pos_tun")){

                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") &&  i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++){
                                tempV.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++){
                                if(!duplicate(tempV, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))){
                                    tempH.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}}


                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++){
                                if(!duplicate(tempV, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))){
                                    tempS.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").get(i));}}
                            LevelEditor.super.obj.remove("pos_tun");
                        }

                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        LevelEditor.super.obj.put("pos_tun", temp);
                        }
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});

        addKeyBinding(this, "H", new AbstractAction() {//Tunnel Horizontal
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();

                        JSONObject temp = new JSONObject();
                        JSONArray tempV = new JSONArray();
                        JSONArray tempH = new JSONArray();
                        JSONArray tempS = new JSONArray();
                        int[] field = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(field[0] >= 0 && field[1] >= 0){

                        tempH.put(new JSONArray(field));
                        if (LevelEditor.super.obj.has("pos_tun")) {

                            for (int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                tempH.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));

                            for (int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++) {
                                if (!duplicate(tempH, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))) {
                                    tempV.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i));
                                }
                            }

                            for (int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++) {
                                if (!duplicate(tempH, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))) {
                                    tempS.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i));
                                }
                            }
                            LevelEditor.super.obj.remove("pos_tun");
                        }
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        LevelEditor.super.obj.put("pos_tun", temp);
                    }
                    }
                };
                addMouseListener(globalMouseAdapter);


            }});


        addKeyBinding(this, "S", new AbstractAction() {//Tunnel Space
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();
                        int[] field = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(field[0] >= 0 && field[1] >= 0){
                        JSONObject temp = new JSONObject();
                        JSONArray tempV = new JSONArray();
                        JSONArray tempH = new JSONArray();
                        JSONArray tempS = new JSONArray();

                        tempS.put(new JSONArray(field));
                        if(LevelEditor.super.obj.has("pos_tun")){
                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++)
                                tempS.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").get(i));

                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++)
                                if(!duplicate(tempS, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))){
                                    tempV.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("horizontal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                if(!duplicate(tempS, LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))){
                                    tempH.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}

                            LevelEditor.super.obj.remove("pos_tun");
                        }
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        LevelEditor.super.obj.put("pos_tun", temp);
                        }
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});


        addKeyBinding(this, "R", new AbstractAction() {//Remove an element
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int x = e.getX();
                        int y = e.getY();
                        int[] deleted = LevelEditor.super.getFieldOf(new int[]{x, y});
                        if(deleted[0] >= 0 && deleted[1] >= 0){
                        if(LevelEditor.super.obj.has("pos_tun")){
                            JSONObject temp = new JSONObject();
                            JSONArray tempV = new JSONArray();
                            JSONArray tempH = new JSONArray();
                            JSONArray tempS = new JSONArray();
                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("space") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++)
                                if(!(deleted[0] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))[0] &&
                                        deleted[1] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))[1])){
                                    tempS.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("space").get(i));}

                            for(int i = 0; LevelEditor.super.obj.getJSONObject("pos_tun").has("vertikal") && i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++)
                                if(!(deleted[0] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))[0] &&
                                        deleted[1] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))[1])){
                                    tempV.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; i < LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                if(!(deleted[0] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))[0] &&
                                        deleted[1] == toArray(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))[1])){
                                    tempH.put(LevelEditor.super.obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}

                        LevelEditor.super.obj.remove("pos_tun");
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        LevelEditor.super.obj.put("pos_tun", temp);
                        }
                        if(LevelEditor.super.obj.has("spawn_p1") && deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("spawn_p1"))[0] && deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("spawn_p1"))[1]) {
                            LevelEditor.super.obj.remove("spawn_p1");
                            spawn_sp1 = null;
                        }
                        if(LevelEditor.super.obj.has("spawn_p2") && deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("spawn_p2"))[0] && deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("spawn_p2"))[1]) {
                            LevelEditor.super.obj.remove("spawn_p2");
                            spawn_sp2 = null;
                        }
                        if(LevelEditor.super.obj.has("spawn_mon") && deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("spawn_mon"))[0] && deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("spawn_mon"))[1]) {
                            LevelEditor.super.obj.remove("spawn_mon");
                            spawn_monster = null;
                        }
                        if(LevelEditor.super.obj.has("spawn_cherry") && deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("spawn_cherry"))[0] && deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("spawn_cherry"))[1]) {
                            LevelEditor.super.obj.remove("spawn_cherry");
                        }

                        if(LevelEditor.super.obj.has("pos_diam")) {
                            JSONArray tempDiamant = new JSONArray();
                            for(int i = 0; i < LevelEditor.super.obj.getJSONArray("pos_diam").length(); i++) {
                                if (!(deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("pos_diam").getJSONArray(i))[0] &&
                                        deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("pos_diam").getJSONArray(i))[1])) {
                                    tempDiamant.put(LevelEditor.super.obj.getJSONArray("pos_diam").getJSONArray(i));
                                }
                            }
                        LevelEditor.super.obj.put("pos_diam", tempDiamant);
                        }
                            if(LevelEditor.super.obj.has("pos_money")) {
                                JSONArray tempGeld = new JSONArray();
                                for(int i = 0; i < LevelEditor.super.obj.getJSONArray("pos_money").length(); i++) {
                                    if (!(deleted[0] == toArray(LevelEditor.super.obj.getJSONArray("pos_money").getJSONArray(i))[0] &&
                                            deleted[1] == toArray(LevelEditor.super.obj.getJSONArray("pos_money").getJSONArray(i))[1])) {
                                        tempGeld.put(LevelEditor.super.obj.getJSONArray("pos_money").getJSONArray(i));
                                    }
                                }
                                LevelEditor.super.obj.put("pos_money", tempGeld);
                            }
                    }
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});

    }



    public boolean duplicate(JSONArray temp, JSONArray coords){

        for(int i = 0; i < temp.length(); i++){
            if(toArray(coords)[0]
                    == toArray(temp.getJSONArray(i))[0]
                    && toArray(coords)[1] ==
                    toArray(temp.getJSONArray(i))[1]){
                return true;
            }
        }
        return false;
    }

    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
}





