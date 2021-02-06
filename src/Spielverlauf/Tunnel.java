package Spielverlauf;

import java.io.Serializable;

/**
 * Von ihr intanzierte Objekte stellen ein Tunnelelement innerhalb eines Spielfeldes dar. Sie enthalten die {@link #fieldPosition Feldposition} und die {@link #typ Art} des Tunnels.
 */
public class Tunnel implements Serializable {


	/** Feldposition */
	int[] fieldPosition;
	/** Tunnelart (siehe auch {@link TUNNELTYP}) */
	TUNNELTYP typ;

	/** Erstellt ein Tunnelelement an der übergebenen Feldposition. Die Tunnelart wird 1 zu 1 wie übergeben, übernommen.
	 * @param fp Feldposition
	 * @param t Tunnelart
	 */
	public Tunnel(int[] fp, TUNNELTYP t){
		fieldPosition = fp;
		typ = t;
	}

	/** Erstellt ein Tunnelelement an der übergebenen Feldposition. Die Tunnelart wird auf Grundlage einer Richtung bestimmt.
	 * @param fp Feldposition
	 * @param d Bewegungsrichtung
	 */
	public Tunnel(int[] fp, DIRECTION d){
		fieldPosition = fp;
		if(d == DIRECTION.RIGHT || d == DIRECTION.LEFT)
			typ = TUNNELTYP.HORIZONTAL;
		else
			typ = TUNNELTYP.VERTICAL;
	}

	/**
	 * Liefert die Ausrichtugn des Tunnels. Siehe auch: {@link TUNNELTYP}
	 * @return Art des Tunnels
	 */
	public TUNNELTYP getTyp() {
		return typ;
	}

	/**
	 * Liefert die aktuelle Position des Tunnels in Felder gerechnet
	 * @return Feldposition
	 */
	public int[] getField() {
		return fieldPosition;
	}

}