package Spielbereitstellug;

import Menuefuehrung.Filesystem;
import Spielverlauf.Skin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/***
 * Klasse zum Zeichnen des Spielfeldes + Inhalt
 */
public abstract class Render extends JPanel implements Filesystem {

    protected int field_size;
    protected int old_field_size;
    protected Skin current_skin;
    protected boolean devFrames;
    protected final double[] border = {0.4, 0.2}; // Wandstärke x,y
    protected final double topbarHeight = 1; // Faktor von Feldgröße
    protected JSONObject obj;

    /***
     * Konstruktor liest Skin-Datei ein
     */
    public Render(Skin skin)  {

        // initialisiere Skin
        current_skin = skin; // Loades original_skin.png and original.json from skins/

        obj = new JSONObject();

    }

    /***
     * Methode prüft Größenverhältnis der Spiel-Elemente und zeichnet Tunnel, Diamanten, Geldsäcke, ...
     * @param g Graphics Objekt, auf dem alles Angezeigt wird
     */
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        refreshSizing();

        // Prepering

        int[] borderOffset = getBorderOffset();

        // Zeichne Hintergrund

        setBackground(Color.black);
        BufferedImage backgroundImg = current_skin.getImage("backg_typ1", field_size);;

        if(obj.has("bonus")){
            boolean bonusMode = obj.getBoolean("bonus");
            if(bonusMode)
                backgroundImg = current_skin.getImage("backg_typ2", field_size);
        }

        TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(slatetp);
        int[] pg_size = toArray(obj.getJSONArray("pg_size"));
        g2d.fillRect(0, getTopBarHeight(), field_size * pg_size[0] + borderOffset[0] * 2, field_size * pg_size[1] + borderOffset[1] * 2);

        if(obj.has("pos_tun")) {
            JSONObject tunnel = obj.getJSONObject("pos_tun");
            JSONArray vTun = tunnel.getJSONArray("vertikal");
            JSONArray hTun = tunnel.getJSONArray("horizontal");
            JSONArray sTun = tunnel.getJSONArray("space");

            if (tunnel.has("vertikal")) {
                BufferedImage unscaledImg = current_skin.getImage("tunnel_vert", field_size);
                for (int i = 0; i < vTun.length(); i++) {

                    int[] field = toArray(vTun.getJSONArray(i));
                    int[] middle = getCenterOf(field);
                    int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
                    int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

                    g.drawImage(unscaledImg, x_pixel, y_pixel, null);

                    if (devFrames) {
                        g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
                        g.setColor(Color.RED);
                    }
                }
            }
            if (tunnel.has("horizontal")) {
                BufferedImage unscaledImg = current_skin.getImage("tunnel_hori", field_size);
                for (int i = 0; i < hTun.length(); i++) {


                    int[] field = toArray(hTun.getJSONArray(i));
                    int[] middle = getCenterOf(field);
                    int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
                    int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

                    g.drawImage(unscaledImg, x_pixel, y_pixel, null);

                    if (devFrames) {
                        g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
                        g.setColor(Color.RED);
                    }
                }
            }
            if (tunnel.has("space")) {
                BufferedImage unscaledImg = current_skin.getImage("tunnel_space", field_size);
                for (int i = 0; i < sTun.length(); i++) {


                    int[] field = toArray(sTun.getJSONArray(i));
                    int[] middle = getCenterOf(field);
                    int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
                    int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

                    g.drawImage(unscaledImg, x_pixel, y_pixel, null);

                    if (devFrames) {
                        g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
                        g.setColor(Color.RED);
                    }
                }
            }
        }

        // Zeichne Diamanten
        if (obj.has("pos_diam")) {
            JSONArray diamanten = obj.getJSONArray("pos_diam");

            BufferedImage diamImg = current_skin.getImage("diamond", field_size);
            for (int i = 0; i < diamanten.length(); i++) {
                int[] field = toArray(diamanten.getJSONArray(i));
                int[] middle = getCenterOf(field);
                int x_pixel = middle[0] - (diamImg.getWidth() / 2);
                int y_pixel = middle[1] - (diamImg.getHeight() / 2);

                g.drawImage(diamImg, x_pixel, y_pixel, null);
            }
        }

