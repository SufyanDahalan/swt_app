package Spielverlauf;

public class Nobbin extends Monster {

	private Animation animation;

	public Nobbin(int[] spawn_monster, Skin sk) {
		super(spawn_monster);
		animation = sk.getAnimation("nobbin");
	}

	@Override
	public Animation getAnimation(){
		return animation;
	}
}