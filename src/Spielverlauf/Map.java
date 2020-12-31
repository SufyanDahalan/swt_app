package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Map {

	// Playground

	private JSONArray playground_size;

	// Contente
	private ArrayList<Monster> monster;
	private JSONArray spawn_monster;
	private JSONArray spawn_sp1;
	private JSONArray spawn_sp2;
	private ArrayList<Feuerball> feuerball;
	private ArrayList<Diamant> diamanten;
	private ArrayList<Geldsack> geldsaecke;
	private ArrayList<Geld> geld;
	private ArrayList<Tunnel> tunnel;
	private Kirsche kirsche;

	public Map(JSONObject obj) {

		// Set initial Content

		playground_size = obj.getJSONArray("pg_size");

		geldsaecke = new ArrayList<Geldsack>();
		diamanten = new ArrayList<Diamant>();
		tunnel = new ArrayList<Tunnel>();

		kirsche = new Kirsche(spawn_monster);
		geld = new ArrayList<Geld>();
		feuerball= new ArrayList<Feuerball>();

		monster = new ArrayList<Monster>();

		spawn_monster = obj.getJSONArray("spawn_mon");
		spawn_sp1 = obj.getJSONArray("spawn_p1");
		spawn_sp2 = obj.getJSONArray("spawn_p2");

		// Füge initiale Diamanten ein

		JSONArray pos_diam = obj.getJSONArray("pos_diam");

		for (int i = 0; i < pos_diam.length(); i++) {

			JSONArray single_item = pos_diam.getJSONArray(i);

			diamanten.add(new Diamant(single_item));
		}

		// Füge initiale Geldsäcke ein

		JSONArray pos_money = obj.getJSONArray("pos_money");

		for (int i = 0; i < pos_money.length(); i++) {

			JSONArray single_money = pos_money.getJSONArray(i);

			geldsaecke.add(new Geldsack(single_money));
		}

		//// Set initial tunnels

		JSONObject kind_tun = obj.getJSONObject("pos_tun");

		// Set vertical tunnel
		JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");

		for (int i = 0; i < pos_tun_vertikal.length(); i++) {

			JSONArray single_tunnel = pos_tun_vertikal.getJSONArray(i);

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.VERTICAL));
		}

		// Set landscape tunnel
		JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");

		for (int i = 0; i < pos_tun_horizontal.length(); i++) {

			JSONArray single_tunnel = pos_tun_horizontal.getJSONArray(i);

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.HORIZONTAL));
		}

		// Set holes
		JSONArray pos_tun_space = kind_tun.getJSONArray("space");

		for (int i = 0; i < pos_tun_space.length(); i++) {

			JSONArray single_tunnel = pos_tun_space.getJSONArray(i);

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.SPACE));
		}

	}

	public Map(Map m) {

		playground_size = new JSONArray(m.playground_size);

		monster = new ArrayList<Monster>(m.monster);
		spawn_monster = new JSONArray(m.spawn_monster);
		spawn_sp1 = new JSONArray(m.spawn_sp1);
		spawn_sp2 = new JSONArray(m.spawn_sp2);
		feuerball = new ArrayList<Feuerball>(m.feuerball);
		diamanten = new ArrayList<Diamant>(m.diamanten);
		geldsaecke = new ArrayList<Geldsack>(m.geldsaecke);
		geld = new ArrayList<Geld>(m.geld);
		tunnel = new ArrayList<Tunnel>(m.tunnel);
		kirsche = new Kirsche(m.spawn_monster);
	}

	////// Content handling functions

	/// Monster

	// typische getter und setter

	public ArrayList<Monster> getMonster() {
		return monster;
	}

	public ArrayList<Hobbin> getHobbins(){

		ArrayList<Hobbin> hobbins = new ArrayList<Hobbin>();

		for (Monster m : monster){

			if( m instanceof Hobbin )
				hobbins.add( (Hobbin)m );
		}

		return hobbins;
	}

	public ArrayList<Nobbin> getNobbins(){

		ArrayList<Nobbin> nobbins = new ArrayList<Nobbin>();

		for (Monster m : monster){

			if( m instanceof Nobbin )
				nobbins.add( (Nobbin)m );
		}

		return nobbins;
	}

	// add and remove

	/**
	 * Setzt eine Monster in die Map ein.
	 *
	 */
	public void spawnMonster() {
		Monster m = new Nobbin(spawn_monster);
		monster.add(m);
	}

	public void removeMonster(int i) {
		monster.remove(i);
	}

	// sonstige

	public JSONArray getSpawn_monster() {
		return spawn_monster;
	}

	public int getMonsterAmmount() {
		return monster.size();
	}

	/// Kirsche

	// typische getter und setter

	public Kirsche getKirsche() {
		return kirsche;
	}

	public void setKirsche(Kirsche k) {
		kirsche = k;
	}

	// Kische verstecke/anzeigen

	public void showKirsche() {
		kirsche.setVisible(true);
	}

	public void hideKirsche() {
		kirsche.setVisible(false);
	}

	/// Tunnel

	// typische getter und setter

	public ArrayList<Tunnel> getTunnel() {
		return tunnel;
	}

	public void setTunnel(ArrayList t) {
		tunnel = t;
	}

	// Tunnel in Liste einfügen/entfernen

	public void addTunnel(Tunnel t) { // ehemals tunnelSetzen
		tunnel.add(t);
	}

	public void removeTunnel(int i) {
		tunnel.remove(i);
	}

	//// Geldsäcke

	// getter und setter Geldsäcke
	public ArrayList<Geldsack> getGeldsaecke() {
		return geldsaecke;
	}

	public void setGeldsaecke(ArrayList g) {
		geldsaecke = g;
	}

	// add und remove Geldsäcke

	public void addGeldsack(Geldsack g) {
		geldsaecke.add(g);
	}

	public Geldsack removeGeldsack(int i) {
		return geldsaecke.remove(i);
	}

	//// Geld


	// typische getter und setter
	public ArrayList<Geld> getGeld() {
		return geld;
	}

	public void setGeld(ArrayList g) {
		geld = g;
	}

	// add und remove Geld

	public void addGeld(Geld g) {
		geld.add(g);
	}

	public Geld removeGeld(int i) {
		return geld.remove(i);
	}

	//TODO: Graben
	public void graben(Spieler sp1, Spieler sp2) {
	}

	/// Feuerball

	//getter und setter
	public ArrayList<Feuerball> getFeuerball() {
		return feuerball;
	}
	public void setFeuerball(ArrayList f){
		feuerball=f;
	}

	public void addFeuerball(Feuerball f) {
		feuerball.add(f);
	}

	/// Diamanten

	//getter und setter

	public ArrayList<Diamant> getDiamonds() {
		return diamanten;
	}

	/// Others

	public JSONArray getPGSize() {
		return playground_size;
	}

	public JSONArray getSpawn_SP1(){return spawn_sp1;}
	public JSONArray getSpawn_SP2(){return spawn_sp2;}

	public void setzeHobbin(JSONArray pos) {

		monster.add( new Hobbin(pos) );
	}
}
