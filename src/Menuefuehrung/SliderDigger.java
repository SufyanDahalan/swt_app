package Menuefuehrung;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class SliderDigger extends BasicSliderUI {

        public SliderDigger(JSlider slider) {
            super(slider);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Rectangle t = trackRect;
            g2d.setPaint(Color.LIGHT_GRAY);
            g2d.fillRect(t.x, t.y, t.width, t.height);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = thumbRect;
            g2d.setColor(Color.red);
            g2d.fillRect(t.x, t.y, t.width, t.height);
        }

}
