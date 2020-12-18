package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Map extends JPanel {

	// GUI
	private int[] panel_size;

	// Playground

	private JSONArray playground_size;

	// Contente
	private ArrayList<Monster> monster;
	private JSONArray spawn_monster;
	private Spieler sp1;
	private JSONArray spawn_sp1;
	private Spieler sp2;
	private JSONArray spawn_sp2;
	private ArrayList<Diamant> diamanten;
	private ArrayList<Geldsack> geldsaecke;
	private ArrayList<Tunnel> tunnel;
	private Kirsche kirsche; // TODO: prüfen ob sinnvoll zu speichern
	// TODO: ggf. JSON simple verwenden

		public Map(JSONObject obj, int[] s){

			// Setup GUI

			size = s;

			// Setup Playground

			playground_size = obj.getJSONArray("pg_size");


			// Set Content

			kirsche = new Kirsche(spawn_monster);

			spawn_monster = obj.getJSONArray("spawn_mon");
			spawn_sp1 = obj.getJSONArray("spawn_p1");
			spawn_sp2 = obj.getJSONArray("spawn_p2");

			JSONArray pos_diam = obj.getJSONArray("pos_diam");

			for (int i = 0; i < pos_diam.length(); i++) {

				JSONArray single_diamant = pos_diam.getJSONArray(i);

				diamanten.add( new Diamant( single_diamant ) );
			}

			JSONArray pos_money = obj.getJSONArray("pos_money");

			for (int i = 0; i < pos_money.length(); i++) {

				JSONArray single_money = pos_money.getJSONArray(i);

				geldsaecke.add( new Geldsack( single_money ) );
			}

			//// Set initial tunnels

			JSONObject kind_tun = obj.getJSONObject("pos_tun");

			// Set vertical tunnel
			JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");

			for (int i = 0; i < pos_tun_vertikal.length(); i++) {

				JSONArray single_tunnel = pos_tun_vertikal.getJSONArray(i);

				tunnel.add ( new TunnelVertikal(single_tunnel) );
			}

			// Set landscape tunnel
			JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");

			for (int i = 0; i < pos_tun_horizontal.length(); i++) {

				JSONArray single_tunnel = pos_tun_horizontal.getJSONArray(i);

				tunnel.add ( new TunnelHorizontal(single_tunnel) );
			}

			// Set holes
			JSONArray pos_tun_space = kind_tun.getJSONArray("space");

			for (int i = 0; i < pos_tun_space.length(); i++) {

				JSONArray single_tunnel = pos_tun_space.getJSONArray(i);

				tunnel.add ( new TunnelHorizontal(single_tunnel) );
			}

	}


	// GUI handling

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < diamanten.size(); i++) {

			g.drawRect(single_diamant.getInt(1)*field_size, field_size, field_size);
			g.setColor(Color.RED);
		}


	}

	@Override
	public Dimension getPreferredSize() {

		int field_size;
		int w_temp_size = panel_size[0]/playground_size.getInt(0) ;
		int h_temp_size = panel_size[1]/playground_size.getInt(1) ;

		if (w_temp_size > h_temp_size)
			field_size = h_temp_size;
		else
			field_size = w_temp_size;

		return new Dimension(playground_size[0]*field_size, playground_size[1]*field_size);
	}

	// Content handling

	/**
	 * Setzt einen Spieler in die Map ein.
	 * @param s Spieler der in die Karte eingesetzt wird.
	 * @return Liefert false, falls bereits ein Spieler in der Karte ist.
	 */
	// Singelplayer
	public boolean spawnSpieler(Spieler s){

			if( !s.equals(null) ) {
				sp1 = s;
				return true;
			}
			else
				return false; // falls Spieler bereits belegt
	}

	/**
	 * Setzt zwei Spieler in die Map ein.
	 * @param s1 Spieler 1 der in die Karte eingesetzt wird.
	 * @param s2 Spieler 2 der in die Karte eingesetzt wird.
	 * @return Liefert false, falls bereits ein Spieler in der Karte ist.
	 */
	// Multiplayer
	public boolean spawnSpieler(Spieler s1, Spieler s2){

		if( !s1.equals(null) && !s2.equals(null) ) {
			sp1 = s1;
			sp2 = s2;
			return true;
		}
		else
			return false; // falls Spieler bereits belegt
	}

	/**
	 * Setzt eine Monster in die Map ein.
	 * @param m Monster das in die Karte eingesetzt wird.
	 */
	public void spawnMonster(Monster m){
		monster.add(m);
	}

	public void removeMonster() {
		// TODO: impl.
	}

	public void setKirsche(){
		kirsche.setVisible(true);
	}

	public void removeKirsche() {
		kirsche.setVisible(false);
	}

	// TODO: Geldsack animation Auswirklung

	public void setTunnel(Tunnel t ) { // ehemals tunnelSetzen
		tunnel.add(t);
	}

	public JSONArray getSpawn_monster() {
		return spawn_monster;
	}

	public int getMonsterAmmount () {
		return monster.size();
	}

}