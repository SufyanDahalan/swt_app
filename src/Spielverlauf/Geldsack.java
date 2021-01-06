package Spielverlauf;
import org.json.JSONArray;
import Spielverlauf.*;


public class Geldsack extends Item {

	private int wertung = 0;
	private DIRECTION moveDir;

	public Geldsack(int[] pos) {
		super(pos);
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

}