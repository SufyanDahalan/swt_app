package Menuefuehrung;

import Spielbereitstellug.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import Spielverlauf.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.regex.*;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LevelEditor extends JPanel implements MouseListener {
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    MouseAdapter globalMouseAdapter;

    private int[] playground_size = { 15, 10 };

    private int[] spawn_monster;
    private int[] spawn_sp1;
    private int[] spawn_sp2;
    private ArrayList<Diamant> diamanten;
    private ArrayList<Geldsack> geldsaecke;
    private ArrayList<Tunnel> tunnel;
    private Kirsche kirsche;
    private final String levelfolder_name = "bin/level/"; // ./level/level-01.json ...

    JSONObject obj = new JSONObject();
    // JSONArray pos_diam;

    private final String skinfolder_name = "bin/skins/"; // ./skin/sink_original.json,...
    private final String skinName = "original_skin"; // Skinnname
    // static content
    private final Skin current_skin;
    private int field_size;
    // private int old_field_size;
    private final double[] border = { 0.4, 0.2 }; // Wandstärke x,y
    private final double topbarHeight = 1; // Faktor von Feldgröße

    public /* static */ void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);// add new keybinding
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }


    public LevelEditor() {
        current_skin = new Skin(new File(skinfolder_name), skinName);
        keybindings();
        diamanten = new ArrayList<Diamant>();
        geldsaecke = new ArrayList<Geldsack>();
        tunnel = new ArrayList<Tunnel>();

        Name();
        obj.put("pg_size", playground_size);
        // Skin current_skin = new Skin(new File(skinfolder_name), skinName); // Loades
        // original_skin.png and original.json from skins/
        refreshSizing();

    }

    public void validateAndSave() {// can be called with ctrl+s


        if (obj.has("name") && obj.has("pg_size") && obj.has("spawn_p1") && obj.has("spawn_p2") && obj.has("spawn_mon")
                && obj.has("spawn_cherry") && obj.has("pos_diam") && obj.has("pos_money") && obj.has("pos_tun")) {

            ArrayList<Integer> mapNumber = new ArrayList<Integer>();
            String[] maps = new File(levelfolder_name).list();
            for(String map : maps){
                map = map.substring(0, 8);
                System.out.println("names of maps: "+map);

                Matcher matcher = lastIntPattern.matcher(map);
                if (matcher.find()) {String someNumberStr = matcher.group(1);
                    mapNumber.add(Integer.parseInt(someNumberStr));}
            }
            String name;
            DecimalFormat formatter = new DecimalFormat("00");
            // int a = Collections.max(mapNumber)+1;
            // String aFormatted = formatter.format(a);

            if(!mapNumber.isEmpty()){
                int a = Collections.max(mapNumber)+1;
                String aFormatted = formatter.format(a);
                name = "level_"+aFormatted+".json";
            }else{
                name = "level_01.json";
            }


            try {
                System.out.println("name: "+levelfolder_name+"+"+name);
                Files.write(Paths.get(levelfolder_name + name), obj.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }




    private void refreshSizing() {//TODO: black borders to the right and top should not be rendered

        // Speichere Änderung
        // old_field_size = field_size;
        // setze Feldgröße

        Dimension d = this.getSize();

        int felderX = playground_size[0];
        int felderY = playground_size[1];

        if(d.width == 0 || d.height == 0)
            d = new Dimension(500, 500);

        int w_temp_size = (int)((double)d.width / ( (double)felderX + ( 2*border[0]) ));
        int h_temp_size = (int)((double)d.height / ( (double)felderY + ( 2*border[1]) + topbarHeight ));

        field_size = Math.min(w_temp_size, h_temp_size);

    }

    protected void paintComponent(Graphics g) {//TODO: suggestion:-  add shortcuts to black top and right "bar"

        super.paintComponent(g);

        refreshSizing();

        // Prepering

        int[] borderOffset = getBorderOffset();

        // Zeichne Hintergrund
        setBackground(Color.black);
        BufferedImage backgroundImg = current_skin.getImage("backg_typ1", field_size);
        TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(slatetp);
        int[] pg_size = playground_size;
        g2d.fillRect(0, getTopBarHeight(), field_size*pg_size[0]+borderOffset[0]*2, field_size*pg_size[1]+borderOffset[1]*2);




        // Zeichne Tunnel
        BufferedImage horzTunImg = current_skin.getImage("tunnel_hori", field_size);
        BufferedImage vertTunImg = current_skin.getImage("tunnel_vert", field_size);
        BufferedImage spacTunImg = current_skin.getImage("tunnel_space", field_size);

        if(obj.has("pos_tun")){

            JSONObject kind_tun = obj.getJSONObject("pos_tun");

            // Set vertical tunnel
            if(kind_tun.has("vertikal")){
                JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");

                for (int i = 0; i < pos_tun_vertikal.length(); i++) {
                    int[] single_tunnel = toArray(pos_tun_vertikal.getJSONArray(i));
                    tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.VERTICAL, current_skin));
                }
                if(kind_tun.has("horizontal")){
                    JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");

                    for (int i = 0; i < pos_tun_horizontal.length(); i++) {

                        int[] single_tunnel = toArray(pos_tun_horizontal.getJSONArray(i));

                        tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.HORIZONTAL, current_skin));
                    }
                }

                if(kind_tun.has("space")){

                    JSONArray pos_tun_space = kind_tun.getJSONArray("space");

                    for (int i = 0; i < pos_tun_space.length(); i++) {

                        int[] single_tunnel = toArray(pos_tun_space.getJSONArray(i));
                        tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.SPACE, current_skin));
                    }
                }
                for (int i = 0; i < tunnel.size(); i++) {

                    Tunnel single_item = tunnel.get(i);

                    BufferedImage unscaledImg;

                    if (single_item.getTyp().equals(TUNNELTYP.HORIZONTAL))
                        unscaledImg = horzTunImg;
                    else if (single_item.getTyp().equals(TUNNELTYP.VERTICAL))
                        unscaledImg = vertTunImg;
                    else
                        unscaledImg = spacTunImg;

                    int[] field = single_item.getField();
                    int[] middle = getCenterOf(field);
                    int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
                    int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

                    g.drawImage(unscaledImg, x_pixel, y_pixel, null);
                }
                tunnel.clear();
            }
        }

        // Zeichne Spieler
        if(spawn_sp1 != null) {
            Animation ani_right = current_skin.getAnimation("digger_red_right");
            BufferedImage sp1Img = null;
            sp1Img = ani_right.nextFrame(field_size);
            int x_pixel = spawn_sp1[0] - (sp1Img.getWidth() / 2);
            int y_pixel = spawn_sp1[1] - (sp1Img.getHeight() / 2);
            g.drawImage(sp1Img, x_pixel, y_pixel, null);
        }
        if(spawn_sp2 != null) {
            Animation ani_left = current_skin.getAnimation("digger_gre_left");
            BufferedImage spImg = null;
            spImg = ani_left.nextFrame(field_size);
            int x_pixel = spawn_sp2[0] - (spImg.getWidth() / 2);
            int y_pixel = spawn_sp2[1] - (spImg.getHeight() / 2);
            g.drawImage(spImg, x_pixel, y_pixel, null);
        }

        // Zeichne Diamanten
        BufferedImage diamImg = current_skin.getImage("diamond", field_size);
        if((diamanten != null) && !diamanten.isEmpty()){
            for (int i = 0; i < diamanten.size(); i++) {
                Diamant single_item = diamanten.get(i);
                int[] field = single_item.getField();
                int[] middle = getCenterOf(field);
                int x_pixel = middle[0] - (diamImg.getWidth() / 2);
                int y_pixel = middle[1] - (diamImg.getHeight() / 2);

                g.drawImage(diamImg, x_pixel, y_pixel, null);
            }
        }

        // Zeichne Geldsäcke
        BufferedImage moneyPodImg = current_skin.getImage("money_static", field_size);
        if((geldsaecke != null) && !geldsaecke.isEmpty()){
            for (int i = 0; i < geldsaecke.size(); i++) {
                Geldsack single_item = geldsaecke.get(i);
                int[] field = single_item.getField();
                int[] middle = getCenterOf(field);
                int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
                int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

                g.drawImage(moneyPodImg, x_pixel, y_pixel, null);
            }
        }

        // Monster
        Animation ani_nobbin = current_skin.getAnimation("nobbin");
        BufferedImage nobbinImg = ani_nobbin.nextFrame(field_size);
        if(spawn_monster != null){
            int x_pixel = spawn_monster[0] - (nobbinImg.getWidth() / 2);
            int y_pixel = spawn_monster[1] - (nobbinImg.getHeight() / 2);

            g.drawImage(nobbinImg, x_pixel, y_pixel, null);
        }

        // Kirsche
        if(kirsche != null){
            BufferedImage kirscheImg = current_skin.getImage("cherry", field_size);

            int[] field = kirsche.getField();

            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (kirscheImg.getWidth() / 2);
            int y_pixel = middle[1] - (kirscheImg.getHeight() / 2);

            g.drawImage(kirscheImg, x_pixel, y_pixel, null);

        }

        repaint();
    }

    //originally getCenterOf() in Spiel.java
    int[] PixelToInt(int[] pixelPos) {
        int[]fp = new int[2];
        int[] borderOffset = getBorderOffset();
        fp[0] = ((pixelPos[0]-(field_size/2)-borderOffset[0])/field_size)+1;
        fp[1] = ((pixelPos[1]-(field_size/2)-borderOffset[1])/field_size)+1;
        return fp;
    }

    int[] getCenterOf(int[] fp) {

        int x_field = fp[0] - 1;
        int y_field = fp[1] - 1;

        int[] borderOffset = getBorderOffset();

        int[] pixelPos = new int[2];

        pixelPos[0] = x_field * field_size + (field_size / 2) + borderOffset[0];
        pixelPos[1] = y_field * field_size + (field_size / 2) + borderOffset[1] + getTopBarHeight();

        return pixelPos;
    }



    void Name(){
        String[] maps = new File(levelfolder_name).list();
        ArrayList<Integer> mapNumber = new ArrayList<Integer>();

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
                // latestName.substring(latestName.length()-1);
                Matcher matcher = lastIntPattern.matcher(latestName);
                if (matcher.find()) {
                    String someNumberStr = matcher.group(1);
                    mapNumber.add(Integer.parseInt(someNumberStr));
                }
            }
        }
        if(!mapNumber.isEmpty()){
            obj.put("name", "User_L"+(Collections.max(mapNumber)+1));
        }else if(mapNumber.isEmpty()){
            obj.put("name", "User_L"+1);
        }
    }
    //this function defines the name of the JSON key "name", not the name under which
    //the JSON file is to be saved


    void keybindings(){


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
                        ArrayList<Integer> coordinations = new ArrayList<Integer>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = PixelToInt(new int[]{x, y});
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if(obj.has("pos_tun") && ((obj.getJSONObject("pos_tun").has("vertikal") && duplicate( obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) || 
                        (obj.getJSONObject("pos_tun").has("horizontal") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) || 
                        (obj.getJSONObject("pos_tun").has("space") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its on a tunnel
                        obj.put("spawn_p1", coordinations);
                        spawn_sp1 = new int[]{x, y};
                    }
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
                        ArrayList<Integer> coordinations = new ArrayList<Integer>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = PixelToInt(new int[]{x, y});
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if(obj.has("pos_tun") && ((obj.getJSONObject("pos_tun").has("vertikal") && duplicate( obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) || 
                        (obj.getJSONObject("pos_tun").has("horizontal") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) || 
                        (obj.getJSONObject("pos_tun").has("space") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its on a tunnel
                            obj.put("spawn_p2", coordinations);
                            spawn_sp2 = new int[]{x, y};
                    }
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
                    ArrayList<Integer> coordinations = new ArrayList<Integer>();
                    int x = e.getX();
                    int y = e.getY();
                    int[] P1 = PixelToInt(new int[]{x, y});
                    coordinations.add(P1[0]);
                    coordinations.add(P1[1]);
                    
                    if(obj.has("pos_tun") && (obj.getJSONObject("pos_tun").has("vertikal") &&  (duplicate(obj.getJSONObject("pos_tun").getJSONArray("vertikal"), new JSONArray(coordinations))) || 
                    (obj.getJSONObject("pos_tun").has("horizontal") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("horizontal"), new JSONArray(coordinations))) || 
                    (obj.getJSONObject("pos_tun").has("space") && duplicate(obj.getJSONObject("pos_tun").getJSONArray("space"), new JSONArray(coordinations))))){//make sure its on a tunnel
                        obj.put("spawn_mon", coordinations);
                        spawn_monster = new int[]{x, y};
                        }
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
                        ArrayList<Integer> coordinations = new ArrayList<Integer>();
                        int x = e.getX();
                        int y = e.getY();
                        int[] P1 = PixelToInt(new int[]{x, y});
                        coordinations.add(P1[0]);
                        coordinations.add(P1[1]);
                        if((!obj.has("pos_money") || !duplicate(obj.getJSONArray("pos_money"), new JSONArray(coordinations)) ) && (!obj.has("pos_diam") || !duplicate(obj.getJSONArray("pos_diam"),
                          new JSONArray(coordinations)))){//make sure its not overlapping with another item
                            obj.put("spawn_cherry", new JSONArray(coordinations));
                            kirsche = new Kirsche(PixelToInt(new int[]{x, y}), current_skin);
                    }
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
                        JSONArray D = new JSONArray();
                        D.put(new JSONArray(PixelToInt(new int[]{x, y})));
                        
                        
                        if((!obj.has("pos_money") || !duplicate(obj.getJSONArray("pos_money"),  (JSONArray)D.get(0)) ) && (!obj.has("spawn_cherry") || 
                        ((int)obj.getJSONArray("spawn_cherry").get(0) != PixelToInt(new int[]{x, y})[0] || (int)obj.getJSONArray("spawn_cherry").get(1) != PixelToInt(new int[]{x, y})[1])
                        )){//make sure its not overlapping with another item
                            
                        diamanten.add(new Diamant(PixelToInt(new int[]{x, y}), current_skin));
                        if(obj.has("pos_diam")){
                            JSONArray temp = obj.getJSONArray("pos_diam");
                            for(int i = 0; i < obj.getJSONArray("pos_diam").length(); i++){
                                D.put(temp.get(i));
                            }
                            obj.remove("pos_diam");
                        }
                        obj.put("pos_diam", D);
                    }
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
                        JSONArray D = new JSONArray();
                        D.put(new JSONArray(PixelToInt(new int[]{x, y})));
                        if((!obj.has("pos_diam") || !duplicate(obj.getJSONArray("pos_diam"), (JSONArray)D.get(0)) ) && (!obj.has("spawn_cherry") || 
                        ((int)obj.getJSONArray("spawn_cherry").get(0) != PixelToInt(new int[]{x, y})[0] || (int)obj.getJSONArray("spawn_cherry").get(1) != PixelToInt(new int[]{x, y})[1])
                        ))//make sure its not overlapping with another item
                            {
                                if(obj.has("pos_money")){
                                    JSONArray temp = obj.getJSONArray("pos_money");
                                    for(int i = 0; i < obj.getJSONArray("pos_money").length(); i++){
                                        D.put(temp.get(i));
                                    }
                                    obj.remove("pos_money");
                                }
                                geldsaecke.add(new Geldsack(PixelToInt(new int[]{x, y}), current_skin));
                                obj.put("pos_money", D);
                            }
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

                        tempV.put(new JSONArray((PixelToInt(new int[]{x, y}))));
                        if(obj.has("pos_tun")){

                            for(int i = 0; obj.getJSONObject("pos_tun").has("vertikal") &&  i < obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++){//TODO: add conditions like this one
                                tempV.put(obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; obj.getJSONObject("pos_tun").has("horizontal") && i < obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++){
                                if(!duplicate(tempV, obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))){
                                    tempH.put(obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}}


                            for(int i = 0; obj.getJSONObject("pos_tun").has("space") && i < obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++){
                                if(!duplicate(tempV, obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))){
                                    tempS.put(obj.getJSONObject("pos_tun").getJSONArray("space").get(i));}}
                            obj.remove("pos_tun");
                        }



                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        obj.put("pos_tun", temp);
                    }
                };
                addMouseListener(globalMouseAdapter);


            }});//TODO:check for redundancy in and across Tunnel types

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

                        tempH.put(new JSONArray((PixelToInt(new int[]{x, y}))));
                        if(obj.has("pos_tun")){

                            for(int i = 0; obj.getJSONObject("pos_tun").has("horizontal") && i < obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                tempH.put(obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));

                            for(int i = 0; obj.getJSONObject("pos_tun").has("vertikal") && i < obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++){
                                if(!duplicate(tempH, obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))){
                                    tempV.put(obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i));}}

                            for(int i = 0; obj.getJSONObject("pos_tun").has("space") && i < obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++){
                                if(!duplicate(tempH, obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))){
                                    tempS.put(obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i));
                                }
                            }
                            obj.remove("pos_tun");
                        }
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        obj.put("pos_tun", temp);
                    }
                };
                addMouseListener(globalMouseAdapter);


            }});//TODO:check for redundancy in and across Tunnel types


        addKeyBinding(this, "S", new AbstractAction() {//Tunnel Space
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

                        tempS.put(new JSONArray((PixelToInt(new int[]{x, y}))));
                        if(obj.has("pos_tun")){
                            for(int i = 0; obj.getJSONObject("pos_tun").has("space") && i < obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++)
                                tempS.put(obj.getJSONObject("pos_tun").getJSONArray("space").get(i));

                            for(int i = 0; obj.getJSONObject("pos_tun").has("vertikal") && i < obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++)
                                if(!duplicate(tempS, obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))){
                                    tempV.put(obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; obj.getJSONObject("pos_tun").has("horizontal") && i < obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                if(!duplicate(tempS, obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))){
                                    tempH.put(obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}

                            obj.remove("pos_tun");
                        }
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        obj.put("pos_tun", temp);
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});//TODO:check for redundancy in and across Tunnel types




        addKeyBinding(this, "R", new AbstractAction() {//Remove a Tunnel
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMouseListener(globalMouseAdapter);//Nullify previous Mousebinding
                globalMouseAdapter = new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        int[] deletedTunnel = PixelToInt(new int[]{e.getX(), e.getY()});
                        JSONObject temp = new JSONObject();
                        JSONArray tempV = new JSONArray();
                        JSONArray tempH = new JSONArray();
                        JSONArray tempS = new JSONArray();

                        if(obj.has("pos_tun")){
                            for(int i = 0; obj.getJSONObject("pos_tun").has("space") && i < obj.getJSONObject("pos_tun").getJSONArray("space").length(); i++)

                                if(!(deletedTunnel[0] == toArray(obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))[0] &&
                                        deletedTunnel[1] == toArray(obj.getJSONObject("pos_tun").getJSONArray("space").getJSONArray(i))[1])){
                                    tempS.put(obj.getJSONObject("pos_tun").getJSONArray("space").get(i));}

                            for(int i = 0; obj.getJSONObject("pos_tun").has("vertikal") && i < obj.getJSONObject("pos_tun").getJSONArray("vertikal").length(); i++)
                                if(!(deletedTunnel[0] == toArray(obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))[0] &&
                                        deletedTunnel[1] == toArray(obj.getJSONObject("pos_tun").getJSONArray("vertikal").getJSONArray(i))[1])){
                                    tempV.put(obj.getJSONObject("pos_tun").getJSONArray("vertikal").get(i));}

                            for(int i = 0; i < obj.getJSONObject("pos_tun").getJSONArray("horizontal").length(); i++)
                                if(!(deletedTunnel[0] == toArray(obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))[0] &&
                                        deletedTunnel[1] == toArray(obj.getJSONObject("pos_tun").getJSONArray("horizontal").getJSONArray(i))[1])){
                                    tempH.put(obj.getJSONObject("pos_tun").getJSONArray("horizontal").get(i));}

                            obj.remove("pos_tun");
                        }
                        temp.put("vertikal", tempV);
                        temp.put("horizontal", tempH);
                        temp.put("space", tempS);
                        obj.put("pos_tun", temp);
                    }
                };
                addMouseListener(globalMouseAdapter);
            }});//TODO:check for redundancy in and across Tunnel types

    }

    public int[] getBorderOffset(){
        int[] borderOffset = new int[2];
        borderOffset[0] = (int)(field_size*border[0]);
        borderOffset[1] = (int)(field_size*border[1]);
        return borderOffset;
    }

    public int getTopBarHeight(){
        return (int)(field_size*topbarHeight);
    }

    private int[] toArray(JSONArray ja){
        int[] ia = new int[2];
        ia[0] = ja.getInt(0);
        ia[1] = ja.getInt(1);
        return ia;
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





