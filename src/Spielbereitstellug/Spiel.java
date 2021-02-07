package Spielbereitstellug;

import Menuefuehrung.Filesystem;
import Spielverlauf.*;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/***
 * Klasse für den Spiel-Ablauf, organisiert alle Inhalte, Steuerungen und Darstellungen des Spielfensters
 */
public class Spiel extends Render implements Runnable, Filesystem {

    // dev ops

    final boolean monsterSpawn = true;
    final boolean dieing = true;

    // game setup
    final long DELAY_PERIOD = 15;

    // static content
    private final ArrayList<Map> mapChain;
    private final long bounsTime = 10000;
    protected Spieler sp1;
    protected Spieler sp2;
    boolean isMultiplayer;
    boolean isHost;
    Netzwerksteuerung netControl;
    Thread netexchange;
    Chat chat;
    // Speed
    int spieler_steps;
    int feuerball_steps;
    int geldsack_steps = 3;
    int monster_steps;
    int current_map = 0;

    Thread loopThreat;

    // loop global
    int anzMon = 0;
    boolean bounsmodus = false;
    long monRTime;
    private EndListener el;
    /**  */
    private long bounsRemTime;
	/** Punkte Zahl für die es je ein Leben gibt */
	final int newLifeScore = 20000;
	/** Zähler für das erreichen des newLifeScore */
    private int incLifeCount = 0;

	/** Runden bis der Speed erhöht wird */
    private int nextSpeedCnt = 5;
	/** aktueller Spielstand */
    private int spielstand;
	/** aktuell bespieltes Level */
    private Level aktuelles_level;

    /***
     * Konstruktor mit Standardwerten im Singleplayer-Modus, ohne Netzwerksteuerung und Chat
     */
    public Spiel(Skin skin) {
        this(true, false, null, null, skin);
    }

    /***
     * Konstruktor mit richtiger Initialisierung der Werte
     * @param isHost Booleanvariable für Unterscheidung Client / Host
     * @param isMultiplayer Booleanvariable für Unterscheidung Single- / Multiplayermodus
     * @param netC Netzwerksteuerung für sämtliche Kommunikation vom und zum Spielpartner
     * @param c Chat-Objekt, ist Teil des Multiplayermodus
     */
    public Spiel(boolean isHost, boolean isMultiplayer, Netzwerksteuerung netC, Chat c, Skin skin) {
        super(skin);
        // initalisiere game setup

        this.isHost = isHost;
        this.isMultiplayer = isMultiplayer;
        bounsRemTime = bounsTime;
        this.chat = c;

        // initialisiere Netzwerksteuerung
        netControl = netC;

        // initialisiere Mapchain

        // create Map and add it to chain

        mapChain = new ArrayList<>();

        String[] maps = new File(levelfolder_name).list(); // read Level from Folder

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
        monRTime = aktuelles_level.getRegenTimeMonster();

        // refresh sizing
        obj = aktuelles_level.getMap().exportStaticsAsJSON();


        feuerball_steps = field_size / 15;
        spieler_steps = field_size / 10;
        monster_steps = aktuelles_level.getSpeed();

        //System.out.println(field_size);


        if (isMultiplayer) {

            int border = field_size / 2;
            this.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);
            bottomPanel.add(chat, BorderLayout.LINE_END);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);

            JButton toggelBtn = new JButton();
            toggelBtn.setIcon(new ImageIcon(current_skin.getImage("chat_btn", field_size)));
            toggelBtn.setOpaque(false);
            toggelBtn.setBorderPainted(false);
            toggelBtn.setContentAreaFilled(false);
            toggelBtn.setFocusable(false);
            topPanel.add(toggelBtn, BorderLayout.LINE_END);

            toggelBtn.addActionListener(e -> {
                if (chat.isVisible()){
                    chat.setVisible(false); chat.setSpaceBarUsage(false);
                }
                else
                    chat.setVisible(true);
            });

            chat.setVisible(false);

