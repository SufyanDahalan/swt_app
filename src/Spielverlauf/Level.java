package Spielverlauf;

import javax.swing.*;

public class Level extends JPanel{

	private int geschwindigkeit;
	private int regeneration_feuer;
	private Map karte;
	private int temp_kills = 0;
	private int max_monster;

	public Level(int max_mon){
		max_monster = max_mon;
	}

	public void setGeschwindigkeit(int g){
		geschwindigkeit = g;
	}

	public void loop() {

		if (karte.getMonsterAmmount() < max_monster ) {
			karte.setMonster(new Nobbin(karte.getSpawn_monster()));
		}


		if (temp_kills == 4) { // Bdingung kann angepasst werden
			karte.setKirsche(new Kirsche(karte.getSpawn_monster()));
			temp_kills = 0;
		}


	}



}