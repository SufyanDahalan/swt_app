package Spielverlauf;

public class Map {

	private Monster[] monster;
	private JSONArray spawn_monster;
	private Spieler sp1;
	private JSONArray spawn_sp1;
	private Spieler sp2;
	private JSONArray spawn_sp2;
	private Diamant[] diamanten;
	private Geldsack[] geldsaecke;
	private Tunnel[] tunnel;
	private Kirsche kirsche; // TODO: prüfen ob sinnvoll zu speichern


	public Map(JSONObject obj){

		spawn_monster = obj.getJSONArray("spawn_mon");
		spawn_sp1 = obj.getJSONArray("spawn_p1");
		spawn_sp2 = obj.getJSONArray("spawn_p2");

		JSONArray pos_diam = obj.getJSONArray("pos_diam");

		for (int i = 0; i < pos_diam.length(); i++) {

			int[] single_diamant = pos_diam.getJSONArray(i);

			diamanten[i] = new Diamant( single_diamant );
		}
			
		JSONArray pos_money = obj.getJSONArray("pos_money");

		for (int i = 0; i < pos_money.length(); i++) {
			
			int[] single_money = pos_money.getJSONArray(i);

			geldsaecke[i] = new Geldsack( single_money );
		}

		JSONObject kind_tun = obj.JSONObject("pos_tun");

		JSONArray pos_tun_vertikal = kind_tun.getJSONArray("vertikal");
		JSONArray pos_tun_horizontal = kind_tun.getJSONArray("horizontal");
		JSONArray pos_tun_space = kind_tun.getJSONArray("space");

		for (int i = 0; i < pos_tun_vertikal.length(); i++) {
			
			int[] single_tunnel = pos_tun_vertikal.getJSONArray(i);

			tunnel[i] = new Tunnel(p );
		}

	}


	// TODO: Geldsack animation Auswirklung




	/**
	 * 
	 * @param pos
	 */
	public void tunnelSetzen(int[] pos) {
		// TODO - implement Map.tunnelSetzen
		throw new UnsupportedOperationException()

	}

}