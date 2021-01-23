package Spielverlauf;
import java.util.ArrayList;

public class Hobbin extends Monster {


	public Hobbin(int[] spawn_monster) {
		super(spawn_monster);
	}

/*
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
*/

}