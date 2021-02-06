package Spielverlauf;

/***
 * Klasse für die Kirsche,
 * enthält Wertung (im Bezug auf Scoreerhöhung), eine TimeToLive in der die Kirsche eingesammelt werden muss
 */
public class Kirsche extends Item {

	private int wertung = 1000;
	private long liveTime = 15000; //ms

	/***
	 * Erstellt Kirsche an gewünschter Position
	 * @param fp Position
	 */
	public Kirsche(int[] fp) {
		super(fp);
	}

	/***
	 * Getter für die Wertung
	 * @return Wertung
	 */
	@Override
	public int getValue() {
		return wertung;
	}

	/***
	 * verringert die TTL um verstichene Zeit
	 * @param delay_period verstichene Zeit
	 */
	public void decRemainingTime(long delay_period) {
		liveTime -= delay_period;
	}

	/***
	 * prüft ob die TTL abgelaufen ist
	 * @return Boolean: True, falls TTL abgelaufen
	 */
	public boolean outOfTime(){
		return liveTime>0?false:true;
	}
}