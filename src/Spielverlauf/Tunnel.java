package Spielverlauf;

public class Tunnel{

	int[] fieldPosition;
	TUNNELTYP typ;


	public Tunnel(int[] fp, TUNNELTYP t){
		fieldPosition = fp;
		typ = t;
	}

	public int[] getPosition() {
		return null;
	};

	public TUNNELTYP getTyp() {
		return typ;
	}


	public int[] getField() {
		return fieldPosition;
	}
}