package Spielbereitstellug;

import Spielverlauf.*;
import org.json.JSONObject;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class Spiel extends Render implements Runnable {

	// game setup

	boolean isMultiplayer;
	boolean isHost;

	Netzwerksteuerung netControl;

	private EndListener el;

	// Speed

	int feuerball_steps;
	final int geldsack_steps = 3;
	int monster_steps = 5;
	final long DELAY_PERIOD = 15;

	protected Spieler sp1;
	protected Spieler sp2;



	// System-/ Filestructure

	private final String levelfolder_name = "bin/level/"; // ./level/level-01.json ...

	// static content
	private final ArrayList<Map> mapChain;
	int current_map = 0;



	// loop global
	int anzMon = 0;
	boolean bounsmodus = false;
	private long bounsRemTime;
	private final long bounsTime = 10000;
	long monRTime;

	private int spielstand;

	private Level aktuelles_level;

	public Spiel(){
		this(true, false, null);
	}

	public Spiel( boolean isHost, boolean isMultiplayer, Netzwerksteuerung netC) {
		// initalisiere game setup

		this.isHost = isHost;
		this.isMultiplayer = isMultiplayer;
		bounsRemTime=bounsTime;

		// initialisiere Netzwerksteuerung
		netControl = netC;

		// initialisiere Mapchain

		String[] maps = new File(levelfolder_name).list(); // read Level from Folder

		// create Map and add it to chain

		mapChain = new ArrayList<>();

		for (String map : maps) {

			// read Level-File
			JSONObject objf = null;
			try {
				objf = new JSONObject(new String(Files.readAllBytes(Paths.get(levelfolder_name + map))));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create Map and add to chain
			mapChain.add(new Map(objf, current_skin));
		}

		// add Player

		createNextLevel();


		setFbRegTime();
		monRTime = aktuelles_level.getRegenTimeFb();

		// refresh sizing
		obj = aktuelles_level.getMap().exportStaticsAsJSON();

		feuerball_steps = field_size/15;
		//monster_steps = field_size/aktuelles_level.getSpeed();

		System.out.println(field_size);

	}

	/**
	 *	Spiellogik, die Positionen prüft und Ereignisse aufruft.
	 * @return false if plaer dead; else true if game schoud be contiued
	 */

	private  void setFbRegTime(){
		// tell players the regentime

		if(sp1!=null)
			sp1.setFbRegeneration(aktuelles_level.getRegenTimeFb());

		if(sp2!=null)
			sp2.setFbRegeneration(aktuelles_level.getRegenTimeFb());
	}

	private boolean loop() {

		// Take Time, set period
		long beginTime = System.currentTimeMillis();



		if(isHost) {
			// link current lists
			ArrayList<Diamant> diamants = aktuelles_level.getMap().getDiamonds();
			ArrayList<Monster> monsters = aktuelles_level.getMap().getMonster();
			ArrayList<Hobbin> hobbins = aktuelles_level.getMap().getHobbins();
			ArrayList<Nobbin> nobbins = aktuelles_level.getMap().getNobbins();
			ArrayList<Geldsack> geldsacke = aktuelles_level.getMap().getGeldsaecke();
			ArrayList<Geld> gelds = aktuelles_level.getMap().getGeld();
			ArrayList<Tunnel> tunnels = aktuelles_level.getMap().getTunnel();
			ArrayList<Feuerball> feuerballs = aktuelles_level.getMap().getFeuerball();
			Kirsche kirsche = aktuelles_level.getMap().getKirsche();


			/// Prüfroutinen

			ArrayList<Spieler> spielers = new ArrayList<>();
			if (sp2 != null && sp2.isAlive())
				spielers.add(sp2);

			if (sp1 != null && sp1.isAlive())
				spielers.add(sp1);

			for (Iterator<Spieler> spIterator = spielers.iterator(); spIterator.hasNext(); ) {
				Spieler sp = spIterator.next();

				// alle Diamanten gesammel?
				if (aktuelles_level.getMap().getDiamonds().size() == 0) {
					// dann nächstes Level
					createNextLevel();

					//reset players position
					sp.setPosition(getCenterOf(aktuelles_level.getMap().getSpawn_SP1()));

					// vervolständigen zB von Scores
				}


				// Spieler triffen Diamant
				for (Iterator<Diamant> iterator = diamants.iterator(); iterator.hasNext(); ) {
					Diamant single_item = iterator.next();
					if (Arrays.equals(single_item.getField(), getFieldOf(sp.getPosition()))) {
						iterator.remove();
						spielstand += single_item.getValue();
					}
				}

				// Spieler triffen Boden
				int[] fpSp = getFieldOf(sp.getPosition());
				DIRECTION dirSp = sp.getMoveDir();

				ArrayList<Tunnel> tt = aktuelles_level.getMap().getTunnel(fpSp);

				if (tt.size() == 1) {
					TUNNELTYP ttyp = tt.get(0).getTyp();

					if (((dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN) && ttyp == TUNNELTYP.HORIZONTAL) ||
							((dirSp == DIRECTION.RIGHT || dirSp == DIRECTION.LEFT) && ttyp == TUNNELTYP.VERTICAL)) {

						TUNNELTYP arrangement;

						if (dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN)
							arrangement = TUNNELTYP.VERTICAL;
						else
							arrangement = TUNNELTYP.HORIZONTAL;

						aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, arrangement, current_skin));
					}
				} else if (tt.size() == 0) {
					if (dirSp == DIRECTION.RIGHT || dirSp == DIRECTION.LEFT)
						aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.HORIZONTAL, current_skin));
					else if (dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN)
						aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.VERTICAL, current_skin));
				}


				// Spieler triffen Geld
				for (Iterator<Geld> iterator = gelds.iterator(); iterator.hasNext(); ) {
					Geld gd = iterator.next();
					if (Arrays.equals(gd.getField(), getFieldOf(sp.getPosition()))) {
						iterator.remove();
						spielstand += gd.getValue();
					}
				}

