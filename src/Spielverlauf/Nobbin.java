package Spielverlauf;

public class Nobbin extends Monster {

	private Animation animation;

	public Nobbin(int[] spawn_monster, Skin sk) {
		super(spawn_monster);
		animation = sk.getAnimation("nobbin");
	}

	@Override
	public boolean testMove(DIRECTION d){
		// ist Erde oder Wand
		if(false) //TODO: Move is accepted or not
			return true;
		else
			return false;
	}

	@Override
	public Animation getAnimation(){
		return animation;
	}
}