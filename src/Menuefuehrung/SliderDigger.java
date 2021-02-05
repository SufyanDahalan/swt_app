package Menuefuehrung;

import Spielverlauf.Skin;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/***
 * Klasse für einen Slider, wird in den Optionen für die Lautstärkeregelung eingesetzt
 */
public class SliderDigger extends BasicSliderUI {

    Skin skin;

    public SliderDigger(JSlider slider, Skin skin) {
        super(slider);
        this.skin = skin;
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle t = trackRect;
        g2d.setPaint(Color.black);
        g2d.fillRect(t.x-tickRect.width/2, t.y, t.width+tickRect.width, t.height);

        g2d.setPaint(Color.orange);
        g2d.fillRect(t.x, t.y+t.height/4, t.width, t.height/2);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle t = thumbRect;

        BufferedImage pic = skin.getImage("diamond", t.height*2);
        g2d.drawImage(pic, t.x-pic.getWidth()/2, t.y, null);
    }

}
