package Spielverlauf;

public class Nobbin extends Monster {

	public Nobbin(int[] spawn_monster) {
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