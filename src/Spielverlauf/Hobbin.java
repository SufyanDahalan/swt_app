package Spielverlauf;

import org.json.JSONArray;

public class Hobbin extends Monster {

	@Override
	public boolean testeBewegung(DIRECTION d){
		// ist Wand
		if() //TODO: Move is accepted or not
			return true;
		else
			return false;
	}

	public boolean graben(Map m, DIRECTION d) {
		if(testeBewegung(d)) {
			m.setTunnel(position);
			return true; // Bewegung erlaubt
		}
		else
			return false; // wenn Bewegng nicht erlaubt
	}

	public Hobbin(JSONArray spawn_monster){
		super(spawn_monster);
	}

}