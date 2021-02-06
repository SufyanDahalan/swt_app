package Spielverlauf;

import java.io.Serializable;

/***
 * Paket was der Client periodisch an den Host sendet, enthält: Spieler-Objekt, Schießbefehl des Feuerballs, neue Chatnachrichten, Spielfeldgröße
 */
public class ClientPackage implements Serializable {
    private Spieler sp;
    private boolean fb_try;
    private String versandQueue;
    private int field_size;

    /***
     * Konstruktor
     * @param s Spieler-Objekt von Spieler2
     * @param fb_try Booleanvariable: True für Wunsch Feuerball abzufeuern
     * @param vs String mit allen Neuen Chatnachrichten vom Client
     * @param fs int Feldgröße
     */
    public ClientPackage(Spieler s, boolean fb_try, String vs, int fs){
        this.sp = s;
        this.fb_try = fb_try;
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
     * Getter für Wunsch Feuerball abzufeuern
     * @return Boolean fb_try
     */
    public boolean isFb_try() {
        return fb_try;
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
