package Spielverlauf;

/***
 * Ausprägung des Monsters mit einem Auge (immer Seitlich gezeigt)
 */
public class Nobbin extends Monster {
	public int z=0, u =0,x=0;

	/***
	 * Konstruktor spawnt Nobbin an übergebener Position unter Benutzung des Monster-Konstruktors
	 * @param spawn_monster Position
	 */
	public Nobbin(int[] spawn_monster) {
		super(spawn_monster);
	}

}
