package Spielverlauf;

import java.io.Serializable;

/***
 * Klasse für Diamant-Objekt,
 * enthält Punktzahl für eingesammelten Diamanten
 */
public class Diamant extends Item implements Serializable {

	private int wertung = 25;

	/***
	 * erstellt einen Diamanten an jeder gewünschten Stelle
	 * @param fp int[] mit Diamant-Positionen
	 */
	public Diamant(int[] fp) {
		super(fp);
	}

	/***
	 * Copy-Konstuktor klont bestehenden Diamanten
	 * @param d Diamant
	 */
	public Diamant(Diamant d){
		super(d.getField().clone());
	}

	/***
	 * Getter für Diamant-Wert (Punktzahl beim Einsammeln)
	 * @return int wertung
	 */
	@Override
	public int getValue() {
		return wertung;
	}
}