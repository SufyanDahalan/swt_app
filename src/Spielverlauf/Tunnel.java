package Spielverlauf;

import java.io.Serializable;

public class Tunnel implements Serializable {

	int[] fieldPosition;
	TUNNELTYP typ;

	public Tunnel(int[] fp, TUNNELTYP t){
		fieldPosition = fp;
		typ = t;
	}

	public Tunnel(int[] fp, DIRECTION d){
		fieldPosition = fp;
		if(d == DIRECTION.RIGHT || d == DIRECTION.LEFT)
			typ = TUNNELTYP.HORIZONTAL;
		else
			typ = TUNNELTYP.VERTICAL;
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