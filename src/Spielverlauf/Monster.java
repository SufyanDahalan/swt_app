package Spielverlauf;

import java.io.Serializable;

public abstract class Monster implements Serializable {

	private int[] position;
	protected int wertung = 250;
	protected DIRECTION moveDir;
	protected boolean[] blocks;

	public Monster(int[] pos) {
		position = pos;
		moveDir = DIRECTION.RIGHT;
		blocks = new boolean[]{true,true,true,true};
	}

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

    public DIRECTION getMoveDir() {
		return moveDir;
    }
	public void setMoveDir(DIRECTION dir) {
		moveDir = dir;
	}

    public int getWertung(){
    	return wertung;
	}

	public boolean isBlocked(DIRECTION d){

    	int i;

    	switch (d){
			case UP: 	i=0;
						break;
			case RIGHT:	i=1;
						break;
			case DOWN:	i=2;
						break;
			case LEFT: 	i=3;
						break;
			default: 	i=2;
						break;
		}

		return blocks[i];
	}

	public void setBlocks(boolean[] blocks) {
		this.blocks = blocks;
	}

	public void removeBlocks(){
    	blocks = new boolean[]{false,false,false,false};
	}
}