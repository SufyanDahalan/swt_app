package Spielverlauf;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animation {
    private int frames;
    private Skin s;

    private int index = 0;
    private int count = 0;
    long DELAY_PERIOD;
    long beginTime = System.currentTimeMillis();

    private BufferedImage[] images;
    private BufferedImage currentImg;

    public Animation(int speed, BufferedImage[] bi, Skin sk) {
        DELAY_PERIOD = speed;
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
        long beginTime = System.currentTimeMillis();
        long timeTaken = System.currentTimeMillis() - beginTime;
        long sleepTime = DELAY_PERIOD - timeTaken;

        if (sleepTime >= 0) {
            try{Thread.sleep(sleepTime);
                count = (count+1)%frames;
            } catch(InterruptedException e){}
        }

        return s.scale(images[count], fs);
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
