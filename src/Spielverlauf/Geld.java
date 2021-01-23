package Spielverlauf;

import java.awt.image.BufferedImage;

public class Geld extends Item{

    private int wertung = 500;
    private long liveTime = 10000; //ms

    public Geld(int[] fp) {
        super(fp);
    }

    @Override
    public int getValue() {
        return wertung;
    }

    public void decRemainingTime(long delay_period) {
        liveTime -= delay_period;
    }

    public boolean outOfTime(){
        return liveTime>0?false:true;
    }
}
