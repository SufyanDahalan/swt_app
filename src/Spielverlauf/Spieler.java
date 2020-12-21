package Spielverlauf;

import org.json.JSONArray;

import javax.swing.*;

public class Spieler {

	protected int[] position;
	protected String name;
	protected int alter;
	private int leben = 3;

	public Spieler(JSONArray pos) {
		position = new int[2];

		position[0] = pos.getInt(0);
		position[1] = pos.getInt(1);
	}

	public void sterben() {
		// TODO - implement Spieler.sterben
		throw new UnsupportedOperationException();
	}

	public boolean sichBewegen() {
		// TODO - implement Spieler.sichBewegen
		throw new UnsupportedOperationException();
	}

	public int[] getPosition(){return position;}

	public void addPos(int x, int y){
		position[0]+=x;
		position[1]+=y;
	}

}