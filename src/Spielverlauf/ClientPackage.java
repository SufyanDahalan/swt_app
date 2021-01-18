package Spielverlauf;

public class ClientPackage {
    private int[] pos;
    private boolean fb_try;

    public ClientPackage(int[] pos, boolean fb_try){
        this.pos = pos;
        this.fb_try = fb_try;
    }

    public int[] getPos() {
        return pos;
    }

    public boolean isFb_try() {
        return fb_try;
    }


}
