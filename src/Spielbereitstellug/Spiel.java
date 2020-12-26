package Spielbereitstellug;

import Spielverlauf.Level;
import Spielverlauf.Skin;
import Spielverlauf.Map;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;


public class Spiel {

	// System-/ Filestructure

	private final String skinName = "original_skin"; // Skinnname
	private final String levelfolder_name = "level"; // ./level/level-01.json ...
	private final String skinfolder_name = "skins"; // ./skin/sink_original.json,...

	// static content

	private Skin current_skin;
	private ArrayList<Map> mapChain;
	Iterator<Map> mapIter;

	// temp. Att.

	private Level aktuelles_level;
	private int spielstand;


	public Spiel() {

		// initialisiere Skin
		current_skin = new Skin(new File(skinfolder_name), skinName); // Loades original_skin.png and original.json from skins/

		// initialisiere Mapchain

		String[] maps = new File(levelfolder_name).list(); // read Level from Folder

		// create Map and add it to chain

		System.out.println(maps.length);

		mapChain = new ArrayList<Map>();
		mapIter = mapChain.iterator();

		for(int i=0; i<maps.length; i++) {

			// read Level-File
			JSONObject obj = null;
			try {
				obj = new JSONObject(new String(Files.readAllBytes(Paths.get(levelfolder_name + maps[i]))));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create Map and add to chain
			int[] size = {1000, 1000};
			mapChain.add(new Map(obj, size, current_skin));
		}

		// Stark gameloop

		while(loop());

	}

	/**
	 *	Spiellogik, die Positionen prüft und Ereignisse aufruft.
	 * @return false if plaer dead; else true if game schoud be contiued
	 */

	private boolean loop(){

		/// Prüfroutinen

		// alle Diamanten gesammel?
		if(aktuelles_level.getMap().getDiamonds().size() == 0) {
			// dann nächstes Level
			aktuelles_level = createNextLevel();

			// vervolständigen zB von Scores
			return true;
		}

		// alle Monster getötet?
		if(aktuelles_level.getMap().getMonster().size() == 0) {
			// dann nächstes Level
			aktuelles_level = createNextLevel();

			// vervolständigen zB von Scores
			return true;
		}

		// Folgende Ereignisse müssen ausfrmuliert und entsprechende Folgen triggern

		// Spieler trifft Diamant
		// Spieler trifft Nobbin
		// Spieler trifft Hobbin
		// Spieler trifft Boden
		// Spieler trifft Geldsack
		// Spieler trifft Geld
		// Spieler trifft Wand
		// Spieler trifft Kirsche -> Bonsmodus aktivieren
		// Hobbin trifft Diamant
		// Hobbin trifft Boden
		// Hobbin trifft Geldsack (legend oder fallend unterscheiden)
		// Hobbin trifft Geld
		// Hobbin trifft Nobbin
		// Hobbin trifft Hobbin
		// Hobbin trifft Wand
		// Hobbin trifft Kirsche
		// Nobbin trifft Boden
		// Nobbin trifft Nobbin
		// Nobbin trifft Geldsack (liegend oder fallend unterscheiden)
		// Nobbin trifft Geld

		// Bewegung werden durch Algorithmus oder Tastatuseingabe oder Netzwerksteuerung direkt im Mapobjekt geändert und durch repaint übernommen
		/// in jedem Fall wir true zurückgegeben. Andernfalls beendet sich die loop() und das spiel gilt als beendet. Darauf folgt dann die eintragung der ergebnisse ect.

		return false; // Spiel beendet
	}

	// creates next Level, increases speed and decrease regtime

	private Level createNextLevel(){

		int new_s = aktuelles_level.getSpeed() + 1;
		int new_r = aktuelles_level.getRegenTime() + 1;
		int new_mm = aktuelles_level.getMaxMonster() + 1;
		Map nextMap; // nächste Map als KOPIE!!! einsetzen. Sonst wird die Mapchain manipuliert und Folgelevel sind verändert.

		return new Level(new_s,new_r,new_mm, nextMap); // Maps have to be cloned!!! otherwise we override the initial mapchain
	}

	public void start() {
		// TODO - implement Spiel.start
		throw new UnsupportedOperationException();
	}

	public void beenden() {
		// TODO - implement Spiel.beenden
		throw new UnsupportedOperationException();
	}

	public void pausieren() {
		// TODO - implement Spiel.pausieren
		throw new UnsupportedOperationException();
	}

}