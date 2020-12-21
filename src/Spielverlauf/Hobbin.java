package Spielverlauf;

import org.json.JSONArray;

public class Hobbin extends Monster {

	@Override
	public boolean testMove(DIRECTION d){
		// ist Wand
		if(false) //TODO: check wether Move is accepted or not
			return true;
		else
			return false;
	}

	public boolean graben(Map m, DIRECTION d) {
		if( testMove(d) ) {

			Tunnel t;

			if(d.equals(DIRECTION.RIGHT) || d.equals(DIRECTION.LEFT))
				t = new Tunnel(position, TUNNELTYP.VERTICAL);
			else
				t = new Tunnel(position, TUNNELTYP.HORIZONTAL);

			m.addTunnel(t);
			return true;
		}
		else
			return false; // wenn Bewegng nicht erlaubt
	}

	public Hobbin(JSONArray spawn_monster){
		super(spawn_monster);
	}

}