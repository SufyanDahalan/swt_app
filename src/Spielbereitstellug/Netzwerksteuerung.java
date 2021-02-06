package Spielbereitstellug;

import Spielverlauf.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class Netzwerksteuerung {

	public static final int PORT = 65432; // ein "freier" d.h. dynamischer Port

	public ObjectOutputStream objectOutputStream;
	public ObjectInputStream objectInputStream;
	public boolean isHost;

	private ServerSocket serverSocket;
	private Socket streamSocket;

	private InetAddress ip;

	private String versandQueue = "";
	private String empfangsQueue = "";

	/***
	 * wird vom Server aufgerufen, um auf Verbindung des Clients zu warten
	 * @param serverSocket bekommt erstellen Serversocket
	 * @return gibt verbundenen Socket zurück
	 * @throws IOException IOException falls der Socket die Verbindung nicht annehmen kann
	 */
	Socket awaitingConnection(ServerSocket serverSocket) throws IOException {
		Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
		return socket;
	}


	public Netzwerksteuerung(){
		this(true, null);
	}

	/***
	 * Konstruktor, dem man die IP-Adresse übergeben kann.
	 * @param ip IP-Adresse des Servers
	 */
	public Netzwerksteuerung(InetAddress ip){
		this(false, ip);
	}

	/***
	 * Konstruktor kann auch vom Client genutzt werden. In diesem Fall mit isHost = False
	 * @param isHost Variable speichert Nutzerentscheidung
	 * @param ip IP-Adresse des Clients
	 */
	public Netzwerksteuerung(boolean isHost, InetAddress ip) {

		this.ip =ip;
		this.isHost = isHost;

		connect();
		System.out.println("Client ist online!\n");
		killConnection();

	}

	/***
	 * Methode zum Verschicken des Map-Objektes, wird periodisch vom Host aufgerufen
	 * @param s Spiel-Objekt, aus dem die Map bezogen wird
	 */
    public void serverExchange( Spiel s) {

		connect();
		// Server OUT
		// gibt noch Probleme beim serialisieren vom Bufferedimage
		
		ServerPackage sp = new ServerPackage(s.getLevel().getMap(), s.getSpielstand(), s.sp1, s.sp2, versandQueue, s.field_size);
		versandQueue = "";

		// Sende sp hier mit objectOutputStream_outToClient
		try {
			streamSocket.setSoTimeout(250);
			objectOutputStream.writeObject(sp);
		}
		catch(SocketTimeoutException timeout){
			System.out.println("waiting too long. next!");
 		}
 		catch (IOException e) {
			e.printStackTrace();
		}


		// Server IN
		ClientPackage cp= null; // get this package

		// Empfange sp hier mit objectInputStream_inFromClient
		try {
			cp = (ClientPackage) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if(cp != null) {
			if(cp.getSp() != null) {

				double scale = (double)s.field_size/(double)cp.getFieldSize();
				int[] pos = cp.getSp().getPosition();
				pos[0] *= scale;
				pos[1] *= scale;


				s.sp2.setPosition(pos);
				s.sp2.setMoveDir(cp.getSp().getMoveDir());

				// noch nicht sicher wie, aber falls der spieler einen fb abfeuert dann
				if (cp.getSp().getFired()) {
					s.spawnFeuerball(s.sp2);
				}
			}

			s.getChat().empfangen(cp.getVS());
		}
		else
			System.out.println("cp is null");

		killConnection();
	}

	/***
	 * Methode zum verschicken von Steuerungsinformationen, wird periodisch vom Client aufgerufen
	 * Verschickt wird die Bewegung der Spielfigur, sowie der Schießbefehl des Feuerballs
	 * @param s Spiel, aus dem die Steuerungsinformationen bezogen werden
	 */
	public void clientExchange(Spiel s) {
		connect();
		// Client OUT

		ClientPackage cp = new ClientPackage(s.sp2, versandQueue, s.field_size);

		versandQueue = "";

		// Sende cp hier mit objectOutputStream_outToServer
		try {
			objectOutputStream.writeObject(cp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(s.sp2.getFired())
			s.sp2.setFired(false);

		// Client IN
		ServerPackage sp = null; // get this package
		// gibt noch Probleme beim serialisieren vom Bufferedimage

		try {
			sp = (ServerPackage) objectInputStream.readObject();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (sp != null){

			double scale = (double)s.field_size/(double)sp.getFieldSize();

			Map map = sp.getMap();

			ArrayList<Geldsack> gsl = map.getGeldsaecke();
			ArrayList<Monster> ml = map.getMonster();
			ArrayList<Feuerball> fl = map.getFeuerball();

			for (Geldsack gs : gsl) {
				int[] pos = gs.getPosition();
				pos[0] *= scale;
				pos[1] *= scale;
			}

			for (Monster m : ml) {
				int[] pos = m.getPosition();
				pos[0] *= scale;
				pos[1] *= scale;
			}

			for (Feuerball f : fl) {
				int[] pos = f.getPosition();
				pos[0] *= scale;
				pos[1] *= scale;
			}

			s.setMap(map);
			s.setSpielstand(sp.getSpielstand());

			Spieler sp1 = sp.getSp1();

			scale = (double)s.field_size/(double)sp.getFieldSize();
			int[] pos = sp1.getPosition();
			pos[0] *= scale;
			pos[1] *= scale;

			s.sp1 = sp1;
			s.sp2.setLeben(sp.getSp2().getLeben());
			s.getChat().empfangen(sp.getVS());

		}

		killConnection();
	}

	/***
	 * Methode zum sauberen Beenden der Verbindung
	 * Wird von Client und Server verwendet
	 */
	private void killConnection() {
		if (isHost){
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				streamSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * Methode für Verbindungsaufbau
	 * Erstellt benötigte Sockets
	 */
	private void connect(){
		try {

			if (isHost) {
				serverSocket = new ServerSocket(PORT); // serverSocket startet TCP-Verbindungsaufbau (UDP wäre Datagram Socket)
				streamSocket = awaitingConnection(serverSocket); // hier kann sich der Client verbinden

				// displayMyIP();

			} else {    /*in diesem Fall ist unsere Instanz der Client*/
				streamSocket = new Socket(ip, PORT);
				//streamSocket = new Socket(ip, CLIENTPORT);
			}

			OutputStream outStream = streamSocket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outStream);

			//PrintWriter writer = new PrintWriter(outStream);

			InputStream inStream = streamSocket.getInputStream();
			objectInputStream = new ObjectInputStream(inStream);


		}catch(IOException e){e.printStackTrace();}
	}

	/***
	 * Schnittstelle zum Verschicken von Textnachrichten, wird vom Chat benutzt
	 * Chatnachrichten werden zunächst in einer versandQueue gesammelt, diese wird periodisch übertragen
	 * @param text zu sendende Textnachricht
	 */
	public void sendMsg(String text) {
		versandQueue += text;
	}

}