package Spielverlauf;

import java.awt.*;
import java.io.Serializable;

public class ServerPackage implements Serializable {
    private Map map;
    private int spielstand;
    private Spieler sp;

    public ServerPackage(Map map, int spielstand, Spieler sp) {
        this.map = map;
        this.spielstand = spielstand;
        this.sp = sp;
    }

    public Map getMap() {
        return map;
    }

    public int getSpielstand() {
        return spielstand;
    }

    public Spieler getSp() {
        return sp;
    }
}
