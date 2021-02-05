package Spielverlauf;

/***
 * Klasse für das Level-Objekt,
 * enthält Geschwindigkeit, Regenerationszeiten für Monster und Feuerball, sowie maximale Monsteranzahl
 */
public class Level {

	// Eigenschaften abhängig von Level-Stufe

	private final int geschwindigkeit;
	private final int regeneration_feuer;
	private final int regeneration_monster;
	private final int max_monster; // Anzahl der gleichzeitig möglichen Monster

	// Konstanten

	private final Map karte;

	// temp. Attr.

	private int temp_kills = 0;

	/***
	 * einfacher Konstruktor übernimmt die übergebenen Elemente
	 * @param gesch int Geschwindigkeit
	 * @param reg_feu int Feuerball Regenerationszeit
	 * @param max_mon int maximale Monsteranzahl
	 * @param reg_mon int Monster Regenerationszeit
	 * @param m Map des Levels
	 */
	public Level(int gesch, int reg_feu, int max_mon, int reg_mon, Map m) {
		karte = m; // TODO: Anpassen
		max_monster = max_mon; // TODO: Anpassen
		geschwindigkeit = gesch; // TODO: Anpassen
		regeneration_feuer = reg_feu; // TODO: Anpassen
		regeneration_monster = reg_mon;
	}

	/***
	 * Getter für die Geschwindigkeit
	 * @return int Geschwindigkeit
	 */
	public int getSpeed() {
		return geschwindigkeit;
	}

	/***
	 * Getter für die Feuerball Regenerationszeit
	 * @return int Regenerationszeit
	 */
	public int getRegenTimeFb() {
		return regeneration_feuer;
	}

	/***
	 * Getter für die Monster Regenerationszeit
	 * @return int Regenerationszeit
	 */
	public int getRegenTimeMonster() {
		return regeneration_monster;
	}

	/***
	 * Getter für maximale Monsteranzahl
	 * @return int max Monsteranzahl
	 */
	public int getMaxMonster() {
		return max_monster;
	}

	/***
	 * Getter für das Map-Objekt
	 * @return Map
	 */
	public Map getMap() {
		return karte;
	}

}