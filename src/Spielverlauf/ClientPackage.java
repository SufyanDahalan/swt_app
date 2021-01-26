package Spielverlauf;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.concurrent.RecursiveTask;

public class ClientPackage implements Serializable {
    private Spieler sp;
    private boolean fb_try;

    public ClientPackage(Spieler s, boolean fb_try){
        this.sp = s;
        this.fb_try = fb_try;
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
}
