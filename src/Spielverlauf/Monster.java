package Spielverlauf;

import org.json.JSONArray;

public abstract class Monster {

	protected JSONArray position;
	protected JSONArray field;
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

	public Monster(JSONArray spawn) {
		position = spawn;
		field=spawn;
	}

	public JSONArray getPosition(){
		return position;
	};

	public JSONArray getField(){
		return field;
	}

}