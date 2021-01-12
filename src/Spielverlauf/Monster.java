package Spielverlauf;

public abstract class Monster {

	protected int[] position;
	protected int wertung = 250;
	protected boolean graben;
	protected DIRECTION moveDir;

	public Monster(int[] pos) {
		position = pos;
		moveDir = DIRECTION.RIGHT;
	}

	public void sichBewegen() {
		// TODO - implement Monster.sichBewegen
		throw new UnsupportedOperationException();
	}

	public void toeten() {
		// TODO - implement Monster.toeten
		throw new UnsupportedOperationException();
	}

	abstract public boolean testMove(DIRECTION d);

	public int[] getPosition(){
		return position;
	};

	public void setPosition(int[] pos) {
		position = pos;
	}

	public void addPosOff(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

	public abstract Animation getAnimation();

    public DIRECTION getMoveDir() {
		return moveDir;
    }

    public int getWertung(){
    	return wertung;
	}
}