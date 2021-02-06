package Spielverlauf;

import java.io.Serializable;

/***
 * Paket was der Server periodisch an den Client sendet, enthält: Map-Objekt, Spielstand, Spieler-Objekte, Feldgröße, neue Chatnachrichten
 */
public class ServerPackage implements Serializable {
    private Map map;
    private int spielstand;
    private Spieler sp1, sp2;
    private int field_size;
    private String versandQueue;

    /***
     * Konstruktor packt übergebene Elemente in eigenem Objekt zusammen
     * @param map Map-Objekt
     * @param spielstand int Spielstand
     * @param sp1 Spieler1 (Host)
     * @param sp2 Spieler2 (Client)
     * @param vs String mit neuen Chatnachrichten
     * @param fs int Feldgröße
     */
    public ServerPackage(Map map, int spielstand, Spieler sp1, Spieler sp2, String vs, int fs) {
        this.map = map;
        this.spielstand = spielstand;
        this.sp1 = sp1;
        this.sp2 = sp2;
        this.versandQueue = vs;
        this.field_size = fs;
    }

    /***
     * Getter für das Map-Objekt
     * @return Map
     */
    public Map getMap() {
        return map;
    }

    /***
     * Getter für den Spielstand
     * @return int Spielstand
     */
    public int getSpielstand() {
        return spielstand;
    }

    /***
     * Getter für Spieler1
     * @return Spieler-Objekt
     */
    public Spieler getSp1() {
        return sp1;
    }

    /***
     * Getter für Spieler2
     * @return Spieler-Objekt
     */
    public Spieler getSp2() {
        return sp2;
    }

    /***
     * Getter für neue Chatnachrichten
     * @return String versandQueue
     */
    public String getVS() {
        return versandQueue;
    }

    /***
     * Getter für die Feldgröße
     * @return int Feldgröße
     */
    public int getFieldSize() {
        return field_size;
    }
}
