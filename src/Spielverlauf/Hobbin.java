package Spielverlauf;
import java.util.ArrayList;

public class Hobbin extends Monster {

	ArrayList<Animation> animations;

	public Hobbin(int[] spawn_monster, Skin sk){
		super(spawn_monster);

		animations = new ArrayList<>();

		animations.add(sk.getAnimation("hobbin_right"));
		animations.add(sk.getAnimation("hobbin_left"));
	}

	@Override
	public boolean testMove(DIRECTION d){
		// ist Wand
		if(false) //TODO: check wether Move is accepted or not
			return true;
		else
			return false;
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
	@Override
	public Animation getAnimation() {

		Animation an = null;

		for (Animation a : animations)
			if (a.getDir() == moveDir){
				an = a;
				break;
			}

		return an;
	}

}