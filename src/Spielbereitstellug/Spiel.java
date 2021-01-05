package Spielbereitstellug;

import Spielverlauf.*;
import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class Spiel extends JPanel implements Runnable {

	//dev stuff

	boolean devFrames = false;

	// System-/ Filestructure

	private final String skinName = "original_skin"; // Skinnname
	private final String levelfolder_name = "bin/level/"; // ./level/level-01.json ...
	private final String skinfolder_name = "bin/skins/"; // ./skin/sink_original.json,...

	// static content
	private final Skin current_skin;
	private final ArrayList<Map> mapChain;
	int current_map = 0;
	private final double[] border = {0.4, 0.2}; // Wandstärke x,y
	private final double topbarHeight = 1; // Faktor von Feldgröße

	// temp. Att.

	private Level aktuelles_level;
	private int spielstand;

	private int field_size;
	private int old_field_size;

	// Spieler

	private Spieler sp1;
	private Spieler sp2;

	// Feurball

	private Feuerball feuerball_sp1;

	// Testing

	private int way = 5;
	private boolean down = true;


	public Spiel(int[] panel_size) {

		// initialisiere Skin
		current_skin = new Skin(new File(skinfolder_name), skinName); // Loades original_skin.png and original.json from skins/

		// initialisiere Mapchain

		String[] maps = new File(levelfolder_name).list(); // read Level from Folder

		// create Map and add it to chain

		System.out.println("Anzahl der gelesenen Karten: " + maps.length);

		mapChain = new ArrayList<>();

		for (String map : maps) {

			// read Level-File
			JSONObject obj = null;
			try {
				obj = new JSONObject(new String(Files.readAllBytes(Paths.get(levelfolder_name + map))));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create Map and add to chain
			mapChain.add(new Map(obj));
		}

		// add Player

		sp1 = null;
		sp2 = null;


		feuerball_sp1 = null;


		aktuelles_level = createNextLevel();

		// refresh sizing

		refreshSizing();


		// TESTING


		// setze Monster ein
		ArrayList<Monster> monster = aktuelles_level.getMap().getMonster();

		monster.add(new Nobbin(getCenterOf(aktuelles_level.getMap().getSpawn_monster())));
		monster.add(new Nobbin(getCenterOf(aktuelles_level.getMap().getSpawn_monster())));
		monster.add(new Nobbin(getCenterOf(aktuelles_level.getMap().getSpawn_monster())));


		int[] fp = {1,1};
		int[] pos = getCenterOf(fp);

		aktuelles_level.getMap().setzeHobbin(pos);

		// setze Kirsche ein

		aktuelles_level.getMap().showKirsche();

		// setze Geld ein

		int[] gfp = {3,3};
		aktuelles_level.getMap().addGeld( new Geld(gfp) );

		// Ausgaben, Infos
		System.out.println("Anzahl Nobbins: " + aktuelles_level.getMap().getNobbins().size());
		System.out.println("Anzahl Hobbins: " + aktuelles_level.getMap().getHobbins().size());

	}

	private void refreshSizing() {

		// Speichere Änderung
		old_field_size = field_size;

		// setze Feldgröße

		Dimension d = this.getSize();

		int felderX = aktuelles_level.getMap().getPGSize()[0];
		int felderY = aktuelles_level.getMap().getPGSize()[1];

		if(d.width == 0 || d.height == 0)
			d = new Dimension(500, 500);

		int w_temp_size = (int)((double)d.width / ( (double)felderX + ( 2*border[0]) ));
		int h_temp_size = (int)((double)d.height / ( (double)felderY + ( 2*border[1]) + topbarHeight ));

		field_size = Math.min(w_temp_size, h_temp_size);

		// berechne neue Pixelpositionen

		if(field_size != old_field_size && old_field_size != 0) {
			double factor = (double) field_size / (double) old_field_size;

			if(sp1 != null)
				for (int i = 0; i <= sp1.getPosition().length - 1; i++)
					sp1.getPosition()[i] *= factor;

			if(sp2 != null)
				for (int i = 0; i <= sp2.getPosition().length - 1; i++)
					sp2.getPosition()[i] *= factor;

			for (Monster m : aktuelles_level.getMap().getMonster())
				for (int i = 0; i <= m.getPosition().length - 1; i++)
					m.getPosition()[i] *= factor;
		}

	}

	private int getTopBarHeight(){
		return (int)(field_size*topbarHeight);
	}

	public int[] getFieldOf(int[] pos){

		int[] borderOffest = getBorderOffset();

		int[] fp = new int[2];

		if((pos[0]-borderOffest[0]) < 0)
			fp[0] = -1;
		else
			fp[0] = ((pos[0]-borderOffest[0])/field_size) + 1;

		if((pos[1]-borderOffest[1]-getTopBarHeight()) < 0)
			fp[1] = -1;
		else
			fp[1] = ((pos[1]-borderOffest[1]-getTopBarHeight())/field_size) + 1;

		return fp;
	}

	/**
	 *	Spiellogik, die Positionen prüft und Ereignisse aufruft.
	 * @return false if plaer dead; else true if game schoud be contiued
	 */

	private boolean loop(){

		long DELAY_PERIOD = 10;
		long beginTime = System.currentTimeMillis();

		/// Prüfroutinen

		// alle Diamanten gesammel?

		if(aktuelles_level.getMap().getDiamonds().size() == 0) {
			// dann nächstes Level
			aktuelles_level = createNextLevel();

			//reset players position
			sp1.setPosition( getCenterOf(aktuelles_level.getMap().getSpawn_SP1()) );

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
			if(Arrays.equals(single_item.getField(),getFieldOf(sp1.getPosition()))) {
				iterator.remove();
				spielstand += single_item.getValue();
			}
		}


		// Spieler trifft Monster
		ArrayList<Monster> monsters= aktuelles_level.getMap().getMonster();
		for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
			Monster m = iterator.next();
			if(Arrays.equals(getFieldOf(m.getPosition()),( getFieldOf(sp1.getPosition())))){
				if(sp1.isAlive()) {
					sp1.decrementLife();
					sp1.setPosition(getCenterOf(aktuelles_level.getMap().getSpawn_SP1()));
				}
				else
					System.out.println("Ende");
			}
			//sp2 üp
		}


		// Spieler trifft Boden

		int[] fpSp = getFieldOf(sp1.getPosition());
		DIRECTION dirSp = sp1.getMoveDir();

		ArrayList<Tunnel> tt = aktuelles_level.getMap().getTunnel(fpSp);

		if(tt.size() == 1){
			TUNNELTYP ttyp = tt.get(0).getTyp();


			if( ((dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN) && ttyp == TUNNELTYP.HORIZONTAL) ||
				((dirSp == DIRECTION.RIGHT || dirSp == DIRECTION.LEFT) && ttyp == TUNNELTYP.VERTICAL) ){

				TUNNELTYP arrangement;

				if(dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN)
					arrangement = TUNNELTYP.VERTICAL;
				else
					arrangement = TUNNELTYP.HORIZONTAL;

				aktuelles_level.getMap().addTunnel( new Tunnel(fpSp, arrangement) );
			}
		}
		else if (tt.size() == 0){
			if (dirSp == DIRECTION.RIGHT || dirSp == DIRECTION.LEFT)
				aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.HORIZONTAL));
			else if (dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN)
				aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.VERTICAL));
		}


		// Spieler trifft Geldsack
		ArrayList<Geldsack> geldsacke= aktuelles_level.getMap().getGeldsaecke();
		for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext();) {
			Geldsack g = iterator.next();
			if (Arrays.equals(g.getPosition(),getFieldOf(sp1.getPosition()))) {
				if (sp1.getMoveDir() == DIRECTION.RIGHT) {
					System.out.println("push right");
					g.addPosOff(1,0);
				} else if (sp1.getMoveDir() == DIRECTION.LEFT) {
					System.out.println("push left");
					g.addPosOff(-1,0);
				}
			}
		}

		// Spieler trifft Geld
		ArrayList<Geld> gelds= aktuelles_level.getMap().getGeld();
		for (Iterator<Geld> iterator = gelds.iterator(); iterator.hasNext();) {
			Geld gd = iterator.next();

			if (Arrays.equals(gd.getField(), getFieldOf(sp1.getPosition()))) {

				iterator.remove();
				spielstand += gd.getValue();
			}
		}

		// Create Cherry (by killing X Monster)
		// Spieler trifft Kirsche -> Bonsmodus aktivieren
		if (aktuelles_level.getMap().getKirsche().getVisible()) {
			if (Arrays.equals(aktuelles_level.getMap().getKirsche().getField(), getFieldOf(sp1.getPosition()))) {
				aktuelles_level.getMap().hideKirsche();
				spielstand += aktuelles_level.getMap().getKirsche().getValue();
			}
		}
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

		aktuelles_level.getMap().getMonster().get(0).addPosOff(-1,0);
		aktuelles_level.getMap().getMonster().get(1).addPosOff(0,1);


		// Bewegung werden durch Algorithmus oder Tastatuseingabe oder Netzwerksteuerung direkt im Mapobjekt geändert und durch repaint übernommen
		/// in jedem Fall wir true zurückgegeben. Andernfalls beendet sich die loop() und das spiel gilt als beendet. Darauf folgt dann die eintragung der ergebnisse ect.

		if(!sp1.isAlive())
			return false; // Spiel beendet


		long timeTaken = System.currentTimeMillis() - beginTime;
		long sleepTime = DELAY_PERIOD - timeTaken;

		if (sleepTime >= 0) {
			try{Thread.sleep(sleepTime);} catch(InterruptedException e){}
		}
		this.repaint();
		return true;

	}

	int[] getCenterOf(int[] fp) {

		int x_field = fp[0] - 1;
		int y_field = fp[1] - 1;

		int[] borderOffset = getBorderOffset();

		int[] pixelPos = new int[2];

		pixelPos[0] = x_field * field_size + (field_size / 2) + borderOffset[0];
		pixelPos[1] = y_field * field_size + (field_size / 2) + borderOffset[1] + getTopBarHeight();

		return pixelPos;
	}

	public void spawnSpieler(boolean isMultiplayer) {

		System.out.println("FieldSize: " + field_size);

		int[] pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP1());
		sp1 = new Spieler(pixelPos[0], pixelPos[1]);

		if(isMultiplayer) {
			pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP2());
			sp2 = new Spieler(pixelPos[0], pixelPos[1]);
		}

	}

	public Spieler getSP1(){
		return sp1;
	}

	public void spawnFeuerball(DIRECTION dir, int[] pos) {
		feuerball_sp1 = new Feuerball(pos, dir);
	}

	public Feuerball getFeuerball_sp1(){ return feuerball_sp1; }

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

		refreshSizing();

		// Prepering

		int[] borderOffset = getBorderOffset();

		// Zeichne Hintergrund

		setBackground(Color.black);
		BufferedImage backgroundImg = current_skin.getImage("backg_typ1", field_size);
		TexturePaint slatetp = new TexturePaint(backgroundImg, new Rectangle(0, 0, backgroundImg.getWidth(), backgroundImg.getHeight()));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(slatetp);
		int[] pg_size = aktuelles_level.getMap().getPGSize();
		g2d.fillRect(0, getTopBarHeight(), field_size*pg_size[0]+borderOffset[0]*2, field_size*pg_size[1]+borderOffset[1]*2);

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

			int[] field = single_item.getField();
			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (unscaledImg.getWidth() / 2);
			int y_pixel = middle[1] - (unscaledImg.getHeight() / 2);

			g.drawImage(unscaledImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Zeichne Diamanten
		BufferedImage diamImg = current_skin.getImage("diamond", field_size);

		ArrayList<Diamant> diamanten = aktuelles_level.getMap().getDiamonds();

		for (int i = 0; i < diamanten.size(); i++) {
			Diamant single_item = diamanten.get(i);

			int[] field = single_item.getField();
			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (diamImg.getWidth() / 2);
			int y_pixel = middle[1] - (diamImg.getHeight() / 2);

			g.drawImage(diamImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}


		// Zeichne Geldsäcke
		BufferedImage moneyPodImg = current_skin.getImage("money_static", field_size);

		ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

		for (int i = 0; i < geldsaecke.size(); i++) {
			Geldsack single_item = geldsaecke.get(i);

			int[] field = single_item.getField();
			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
			int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

			g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Monster

		BufferedImage hobbinImg = current_skin.getImage("hobbin_left_f1", field_size);

		ArrayList<Hobbin> hobbins = aktuelles_level.getMap().getHobbins();

		for (int i = 0; i < hobbins.size(); i++) {
			Hobbin single_item = hobbins.get(i);

			int x_pixel = single_item.getPosition()[0] - (hobbinImg.getWidth() / 2);
			int y_pixel = single_item.getPosition()[1] - (hobbinImg.getHeight() / 2);

			g.drawImage(hobbinImg, x_pixel, y_pixel, null);

			if(devFrames) {
				int[] field = getFieldOf(single_item.getPosition());
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		BufferedImage nobbinImg = current_skin.getImage("nobbin_f1", field_size);

		ArrayList<Nobbin> nobbins = aktuelles_level.getMap().getNobbins();

		for (int i = 0; i < nobbins.size(); i++) {
			Nobbin single_item = nobbins.get(i);

			int x_pixel = single_item.getPosition()[0] - (nobbinImg.getWidth() / 2);
			int y_pixel = single_item.getPosition()[1] - (nobbinImg.getHeight() / 2);

			g.drawImage(nobbinImg, x_pixel, y_pixel, null);

			if(devFrames) {
				int[] field = getFieldOf(single_item.getPosition());
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Feuerball

		BufferedImage feuerballImg = current_skin.getImage("fireball_red_f1", field_size);

		ArrayList<Feuerball> feuerball = aktuelles_level.getMap().getFeuerball();


		for (int i = 0; i < feuerball.size(); i++) {
			Feuerball single_item = feuerball.get(i);

			int[] pos = single_item.getPosition();
			int x_pixel = pos[0] - (moneyPodImg.getWidth() / 2);
			int y_pixel = pos[1] - (moneyPodImg.getHeight() / 2);

			g.drawImage(feuerballImg, x_pixel, y_pixel, null);

			if(devFrames) {
				int[] field = getFieldOf(single_item.getPosition());
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Geld

		BufferedImage geldImg = current_skin.getImage("money_fall_f6", field_size);

		ArrayList<Geld> geld = aktuelles_level.getMap().getGeld();

		for (int i = 0; i < geld.size(); i++) {
			Geld single_item = geld.get(i);

			int[] field = single_item.getField();
			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
			int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

			// scaling ...

			g.drawImage(geldImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Kirsche
		if(aktuelles_level.getMap().getKirsche().getVisible()){
			BufferedImage kirscheImg = current_skin.getImage("cherry", field_size);

			int[] field = aktuelles_level.getMap().getKirsche().getField();

			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (kirscheImg.getWidth() / 2);
			int y_pixel = middle[1] - (kirscheImg.getHeight() / 2);

			g.drawImage(kirscheImg, x_pixel, y_pixel, null);

			if (devFrames) {
				g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Spieler

		if(sp1 != null) {
			if(sp1.isAlive()) {

				Animation ani_left = current_skin.getAnimation("digger_red_left");
				Animation ani_right = current_skin.getAnimation("digger_red_right");
				Animation ani_up = current_skin.getAnimation("digger_red_up");
				Animation ani_down = current_skin.getAnimation("digger_red_down");

				BufferedImage sp1Img = null;

				if (sp1.getMoveDir() == DIRECTION.RIGHT) {
					sp1Img = ani_right.nextFrame(field_size);
				}
				if (sp1.getMoveDir() == DIRECTION.LEFT) {
					sp1Img = ani_left.nextFrame(field_size);
				}
				if (sp1.getMoveDir() == DIRECTION.UP) {
					sp1Img = ani_up.nextFrame(field_size);
				}
				if (sp1.getMoveDir() == DIRECTION.DOWN) {
					sp1Img = ani_down.nextFrame(field_size);
				}


				int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);
				g.drawImage(sp1Img, x_pixel, y_pixel, null);

			}
			else {
				// gegen Geist ersetzen
				BufferedImage sp1Img = current_skin.getImage("grave_f5", field_size);
				int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);
				g.drawImage(sp1Img, x_pixel, y_pixel, null);
			}
		}

		if(sp2 != null) {
			Animation gre_ani_left = current_skin.getAnimation("digger_gre_left");
			Animation gre_ani_right = current_skin.getAnimation("digger_gre_right");
			Animation gre_ani_up = current_skin.getAnimation("digger_gre_up");
			Animation gre_ani_down = current_skin.getAnimation("digger_gre_down");

			BufferedImage sp2Img = null;
			sp2Img = gre_ani_right.nextFrame(field_size);

			int x_pixel = sp2.getPosition()[0] - (sp2Img.getWidth() / 2);
			int y_pixel = sp2.getPosition()[1] - (sp2Img.getHeight() / 2);
			g.drawImage(sp2Img, x_pixel, y_pixel, null);

		}

		// Zeichne Score
		int margin_y = field_size/4;
		int margin_x = field_size/2;

		int fontSize = field_size/2;
		g.setFont(current_skin.getFont().deriveFont(Font.PLAIN, fontSize));
		g.setColor(Color.white);
		g.drawString(String.format("%05d", spielstand), margin_x, margin_y+fontSize);

		// Zeichne Leben


		// Zeichne Leben von SP1
		BufferedImage sp1Img =current_skin.getImage("statusbar_digger_MP_red", field_size);
		margin_x = 3*field_size;
		for(int i = sp1.getLeben(); i > 0; i--) {
			g.drawImage(sp1Img, margin_x, margin_y, null);
			margin_x += sp1Img.getWidth();
		}

		// Zeichene auch leben von SP2
		if(sp2 != null) {
			margin_x = 9*field_size;
			BufferedImage sp2Img = current_skin.getImage("statusbar_digger_MP_gre", field_size);
			for(int i = sp2.getLeben(); i > 0; i--) {
				g.drawImage(sp2Img, margin_x, margin_y, null);
				margin_x -= sp1Img.getWidth();
			}

		}

	}

	private int[] getBorderOffset(){

		int[] borderOffset = new int[2];
		borderOffset[0] = (int)(field_size*border[0]);
		borderOffset[1] = (int)(field_size*border[1]);

		return borderOffset;
	}


	@Override
	public void run() {
		while(loop());
	}

	@Override
	public Dimension getPreferredSize() {

		int[] borderOffset = getBorderOffset();

		int[] playground_size = aktuelles_level.getMap().getPGSize();

		Dimension d = new Dimension(playground_size[0] * field_size + 2* borderOffset[0], playground_size[1] * field_size + 2* borderOffset[1] + getTopBarHeight());

		System.out.println(d);

		return d;
	}


	public int getFieldSize() {
		return field_size;
	}

	public void moveSP(int velx, int vely) {
		int[] spPos = sp1.getPosition().clone();

		spPos[0] += velx;
		spPos[1] += vely;

		int[] newField = getFieldOf(spPos);
		int[] pgSize = aktuelles_level.getMap().getPGSize();

		if(0 < newField[0] && newField[0] <= pgSize[0] && 0 < newField[1] && newField[1] <= pgSize[1])
			sp1.addPosOff(velx,vely);

	}
}