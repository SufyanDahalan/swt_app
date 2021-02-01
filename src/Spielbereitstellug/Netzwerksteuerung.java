package Spielbereitstellug;

import Spielverlauf.ClientPackage;
import Spielverlauf.ServerPackage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


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
	 * @throws IOException
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
		
		ServerPackage sp = new ServerPackage(s.getLevel().getMap(), s.getSpielstand(), s.sp1, s.sp2, versandQueue);
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
				s.sp2.setPosition(cp.getSp().getPosition());
				s.sp2.setMoveDir(cp.getSp().getMoveDir());

				// noch nicht sicher wie, aber falls der spieler einen fb abfeuert dann
				if (cp.isFb_try()) {
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
		boolean try_fb = s.sp2.getFired();
		s.sp2.setFired(false);

		ClientPackage cp = new ClientPackage(s.sp2, try_fb, versandQueue);
		versandQueue = "";

		// Sende cp hier mit objectOutputStream_outToServer
		try {
			objectOutputStream.writeObject(cp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Client IN
		ServerPackage sp = null; // get this package
		// gibt noch Probleme beim serialisieren vom Bufferedimage

		try {
			streamSocket.setSoTimeout(250);
			sp = (ServerPackage) objectInputStream.readObject();
		}
		catch(SocketTimeoutException timeout){
			System.out.println("waiting too long. next!");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (sp != null){
			s.setMap(sp.getMap());
			s.setSpielstand(sp.getSpielstand());
			s.sp1 = sp.getSp1();
			s.sp2.setLeben(sp.getSp2().getLeben());
			s.sp2.setFired(sp.getSp2().getFired());
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