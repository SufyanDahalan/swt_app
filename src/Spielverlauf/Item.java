package Spielverlauf;

import java.io.Serializable;

/***
 * Abstrakte Klasse für alle einsammelbaren Elemente des Spielfelds,
 *
 * wird von Geld, Geldsack, Kirsche, Diamant, ... implementiert
 */
public abstract class Item implements Serializable {

	protected int[] field;

	public Item(int[] fp) {
		field = fp;
	}

	public int[] getField() {
		return field;
	}

	public abstract int getValue();

}
