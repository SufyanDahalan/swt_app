package Spielverlauf;

public class Feuerball {

	private int[] position;
	private DIRECTION dir;

	/**
	 * 
	 * @param pos
	 * @param d
	 */
	public Feuerball(int[] pos, DIRECTION d) {

		dir = d;
		position = pos;
	}

	public int[] getPosition() {
		return position;
	}
}