package Spielverlauf;

import java.io.Serializable;

public class ClientPackage implements Serializable {
    private int[] pos;
    private DIRECTION dir;
    private boolean fb_try;

    public ClientPackage(Spieler s, boolean fb_try){
        this.pos = s.getPosition();
        this.fb_try = fb_try;
        this.dir = s.getMoveDir();
    }

    public int[] getPos() {
        return pos;
    }

    public boolean isFb_try() {
        return fb_try;
    }

    public DIRECTION getMoveDir() {
        return dir;
    }
}
