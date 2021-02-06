package Spielverlauf;

import java.io.Serializable;

/***
 * Paket was der Client periodisch an den Host sendet, enthält: Spieler-Objekt, Schießbefehl des Feuerballs, neue Chatnachrichten, Spielfeldgröße
 */
public class ClientPackage implements Serializable {
    private Spieler sp;
    private String versandQueue;
    private int field_size;

    /***
     * Konstruktor
     * @param s Spieler-Objekt von Spieler2
     * @param vs String mit allen Neuen Chatnachrichten vom Client
     * @param fs int Feldgröße
     */
    public ClientPackage(Spieler s, String vs, int fs){
        this.sp = s;
        this.versandQueue = vs;
        this.field_size = fs;
    }

    /***
     * Getter: String Variable für neue Chatnachrichten
     * @return versandQueue
     */
    public String getVS(){
        return versandQueue;
    }

    /***
     * Getter für Spieler-Objekt
     * @return Spieler2
     */
    public Spieler getSp() {
        return sp;
    }

    /***
     * Getter für Spielfeldgröße
     * @return int Spielfeldgröße
     */
    public int getFieldSize() {
        return field_size;
    }
}
