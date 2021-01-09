package Spielverlauf;

import java.awt.image.BufferedImage;

public class Geld extends Item{

    private int wertung = 500;
    private Animation animation;

    public Geld(int[] fp, Skin sk) {
        super(fp);
        animation = new Animation(sk.getAnimation("Geld"));
    }

    @Override
    public int getValue() {
        return wertung;
    }

    public Animation getAnimation(){
        return animation;
    }
}
