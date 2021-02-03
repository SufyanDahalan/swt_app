package Spielverlauf;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.concurrent.RecursiveTask;

public class ClientPackage implements Serializable {
    private Spieler sp;
    private boolean fb_try;
    private String versandQueue;
    private int field_size;

    public ClientPackage(Spieler s, boolean fb_try, String vs, int fs){
        this.sp = s;
        this.fb_try = fb_try;
        this.versandQueue = vs;
        this.field_size = fs;
    }

    public String getVS(){
        return versandQueue;
    }

    public boolean isFb_try() {
        return fb_try;
    }

    public Spieler getSp() {
        return sp;
    }

    public int getFieldSize() {
        return field_size;
    }
}
