package Spielbereitstellug;

import Spielverlauf.Level;
import Spielverlauf.Skin;
import Spielverlauf.Map;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Spiel {

	private String skinName = "original_skin"; // only for testing

	private String skinfolder_name = "skins";
	private String levelfolder_name = "level";
	private int aktuelles_level;
	private int spielstand;
	private Skin current_skin;
	private ArrayList<Map> levelchain;

	public Spiel() {

		// initialisiere Skin
		current_skin = new Skin(new File(skinfolder_name), skinName); // Loades original.png and original.json from skins/

		// initialisiere Mapchain
		// TODO:
		//  1. search JSON Level in Folder Level/
		//  2. read and create them
		//  3. add tem to "levechain"
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