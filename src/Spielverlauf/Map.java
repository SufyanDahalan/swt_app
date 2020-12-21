package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Map extends JPanel {

	// GUI
	private int[] panel_size;
	private Skin skin;
	private int field_size;

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
	private ArrayList<Geld> geld;
	private ArrayList<Tunnel> tunnel;
	private Kirsche kirsche; // TODO: prüfen ob sinnvoll zu speichern

	public Map(JSONObject obj, int[] panelSize, Skin sk) {

		// Setup GUI

		panel_size = panelSize;
		skin = sk;

		// Setup Playground

		playground_size = obj.getJSONArray("pg_size");


		// Set initial Content

		kirsche = new Kirsche(spawn_monster);
		monster = new ArrayList<Monster>();
		geldsaecke = new ArrayList<Geldsack>();
		geld = new ArrayList<Geld>();
		diamanten = new ArrayList<Diamant>();
		tunnel = new ArrayList<Tunnel>();
		sp1 = null;
		sp2 = null;

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

	// GUI handling

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		// Zeichne Hintergrund

		BufferedImage backgroundImg = skin.getImage("backg_typ1");
		TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(slatetp);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Zeichne Tunnel
		BufferedImage horzTunImg = skin.getImage("tunnel_hori");
		BufferedImage vertTunImg = skin.getImage("tunnel_vert");
		BufferedImage spacTunImg = skin.getImage("tunnel_space");

		for (int i = 0; i < tunnel.size(); i++) {

			Tunnel single_item = tunnel.get(i);

			BufferedImage unscaledImg;

			if (single_item.typ.equals(TUNNELTYP.HORIZOTAL))
				unscaledImg = horzTunImg;
			else if (single_item.typ.equals(TUNNELTYP.VERTICAL))
				unscaledImg = vertTunImg;
			else
				unscaledImg = spacTunImg;
			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (unscaledImg.getWidth() / 2) + (field_size / 2);
			int y_pixel = y_field * field_size - (unscaledImg.getHeight() / 2) + (field_size / 2);

			g.drawImage(unscaledImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size, y_field * field_size, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Zeichne Diamanten
		BufferedImage diamImg = skin.getImage("diamond");

		for (int i = 0; i < diamanten.size(); i++) {
			Diamant single_item = diamanten.get(i);

			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (diamImg.getWidth() / 2) + (field_size / 2);
			int y_pixel = y_field * field_size - (diamImg.getHeight() / 2) + (field_size / 2);

			g.drawImage(diamImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size, y_field * field_size, field_size, field_size);
			g.setColor(Color.RED);
		}


		// Zeichne Geld
		BufferedImage moneyPodImg = skin.getImage("money_static");

		for (int i = 0; i < geldsaecke.size(); i++) {
			Geldsack single_item = geldsaecke.get(i);

			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (diamImg.getWidth() / 2) + (field_size / 2);
			int y_pixel = y_field * field_size - (diamImg.getHeight() / 2) + (field_size / 2);


			g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size, y_field * field_size, field_size, field_size);
			g.setColor(Color.RED);
		}

	}

	@Override
	public Dimension getPreferredSize() {

		int w_temp_size = panel_size[0] / playground_size.getInt(0);
		int h_temp_size = panel_size[1] / playground_size.getInt(1);

		if (w_temp_size > h_temp_size)
			field_size = h_temp_size;
		else
			field_size = w_temp_size;

		return new Dimension(playground_size.getInt(0) * field_size, playground_size.getInt(1) * field_size);
	}

	////// Content handling functions

	/// Spieler

	/**
	 * Setzt einen Spieler in die Map ein.
	 *
	 * @param s Spieler der in die Karte eingesetzt wird.
	 * @return Liefert false, falls bereits ein Spieler in der Karte ist.
	 */
	// Singelplayer
	public boolean spawnSpieler(Spieler s) {

		if (!s.equals(null)) {
			sp1 = s;
			return true;
		} else
			return false; // falls Spieler bereits belegt
	}

	/**
	 * Setzt zwei Spieler in die Map ein.
	 *
	 * @param s1 Spieler 1 der in die Karte eingesetzt wird.
	 * @param s2 Spieler 2 der in die Karte eingesetzt wird.
	 * @return Liefert false, falls bereits ein Spieler in der Karte ist.
	 */
	// Multiplayer
	public boolean spawnSpieler(Spieler s1, Spieler s2) {

		if (!s1.equals(null) && !s2.equals(null)) {
			sp1 = s1;
			sp2 = s2;
			return true;
		} else
			return false; // falls Spieler bereits belegt
	}

	/// Monster

	// typische getter und setter

	public ArrayList<Monster> getMonster() {
		return monster;
	}

	// add and remove

	/**
	 * Setzt eine Monster in die Map ein.
	 *
	 * @param m Monster das in die Karte eingesetzt wird.
	 */
	public void spawnMonster(Monster m) {
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

}
