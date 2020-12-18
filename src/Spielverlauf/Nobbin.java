package Spielverlauf;

import org.json.JSONArray;

public class Nobbin extends Monster {

	public Nobbin(JSONArray spawn_monster) {
		super(spawn_monster);
	}

	@Override
	public boolean testMove(DIRECTION d){
		// ist Erde oder Wand
		if(false) //TODO: Move is accepted or not
			return true;
		else
			return false;
	}
}