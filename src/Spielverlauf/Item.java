package Spielverlauf;

public abstract class Item {

	protected int[] field;
	protected int[] position;
	protected int wertung;

	public Item(int[] fp) {
		field = fp;
		position = fp;
	}
	public int[] getField() {
		return field;
	}
	public void setField(int[] i){
		i=field;
	}

    public abstract int getValue();

    public int[] getPosition(){return position;}
}