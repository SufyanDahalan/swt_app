package Spielverlauf;


public class Kirsche extends Item {

	private int wertung = 1000;
	private boolean visible = false;

	public Kirsche(int[] fp) {
		super(fp);
	}

    public void setVisible(boolean v) {
		visible = v;
	}
}