/*--------------------------------------------------------------------------------------------------------------------*/

				///Geldsack:

				//Geldsack trifft Tunnel // Geldscak trifft Spieler 1 // Geldscak trifft Spieler 2 //Geld erstellen
				for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext(); ) {
					Geldsack gs = iterator.next();

					// nach l/r bewegen
					if (Arrays.equals(getFieldOf(gs.getPosition()), getFieldOf(sp.getPosition()))) {
						int[] PGSize = aktuelles_level.getMap().getPGSize();
						int[] newField1 = getFieldOf(gs.getPosition());
						if (sp.getMoveDir() == DIRECTION.RIGHT) {
							if (newField1[0] < PGSize[0])
								gs.addPosOff(field_size, 0);
						} else if (sp.getMoveDir() == DIRECTION.LEFT) {
							if (1 < newField1[0])
								gs.addPosOff(-field_size, 0);
						}
					}

					// Geldsack trifft Geldsack
					for (Iterator<Geldsack> it = geldsacke.iterator(); it.hasNext(); ) {
						Geldsack g2 = it.next();
						int[] PGSize = aktuelles_level.getMap().getPGSize();
						int[] newField1 = getFieldOf(gs.getPosition());
						int[] newField2 = getFieldOf(g2.getPosition());
						if (gs != g2) {
							if (Arrays.equals(getFieldOf(gs.getPosition()), getFieldOf(g2.getPosition()))) {
								if (newField1[0] + newField2[0] < PGSize[0]) {
									g2.addPosOff(field_size, 0);
								}if (1 < newField2[0] + newField1[0]) {
									g2.addPosOff(-field_size, 0);
								}
							}
						}
					}

					// Geldsack fällt auf Spieler

					if (Arrays.equals(getFieldOf(sp.getPosition()), getFieldOf(gs.getPosition())) && gs.getFalling()) {
						if (sp.isAlive()) {
							if (sp.decrementLife())
								sp.setPosition(getCenterOf(aktuelles_level.getMap().getSpawn_SP1()));

							bounsmodus = false;
							anzMon = 0;
							monsters.clear();
							iterator.remove();
							break;
						}
					}
				}

				///Bonsmodus aktivieren:
				// Spieler trifft Kirsche ->
				if (kirsche != null) {
					if (Arrays.equals(kirsche.getField(), getFieldOf(sp.getPosition()))) {
						aktuelles_level.getMap().removeKirsche();
						spielstand += kirsche.getValue();
						bounsmodus = true;
					}
				}

				// prüfe sp regteimes

				if(sp.getFired()){

					if(sp.getRegTime() < (long)0){
						sp.setFired(false);
						sp.setFbRegeneration(aktuelles_level.getRegenTimeFb());
					}
					else
						sp.decRegTime(DELAY_PERIOD);
				}

