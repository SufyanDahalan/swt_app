package Spielverlauf;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animation {
    private int speed;
    private int frames;
    private Skin s;

    private int index = 0;
    private int count = 0;

    private BufferedImage[] images;
    private BufferedImage currentImg;

    public Animation(int speed, BufferedImage[] bi, Skin sk) {
        this.speed = speed;
        images = bi;
        frames = bi.length;
        s = sk;
    }

    /*
    public void runAnimation() {
        index++;
        if (index > speed) {
            index = 0;
            nextFrame();
        }
    }*/

    public BufferedImage nextFrame(int fs) {

        count = (count+1)%frames;

        return s.scale(images[count-1], fs);
    }
/*
    public void drawAnimation(Graphics g, int x, int y) {
        g.drawImage(currentImg, x, y, null);
    }

    public void drawAnimation(Graphics g, int x, int y, int scaleX, int scalY) {
        g.drawImage(currentImg, x, y, scaleX, scalY, null);
    }

 */
}
