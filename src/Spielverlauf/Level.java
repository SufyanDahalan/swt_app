package Spielverlauf;

public class Level {

	// Eigenschaften abhängig von Level-Stufe

	private final int geschwindigkeit;
	private final int regeneration_feuer;
	private final int max_monster; // Anzahl der gleichzeitig möglichen Monster

	// Konstanten

	private final Map karte;

	// temp. Attr.

	private int temp_kills = 0;

	public Level(int gesch, int reg_feu, int max_mon, Map m) {
		karte = m; // TODO: Anpassen
		max_monster = max_mon; // TODO: Anpassen
		geschwindigkeit = gesch; // TODO: Anpassen
		regeneration_feuer = reg_feu; // TODO: Anpassen
	}

	public void loop() {

		if (karte.getMonsterAmmount() < max_monster ) {
			// karte.spawnMonster(new Nobbin(karte.getSpawn_monster()));
		}

		if (temp_kills == 4) { // TODO: Spielverhalten abstimmen udn Bedingung anpassen
			//karte.setKirsche();
			temp_kills = 0;
		}

	}


	public int getSpeed() {
		return geschwindigkeit;
	}

	public int getRegenTime() {
		return regeneration_feuer;
	}

	public int getMaxMonster() {
		return max_monster;
	}

	public Map getMap() {
		return karte;
	}
}