            this.setLayout(new BorderLayout());
            this.add(topPanel, BorderLayout.PAGE_START);
            this.add(bottomPanel, BorderLayout.PAGE_END);
        }
    }

    /**
     * Spiellogik, die Positionen prüft und Ereignisse aufruft.
     *
     * @return false if player dead; else true if game schoud be contiued
     */

    private void setFbRegTime() {
        // tell players the regentime

        if (sp1 != null)
            sp1.setFbRegeneration(aktuelles_level.getRegenTimeFb());

        if (sp2 != null)
            sp2.setFbRegeneration(aktuelles_level.getRegenTimeFb());
    }

    public int getSPSteps() {
        return spieler_steps;
    }

    /***
     * Getter für das Chat-Objekt
     * @return Chat
     */
    public Chat getChat() {
        return chat;
    }

    private boolean loop() {

        // Take Time, set period
        long beginTime = System.currentTimeMillis();

        if (isHost) {
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
                if (aktuelles_level.getMap().getDiamonds().isEmpty()) {
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
                        incScore(single_item.getValue());
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

                        aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, arrangement));
                    }
                } else if (tt.size() == 0) {
                    if (dirSp == DIRECTION.RIGHT || dirSp == DIRECTION.LEFT)
                        aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.HORIZONTAL));
                    else if (dirSp == DIRECTION.UP || dirSp == DIRECTION.DOWN)
                        aktuelles_level.getMap().addTunnel(new Tunnel(fpSp, TUNNELTYP.VERTICAL));
                }


                // Spieler triffen Geld
                for (Iterator<Geld> iterator = gelds.iterator(); iterator.hasNext(); ) {
                    Geld gd = iterator.next();
                    if (Arrays.equals(gd.getField(), getFieldOf(sp.getPosition()))) {
                        iterator.remove();
                        incScore(gd.getValue());
                    }
                }

                /*--------------------------------------------------------------------------------------------------------------------*/

                ///Geldsack:

                //Geldsack trifft Tunnel // Geldscak trifft Spieler 1 // Geldscak trifft Spieler 2 //Geld erstellen
                for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext(); ) {
                    Geldsack gs = iterator.next();

                    // nach l/r bewegen
                    if (Arrays.equals(getFieldOf(gs.getPosition()), getFieldOf(sp.getPosition())) && !gs.getFalling()) {
                        int[] PGSize = aktuelles_level.getMap().getPGSize();
                        int[] newField1 = getFieldOf(gs.getPosition());
                        if (sp.getMoveDir() == DIRECTION.RIGHT) {
                            if (newField1[0] < PGSize[0]) {
                                gs.addPosOff(field_size, 0);
                            }
                        } else if (sp.getMoveDir() == DIRECTION.LEFT) {
                            if (1 < newField1[0]) {
                                gs.addPosOff(-field_size, 0);
                            }
                        }
                    }
                    // Geldsack trifft Geldsack
                    for (Iterator<Geldsack> it = geldsacke.iterator(); it.hasNext(); ) {
                        Geldsack g2 = it.next();
                        int[] PGSize = aktuelles_level.getMap().getPGSize();
                        int[] newField1 = getFieldOf(gs.getPosition());
                        int[] newField2 = getFieldOf(g2.getPosition());
                        if (gs != g2) {
                            if (Arrays.equals(newField1, newField2)) {
                                if (sp.getMoveDir() == DIRECTION.RIGHT && newField2[0] < PGSize[0]) {
                                    g2.addPosOff(field_size, 0);
                                }
                                if (sp.getMoveDir() == DIRECTION.LEFT && 1 < newField2[0]) {
                                    g2.addPosOff(-field_size, 0);
                                }
                            }
                        }

                        // Geldsack fällt auf Spieler

                        if (Arrays.equals(getFieldOf(sp.getPosition()), getFieldOf(gs.getPosition())) && gs.getFalling()) {
                            if (sp.isAlive()) {
                                if (sp.decrementLife())
                                    sp.setPosition(getCenterOf(aktuelles_level.getMap().getSpawn_SP1()));

                                bounsmodus = false;
                                aktuelles_level.getMap().setBonus(false);
                                anzMon = 0;
                                monsters.clear();
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
                ///Bonsmodus aktivieren:
                // Spieler trifft Kirsche ->
                if (kirsche != null) {
                    if (Arrays.equals(kirsche.getField(), getFieldOf(sp.getPosition()))) {
                        aktuelles_level.getMap().removeKirsche();
                        incScore(kirsche.getValue());
                        bounsmodus = true;
                        aktuelles_level.getMap().setBonus(true);
                    }
                }

                // prüfe sp regteimes

                if (sp.getFired()) {

                    if (sp.getRegTime() < (long) 0) {
                        sp.setFired(false);
                        sp.setFbRegeneration(aktuelles_level.getRegenTimeFb());
                    } else
                        sp.decRegTime(DELAY_PERIOD);
                }

                /*--------------------------------------------------------------------------------------------------------------------*/
                ///Monster:


                for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext(); ) {
                    Monster mon = iterator.next();

                    if (Arrays.equals(getFieldOf(sp.getPosition()), getFieldOf(mon.getPosition()))) {
                        if (bounsmodus) {
                            incScore(mon.getWertung());
                            iterator.remove();
                        } else {
                            //Monster trifft Spieler
                            if (sp.isAlive() && dieing) {
                                //System.out.println("sp is on m");
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


            if (incLifeCount > newLifeScore) {
                sp1.incrementLife();
                if (sp2 != null)
                    sp2.incrementLife();
                incLifeCount = 0;
            }

            monster_steps = aktuelles_level.getSpeed();
            for (Iterator<Nobbin> iterator = nobbins.iterator(); iterator.hasNext(); ) {
                Nobbin m = iterator.next();
                if (m.isBusy()) {
                    m.addPosOff(monster_steps, m.getMoveDir());
                    m.setStepCount(m.getStepCount() - monster_steps);
                } else {


                    int[] m_pos = m.getPosition();
                    int[] s_pos = sp1.getPosition();

                    if(isMultiplayer){
                        int[] s2_pos = sp2.getPosition().clone();

                        if(sp2.isAlive() && (!sp1.isAlive() || Math.abs(s2_pos[0] - m_pos[0])+Math.abs(s2_pos[1] - m_pos[1]) < Math.abs(s_pos[0] - m_pos[0])+Math.abs(s_pos[1] - m_pos[1])))
                            s_pos = s2_pos;

                    }

                    int x_off = 0;
                    int y_off = 0;
                    int nextfieldx = 0;
                    int nextfieldy = 0;

                    if (m_pos[0] > s_pos[0]) {
                        x_off = -1;
                        nextfieldx = -field_size;
                    } else if (m_pos[0] < s_pos[0]) {
                        x_off = 1;
                        nextfieldx = field_size;
                    }

                    if (m_pos[1] > s_pos[1]) {
                        y_off = -1;
                        nextfieldy = -field_size;
                    } else if (m_pos[1] < s_pos[1]) {
                        y_off = 1;
                        nextfieldy = field_size;
                    }


                    if (m.z != 0) {
                       

                        if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], m_pos[1] + field_size})).isEmpty()) {
                            
                            m.setStepCount(field_size);
                            m.setMoveDir(DIRECTION.DOWN);
                            if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{nextfieldx + m_pos[0], m_pos[1]})).isEmpty()) {
                               
                                if (x_off < 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.LEFT);

                                } else {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.RIGHT);
                                }
                                m.z = 0;
                            }


                        } else if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0] + nextfieldx, m_pos[1]})).isEmpty()) {
                            
                            if (x_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.LEFT);

                            } else {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.RIGHT);
                            }
                            m.z = 0;


                        }


                    } else if (m.u != 0) {
                        
                        if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0] - field_size, m_pos[1]})).isEmpty()) {
                            
                            m.setStepCount(field_size);
                            m.setMoveDir(DIRECTION.LEFT);
                            if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], nextfieldy + m_pos[1]})).isEmpty()) {
                               
                                if (y_off < 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.UP);

                                } else {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.DOWN);
                                }
                                m.u = 0;
                            }


                        } else if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], m_pos[1] + nextfieldy})).isEmpty()) {
                            
                            if (y_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.UP);

                            } else {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.DOWN);
                            }
                            m.u = 0;


                        }


                    } else if (m.x != 0) {

                        if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], m_pos[1] - nextfieldy})).isEmpty()) {
                           
                            if (y_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.DOWN);

                            } else {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.UP);
                            }

                            if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0] + nextfieldx, m_pos[1]})).isEmpty()) {
                                
                                if (x_off < 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.LEFT);

                                } else {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.RIGHT);
                                }
                                m.x = 0;
                            }


                        } else if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0] - nextfieldx, m_pos[1]})).isEmpty()) {
                            
                            if (x_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.RIGHT);

                            } else if (x_off > 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.LEFT);
                            }

                            if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], nextfieldy + m_pos[1]})).isEmpty()) {
                                
                                if (y_off < 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.DOWN);

                                } else if (y_off > 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.UP);
                                }
                                m.x = 0;
                            }


                        }
                        if (!aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0] + nextfieldx, m_pos[1]})).isEmpty()) {
                           
                            if (x_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.LEFT);

                            } else {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.RIGHT);
                            }
                            m.x = 0;
                        }


                    } else {
                       


                        if (getFieldOf(m_pos)[1] == getFieldOf(s_pos)[1]) {
                            if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{nextfieldx + m_pos[0], m_pos[1]})).isEmpty()) {//check if nextpos is a tunnel or no, and then choose to execute the move or no
                                
                                m.z++;
                                


                            }
                            y_off = 0;
                            nextfieldy = 0;
                        }
                        if (getFieldOf(m_pos)[0] == getFieldOf(s_pos)[0]) {
                            if (aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], nextfieldy + m_pos[1]})).isEmpty()) {//check if nextpos is a tunnel or no, and then choose to execute the move or no
                               
                                m.u++;

                               
                            }
                            x_off = 0;
                            nextfieldx = 0;

                        }
                       


                        if (nextfieldx != 0 && !aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{nextfieldx + m_pos[0], m_pos[1]})).isEmpty()) {
                            
                            if (m.z == 0 && m.u == 0) {
                               
                                if (x_off < 0) {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.LEFT);
                                } else {
                                    m.setStepCount(field_size);
                                    m.setMoveDir(DIRECTION.RIGHT);
                                }

                                m.z = 0;


                            }

                        } else if (nextfieldy != 0 && !aktuelles_level.getMap().getTunnel(getFieldOf(new int[]{m_pos[0], m_pos[1] + nextfieldy})).isEmpty()) {
                            

                           
                            if (y_off < 0) {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.UP);

                            } else {
                                m.setStepCount(field_size);
                                m.setMoveDir(DIRECTION.DOWN);
                            }
                           


                        } else m.x++;


                    }
                }
            }

			///Monster Hobbin
            for (Iterator<Hobbin> iterator = hobbins.iterator(); iterator.hasNext(); ) {
                Hobbin m = iterator.next();

                if (m.isBusy()) {
                    m.addPosOff(monster_steps, m.getMoveDir());
                    m.setStepCount(m.getStepCount() - monster_steps);
                } else {

                    int[] m_pos = m.getPosition().clone();
                    int[] s_pos = sp1.getPosition().clone();

                    if(isMultiplayer){
                        int[] s2_pos = sp2.getPosition().clone();

                        if(sp2.isAlive() && (!sp1.isAlive() || Math.abs(s2_pos[0] - m_pos[0])+Math.abs(s2_pos[1] - m_pos[1]) < Math.abs(s_pos[0] - m_pos[0])+Math.abs(s_pos[1] - m_pos[1])))
                            s_pos = s2_pos;

                    }

                    // Bestimme und setze geblockte Richtungen
                    m.removeBlocks();
                    boolean[] blocks = {false, false, false, false};

                    // wenn auf Kreuzung, dann Rhtung entscheiden
                    int[] upper_field = getFieldOf(m_pos);
                    upper_field[1] -= 1;
                    int[] lower_field = getFieldOf(m_pos);
                    lower_field[1] += 1;
                    int[] left_field = getFieldOf(m_pos);
                    left_field[0] -= 1;
                    int[] right_field = getFieldOf(m_pos);
                    right_field[0] += 1;

                    int[][] check_fields = new int[4][2];
                    check_fields[0] = upper_field;
                    check_fields[1] = right_field;
                    check_fields[2] = lower_field;
                    check_fields[3] = left_field;

                    for (int i = 0; i < check_fields.length; i++) {
                        int[] f = check_fields[i];
                        if (f[0] < 1 || f[0] > aktuelles_level.getMap().getPGSize()[0] || f[1] < 1 || f[1] > aktuelles_level.getMap().getPGSize()[1])
                            blocks[i] = true;
                    }

                    m.setBlocks(blocks);


                    int x_off = 0;
                    int y_off = 0;
                    DIRECTION x_dir;
                    DIRECTION y_dir;
                    boolean x_priority;

                    if (m_pos[0] > s_pos[0]) {
                        x_dir = DIRECTION.LEFT;
                    } else {
                        x_dir = DIRECTION.RIGHT;
                    }

                    if (m_pos[1] > s_pos[1]) {
                        y_dir = DIRECTION.UP;
                    } else {
                        y_dir = DIRECTION.DOWN;
                    }

                    if (Math.abs(m_pos[1] - s_pos[1]) < Math.abs(m_pos[0] - s_pos[0]))
                        x_priority = true; // falls x strecke größer
                    else
                        x_priority = false; // falls y strecke größer

                    DIRECTION resultDir;

                    if (!m.isBlocked(x_dir) && !m.isBlocked(y_dir))
                        resultDir = x_priority ? x_dir : y_dir;
                    else if (!m.isBlocked(x_dir) && m.isBlocked(y_dir))
                        resultDir = x_dir;
                    else if (m.isBlocked(x_dir) && !m.isBlocked(y_dir))
                        resultDir = y_dir;
                    else
                        resultDir = null;

                    if (resultDir != null) {
                        m.setStepCount(field_size);
                        m.setMoveDir(resultDir);
                    }
                }
            }

            for (Iterator<Hobbin> iterator = hobbins.iterator(); iterator.hasNext(); ) {
                Hobbin h = iterator.next();

                int[] field = getFieldOf(h.getPosition());

                if (aktuelles_level.getMap().getTunnel(field).isEmpty()) {
                    aktuelles_level.getMap().addTunnel(new Tunnel(field, h.getMoveDir()));
                }
            }

            // Spielerunabhängig

            // Gelsäcke
            for (Iterator<Geldsack> iterator = geldsacke.iterator(); iterator.hasNext(); ) {
                Geldsack gs = iterator.next();

                // Geldsack trifft auf Boden
                int[] current_field = getFieldOf(gs.getPosition());
                int[] check_field = current_field.clone();
                check_field[1]++;

                if (!gs.getFalling() && !gs.getShaking() && aktuelles_level.getMap().getTunnel(check_field).size() > 0) {

                    boolean secureFlag = false;

                    for (Iterator<Monster> m_iter = monsters.iterator(); m_iter.hasNext(); ) {
                        Monster m = m_iter.next();

                        if (Arrays.equals(getFieldOf(m.getPosition()), check_field))
                            secureFlag = true;
                    }

                    for (Iterator<Spieler> s_iter = spielers.iterator(); s_iter.hasNext(); ) {
                        Spieler s = s_iter.next();

                        if (Arrays.equals(getFieldOf(s.getPosition()), check_field))
                            secureFlag = true;
                    }

                    if (secureFlag)
                        gs.setShaking(true);
                    else
                        gs.setFalling(true);

                }

                if (gs.getShaking()) {
                    if (gs.outOfTime()) {
                        gs.setShaking(false);
                        gs.setFalling(true);
                    } else
                        gs.decRemainingTime(DELAY_PERIOD);
                } else if (gs.getFalling()) {
                    geldsack_steps = field_size / 20;
                    if (gs.getPosition()[1] < getCenterOf(getFieldOf(gs.getPosition()))[1] || (gs.getPosition()[1] >= getCenterOf(getFieldOf(gs.getPosition()))[1] && aktuelles_level.getMap().getTunnel(check_field).size() > 0)) {
                        gs.addPosOff(0, geldsack_steps);
                        gs.incFallHeight(geldsack_steps);
                    } else {
                        if (gs.getFallHeight() - 2 > field_size) {
                            aktuelles_level.getMap().addGeld(new Geld(getFieldOf(gs.getPosition())));
                            iterator.remove();
                        } else {
                            gs.resetFallHeight();
                            gs.resetLiveTime();
                            gs.setFalling(false);
                        }
                    }
                }

                //Geldsack fällt auf Monster
                for (Iterator<Monster> m_iter = monsters.iterator(); m_iter.hasNext(); ) {
                    Monster m = m_iter.next();
                    if (gs.getFalling() && Arrays.equals(getFieldOf(gs.getPosition()), getFieldOf(m.getPosition()))) {
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
                        incScore(m.getWertung());
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
                int[] fb_pos = getFieldOf(fb.getPosition());
                if (aktuelles_level.getMap().getTunnel(fb_pos).isEmpty()) {
                    iterator.remove();
                    break breakoutpoint;
                }
            }

            //add Kirsche
            if (anzMon == aktuelles_level.getMaxMonster()) {
                aktuelles_level.getMap().setKirsche(new Kirsche(aktuelles_level.getMap().getSpawn_cherry()));
                anzMon = 0;
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
                    if (Arrays.equals(getFieldOf(g.getPosition()), getFieldOf(m.getPosition())) && !g.getFalling()) {
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
            Monster m1 = null;
            for (Iterator<Nobbin> iter = nobbins.iterator(); iter.hasNext(); ) {
                Nobbin n1 = iter.next();
                for (Iterator<Nobbin> it = nobbins.iterator(); it.hasNext(); ) {
                    Nobbin n2 = it.next();
                    if (n1 != n2) {
                        if (monsters.size() <= Max_Monster && !Arrays.equals(getFieldOf(n1.getPosition()), MSpoint)
                                && !Arrays.equals(getFieldOf(n2.getPosition()), MSpoint)) {
                            if (Arrays.equals(getFieldOf(n1.getPosition()), getFieldOf(n2.getPosition()))) {
                                aktuelles_level.getMap().setzeHobbin(getCenterOf(getFieldOf(n1.getPosition())));
                                m1 = n1;
                                break;
                            }
                        }
                    }
                }
            }
            if (m1 != null) {
                monsters.remove(m1);
            }


            // ------- Counter

            //Monster Anzahl aktualisieren
            if (aktuelles_level.getMap().getMonsterAmmount() < aktuelles_level.getMaxMonster() && kirsche == null) {
                if (monRTime < 0) {
                    if (monsterSpawn) {
                        monsters.add(new Nobbin(getCenterOf(aktuelles_level.getMap().getSpawn_monster())));
                        //System.out.println(getCenterOf(aktuelles_level.getMap().getSpawn_monster())[0]+" "+getCenterOf(aktuelles_level.getMap().getSpawn_monster())[1]);
                    }
                    monRTime = aktuelles_level.getRegenTimeMonster();
                } else
                    monRTime -= DELAY_PERIOD;
            }

            // Monster trifft Spieler im Bonusmode
            if (bounsmodus) {
                if (bounsRemTime < (long) 0) {
                    bounsRemTime = bounsTime;
                    bounsmodus = false;
                    aktuelles_level.getMap().setBonus(false);
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
                } else
                    kirsche.decRemainingTime(DELAY_PERIOD);
            }
            // ---- Counter ende


            if ((!sp1.isAlive() && !isMultiplayer) || (!sp1.isAlive() && isMultiplayer && !sp2.isAlive())) {

                // informiere system
                if (el != null) {
                    el.onCompleted(spielstand);
                }
                return false; // Spiel beendet
            } else if (spielers.size() == 0) {
                //System.out.println("kein Spieler gespawnt. Loop beendet");
                return false;
            }

            // Netzwerkpart
            // alle Änderungen sind nun vollzogen. Die Map kann nun an die Netzwerksteuerung gegeben und zum zweiten Spieler gesendet werden.

            // Sende Mapobj
            if (isMultiplayer && netControl != null && (netexchange == null || !netexchange.isAlive())) {

                Spiel spiel = this;

                netexchange = new Thread() {
                    public void run() {
                        netControl.serverExchange(spiel);
                    }
                };
                netexchange.start();
            }

        } else { // !isHost
            if (isMultiplayer && netControl != null && (netexchange == null || !netexchange.isAlive())) {

                Spiel spiel = this;

                netexchange = new Thread() {
                    public void run() {
                        netControl.clientExchange(spiel);
                    }
                };
                netexchange.start();
            }
        }

        super.obj = aktuelles_level.getMap().exportStaticsAsJSON();
        this.repaint();

        // calculate Time
        long timeTaken = System.currentTimeMillis() - beginTime;
        long sleepTime = DELAY_PERIOD - timeTaken;

        if (sleepTime >= 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }

        return true;
    }

    private boolean isOnCrossroad(int[] pos) {

        int tolerance = monster_steps;
        int[] field_middle = getCenterOf(getFieldOf(pos));

        if (field_middle[0] - tolerance <= pos[0] && pos[0] <= field_middle[0] + tolerance && field_middle[1] - tolerance <= pos[1] && pos[1] <= field_middle[1] + tolerance)
            return true;
        else
            return false;
    }

    /***
     * Getter für das aktuelle Level
     * @return Level
     */
    public Level getLevel() {
        return aktuelles_level;
    }

    /***
     * setzt die Spielfiguren zum Levelstart an die richtigen Positionen
     */
    public void spawnSpieler() {

        int[] pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP1());
        sp1 = new Spieler(pixelPos[0], pixelPos[1]);

        if (isMultiplayer) {
            pixelPos = getCenterOf(aktuelles_level.getMap().getSpawn_SP2());
            sp2 = new Spieler(pixelPos[0], pixelPos[1]);
        }


    }

    /***
     * prüft bei Schießbefehl, ob ein Feuerball geschossen werden darf, falls ja: setzt Feuerball ein
     * @param sp Spieler, der den Feuerball abfeuern will
     */
    public void spawnFeuerball(Spieler sp) {
        if (!sp.getFired() && sp.isAlive()) {
            sp.setFired(true);
            sp.setFbRegeneration(aktuelles_level.getRegenTimeFb());
            aktuelles_level.getMap().addFeuerball(new Feuerball(sp.getPosition(), sp.getMoveDir()));
        }
    }

    // creates next Level, increases speed and decrease regtime

    private void createNextLevel() {

        int new_s;
        int new_r;
        int new_mm;
        int new_mr;
        Map nextMap;
        current_map = (current_map + 1) % mapChain.size();
        nextMap = new Map(mapChain.get(current_map)); // nächste Map als KOPIE!!! einsetzen. Sonst wird die Mapchain manipuliert und Folgelevel sind verändert.

        if (aktuelles_level != null) {

        	if(nextSpeedCnt < 0) {
				new_s = aktuelles_level.getSpeed() + 1;
				if (new_s > 2)
					new_s = 2;
			}
			else {
				new_s = aktuelles_level.getSpeed();
				nextSpeedCnt--;
			}

            new_r = aktuelles_level.getRegenTimeFb() - 100;
            if (new_r < 3000)
                new_r = 3000;

            new_mm = aktuelles_level.getMaxMonster() + 1;
			if (new_mm < 10)
				new_mm = 10;

            new_mr = aktuelles_level.getRegenTimeMonster() - 500;
            if (new_mr < 2000)
                new_mr = 2000;
        }
        else {
            new_s = 1;
            new_r = 5000;
            new_mm = 3;
            new_mr = 7000;
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
        loopThreat.stop();
    }

    public void pausieren() {
        //System.out.println("SP pausiert");
    }


    @Override
    public void run() {
        while (loop()) ;
    }

    @Override
    public Dimension getPreferredSize() {

        int[] borderOffset = getBorderOffset();

        int[] playground_size = aktuelles_level.getMap().getPGSize();

        Dimension d = new Dimension(playground_size[0] * field_size + 2 * borderOffset[0], playground_size[1] * field_size + 2 * borderOffset[1] + getTopBarHeight());

        return d;
    }

    /***
     * Achtet darauf, dass ein Spieler nicht das Spielfeld verlässt, bewegt Spieler
     * @param velx Positionsänderung entlang der X-Achse
     * @param vely Positionsänderung entlang der Y-Achse
     * @param s Spieler
     */
    public void moveSP(int velx, int vely, Spieler s) {
        int[] spPos = s.getPosition().clone();

        spPos[0] += velx;
        spPos[1] += vely;

        int[] boundaries = new int[4];
        int offset = spieler_steps / 2;

        int[] bottomright = getCenterOf(aktuelles_level.getMap().getPGSize()); // untere Rechte Ecke
        int[] topleft = getCenterOf(new int[]{1, 1});

        boundaries[0] = topleft[1] -= offset; // Grenze oben
        boundaries[1] = bottomright[0] += offset; // Grenze Rechts
        boundaries[2] = bottomright[1] += offset; // Grenze unten
        boundaries[3] = topleft[0] -= offset; // Grenze Links

        if (boundaries[3] < spPos[0] && spPos[0] < boundaries[1] && boundaries[0] < spPos[1] && spPos[1] < boundaries[2])
            s.addPosOff(velx, vely);
    }

    protected void paintComponent(Graphics g) {
        if (aktuelles_level != null) {
            super.paintComponent(g);

            int[] borderOffset = getBorderOffset();

            // Zeichne Geldsäcke

            ArrayList<Geldsack> geldsaecke = aktuelles_level.getMap().getGeldsaecke();

            for (int i = 0; i < geldsaecke.size(); i++) {

                Geldsack single_item = geldsaecke.get(i);

                BufferedImage moneyPodImg;

                if (single_item.getShaking()) {
                    Animation a = current_skin.getAnimation("money_shaking");
                    moneyPodImg = a.nextFrame(field_size);
                } else
                    moneyPodImg = current_skin.getImage("money_static", field_size);

                int[] field = single_item.getField();
                int[] middle = single_item.getPosition();
                int x_pixel = middle[0] - (moneyPodImg.getWidth() / 2);
                int y_pixel = middle[1] - (moneyPodImg.getHeight() / 2);

                g.drawImage(moneyPodImg, x_pixel, y_pixel, null);

                if (devFrames) {
                    g.drawRect((field[0] - 1) * field_size + borderOffset[0], (field[1] - 1) * field_size + borderOffset[1], field_size, field_size);
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

                if (devFrames) {
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

                if (devFrames) {
                    int[] field = getFieldOf(single_item.getPosition());
                    g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                    g.setColor(Color.RED);
                }
            }

            // Feuerball

            ArrayList<Feuerball> feuerball = aktuelles_level.getMap().getFeuerball();

            for (int i = 0; i < feuerball.size(); i++) {
                Feuerball single_item = feuerball.get(i);
                BufferedImage pic = current_skin.getImage("fireball_red_f1", field_size);

                int[] pos = single_item.getPosition();
                int x_pixel = pos[0] - (pic.getWidth() / 2);
                int y_pixel = pos[1] - (pic.getHeight() / 2);

                g.drawImage(pic, x_pixel, y_pixel, null);

                if (devFrames) {
                    int[] field = getFieldOf(single_item.getPosition());
                    g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                    g.setColor(Color.RED);
                }
            }

            // Geld
            ArrayList<Geld> geld = aktuelles_level.getMap().getGeld();

            for (int i = 0; i < geld.size(); i++) {
                Geld single_item = geld.get(i);
                //Animation a = single_item.getAnimation();

                BufferedImage geldImg = current_skin.getImage("money_fall_f6", field_size); //a.nextFrame(field_size);

                int[] field = single_item.getField();
                int[] middle = getCenterOf(field);
                int x_pixel = middle[0] - (geldImg.getWidth() / 2);
                int y_pixel = middle[1] - (geldImg.getHeight() / 2);

                // scaling ...

                g.drawImage(geldImg, x_pixel, y_pixel, null);

                if (devFrames) {
                    g.drawRect(field[0] * field_size + borderOffset[0], field[1] * field_size + borderOffset[1], field_size, field_size);
                    g.setColor(Color.RED);
                }
            }

            // Spieler

            Spieler[] sp = new Spieler[2];

            sp[0] = sp1;
            sp[1] = sp2;

            Animation ani_left;
            Animation ani_right;
            Animation ani_up;
            Animation ani_down;

            for (int i = 0; i < sp.length; i++) {
                if (sp[i] != null) {
                    if (sp[i].isAlive()) {
                        if (i == 0) {
                            ani_left = current_skin.getAnimation("digger_red_left");
                            ani_right = current_skin.getAnimation("digger_red_right");
                            ani_up = current_skin.getAnimation("digger_red_up");
                            ani_down = current_skin.getAnimation("digger_red_down");
                        } else {
                            ani_left = current_skin.getAnimation("digger_gre_left");
                            ani_right = current_skin.getAnimation("digger_gre_right");
                            ani_up = current_skin.getAnimation("digger_gre_up");
                            ani_down = current_skin.getAnimation("digger_gre_down");
                        }
                    } else {
                        ani_left = current_skin.getAnimation("digger_dead_left");
                        ani_right = current_skin.getAnimation("digger_dead_right");
                        ani_up = current_skin.getAnimation("digger_dead_up");
                        ani_down = current_skin.getAnimation("digger_dead_down");
                    }

                    BufferedImage spImg = null;

                    if (sp[i].getMoveDir() == DIRECTION.RIGHT) {
                        spImg = ani_right.nextFrame(field_size);
                    }
                    if (sp[i].getMoveDir() == DIRECTION.LEFT) {
                        spImg = ani_left.nextFrame(field_size);
                    }
                    if (sp[i].getMoveDir() == DIRECTION.UP) {
                        spImg = ani_up.nextFrame(field_size);
                    }
                    if (sp[i].getMoveDir() == DIRECTION.DOWN) {
                        spImg = ani_down.nextFrame(field_size);
                    }


                    int x_pixel = sp[i].getPosition()[0] - (spImg.getWidth() / 2);
                    int y_pixel = sp[i].getPosition()[1] - (spImg.getHeight() / 2);
                    g.drawImage(spImg, x_pixel, y_pixel, null);
                }
            }


            // Zeichne Score
            int margin_y = field_size / 4;
            int margin_x = field_size / 2;

            int fontSize = field_size / 2;
            g.setFont(current_skin.getFont().deriveFont(Font.PLAIN, fontSize));
            g.setColor(Color.white);
            g.drawString(String.format("%05d", spielstand), margin_x, margin_y + fontSize);

            // Zeichne Leben

            // Zeichne Leben von SP1
            BufferedImage sp1Img = current_skin.getImage("statusbar_digger_MP_red", field_size);
            margin_x = 3 * field_size;
            for (int i = sp1.getLeben(); i > 0; i--) {
                g.drawImage(sp1Img, margin_x, margin_y, null);
                margin_x += sp1Img.getWidth();
            }

            // Zeichene auch leben von SP2
            if (sp2 != null) {
                margin_x = 9 * field_size;
                BufferedImage sp2Img = current_skin.getImage("statusbar_digger_MP_gre", field_size);
                for (int i = sp2.getLeben(); i > 0; i--) {
                    g.drawImage(sp2Img, margin_x, margin_y, null);
                    margin_x -= sp1Img.getWidth();
                }

            }
        }
    }

    /***
     * startet die Spiel-Loop in eigenem Thread
     */
    public void start() {
        loopThreat = new Thread(this);
        loopThreat.start();
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

                for (int i = 0; i <= gs.getPosition().length - 1; i++)
                    gs.getPosition()[i] *= factor;
            }
        }

        feuerball_steps = field_size / 15;
        spieler_steps = field_size / 10;

    }

    public void addListener(EndListener el) {
        this.el = el;
    }

    /***
     * startet neues Level mit übergebener Map
     * @param m Map-Objekt
     */
    public void setMap(Map m) {
        aktuelles_level = new Level(0, 0, 0, 0, m);
    }

    /***
     * Getter für den Spielstand
     * @return int Spielstand
     */
    public int getSpielstand() {
        return spielstand;
    }

    /***
     * Setter für den Spielstand
     * @param st int Spielstand
     */
    public void setSpielstand(int st) {
        spielstand = st;
    }

    /***
     * ändert die Position von Spieler2
     * @param c_pos Position
     */
    public void setClientPos(int[] c_pos) {
        sp2.setPosition(c_pos);
    }

    /***
     * ändert die Richtung von Spieler2
     * @param moveDir Richtung
     */
    public void setClientMoveDir(DIRECTION moveDir) {
        sp2.setMoveDir(moveDir);
    }

    /***
     * erhöht den aktuellen Spielstand um s
     * @param s int Neue Punkte
     */
    private void incScore(int s) {
        spielstand += s;
        incLifeCount += s;
    }

    public void resume() {
        //System.out.println("Spiel wurde fortgesetzt");
    }

    /***
     * Getter: Unterscheidung zwischen Single- und Multiplayermodus
     * @return isMultiplayer
     */
    public boolean getMultiplayer() {
        return isMultiplayer;
    }
}
