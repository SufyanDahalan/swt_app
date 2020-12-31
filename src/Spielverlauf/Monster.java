package Spielverlauf;

import org.json.JSONArray;

public abstract class Monster {

	protected int[] position;
	protected int wertung = 250;
	protected boolean graben;

	public void sichBewegen() {
		// TODO - implement Monster.sichBewegen
		throw new UnsupportedOperationException();
	}

	public void toeten() {
		// TODO - implement Monster.toeten
		throw new UnsupportedOperationException();
	}

	abstract public boolean testMove(DIRECTION d);

	public Monster(int[] pos) {
		position = pos;
	}

	public int[] getPosition(){
		return position;
	};


}