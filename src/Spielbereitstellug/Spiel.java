package Spielbereitstellug;

import Spielverlauf.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;


public class Spiel extends JPanel implements Runnable {

	// System-/ Filestructure

	private final String skinName = "original_skin"; // Skinnname
	private final String levelfolder_name = "bin/level/"; // ./level/level-01.json ...
	private final String skinfolder_name = "bin/skins/"; // ./skin/sink_original.json,...

	// static content
	private Skin current_skin;
	private ArrayList<Map> mapChain;
	int current_map = 0;
	private final double[] border = {0.4, 0.2}; // Wandstärke x,y

	// temp. Att.

	private Level aktuelles_level;
	private int spielstand;

	private int field_size;

	// Spieler

	private Spieler sp1;
	private Spieler sp2;
	private Geldsack gs;

	public Spiel(int[] panel_size) {

		// initialisiere Skin
		current_skin = new Skin(new File(skinfolder_name), skinName); // Loades original_skin.png and original.json from skins/

		// initialisiere Mapchain

		String[] maps = new File(levelfolder_name).list(); // read Level from Folder

		// create Map and add it to chain

		System.out.println("Anzahl der gelesenen Karten: " + maps.length);

		mapChain = new ArrayList<Map>();

		for(int i=0; i<maps.length; i++) {

			// read Level-File
			JSONObject obj = null;
			try {
				obj = new JSONObject(new String(Files.readAllBytes(Paths.get(levelfolder_name + maps[i]))));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create Map and add to chain
			mapChain.add(new Map(obj));
		}

		// add Player

		sp1 = null;
		sp2 = null;



		aktuelles_level = createNextLevel();

		// refresh sizing

		refreshFS(panel_size);


		// TESTING


		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();
		aktuelles_level.getMap().spawnMonster();

		JSONArray pos = new JSONArray("[1,1]");

		aktuelles_level.getMap().setzeHobbin(pos);
		aktuelles_level.getMap().setzeHobbin(pos);
		aktuelles_level.getMap().setzeHobbin(pos);
		aktuelles_level.getMap().setzeHobbin(pos);

		System.out.println("Anzahl Nobbins" + aktuelles_level.getMap().getNobbins().size());
		System.out.println("Anzahl Hobbins" + aktuelles_level.getMap().getHobbins().size());

	}

	private void refreshFS(int[] panel_size) {
		int felderX = aktuelles_level.getMap().getPGSize().getInt(0);
		int felderY = aktuelles_level.getMap().getPGSize().getInt(1);

		int w_temp_size = panel_size[0] / (int)( felderX + ( 2*border[0]) );
		int h_temp_size = panel_size[1] / (int)( felderY + ( 2*border[1]) );

		if (w_temp_size > h_temp_size)
			field_size = h_temp_size;
		else
			field_size = w_temp_size;
	}

	public JSONArray getSPField(Spieler s){

		int x = (s.getPosition()[0]/field_size) + 1;
		int y = (s.getPosition()[1]/field_size) + 1;

		JSONArray pos = new JSONArray("[" + x + "," + y + "]");

		return pos;
	}

	/**
	 *	Spiellogik, die Positionen prüft und Ereignisse aufruft.
	 * @return false if plaer dead; else true if game schoud be contiued
	 */

	private boolean loop(){

		/// Prüfroutinen

		// alle Diamanten gesammel?

		if(aktuelles_level.getMap().getDiamonds().size() == 0) {
			// dann nächstes Level
			aktuelles_level = createNextLevel();
			// vervolständigen zB von Scores

		}

		// alle Monster getötet?
		/*
		if(aktuelles_level.getMap().getMonster().size() == 0) {
			// dann nächstes Level
			aktuelles_level = createNextLevel();

			// vervolständigen zB von Scores

		}
		*/

		// Folgende Ereignisse müssen ausfrmuliert und entsprechende Folgen triggern

		// Spieler trifft Diamant
		ArrayList<Diamant> diamants = aktuelles_level.getMap().getDiamonds();

		for (Iterator<Diamant> iterator = diamants.iterator(); iterator.hasNext();) {
			Diamant single_item = iterator.next();
			if (single_item.getField().similar(getSPField(sp1))) {
				iterator.remove();
			}
		}
		/*
		for (Iterator<Diamant> iterator = diamants.iterator(); iterator.hasNext();) {
			Diamant single_item = iterator.next();
			if(single_item.getField().similar( getSPField(sp2) )) {
				iterator.remove();
			}
		}
		 */

		// Spieler trifft Monster
		ArrayList<Monster> monsters= aktuelles_level.getMap().getMonster();
		for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
			Monster m = iterator.next();
			if(m.getField().similar( getSPField(sp1) )){
				if(sp1.isAlive()) {
					spawnSpieler(false);
					System.out.println(sp1.getLeben());
				}
				else
				System.out.println("Ende");
			}
			//sp2 üp
		}

		// Spieler trifft Boden
		if (sp1.getMoveDir() == DIRECTION.RIGHT || sp1.getMoveDir() == DIRECTION.LEFT)
			aktuelles_level.getMap().addTunnel(new Tunnel(getSPField(sp1), TUNNELTYP.HORIZONTAL));
		else if (sp1.getMoveDir() == DIRECTION.UP || sp1.getMoveDir() == DIRECTION.DOWN)
			aktuelles_level.getMap().addTunnel(new Tunnel(getSPField(sp1), TUNNELTYP.VERTICAL));
/*
		if (sp2.getMoveDir() == DIRECTION.RIGHT || sp2.getMoveDir() == DIRECTION.LEFT)
			aktuelles_level.getMap().addTunnel(new Tunnel(getSPField(sp2), TUNNELTYP.HORIZONTAL));
		else if (sp2.getMoveDir() == DIRECTION.UP || sp2.getMoveDir() == DIRECTION.DOWN)
			aktuelles_level.getMap().addTunnel(new Tunnel(getSPField(sp2), TUNNELTYP.VERTICAL));
*/
		// Spieler trifft Geldsack
		ArrayList<Geldsack> geldsacke= aktuelles_level.getMap().getGeldsaecke();
		for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext();) {
			Geldsack g = iterator.next();
			if (g.getField().similar(getSPField(sp1))) {
				if (sp1.getMoveDir() == DIRECTION.RIGHT) {
					System.out.println("push right");
					g.setField(getSPField(sp1));
				} else if (sp1.getMoveDir() == DIRECTION.LEFT) {
					System.out.println("push left");
				}
			}
		}
		// Spieler trifft Geld
		// Spieler trifft Wand
		// Spieler trifft Kirsche -> Bonsmodus aktivieren
		// Hobbin trifft Diamant
		// Hobbin trifft Boden

