package Spielbereitstellug;

import Spielverlauf.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Render extends JPanel {

    protected int field_size;
    protected int old_field_size;
    protected Skin current_skin;
    protected int spielstand;
    protected boolean devFrames;
    protected Spieler sp1;
    protected Spieler sp2;
    private final double[] border = {0.4, 0.2}; // Wandstärke x,y
    private final double topbarHeight = 1; // Faktor von Feldgröße
    protected JSONObject obj;
    protected Level aktuelles_level;

    protected void paintComponent(Graphics g) {

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
        int[] pg_size = aktuelles_level.getMap().getPGSize();
        g2d.fillRect(0, getTopBarHeight(), field_size*pg_size[0]+borderOffset[0]*2, field_size*pg_size[1]+borderOffset[1]*2);

        ArrayList<Tunnel> tunnel = aktuelles_level.getMap().getTunnel();

        for (int i = 0; i < tunnel.size(); i++) {

            Tunnel single_item = tunnel.get(i);

            BufferedImage unscaledImg = current_skin.scale(single_item.getImage(), field_size);

            int[] field = single_item.getField();
            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
            int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

            g.drawImage(unscaledImg, x_pixel, y_pixel, null);

            if(devFrames) {
                g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Zeichne Diamanten

        ArrayList<Diamant> diamanten = aktuelles_level.getMap().getDiamonds();

        for (int i = 0; i < diamanten.size(); i++) {
            Diamant single_item = diamanten.get(i);
            BufferedImage diamImg = current_skin.scale(single_item.getImage(),field_size);

            int[] field = single_item.getField();
            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (diamImg.getWidth() / 2);
            int y_pixel = middle[1] - (diamImg.getHeight() / 2);

            g.drawImage(diamImg, x_pixel, y_pixel, null);

            if(devFrames) {
                g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }


        // Zeichne Geldsäcke

        ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

        for (int i = 0; i < geldsaecke.size(); i++) {

            Geldsack single_item = geldsaecke.get(i);
            BufferedImage moneyPodImg = current_skin.scale(single_item.getImage(),field_size);

            int[] field = single_item.getField();
            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
            int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

            g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

            if(devFrames) {
                g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Monster
        ArrayList<Hobbin> hobbins = aktuelles_level.getMap().getHobbins();
        Animation ani_hobbin_left = current_skin.getAnimation("hobbin_left");
        Animation ani_hobbin_right = current_skin.getAnimation("hobbin_right");

        BufferedImage hobbinImg = null;

        for (int i = 0; i < hobbins.size(); i++) {
            Hobbin single_item = hobbins.get(i);

            if (single_item.getMoveDir() == DIRECTION.RIGHT) {
                hobbinImg = ani_hobbin_right.nextFrame(field_size);
            }
            if (single_item.getMoveDir() == DIRECTION.LEFT) {
                hobbinImg = ani_hobbin_left.nextFrame(field_size);
            }

            int x_pixel = single_item.getPosition()[0] - (hobbinImg.getWidth() / 2);
            int y_pixel = single_item.getPosition()[1] - (hobbinImg.getHeight() / 2);

            g.drawImage(hobbinImg, x_pixel, y_pixel, null);

            if(devFrames) {
                int[] field = getFieldOf(single_item.getPosition());
                g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        Animation ani_nobbin = current_skin.getAnimation("nobbin");
        BufferedImage nobbinImg = ani_nobbin.nextFrame(field_size);

        ArrayList<Nobbin> nobbins = aktuelles_level.getMap().getNobbins();

        for (int i = 0; i < nobbins.size(); i++) {
            Nobbin single_item = nobbins.get(i);

            int x_pixel = single_item.getPosition()[0] - (nobbinImg.getWidth() / 2);
            int y_pixel = single_item.getPosition()[1] - (nobbinImg.getHeight() / 2);

            g.drawImage(nobbinImg, x_pixel, y_pixel, null);

            if(devFrames) {
                int[] field = getFieldOf(single_item.getPosition());
                g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Feuerball

        ArrayList<Feuerball> feuerball = aktuelles_level.getMap().getFeuerball();

        for (int i = 0; i < feuerball.size(); i++) {
            Feuerball single_item = feuerball.get(i);
            BufferedImage pic = current_skin.scale(single_item.getImage(), field_size);

            int[] pos = single_item.getPosition();
            int x_pixel = pos[0] - (pic.getWidth() / 2);
            int y_pixel = pos[1] - (pic.getHeight() / 2);

            g.drawImage(pic, x_pixel, y_pixel, null);

            if(devFrames) {
                int[] field = getFieldOf(single_item.getPosition());
                g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Geld

        ArrayList<Geld> geld = aktuelles_level.getMap().getGeld();

        for (int i = 0; i < geld.size(); i++) {
            Geld single_item = geld.get(i);
            Animation a = single_item.getAnimation();

            BufferedImage geldImg = a.nextFrame(field_size);

            int[] field = single_item.getField();
            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (geldImg.getWidth() / 2);
            int y_pixel = middle[1] - (geldImg.getHeight() / 2);

            // scaling ...

            g.drawImage(geldImg, x_pixel, y_pixel, null);

            if(devFrames) {
                g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Kirsche
        if(aktuelles_level.getMap().getKirsche().getVisible()){
            BufferedImage kirscheImg = current_skin.getImage("cherry", field_size);

            int[] field = aktuelles_level.getMap().getKirsche().getField();

            int[] middle = getCenterOf(field);
            int x_pixel = middle[0] - (kirscheImg.getWidth() / 2);
            int y_pixel = middle[1] - (kirscheImg.getHeight() / 2);

            g.drawImage(kirscheImg, x_pixel, y_pixel, null);

            if (devFrames) {
                g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
                g.setColor(Color.RED);
            }
        }

        // Spieler

        if(sp1 != null) {
            if(sp1.isAlive()) {

                Animation ani_left = current_skin.getAnimation("digger_red_left");
                Animation ani_right = current_skin.getAnimation("digger_red_right");
                Animation ani_up = current_skin.getAnimation("digger_red_up");
                Animation ani_down = current_skin.getAnimation("digger_red_down");

                BufferedImage sp1Img = null;

                if (sp1.getMoveDir() == DIRECTION.RIGHT) {
                    sp1Img = ani_right.nextFrame(field_size);
                }
                if (sp1.getMoveDir() == DIRECTION.LEFT) {
                    sp1Img = ani_left.nextFrame(field_size);
                }
                if (sp1.getMoveDir() == DIRECTION.UP) {
                    sp1Img = ani_up.nextFrame(field_size);
                }
                if (sp1.getMoveDir() == DIRECTION.DOWN) {
                    sp1Img = ani_down.nextFrame(field_size);
                }


                int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
                int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);
                g.drawImage(sp1Img, x_pixel, y_pixel, null);

            }
            else {
                // gegen Geist ersetzen
                Animation ani_grave = current_skin.getAnimation("Grave");
                BufferedImage sp1Img = ani_grave.nextFrame(field_size);
                int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
                int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);
                g.drawImage(sp1Img, x_pixel, y_pixel, null);
            }
        }

        if(sp2 != null) {
            if(sp2.isAlive()) {
                Animation ani_left = current_skin.getAnimation("digger_gre_left");
                Animation ani_right = current_skin.getAnimation("digger_gre_right");
                Animation ani_up = current_skin.getAnimation("digger_gre_up");
                Animation ani_down = current_skin.getAnimation("digger_gre_down");

                BufferedImage spImg = null;

                if (sp2.getMoveDir() == DIRECTION.RIGHT) {
                    spImg = ani_right.nextFrame(field_size);
                }
                if (sp2.getMoveDir() == DIRECTION.LEFT) {
                    spImg = ani_left.nextFrame(field_size);
                }
                if (sp2.getMoveDir() == DIRECTION.UP) {
                    spImg = ani_up.nextFrame(field_size);
                }
                if (sp2.getMoveDir() == DIRECTION.DOWN) {
                    spImg = ani_down.nextFrame(field_size);
                }


                int x_pixel = sp2.getPosition()[0] - (spImg.getWidth() / 2);
                int y_pixel = sp2.getPosition()[1] - (spImg.getHeight() / 2);
                g.drawImage(spImg, x_pixel, y_pixel, null);
            }
        }

        // Zeichne Score
        int margin_y = field_size/4;
        int margin_x = field_size/2;

        int fontSize = field_size/2;
        g.setFont(current_skin.getFont().deriveFont(Font.PLAIN, fontSize));
        g.setColor(Color.white);
        g.drawString(String.format("%05d", spielstand), margin_x, margin_y+fontSize);

        // Zeichne Leben


        // Zeichne Leben von SP1
        BufferedImage sp1Img =current_skin.getImage("statusbar_digger_MP_red", field_size);
        margin_x = 3*field_size;
        for(int i = sp1.getLeben(); i > 0; i--) {
            g.drawImage(sp1Img, margin_x, margin_y, null);
            margin_x += sp1Img.getWidth();
        }

        // Zeichene auch leben von SP2
        if(sp2 != null) {
            margin_x = 9*field_size;
            BufferedImage sp2Img = current_skin.getImage("statusbar_digger_MP_gre", field_size);
            for(int i = sp2.getLeben(); i > 0; i--) {
                g.drawImage(sp2Img, margin_x, margin_y, null);
                margin_x -= sp1Img.getWidth();
            }

        }

    }


    protected void refreshSizing() {

        // Speichere Änderung
        old_field_size = field_size;

        // setze Feldgröße

        Dimension d = this.getSize();

        int felderX = aktuelles_level.getMap().getPGSize()[0];
        int felderY = aktuelles_level.getMap().getPGSize()[1];

        if(d.width == 0 || d.height == 0)
            d = new Dimension(500, 500);

        int w_temp_size = (int)((double)d.width / ( (double)felderX + ( 2*border[0]) ));
        int h_temp_size = (int)((double)d.height / ( (double)felderY + ( 2*border[1]) + topbarHeight ));

        field_size = Math.min(w_temp_size, h_temp_size);

        // berechne neue Pixelpositionen

        if(field_size != old_field_size && old_field_size != 0) {
            double factor = (double) field_size / (double) old_field_size;

            if(sp1 != null)
                for (int i = 0; i <= sp1.getPosition().length - 1; i++)
                    sp1.getPosition()[i] *= factor;

            if(sp2 != null)
                for (int i = 0; i <= sp2.getPosition().length - 1; i++)
                    sp2.getPosition()[i] *= factor;

            for (Monster m : aktuelles_level.getMap().getMonster())
                for (int i = 0; i <= m.getPosition().length - 1; i++)
                    m.getPosition()[i] *= factor;
        }

    }

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

    int[] getCenterOf(int[] fp) {

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
