package Spielverlauf;

import Spielbereitstellug.Spiel;
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
	private ArrayList<Feuerball> feuerball;
	private ArrayList<Diamant> diamanten;
	private ArrayList<Geldsack> geldsaecke;
	private ArrayList<Geld> geld;
	private ArrayList<Tunnel> tunnel;
	private Kirsche kirsche;
	private double[] border = {0.4, 0.2}; // Wandstärke x,y

	public Map(JSONObject obj, int[] panelSize, Skin sk) {

		// Setup GUI

		panel_size = panelSize;
		skin = sk;

		// Setup Playground

		playground_size = obj.getJSONArray("pg_size");

		int felderX = playground_size.getInt(0);
		int felderY = playground_size.getInt(1);

		int w_temp_size = panel_size[0] / (int)( felderX + ( 2*border[0]) );
		int h_temp_size = panel_size[1] / (int)( felderY + ( 2*border[1]) );

		if (w_temp_size > h_temp_size)
			field_size = h_temp_size;
		else
			field_size = w_temp_size;

		// Set initial Content

		geldsaecke = new ArrayList<Geldsack>();
		diamanten = new ArrayList<Diamant>();
		tunnel = new ArrayList<Tunnel>();

		kirsche = new Kirsche(spawn_monster);
		geld = new ArrayList<Geld>();
		feuerball= new ArrayList<Feuerball>();

		sp1 = null;
		sp2 = null;
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

	// GUI handling

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		// Prepering

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		// Zeichne Hintergrund

		BufferedImage backgroundImg = skin.getImage("backg_typ1", field_size);
		TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(slatetp);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Zeichne Tunnel
		BufferedImage horzTunImg = skin.getImage("tunnel_hori", field_size);
		BufferedImage vertTunImg = skin.getImage("tunnel_vert", field_size);
		BufferedImage spacTunImg = skin.getImage("tunnel_space", field_size);

		for (int i = 0; i < tunnel.size(); i++) {

			Tunnel single_item = tunnel.get(i);

			BufferedImage unscaledImg;

			if (single_item.typ.equals(TUNNELTYP.HORIZONTAL))
				unscaledImg = horzTunImg;
			else if (single_item.typ.equals(TUNNELTYP.VERTICAL))
				unscaledImg = vertTunImg;
			else
				unscaledImg = spacTunImg;
			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (unscaledImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (unscaledImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			g.drawImage(unscaledImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Zeichne Diamanten
		BufferedImage diamImg = skin.getImage("diamond", field_size);

		for (int i = 0; i < diamanten.size(); i++) {
			Diamant single_item = diamanten.get(i);

			int x_field = single_item.getField().getInt(0) - 1;
			int y_field = single_item.getField().getInt(1) - 1;
			int x_pixel = x_field * field_size - (diamImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (diamImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			g.drawImage(diamImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}


		// Zeichne Geld
		BufferedImage moneyPodImg = skin.getImage("money_static", field_size);

		for (int i = 0; i < geldsaecke.size(); i++) {
			Geldsack single_item = geldsaecke.get(i);

			int x_field = single_item.getField().getInt(0) - 1;
			int y_field = single_item.getField().getInt(1) - 1;
			int x_pixel = x_field * field_size - (diamImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (diamImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;


			g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Monster

		BufferedImage monsterImg = skin.getImage("hobbin_left_f1", field_size);

		for (int i = 0; i < monster.size(); i++) {
			Monster single_item = monster.get(i);

			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (monsterImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (monsterImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			// scaling ...

			g.drawImage(monsterImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Feuerball

		BufferedImage feuerballImg = skin.getImage("fireball_red_f1", field_size);

		for (int i = 0; i < feuerball.size(); i++) {
			Feuerball single_item = feuerball.get(i);

			int x_field = single_item.getPosition()[0] - 1;
			int y_field = single_item.getPosition()[1] - 1;
			int x_pixel = x_field * field_size - (feuerballImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (feuerballImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			g.drawImage(feuerballImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Geld

		BufferedImage geldImg = skin.getImage("money_fall_f6", field_size);

		for (int i = 0; i < geld.size(); i++) {
			Geld single_item = geld.get(i);

			int x_field = single_item.getField().getInt(0) - 1;
			int y_field = single_item.getField().getInt(1) - 1;
			int x_pixel = x_field * field_size - (geldImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (geldImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			// scaling ...

			g.drawImage(geldImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Spieler

		if(!sp1.equals(null)) {
			BufferedImage sp1Img = skin.getImage("dig_red_up_f1", field_size);

			int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
			int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);

			g.drawImage(sp1Img, x_pixel, y_pixel, null);

		}

	}

	@Override
	public Dimension getPreferredSize() {

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		return new Dimension(playground_size.getInt(0) * field_size + 2* borderOffsetX, playground_size.getInt(1) * field_size + 2* borderOffsetY);
	}

	////// Content handling functions

	/// Spieler

	public void spawnSpieler(boolean isMultiplayer) {

		int x_field = spawn_sp1.getInt(0) - 1;
		int y_field = spawn_sp1.getInt(1) - 1;

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		int x_pixel = x_field * field_size + (field_size / 2) + borderOffsetX;
		int y_pixel = y_field * field_size + (field_size / 2) + borderOffsetY;

		System.out.println(field_size);

		sp1 = new Spieler(x_pixel, y_pixel);

		if(isMultiplayer)
			x_field = spawn_sp2.getInt(0) - 1;
			y_field = spawn_sp2.getInt(1) - 1;

			x_pixel = x_field * field_size + (field_size / 2) + borderOffsetX;
			y_pixel = y_field * field_size + (field_size / 2) + borderOffsetY;

			sp2 = new Spieler(x_pixel, y_pixel);

	}

	public Spieler getSP1(){ return sp1;}
	public Spieler getSP2(){ return sp2;}
	public JSONArray getSpawnSP1(){return spawn_sp1;}
	public JSONArray getSpwanSP2(){return spawn_sp2;}
	/// Monster

	// typische getter und setter

	public ArrayList<Monster> getMonster() {
		return monster;
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
}
