package Spielverlauf;
import org.json.JSONArray;
import Spielverlauf.*;


public class Geldsack extends Item {

	private int wertung = 0;
	private DIRECTION moveDir;
	private boolean falling;
	private int fallHeight;

	public Geldsack(int[] pos) {
		super(pos);
		falling = false;
		fallHeight = 0;
	}

	public DIRECTION getMoveDir() {
		return moveDir;
	}

	public void setMoveDir(DIRECTION d) {
		moveDir = d;
	}

	@Override
	public int getValue() {
		return wertung;
	}

    public void setFalling(boolean f) {
		falling = f;
    }


	public boolean getFalling() {
		return falling;
	}

	public void incFallHeight() {
		fallHeight++;
	}

	public int getFallHeight() {
		return fallHeight;
	}

	public void resetFallHeight() {
		fallHeight = 0;
	}
}