package Spielverlauf;

import java.awt.image.BufferedImage;

public abstract class Item {

	protected int[] field;
	protected int wertung;
	protected BufferedImage bild;

	public Item(int[] fp, BufferedImage bi) {
		field = fp;
		bild = bi;
	}

	public Item(int[] fp) {
		field = fp;
		bild = null;
	}

	public int[] getField() {
		return field;
	}

	public void setField(int[] i) {
		i = field;
	}

	public abstract int getValue();

}
