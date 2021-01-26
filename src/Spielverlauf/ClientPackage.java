package Spielverlauf;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.concurrent.RecursiveTask;

public class ClientPackage implements Serializable {
    private Spieler sp;

    public ClientPackage(Spieler s){
        this.sp = s;
    }

    public Spieler getSp() {
        return sp;
    }
}