        // Zeichne Geldsäcke

        if (obj.has("pos_money")) {
            JSONArray geldsaecke = obj.getJSONArray("pos_money");

            BufferedImage moneyPodImg = current_skin.getImage("money_static", field_size);
            for (int i = 0; i < geldsaecke.length(); i++) {

                int[] field = toArray(geldsaecke.getJSONArray(i));
                int[] middle = getCenterOf(field);
                int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
                int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

                g.drawImage(moneyPodImg, x_pixel, y_pixel, null);
            }
        }

        // Kirsche

        if (obj.has("spawn_cherry")) {
            BufferedImage kirscheImg = current_skin.getImage("cherry", field_size);

            int[] field = toArray(obj.getJSONArray("spawn_cherry"));

            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (kirscheImg.getWidth() / 2);
            int y_pixel = middle[1] - (kirscheImg.getHeight() / 2);

            g.drawImage(kirscheImg, x_pixel, y_pixel, null);
        }
    }


    /***
     * Methode berechnet Größenverhältnis, wichtig auch beim Vollbildmodus
     */
    protected void refreshSizing() {

        // Speichere Änderung
        old_field_size = field_size;

        // setze Feldgröße

        Dimension d = this.getSize();


        int felderX = toArray(obj.getJSONArray("pg_size"))[0];
        int felderY = toArray(obj.getJSONArray("pg_size"))[1];

        if(d.width == 0 || d.height == 0)
            d = new Dimension(500, 500);

        int w_temp_size = (int)((double)d.width / ( (double)felderX + ( 2*border[0]) ));
        int h_temp_size = (int)((double)d.height / ( (double)felderY + ( 2*border[1]) + topbarHeight ));

        field_size = Math.min(w_temp_size, h_temp_size);
    }

    /***
     * Getter für das Spielfeld-Raster
     * @param pos Spieler- oder Objektposition
     * @return  Rasterkoordinaten
     */
    public int[] getFieldOf(int[] pos){

        int[] borderOffest = getBorderOffset();

        int[] fp = new int[2];

        if((pos[0]-borderOffest[0]) < 0)
            fp[0] = -1;
        else
            fp[0] = ((pos[0]-borderOffest[0])/field_size) + 1;

        if((pos[1]-borderOffest[1]-getTopBarHeight()) < 0)
            fp[1] = -1;
        else
            fp[1] = ((pos[1]-borderOffest[1]-getTopBarHeight())/field_size) + 1;

        return fp;
    }

    /***
     * Getter für die Spielfeldgröße
     * @return Feldgröße
     */
    public int getFieldSize() {
        return field_size;
    }

    protected int getTopBarHeight(){
        return (int)(field_size*topbarHeight);
    }

    protected int[] getBorderOffset(){

        int[] borderOffset = new int[2];
        borderOffset[0] = (int)(field_size*border[0]);
        borderOffset[1] = (int)(field_size*border[1]);

        return borderOffset;
    }

    protected int[] getCenterOf(int[] fp) {

        int x_field = fp[0] - 1;
        int y_field = fp[1] - 1;

        int[] borderOffset = getBorderOffset();

        int[] pixelPos = new int[2];

        pixelPos[0] = x_field * field_size + (field_size / 2) + borderOffset[0];
        pixelPos[1] = y_field * field_size + (field_size / 2) + borderOffset[1] + getTopBarHeight();

        return pixelPos;
    }

    protected int[] toArray(JSONArray ja){

        int[] ia = new int[2];

        ia[0] = ja.getInt(0);
        ia[1] = ja.getInt(1);

        return ia;
    }

}
