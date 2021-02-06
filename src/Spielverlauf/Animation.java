package Spielverlauf;

import java.awt.image.BufferedImage;

/***
 * Animiert bewegte Spielfiguren
 */
public class Animation {

    private Skin s;

    private int count = 0;
    final int DELAY_PERIOD;
    int remaining;
    private DIRECTION richtung;
    private boolean repeatale;
    private boolean again;


    private BufferedImage[] images;


    public Animation(int loss, BufferedImage[] bi, Skin sk, DIRECTION r, boolean rp) {
        DELAY_PERIOD = loss;
        remaining = loss;
        richtung= r;
        images = bi;
        s = sk;
        repeatale = rp;
        again = true;
    }

    public Animation(Animation g) {
        DELAY_PERIOD = g.DELAY_PERIOD;
        remaining = g.remaining;
        richtung= g.richtung;
        images = g.images;
        s = g.s;
        repeatale = g.repeatale;
        again = true;
    }

    public BufferedImage nextFrame(int fs) {

        if(again){
            if (remaining > 0) {
                remaining--;
            } else {
                count = (count + 1) % images.length;
                remaining = DELAY_PERIOD;

                if (count == images.length-1 && !repeatale) {
                    again = false;
                }
            }
        }

        return s.scale(images[count], fs);
    }

    public DIRECTION getDir() {
        return richtung;
    }
}