/*--------------------------------------------------------------------------------------------------------------------*/
				///Monster:


				for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
					Monster mon = iterator.next();

					if (Arrays.equals(getFieldOf(sp.getPosition()), getFieldOf(mon.getPosition()))) {
						if(bounsmodus) {
							spielstand += mon.getWertung();
							iterator.remove();
						}
						else {
							//Monster trifft Spieler
							if(sp.isAlive()) {
								if (sp.decrementLife())
									sp.setPosition(getCenterOf(aktuelles_level.getMap().getSpawn_SP1()));

								anzMon = 0;
								monsters.clear();
								break;
							}

						}
					}

				}
			}

			// ----- end for (Spieler)

			// Hobbin verfolgt Spieler

			//
			// Bitte für den Monsteroffset die globale Varable feuerball_steps nutzen. So kann die Monstergeschw. global angepasst werden.
			//

			for (Iterator<Hobbin> iterator = hobbins.iterator(); iterator.hasNext();) {
				Monster h = iterator.next();

				int[] h_pos = h.getPosition();
				int[] s_pos = sp1.getPosition();
				int[] fph = getFieldOf(h.getPosition());
				int x_off = 0;
				int y_off = 0;

				if (h_pos[0] > s_pos[0])
					x_off = -1;
				else
					x_off = 1;

				if (h_pos[1] > s_pos[1])
					y_off = -1;
				else
					y_off = 1;
				TUNNELTYP arrangement = TUNNELTYP.HORIZONTAL;

				if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{x_off + h_pos[0], h_pos[1]})).isEmpty())
				{
					if (x_off!=0)
						arrangement = TUNNELTYP.HORIZONTAL;


					aktuelles_level.getMap().addTunnel(new Tunnel(fph, arrangement, current_skin));

				}
				if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ h_pos[0], y_off + h_pos[1]})).isEmpty())
				{
					if (y_off!=0)
						arrangement = TUNNELTYP.VERTICAL;


					aktuelles_level.getMap().addTunnel(new Tunnel(fph, arrangement, current_skin));

				}
				h.addPosOff(x_off, y_off);


			}


			// Monster(Nobbin) verfolgt Spieler // still a little buggy 
			for (Iterator<Nobbin> iterator = nobbins.iterator(); iterator.hasNext();) {
				Nobbin m = iterator.next();

				int[] m_pos = m.getPosition();
				int[] s_pos = sp1.getPosition();
				int x_off = 0;
				int y_off = 0;

				if (m_pos[0] > s_pos[0])
					x_off = -1;
				else
					x_off = 1;

				if (m_pos[1] > s_pos[1])

					y_off = -1;
				else
				if(m.z==0){
					y_off = 1;

				}
				else y_off=-1;
				if (m.z !=0){

					m.addPosOff(0, 1);
					//System.out.print(z);
					if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{x_off + m_pos[0], m_pos[1]})).isEmpty()){
						m.addPosOff(0, 1);
						m.z=0;
					}


				}
				else if(m.u !=0){
					if(!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0] -1, m_pos[1]})).isEmpty()){
						//System.out.print(u);
						m.addPosOff(-1, 0);
						if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0],y_off+ m_pos[1]})).isEmpty()){
							m.addPosOff(-1, 0);
							m.u=0;
						}


					}
					else if(!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0] , m_pos[1]+1})).isEmpty()){
						m.addPosOff(0, 1);

					}



				}
				else if(m.x!=0){
					if(!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0] , m_pos[1] - y_off})).isEmpty()){
						//System.out.print(x);
						m.addPosOff(0, -y_off);
						if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0]+x_off, m_pos[1]})).isEmpty()){
							m.addPosOff(0, -y_off);
							m.u=0;
						}


					}
					else if(!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0] - x_off, m_pos[1]})).isEmpty()){
						//System.out.print(m.x);
						m.addPosOff(-x_off, 0);
						if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0],y_off+ m_pos[1]})).isEmpty()){
							m.addPosOff(-x_off, 0);
							m.x=0;
						}


					}


				}
				else {
					if (m_pos[1] == s_pos[1] )
					{
						if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{x_off + m_pos[0],  m_pos[1]})).isEmpty()){//check if nextpos is a tunnel or no, and then choose to execute the move or no
							//m.addPosOff(0, -1);
							m.z++;
							//System.out.print(m.z);


						}}
					if (m_pos[0] == s_pos[0] )
					{
						if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{ m_pos[0],  y_off+m_pos[1]})).isEmpty()){//check if nextpos is a tunnel or no, and then choose to execute the move or no
							//m.addPosOff(0, -1);
							m.u++;
							//System.out.print(m.u);
						}
					}



					if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{x_off + m_pos[0], y_off + m_pos[1]})).isEmpty()){//check if nextpos is a tunnel or no, and then choose to execute the move or no

						if(m.z==0 && m.u ==0){
							m.addPosOff(x_off, y_off);
							if (x_off > 0)
								m.setMoveDir(DIRECTION.RIGHT);
							else
								m.setMoveDir(DIRECTION.LEFT);
						}
						else {
							//m.addPosOff(0, -1);
							System.out.print("test");

						}
					}
					else if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], y_off + m_pos[1]})).isEmpty()) {
						if(m.z==0&& m.u==0){
							m.addPosOff(0, y_off);

						}
						else {
							//y_off = -1;
							m.setMoveDir(DIRECTION.DOWN);
							//m.addPosOff(0,  y_off);
							System.out.print("ml");

						}

					}
					else if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{x_off + m_pos[0], m_pos[1]})).isEmpty()) {
						if(m.z==0 && m.u==0){
							//m.addPosOff(0, y_off);
							m.addPosOff(x_off, 0);
							m.z=0;
							if(x_off >0 )
								m.setMoveDir(DIRECTION.RIGHT);
							else
								m.setMoveDir(DIRECTION.LEFT);

						}}
					else m.x++;



				}}


					/*
					Monster m = aktuelles_level.getMap().getMonster().get(0);
					int[] m_pos = m.getPosition();
					int[] s_pos = sp.getPosition();
					int[] monsternext = m_pos;
					double dx1 = s_pos[0] - m_pos[0];
					double dy1 = s_pos[1] - m_pos[1];
					double norm = Math.sqrt(dx1*dx1+dy1*dy1);
					dx1 /= norm;
					dy1 /= norm;

					dx1 *= 2;
					dy1 *= 2;
					m.addPosOff(0, 0);



					|||
					Monster m = aktuelles_level.getMap().getMonster().get(0);
					int [] m_pos = m.getPosition();
					int[] s_pos = sp.getPosition();


					int  x_off = 0;
					int y_off = 0;
					int[] monsternext = m_pos;


					if (m_pos[0] > s_pos[0]){
						monsternext[0] =  m_pos[0] - 1;
						for (Tunnel num : tunnels) {
							if(num.getField() != monsternext){
								x_off=0;
								break;
							}
							else{
								x_off = -1;}

					}

					}
					else
					{
						monsternext[0] =  m_pos[0] + 1;
						for (Tunnel num : tunnels) {
						if(num.getField()!=monsternext){
							x_off=0;
							break;
						}
						else{
							x_off = 1;}

						}}
					if (m_pos[1] > s_pos[1])
					{
						monsternext[1] =  m_pos[1]  - 1;
						for (Tunnel num : tunnels) {
							if(num.getField()!=monsternext){
								y_off=0;
								break;
							}

						else{
							y_off = -1;}

						}}
					else{
						monsternext[1] =  m_pos[1] + 1;
						for (Tunnel num : tunnels) {
							if(num.getField()!=monsternext){

								y_off=0;
								break;
							}

						else{
							y_off = 1;}

						}}
				m.addPosOff(x_off, y_off);

						*/


			// Hobbin trifft Boden
				/*
				for (Iterator<Hobbin> it = hobbins.iterator(); it.hasNext();){
						Hobbin h = it.next();

					 int[] fph = getFieldOf(h.getPosition());
				DIRECTION dirHp = h.getMoveDir();

				//ArrayList<Tunnel> tt = aktuelles_level.getMap().getTunnel(fph);

				if(tt.size() == 1){
					TUNNELTYP ttyp = tt.get(0).getTyp();


					if( ((dirHp == DIRECTION.UP || dirHp == DIRECTION.DOWN) && ttyp == TUNNELTYP.HORIZONTAL) ||
						((dirHp == DIRECTION.RIGHT || dirHp == DIRECTION.LEFT) && ttyp == TUNNELTYP.VERTICAL) ){

						TUNNELTYP arrangement;

						if(dirHp == DIRECTION.UP || dirHp == DIRECTION.DOWN)
							arrangement = TUNNELTYP.VERTICAL;
						else
							arrangement = TUNNELTYP.HORIZONTAL;

						aktuelles_level.getMap().addTunnel( new Tunnel(fph, arrangement) );
					}
				}}*/

			// Monster trifft Wand // Not yet done
			// for (Iterator<Monster> it = monsters.iterator(); it.hasNext();){
			// 	Monster h = it.next();
			// 	int[] m_pos1 = h.getPosition();
			// 	int[] newField = getFieldOf(m_pos1);
			// 	int[] pgSize = aktuelles_level.getMap().getPGSize();
			// 	if( newField[0] <= 0)
			// 		h.addPosOff(1,0);
			// 	if(newField[0] >= pgSize[0] )
			// 		h.addPosOff(-1,0);
			// 	if( newField[1] <= 0 )
			// 		h.addPosOff(0, 1);
			// 	if(newField[1] >= pgSize[1])
			// 		h.addPosOff(0,-1);
			// }
			/*--------------------------------------------------------------------------------------------------------------------*/

			// Bewegung werden durch Algorithmus oder Tastatuseingabe oder Netzwerksteuerung direkt im Mapobjekt geändert und durch repaint übernommen
			/// in jedem Fall wir true zurückgegeben. Andernfalls beendet sich die loop() und das spiel gilt als beendet. Darauf folgt dann die eintragung der ergebnisse ect.


			// Spielerunabhängig

			// Gelsäcke
			for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext(); ) {
				Geldsack gs = iterator.next();

				// Geldsack trifft auf Boden
				int[] current_field = getFieldOf(gs.getPosition());
				int[] check_field = current_field.clone();
				check_field[1]++;

				if (!gs.getFalling() && aktuelles_level.getMap().getTunnel(check_field).size() > 0) {
					gs.setFalling(true);
				}


				if (gs.getFalling()) {
					if(gs.getPosition()[1]<getCenterOf(getFieldOf(gs.getPosition()))[1] || (gs.getPosition()[1]>=getCenterOf(getFieldOf(gs.getPosition()))[1]&& aktuelles_level.getMap().getTunnel(check_field).size() > 0) ) {
						//0,7 sec vor dem fallen
						if (gs.outOfTime()) {
							gs.addPosOff(0, geldsack_steps);
							gs.incFallHeight();
						}
						else
							gs.decRemainingTime(DELAY_PERIOD);
					}
					else {
						if (gs.getFallHeight() > field_size-25) {
							aktuelles_level.getMap().addGeld(new Geld(getFieldOf(gs.getPosition()), current_skin));
							iterator.remove();
						} else
							gs.resetFallHeight();
					}
				}


				//Geldsack fällt auf Monster
				for (Iterator<Monster> m_iter = monsters.iterator(); m_iter.hasNext();){
					Monster m = m_iter.next();
					if (Arrays.equals(getFieldOf(gs.getPosition()),getFieldOf(m.getPosition())) && gs.getFalling()){
						m_iter.remove();
						anzMon++;
						break;
					}
				}
			}

			//Feuerball trifft Monster

			breakoutpoint:
			for (Iterator<Feuerball> iterator = feuerballs.iterator(); iterator.hasNext(); ) {

				boolean isRemoved = false;

				Feuerball fb = iterator.next();
				for (Iterator<Monster> iter = monsters.iterator(); iter.hasNext(); ) {
					Monster m = iter.next();
					if (Arrays.equals(getFieldOf(fb.getPosition()), getFieldOf(m.getPosition()))) {
						spielstand += m.getWertung();
						anzMon++;
						iterator.remove();
						iter.remove();
						break breakoutpoint;
					}
				}

				//Feuerball trifft Geldsack
				for (Iterator<Geldsack> it = geldsacke.iterator(); it.hasNext(); ) {
					Geldsack gs = it.next();
					if (Arrays.equals(getFieldOf(fb.getPosition()), getFieldOf(gs.getPosition()))) {
						iterator.remove();
						break breakoutpoint;
					}
				}

				if (fb.getMovDir() == DIRECTION.UP) {
					fb.addPosOff(0, -feuerball_steps);
				}
				if (fb.getMovDir() == DIRECTION.DOWN) {
					fb.addPosOff(0, feuerball_steps);
				}
				if (fb.getMovDir() == DIRECTION.RIGHT) {
					fb.addPosOff(feuerball_steps, 0);
				}
				if (fb.getMovDir() == DIRECTION.LEFT) {
					fb.addPosOff(-feuerball_steps, 0);
				}


				//Feuerball trifft Wand
				int[] FBp = getFieldOf(fb.getPosition());
				int[] PGsize = aktuelles_level.getMap().getPGSize();
				if (FBp[0] > PGsize[0] || 1 > FBp[0] || FBp[1] > PGsize[1] || 1 > FBp[1]) {
					iterator.remove();
					break breakoutpoint;
				}

				//Feuerball trifft Boden
				int [] fb_pos = getFieldOf(fb.getPosition());
				if (aktuelles_level.getMap().getTunnel(fb_pos).isEmpty()){
					iterator.remove();
					break breakoutpoint;
				}
			}

			//add Kirsche
			if (anzMon == aktuelles_level.getMaxMonster()){
				aktuelles_level.getMap().setKirsche(new Kirsche(aktuelles_level.getMap().getSpawn_cherry(), current_skin));
				anzMon=0;
			}

			// MOnster

			int[] MSpoint = aktuelles_level.getMap().getSpawn_monster();
			int Max_Monster = aktuelles_level.getMaxMonster();

			// Hobbin trifft Diamant
			for (Iterator<Diamant> iterator = diamants.iterator(); iterator.hasNext(); ) {
				Diamant d = iterator.next();
				for (Iterator<Hobbin> it = hobbins.iterator(); it.hasNext(); ) {
					Hobbin h = it.next();
					if (Arrays.equals(d.getField(), getFieldOf(h.getPosition()))) {
						iterator.remove();
						break;
					}
				}
			}


			// Monster trifft Geld
			for (Iterator<Monster> m_iter = monsters.iterator(); m_iter.hasNext(); ) {
				Monster m = m_iter.next();
				for (Iterator<Geld> g_iter = gelds.iterator(); g_iter.hasNext(); ) {
					Geld g = g_iter.next();
					if (Arrays.equals(g.getField(), getFieldOf(m.getPosition()))) {
						g_iter.remove();
					}
				}
				// Monster trifft Geldsack
				for (Iterator<Geldsack> gs_iter = geldsacke.iterator(); gs_iter.hasNext(); ) {
					Geldsack g = gs_iter.next();
					int[] newField = getFieldOf(g.getPosition());
					int[] PGSize = aktuelles_level.getMap().getPGSize();
					if (Arrays.equals(getFieldOf(g.getPosition()), getFieldOf(m.getPosition()))) {
						if (m.getMoveDir() == DIRECTION.RIGHT) {
							if (newField[0] < PGSize[0])
								g.addPosOff(field_size, 0);
						} else if (m.getMoveDir() == DIRECTION.LEFT) {
							if (1 < newField[0])
								g.addPosOff(-field_size, 0);
						}
					}
				}
			}

			// Nobbin trifft Nobbin && Hobbin setzen
			Monster m1=null;
			for(Iterator<Nobbin> iter = nobbins.iterator(); iter.hasNext(); ) {
				Nobbin n1 = iter.next();
				for(Iterator<Nobbin> it = nobbins.iterator(); it.hasNext(); ) {
					Nobbin n2 = it.next();
					if (n1 != n2) {
						if (monsters.size() <= Max_Monster && !Arrays.equals(getFieldOf(n1.getPosition()), MSpoint)
								&& !Arrays.equals(getFieldOf(n2.getPosition()), MSpoint)) {
							if (Arrays.equals(getFieldOf(n1.getPosition()), getFieldOf(n2.getPosition()))) {
								aktuelles_level.getMap().setzeHobbin(getCenterOf(getFieldOf(n1.getPosition())));
								m1=n1;
								break;
							}
						}
					}
				}
			}
			if (m1!=null){
				monsters.remove(m1);
			}


			// ------- Counter

			//Monster Anzahl aktualisieren
			if (aktuelles_level.getMap().getMonsterAmmount()<aktuelles_level.getMaxMonster() && kirsche == null) {
				if(monRTime < 0) {
					monsters.add(new Nobbin(getCenterOf(aktuelles_level.getMap().getSpawn_monster()), current_skin));
					monRTime = aktuelles_level.getRegenTimeMonster();
				}
				else
					monRTime -= DELAY_PERIOD;
			}

			// Monster trifft Spieler im Bonusmode
			if (bounsmodus) {
				if (bounsRemTime < (long)0) {
					bounsRemTime = bounsTime;
					bounsmodus = false;
				} else
					bounsRemTime -= DELAY_PERIOD;
			}

			//Entferne Geld nach x Sek

			for (Iterator<Geld> iterator = gelds.iterator(); iterator.hasNext(); ) {
				Geld g = iterator.next();

				if (g.outOfTime())
					iterator.remove();
				else
					g.decRemainingTime(DELAY_PERIOD);
			}

			///Bonsmodus aktivieren:
			// Spieler trifft Kirsche ->
			if (kirsche != null) {
				if (kirsche.outOfTime()) {
					aktuelles_level.getMap().removeKirsche();
					anzMon = 0;
				}
				else
					kirsche.decRemainingTime(DELAY_PERIOD);
			}
			// ---- Counter ende


			if ( (!sp1.isAlive() && !isMultiplayer) || (!sp1.isAlive() && isMultiplayer && !sp2.isAlive())) {

				// informiere system
				if (el != null) {
					el.onCompleted(spielstand);
				}
				return false; // Spiel beendet
			}
			else if(spielers.size() == 0) {
				System.out.println("kein Spieler gespawnt. Loop beendet");
				return false;
			}

			// Netzwerkpart
			// alle Änderungen sind nun vollzogen. Die Map kann nun an die Netzwerksteuerung gegeben und zum zweiten Spieler gesendet werden.

			// Sende Mapobj
			if(isMultiplayer && netControl != null)
				netControl.serverSend(aktuelles_level.getMap(), spielstand);


		}
		else{ // !isHost
			netControl.clientGetContent(this);
		}

		super.obj = aktuelles_level.getMap().exportStaticsAsJSON();
		this.repaint();

		// calculate Time
		long timeTaken = System.currentTimeMillis() - beginTime;
		long sleepTime = DELAY_PERIOD - timeTaken;

		if (sleepTime >= 0) {
			try{Thread.sleep(sleepTime);} catch(InterruptedException e){}
		}

		return true;
	}

	public Level getLevel() {
		return aktuelles_level;
	}

	public void spawnSpieler() {

		int[] pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP1());

		ArrayList<Animation> an = new ArrayList<>();

		an.add(current_skin.getAnimation("digger_red_right"));
		an.add(current_skin.getAnimation("digger_red_left"));
		an.add(current_skin.getAnimation("digger_red_up"));
		an.add(current_skin.getAnimation("digger_red_down"));

		sp1 = new Spieler(pixelPos[0], pixelPos[1], an);

		if(isMultiplayer) {
			an.clear();
			an.add(current_skin.getAnimation("digger_gre_right"));
			an.add(current_skin.getAnimation("digger_gre_left"));
			an.add(current_skin.getAnimation("digger_gre_up"));
			an.add(current_skin.getAnimation("digger_gre_down"));

			pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP2());
			sp2 = new Spieler(pixelPos[0], pixelPos[1], an);
		}


	}

	public void spawnFeuerball( Spieler sp) {
		if(!sp.getFired() && sp.isAlive()){
			sp.setFired(true);
			sp.setFbRegeneration(aktuelles_level.getRegenTimeFb());
			aktuelles_level.getMap().addFeuerball(new Feuerball(sp.getPosition(), sp.getMoveDir(), current_skin));
		}
	}

	// creates next Level, increases speed and decrease regtime

	private void createNextLevel() {

		int new_s;
		int new_r;
		int new_mm;
		int new_mr;
		Map nextMap;
		current_map = (current_map+1)%mapChain.size();
		System.out.println("Feldgröße: " + field_size);
		nextMap = new Map(mapChain.get(current_map)); // nächste Map als KOPIE!!! einsetzen. Sonst wird die Mapchain manipuliert und Folgelevel sind verändert.

		if (aktuelles_level != null) {

			new_s = aktuelles_level.getSpeed() + 1;
			new_r = aktuelles_level.getRegenTimeFb() - 100;
			if(new_r < 3000)
				new_r = 3000;

			new_mm = aktuelles_level.getMaxMonster() + 1;

			new_mr = aktuelles_level.getRegenTimeMonster() - 100;
			if(new_mr < 2000)
				new_mr = 2000;
		}
		else {
			new_s = 0;
			new_r = 5000;
			new_mm = 3;
			new_mr = 10000;
		}

		setFbRegTime();

		aktuelles_level = new Level(new_s, new_r, new_mm, new_mr, nextMap);

		refreshSizing();

		ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

		for (int i = 0; i < geldsaecke.size(); i++) {

			Geldsack single_item = geldsaecke.get(i);
			if (single_item.getPosition() == null)
				single_item.setPosition(getCenterOf(single_item.getField()));
		}
	}

	public void beenden() {
		// TODO - implement Spiel.beenden
		throw new UnsupportedOperationException();
	}

	public void pausieren() {
		// TODO - implement Spiel.pausieren
		throw new UnsupportedOperationException();
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

		return d;
	}

	public void moveSP(int velx, int vely, Spieler s) {
		int[] spPos = s.getPosition().clone();

		spPos[0] += velx;
		spPos[1] += vely;

		int[] newField = getFieldOf(spPos);
		int[] pgSize = aktuelles_level.getMap().getPGSize();
		if(0 < newField[0] && newField[0] <= pgSize[0] && 0 < newField[1] && newField[1] <= pgSize[1])
			s.addPosOff(velx,vely);

	}

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int[] borderOffset = getBorderOffset();

		// Zeichne Geldsäcke

		ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

		for (int i = 0; i < geldsaecke.size(); i++) {

			Geldsack single_item = geldsaecke.get(i);

			BufferedImage moneyPodImg = current_skin.scale(single_item.getImage(),field_size);

			int[] field = single_item.getField();
			int[] middle = single_item.getPosition();
			int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
			int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

			g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect((field[0]-1) * field_size + borderOffset[0], (field[1]-1) * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Monster
		ArrayList<Hobbin> hobbins = aktuelles_level.getMap().getHobbins();
		Animation ani_hobbin_left = current_skin.getAnimation("hobbin_left");
		Animation ani_hobbin_right = current_skin.getAnimation("hobbin_right");

		BufferedImage hobbinImg = null;

		for (int i = 0; i < hobbins.size(); i++) {
			Hobbin single_item = hobbins.get(i);

			if (single_item.getMoveDir() == DIRECTION.RIGHT)
				hobbinImg = ani_hobbin_right.nextFrame(field_size);
			else
				hobbinImg = ani_hobbin_left.nextFrame(field_size);


			int x_pixel = single_item.getPosition()[0] - (hobbinImg.getWidth() / 2);
			int y_pixel = single_item.getPosition()[1] - (hobbinImg.getHeight() / 2);

			g.drawImage(hobbinImg, x_pixel, y_pixel, null);

			if(devFrames) {
				int[] field = getFieldOf(single_item.getPosition());
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		Animation ani_nobbin = current_skin.getAnimation("nobbin");
		BufferedImage nobbinImg = ani_nobbin.nextFrame(field_size);

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

		ArrayList<Feuerball> feuerball = aktuelles_level.getMap().getFeuerball();

		for (int i = 0; i < feuerball.size(); i++) {
			Feuerball single_item = feuerball.get(i);
			BufferedImage pic = current_skin.scale(single_item.getImage(), field_size);

			int[] pos = single_item.getPosition();
			int x_pixel = pos[0] - (pic.getWidth() / 2);
			int y_pixel = pos[1] - (pic.getHeight() / 2);

			g.drawImage(pic, x_pixel, y_pixel, null);

			if(devFrames) {
				int[] field = getFieldOf(single_item.getPosition());
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
				g.setColor(Color.RED);
			}
		}

		// Geld

		ArrayList<Geld> geld = aktuelles_level.getMap().getGeld();

		for (int i = 0; i < geld.size(); i++) {
			Geld single_item = geld.get(i);
			Animation a = single_item.getAnimation();

			BufferedImage geldImg = a.nextFrame(field_size);

			int[] field = single_item.getField();
			int[] middle = getCenterOf(field);
			int x_pixel = middle[0] - (geldImg.getWidth() / 2);
			int y_pixel = middle[1] - (geldImg.getHeight() / 2);

			// scaling ...

			g.drawImage(geldImg, x_pixel, y_pixel, null);

			if(devFrames) {
				g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
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
				//Animation ani_grave = current_skin.getAnimation("Grave");
				BufferedImage sp1Img = current_skin.getImage("grave_f5");
				int x_pixel = sp1.getPosition()[0] - (sp1Img.getWidth() / 2);
				int y_pixel = sp1.getPosition()[1] - (sp1Img.getHeight() / 2);
				g.drawImage(sp1Img, x_pixel, y_pixel, null);
			}
		}

		if(sp2 != null) {
			if(sp2.isAlive()) {
				Animation ani_left = current_skin.getAnimation("digger_gre_left");
				Animation ani_right = current_skin.getAnimation("digger_gre_right");
				Animation ani_up = current_skin.getAnimation("digger_gre_up");
				Animation ani_down = current_skin.getAnimation("digger_gre_down");

				BufferedImage spImg = null;

				if (sp2.getMoveDir() == DIRECTION.RIGHT) {
					spImg = ani_right.nextFrame(field_size);
				}
				if (sp2.getMoveDir() == DIRECTION.LEFT) {
					spImg = ani_left.nextFrame(field_size);
				}
				if (sp2.getMoveDir() == DIRECTION.UP) {
					spImg = ani_up.nextFrame(field_size);
				}
				if (sp2.getMoveDir() == DIRECTION.DOWN) {
					spImg = ani_down.nextFrame(field_size);
				}


				int x_pixel = sp2.getPosition()[0] - (spImg.getWidth() / 2);
				int y_pixel = sp2.getPosition()[1] - (spImg.getHeight() / 2);
				g.drawImage(spImg, x_pixel, y_pixel, null);
			}
			else {
				// gegen Geist ersetzen
				//Animation ani_grave = current_skin.getAnimation("Grave");
				BufferedImage spImg = current_skin.getImage("grave_f5", field_size);
				int x_pixel = sp2.getPosition()[0] - (spImg.getWidth() / 2);
				int y_pixel = sp2.getPosition()[1] - (spImg.getHeight() / 2);
				g.drawImage(spImg, x_pixel, y_pixel, null);
			}
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

	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	protected void refreshSizing() {

		// Speichere Änderung
		old_field_size = field_size;

		// setze Feldgröße

		Dimension d = this.getSize();

		int[] feld = aktuelles_level.getMap().getPGSize();


		if (d.width == 0 || d.height == 0)
			d = new Dimension(500, 500);

		int w_temp_size = (int) ((double) d.width / ((double) feld[0] + (2 * border[0])));
		int h_temp_size = (int) ((double) d.height / ((double) feld[1] + (2 * border[1]) + topbarHeight));

		field_size = Math.min(w_temp_size, h_temp_size);

		// berechne neue Pixelpositionen

		if (field_size != old_field_size && old_field_size != 0) {
			double factor = (double) field_size / (double) old_field_size;

			if (sp1 != null)
				for (int i = 0; i <= sp1.getPosition().length - 1; i++)
					sp1.getPosition()[i] *= factor;

			if (sp2 != null)
				for (int i = 0; i <= sp2.getPosition().length - 1; i++)
					sp2.getPosition()[i] *= factor;

			for (Monster m : aktuelles_level.getMap().getMonster())
				for (int i = 0; i <= m.getPosition().length - 1; i++)
					m.getPosition()[i] *= factor;

			for (Geldsack gs : aktuelles_level.getMap().getGeldsaecke()) {

				if (gs.getPosition() == null)
					gs.setPosition(getCenterOf(gs.getField()));

				for (int i= 0; i<= gs.getPosition().length -1; i++)
					gs.getPosition()[i] *= factor;
			}
		}

		//monster_steps = field_size/aktuelles_level.getSpeed();
		feuerball_steps = field_size/15;

	}

	public void addListener(EndListener el) {
		this.el = el;
	}

	public void pause() {
		System.out.println("Pause wurde gedrückt");
	}

	public void setMap( Map m ) {
		aktuelles_level = new Level(0,0,0,0,  m);
	}

	public void setSpielstand(int st) {
		spielstand = st;
	}

	public int getSpielstand() {
		return spielstand;
	}

	public void setClientPos(int[] c_pos) {
		sp2.setPosition(c_pos);
	}
}
