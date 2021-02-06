package Spielverlauf;

/***
 * Klasse für Geld-Objekt, Geld fällt aus einem Geldsack heraus, wenn dieser aus der Höhe auf den Boden trifft,
 * Geld hat einen Wert im Bezug auf den Score beim Aufsammeln, sowie eine TimeToLive, d.h. es verschwindet wenn es nicht aufgesammelt wird
 */
public class Geld extends Item{

    private int wertung = 500;
    private long liveTime = 10000; //ms

    /***
     * Konstruktor erstellt Geld-Objekt an jeder gewünschten Stelle
     * @param fp int[] Geld-Positionen
     */
    public Geld(int[] fp) {
        super(fp);
    }

    /***
     * Getter für den Wert (beim Aufsammeln)
     * @return Score-Wert
     */
    @Override
    public int getValue() {
        return wertung;
    }

    /***
     * Verringert übrige Zeit (TTL) zum aufsammeln um verstichene Zeit
     * @param delay_period verstrichene Zeit
     */
    public void decRemainingTime(long delay_period) {
        liveTime -= delay_period;
    }

    /***
     * überprüft Ablauf der TTL
     * @return Booleanwert: TTL abgelaufen
     */
    public boolean outOfTime(){
        return liveTime>0?false:true;
    }
}
