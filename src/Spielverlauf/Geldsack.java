package Spielverlauf;

import java.io.Serializable;

/***
 * Klasse für Geldsack-Objekt,
 * enthält Position, Feld (im Spielfeldraster), Status (im freien Fall), Fallhöhe, TimeToLive
 */
public class Geldsack implements Serializable {
	private int[] position;
	private int[] field;
	private boolean falling;
	private int fallHeight;
	final private long liveTime = 1000;
	private long remLiveTime;
	private boolean shaking;

	/***
	 * Konstuktor erstellt Geldsack an jeder gewünschten Position, im stehenden Zustand
	 * @param fp Position
	 */
	public Geldsack(int[] fp){
		position=null;
		falling = false;
		shaking = false;
		fallHeight = 0;
		field=fp;
		remLiveTime = liveTime;
	}

	/***
	 * Copy-Konstruktor
	 * @param gs bestehender Geldsack
	 */
	public Geldsack(Geldsack gs){
		position=null;
		falling = false;
		shaking = false;
		fallHeight = 0;
		field=gs.getField().clone();
		remLiveTime = liveTime;
	}

	/***
	 * Setter für Geldsack-Position
	 * @param pos
	 */
	public void setPosition(int[] pos) {
		position = pos;
	}

	/***
	 * Getter für Geldsack-Position
	 * @return Position
	 */
	public int[] getPosition(){return position;}

	/***
	 * für Positionsänderungen
	 * @param x Positionsänderungen in der X-Achse
	 * @param y Positionsänderungen in der Y-Achse
	 */
	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

	/***
	 * Getter für das Feld, in dem sich der Geldsack befindet (im Raster des Spielfelds)
	 * @return
	 */
	public int[] getField() {
		return field;
	}

	/***
	 * für Statusänderung: Fallend / nicht fallend
	 * @param f Boolean "Fall"-Unterscheidung
	 */
	public void setFalling(boolean f) {
		falling = f;
    }

	/***
	 * Getter vom Status
	 * @return Boolean: Fallend / nicht fallend
	 */
	public boolean getFalling() {
		return falling;
	}

	/***
	 * vergrößert Fallhöhe, etwa wenn der Boden unter dem Geldsack abgebaut wird
	 * @param fh int Fallhöhe
	 */
	public void incFallHeight(int fh) {
		fallHeight += fh;
	}

	/***
	 * Getter für die Fallhöhe
	 * @return int Fallhöhe
	 */
	public int getFallHeight() {
		return fallHeight;
	}

	/***
	 * Resetter für die Fallhöhe, setzt diese auf 0
	 */
	public void resetFallHeight() {
		fallHeight = 0;
	}

	/***
	 * verringert TTL um verstrichene Zeit
	 * @param delay_period verstrichene Zeit
	 */
	public void decRemainingTime(long delay_period) {
		remLiveTime -= delay_period;
	}

	/***
	 * prüft ob die TTL abgelaufen ist
	 * @return Boolean: true falls TTL abgelaufen
	 */
	public boolean outOfTime(){
		return remLiveTime>0?false:true;
	}

	/***
	 * Resetter für die TTL, setzt TTL auf definierten Standardwert zurück
	 */
	public void resetLiveTime(){
		remLiveTime = liveTime;
	}

	/***
	 * ändert Animations-Status wackelnd
	 * @param s Boolean: Geldsack wackelt
	 */
	public void setShaking(boolean s){
		shaking  = s;
	}

	/***
	 * Getter des Animations-Status
	 * @return Boolean: Geldsack wackelt
	 */
	public boolean getShaking() {
		return shaking;
	}
}