		// Hobbin trifft Geldsack (legend oder fallend unterscheiden)
		// Hobbin trifft Geld
		// Hobbin trifft Nobbin

		// Hobbin trifft Hobbin
		// Hobbin trifft Wand
		// Hobbin trifft Kirsche
		// Nobbin trifft Boden
		// Nobbin trifft Nobbin

		// Nobbin trifft Geldsack (liegend oder fallend unterscheiden)
		// Nobbin trifft Geld

		// Bewegung werden durch Algorithmus oder Tastatuseingabe oder Netzwerksteuerung direkt im Mapobjekt geändert und durch repaint übernommen
		/// in jedem Fall wir true zurückgegeben. Andernfalls beendet sich die loop() und das spiel gilt als beendet. Darauf folgt dann die eintragung der ergebnisse ect.

		if(!sp1.isAlive())
			return false; // Spiel beendet

		long sleepTime = 10;

		if (sleepTime >= 0) {
			try{Thread.sleep(sleepTime);} catch(InterruptedException e){}
		}

		return true;

	}

	public void spawnSpieler(boolean isMultiplayer) {

		int x_field = aktuelles_level.getMap().getSpawn_SP1().getInt(0) - 1;
		int y_field = aktuelles_level.getMap().getSpawn_SP1().getInt(1) - 1;

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		int x_pixel = x_field * field_size + (field_size / 2) + borderOffsetX;
		int y_pixel = y_field * field_size + (field_size / 2) + borderOffsetY;

		System.out.println(field_size);

		sp1 = new Spieler(x_pixel, y_pixel);

		if(isMultiplayer) {
			x_field = aktuelles_level.getMap().getSpawn_SP2().getInt(0) - 1;
			y_field = aktuelles_level.getMap().getSpawn_SP2().getInt(1) - 1;

			x_pixel = x_field * field_size + (field_size / 2) + borderOffsetX;
			y_pixel = y_field * field_size + (field_size / 2) + borderOffsetY;

			sp2 = new Spieler(x_pixel, y_pixel);
		}

	}

	public Spieler getSP1(){
		return sp1;
	}

	// creates next Level, increases speed and decrease regtime

	private Level createNextLevel() {

		int new_s;
		int new_r;
		int new_mm;
		Map nextMap;
		current_map = (current_map+1)%mapChain.size();
		System.out.println("create next Level with Map: " + current_map);
		nextMap = new Map(mapChain.get(current_map)); // nächste Map als KOPIE!!! einsetzen. Sonst wird die Mapchain manipuliert und Folgelevel sind verändert.

		if (aktuelles_level != null) {

			new_s = aktuelles_level.getSpeed() + 1;
			new_r = aktuelles_level.getRegenTime() + 1;
			new_mm = aktuelles_level.getMaxMonster() + 1;
		}
		else{
			new_s = 0;
			new_r = 0;
			new_mm = 4;
		}

		return new Level(new_s, new_r, new_mm, nextMap);

	}

	public void beenden() {
		// TODO - implement Spiel.beenden
		throw new UnsupportedOperationException();
	}

	public void pausieren() {
		// TODO - implement Spiel.pausieren
		throw new UnsupportedOperationException();
	}

	// GUI handling

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		// Prepering

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		// Zeichne Hintergrund

		BufferedImage backgroundImg = current_skin.getImage("backg_typ1", field_size);
		TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(slatetp);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Zeichne Tunnel
		BufferedImage horzTunImg = current_skin.getImage("tunnel_hori", field_size);
		BufferedImage vertTunImg = current_skin.getImage("tunnel_vert", field_size);
		BufferedImage spacTunImg = current_skin.getImage("tunnel_space", field_size);

		ArrayList<Tunnel> tunnel = aktuelles_level.getMap().getTunnel();

		for (int i = 0; i < tunnel.size(); i++) {

			Tunnel single_item = tunnel.get(i);

			BufferedImage unscaledImg;

			if (single_item.getTyp().equals(TUNNELTYP.HORIZONTAL))
				unscaledImg = horzTunImg;
			else if (single_item.getTyp().equals(TUNNELTYP.VERTICAL))
				unscaledImg = vertTunImg;
			else
				unscaledImg = spacTunImg;
			int x_field = single_item.getField().getInt(0) - 1;
			int y_field = single_item.getField().getInt(1) - 1;
			int x_pixel = x_field * field_size - (unscaledImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (unscaledImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			g.drawImage(unscaledImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Zeichne Diamanten
		BufferedImage diamImg = current_skin.getImage("diamond", field_size);

		ArrayList<Diamant> diamanten = aktuelles_level.getMap().getDiamonds();

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
		BufferedImage moneyPodImg = current_skin.getImage("money_static", field_size);

		ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

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

		BufferedImage hobbinImg = current_skin.getImage("hobbin_left_f1", field_size);

		ArrayList<Hobbin> hobbins = aktuelles_level.getMap().getHobbins();

		for (int i = 0; i < hobbins.size(); i++) {
			Hobbin single_item = hobbins.get(i);

			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (hobbinImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (hobbinImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			// scaling ...

			g.drawImage(hobbinImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		BufferedImage nobbinImg = current_skin.getImage("nobbin_f1", field_size);

		ArrayList<Nobbin> nobbins = aktuelles_level.getMap().getNobbins();

		for (int i = 0; i < nobbins.size(); i++) {
			Nobbin single_item = nobbins.get(i);

			int x_field = single_item.getPosition().getInt(0) - 1;
			int y_field = single_item.getPosition().getInt(1) - 1;
			int x_pixel = x_field * field_size - (nobbinImg.getWidth() / 2) + (field_size / 2) + borderOffsetX;
			int y_pixel = y_field * field_size - (nobbinImg.getHeight() / 2) + (field_size / 2) + borderOffsetY;

			// scaling ...

			g.drawImage(nobbinImg, x_pixel, y_pixel, null);

			// for testing purpose
			g.drawRect(x_field * field_size + borderOffsetX, y_field * field_size + borderOffsetY, field_size, field_size);
			g.setColor(Color.RED);
		}

		// Feuerball

		BufferedImage feuerballImg = current_skin.getImage("fireball_red_f1", field_size);

		ArrayList<Feuerball> feuerball = aktuelles_level.getMap().getFeuerball();

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

		BufferedImage geldImg = current_skin.getImage("money_fall_f6", field_size);

		ArrayList<Geld> geld = aktuelles_level.getMap().getGeld();

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

		if(sp1 != null) {
			BufferedImage sp1Img1 = current_skin.getImage("dig_red_rgt_f1", field_size);
			BufferedImage sp1Img2 = current_skin.getImage("dig_red_lft_f1", field_size);
			BufferedImage sp1Img3 = current_skin.getImage("dig_red_up_f1", field_size);
			BufferedImage sp1Img4 = current_skin.getImage("dig_red_dow_f1", field_size);
			if(sp1.getMoveDir()==DIRECTION.RIGHT) {
				int x_pixel = sp1.getPosition()[0] - (sp1Img1.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img1.getHeight() / 2);
				g.drawImage(sp1Img1, x_pixel, y_pixel, null);
			}
			if(sp1.getMoveDir()==DIRECTION.LEFT) {
				int x_pixel = sp1.getPosition()[0] - (sp1Img2.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img2.getHeight() / 2);
				g.drawImage(sp1Img2, x_pixel, y_pixel, null);
			}
			if(sp1.getMoveDir()==DIRECTION.UP) {
				int x_pixel = sp1.getPosition()[0] - (sp1Img3.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img3.getHeight() / 2);
				g.drawImage(sp1Img3, x_pixel, y_pixel, null);
			}
			if(sp1.getMoveDir()==DIRECTION.DOWN) {
				int x_pixel = sp1.getPosition()[0] - (sp1Img4.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img4.getHeight() / 2);
				g.drawImage(sp1Img4, x_pixel, y_pixel, null);
			}

		}

		if(sp2 != null) {
			BufferedImage sp2Img = current_skin.getImage("dig_gre_up_f1", field_size);

			int x_pixel = sp2.getPosition()[0] - (sp2Img.getWidth() / 2);
			int y_pixel = sp2.getPosition()[1] - (sp2Img.getHeight() / 2);

			g.drawImage(sp2Img, x_pixel, y_pixel, null);

		}

	}

	@Override
	public void run() {
		while(loop());
	}

	@Override
	public Dimension getPreferredSize() {

		int borderOffsetX = (int)(field_size*border[0]);
		int borderOffsetY = (int)(field_size*border[1]);

		JSONArray playground_size = aktuelles_level.getMap().getPGSize();

		return new Dimension(playground_size.getInt(0) * field_size + 2* borderOffsetX, playground_size.getInt(1) * field_size + 2* borderOffsetY);
	}


}