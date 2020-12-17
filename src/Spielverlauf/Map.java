package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Map {

	private ArrayList<Monster> monster;
	private JSONArray spawn_monster;
	private Spieler sp1;
	private JSONArray spawn_sp1;
	private Spieler sp2;
	private JSONArray spawn_sp2;
	private Diamant[] diamanten;
	private Geldsack[] geldsaecke;
	private Tunnel[] tunnel;
	private Kirsche kirsche; // TODO: prüfen ob sinnvoll zu speichern
	// TODO: ggf. JSON simple verwenden

		public Map(JSONObject obj){

		spawn_monster = obj.getJSONArray("spawn_mon");
		spawn_sp1 = obj.getJSONArray("spawn_p1");
		spawn_sp2 = obj.getJSONArray("spawn_p2");

		JSONArray pos_diam = obj.getJSONArray("pos_diam");

		for (int i = 0; i < pos_diam.length(); i++) {

			JSONArray single_diamant = pos_diam.getJSONArray(i);

			diamanten[i] = new Diamant( single_diamant );
		}
			
		JSONArray pos_money = obj.getJSONArray("pos_money");

		for (int i = 0; i < pos_money.length(); i++) {
			
			JSONArray single_money = pos_money.getJSONArray(i);

			geldsaecke[i] = new Geldsack( single_money );
		}

		JSONObject kind_tun = obj.getJSONObject("pos_tun");

		JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");
		JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");
		JSONArray pos_tun_space = kind_tun.getJSONArray("space");

		for (int i = 0; i < pos_tun_vertikal.length(); i++) {
			
			JSONArray single_tunnel = pos_tun_vertikal.getJSONArray(i);

			tunnel[i] = new Tunnel(p );
		}

	}

	// Singelplayer
	public boolean spawnSpieler(Spieler s){

			if( !s.equals(null) ) {
				sp1 = s;
				return true;
			}
			else
				return false; // falls Spieler bereits belegt
	}

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

	public void setMonster(Monster m){
		monster.add(m);
	}

	// TODO: remove Monster

	public boolean setKirsche(Kirsche k){

		if( !k.equals(null) ) {
			kirsche = k;
			return true;
		}
		else
			return false; // falls Spieler bereits belegt
	}

	// TODO: remove Kirsche


	// TODO: Geldsack animation Auswirklung

	public void setTunnel(JSONArray t ) { // ehemeals tunnelSetzen



	}

	public JSONArray getSpawn_monster() {
		return spawn_monster;
	}

	public int getMonsterAmmount (){
			return monster.size();
	}

}