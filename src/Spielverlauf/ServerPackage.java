package Spielverlauf;

import java.awt.*;
import java.io.Serializable;

public class ServerPackage implements Serializable {
    private Map map;
    private int spielstand;
    private Spieler sp1, sp2;
    private String versandQueue;

    public ServerPackage(Map map, int spielstand, Spieler sp1, Spieler sp2, String vs) {
        this.map = map;
        this.spielstand = spielstand;
        this.sp1 = sp1;
        this.sp2 = sp2;
        this.versandQueue = vs;
    }

    public Map getMap() {
        return map;
    }

    public int getSpielstand() {
        return spielstand;
    }

    public Spieler getSp1() {
        return sp1;
    }
    public Spieler getSp2() {
        return sp2;
    }

    public String getVS() {
        return versandQueue;
    }
}
