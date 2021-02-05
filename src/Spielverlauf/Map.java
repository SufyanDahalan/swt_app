package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Map implements Serializable {

	// Playground

	private int[] playground_size;

	// Contente
	private ArrayList<Monster> monster;
	private int[] spawn_monster;
	private int[] spawn_cherry;
	private int[] spawn_sp1;
	private int[] spawn_sp2;
	private ArrayList<Feuerball> feuerball;
	private ArrayList<Diamant> diamanten;
	private ArrayList<Geldsack> geldsaecke;
	private ArrayList<Geld> geld;
	private ArrayList<Tunnel> tunnel;
	private Kirsche kirsche;
	private boolean bonus;

	/***
	 * Konstruktor, baut Map-Objekt aus übergebenem JSON-Objekt und Skin auf,
	 * bezieht Feldgröße und Positionen von Monstern, Spielern und Items aud dem JSON-Objekt
	 * @param obj JSON
	 * @param sk Skin
	 */
	public Map(JSONObject obj, Skin sk) {

		// Set initial Content

		playground_size = toArray(obj.getJSONArray("pg_size"));
		spawn_monster = toArray(obj.getJSONArray("spawn_mon"));
		spawn_sp1 = toArray(obj.getJSONArray("spawn_p1"));
		spawn_sp2 = toArray(obj.getJSONArray("spawn_p2"));
		spawn_cherry = toArray(obj.getJSONArray("spawn_cherry"));

		geldsaecke = new ArrayList<Geldsack>();
		diamanten = new ArrayList<Diamant>();
		tunnel = new ArrayList<Tunnel>();

		geld = new ArrayList<Geld>();
		feuerball= new ArrayList<Feuerball>();

		monster = new ArrayList<Monster>();


		// Füge initiale Diamanten ein

		JSONArray pos_diam = obj.getJSONArray("pos_diam");

		for (int i = 0; i < pos_diam.length(); i++) {

			int[] item_pos = toArray(pos_diam.getJSONArray(i));

			diamanten.add(new Diamant(item_pos));
		}

		// Füge initiale Geldsäcke ein

		JSONArray pos_money = obj.getJSONArray("pos_money");

		for (int i = 0; i < pos_money.length(); i++) {

			int[] item_pos = toArray(pos_money.getJSONArray(i));

			geldsaecke.add(new Geldsack(item_pos));
		}

		//// Set initial tunnels

		JSONObject kind_tun = obj.getJSONObject("pos_tun");

		// Set vertical tunnel
		JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");

		for (int i = 0; i < pos_tun_vertikal.length(); i++) {

			int[] single_tunnel = toArray(pos_tun_vertikal.getJSONArray(i));

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.VERTICAL));
		}

		// Set landscape tunnel
		JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");

		for (int i = 0; i < pos_tun_horizontal.length(); i++) {

			int[] single_tunnel = toArray(pos_tun_horizontal.getJSONArray(i));

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.HORIZONTAL));
		}

		// Set holes
		JSONArray pos_tun_space = kind_tun.getJSONArray("space");

		for (int i = 0; i < pos_tun_space.length(); i++) {

			int[] single_tunnel = toArray(pos_tun_space.getJSONArray(i));

			tunnel.add(new Tunnel(single_tunnel, TUNNELTYP.SPACE));
		}

	}

	/***
	 * Copy-Konstruktor
	 * @param m bestehende Map
	 */
	public Map(Map m) {

		playground_size = m.playground_size.clone();

		monster = new ArrayList<Monster>();
		spawn_monster = m.spawn_monster.clone();
		spawn_sp1 = m.spawn_sp1.clone();
		spawn_sp2 = m.spawn_sp2.clone();
		spawn_cherry = m.spawn_cherry.clone();
		feuerball = new ArrayList<Feuerball>();
		diamanten = new ArrayList<Diamant>();
		for(Diamant d : m.diamanten){
			diamanten.add(new Diamant(d));
		}
		geldsaecke = new ArrayList<Geldsack>();
		for(Geldsack g : m.geldsaecke){
			geldsaecke.add(new Geldsack(g));
		}
		geld = new ArrayList<Geld>();
		tunnel = new ArrayList<Tunnel>(m.tunnel);
		kirsche = m.kirsche;
		bonus = false;
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

	public void removeMonster(int i) {
		monster.remove(i);
	}

	// sonstige

	public int[] getSpawn_monster() {
		return spawn_monster;
	}

	public int getMonsterAmmount() {
		return monster.size();
	}

	public void setBonus(boolean b){
		bonus = b;
	};

	/// Kirsche

	// typische getter und setter

	public Kirsche getKirsche() {
		return kirsche;
	}

	public void setKirsche(Kirsche k) {
		kirsche = k;
	}

	public void removeKirsche(){kirsche = null;}

	/// Tunnel

	// typische getter und setter

	public ArrayList<Tunnel> getTunnel() {
		return tunnel;
	}

	public ArrayList<Tunnel> getTunnel(int[] fp){

		ArrayList<Tunnel> tunneltreffer = new ArrayList<Tunnel>();

		for(Tunnel t: tunnel){
			if( Arrays.equals(t.getField(), fp) )
				tunneltreffer.add(t);
		}
		return tunneltreffer;
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

	public int[] getPGSize() {
		return playground_size;
	}

	public int[] getSpawn_SP1(){return spawn_sp1;}
	public int[] getSpawn_SP2(){return spawn_sp2;}

	public void setzeHobbin(int[] pos) {
		monster.add( new Hobbin(pos) );
	}

	private int[] toArray(JSONArray ja){

		int[] ia = new int[2];

		ia[0] = ja.getInt(0);
		ia[1] = ja.getInt(1);

		return ia;
	}

	/***
	 * Export Methode, kann erstellte Map wieder als JSON-Objekt speichern, nützlich für den Level-Editor
	 * @return JSONObject der Map
	 */
	public JSONObject exportStaticsAsJSON(){

		JSONObject obj = new JSONObject();

		obj.put("pg_size", new JSONArray(new int[]{playground_size[0], playground_size[1]}));
		obj.put("spawn_p1", new JSONArray(new int[]{spawn_sp1[0],spawn_sp1[1]}));
		obj.put("spawn_p2", new JSONArray(new int[]{spawn_sp2[0],spawn_sp2[1]}));
		obj.put("spawn_mon", new JSONArray(new int[]{spawn_monster[0],spawn_monster[1]}));
		obj.put("bonus", bonus);

		if(kirsche != null)
			obj.put("spawn_cherry", new JSONArray(new int[]{kirsche.getField()[0],kirsche.getField()[1]}));

		// Diamneten
		{
			JSONArray D = new JSONArray();

			for (Iterator<Diamant> iterator = diamanten.iterator(); iterator.hasNext(); ) {
				Diamant d = iterator.next();

				D.put(new JSONArray(new int[]{d.field[0], d.field[1]}));
			}
			obj.put("pos_diam", D);
		}

		// Tunnel
		{
			JSONObject temp = new JSONObject();
			JSONArray tempV = new JSONArray();
			JSONArray tempH = new JSONArray();
			JSONArray tempS = new JSONArray();

			for (Iterator<Tunnel> iterator = tunnel.iterator(); iterator.hasNext(); ) {
				Tunnel t = iterator.next();

				if (t.typ == TUNNELTYP.VERTICAL)
					tempV.put(new JSONArray(new int[]{t.fieldPosition[0],t.fieldPosition[1]}));
				else if (t.typ == TUNNELTYP.HORIZONTAL)
					tempH.put(new JSONArray(new int[]{t.fieldPosition[0],t.fieldPosition[1]}));
				else
					tempS.put(new JSONArray(new int[]{t.fieldPosition[0],t.fieldPosition[1]}));


			}

			temp.put("vertikal", tempV);
			temp.put("horizontal", tempH);
			temp.put("space", tempS);
			obj.put("pos_tun", temp);
		}

		return obj;
	}

	public int[] getSpawn_cherry() {
		return spawn_cherry;
	}
}
