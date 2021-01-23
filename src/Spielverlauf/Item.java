package Spielverlauf;

import java.io.Serializable;

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
