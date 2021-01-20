package Spielbereitstellug;

import Spielverlauf.ClientPackage;
import Spielverlauf.Map;
import Spielverlauf.ServerPackage;
import Spielverlauf.Spieler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;


public class Netzwerksteuerung {

	public static final int PORT = 1337;
	public ObjectOutputStream objectOutputStream_outToServer;
	public ObjectInputStream objectInputStream_inFromClient;
	public ObjectOutputStream objectOutputStream_outToClient;
	public ObjectInputStream getObjectInputStream_inFromClient;
	public boolean isHost;

	private ServerSocket serverSocket;
	private Socket client;

	private InetAddress ip;


	void displayMyIP() {

		try{
			URL whatismyip = new URL("http://icanhazip.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine(); // you get the IP as a String
			System.out.println(ip);
		}catch(Exception e){e.printStackTrace();
			System.out.println("Konnte IP-Adresse nicht herausfinden");}

	}

	Socket awaitingConnection(ServerSocket serverSocket) throws IOException {
		Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
		return socket;
	}


	public Netzwerksteuerung(){
		this(true, null);
	}

	public Netzwerksteuerung(InetAddress ip){
		this(false, ip);
	}
	public Netzwerksteuerung(boolean isHost, InetAddress ip) {

		this.ip =ip;
		this.isHost = isHost;

		connect();
		System.out.println("Client ist online!\n");
		killConnection();

	}

    public void serverSend( Map map, int spielstand ) {

		connect();
		// Server OUT
		ServerPackage sp = new ServerPackage(map, spielstand);

		// Sende sp hier mit objectOutputStream_outToClient
		try {
			objectOutputStream_outToClient.writeObject(sp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		killConnection();
	}

    public void serverGetContent(Spiel s) {

		connect();
		// Server IN
		ClientPackage sp= null; // get this package

		// Empfange sp hier mit objectInputStream_inFromClient
		try {
			sp = (ClientPackage) objectInputStream_inFromClient.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


		s.setClientPos(sp.getPos());
		s.setClientMoveDir(sp.getMoveDir());

		// noch nicht sicher wie, aber falls der spieler einen fb abfeuert dann
		if(sp.isFb_try())
			s.spawnFeuerball(s.sp2);

		killConnection();
	}

	public void clientSend(Spieler s, boolean try_fb) {

		connect();
		// Client OUT
		ClientPackage cp = new ClientPackage(s,try_fb);

		// Sende cp hier mit objectOutputStream_outToServer
		try {
			objectOutputStream_outToServer.writeObject(cp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		killConnection();
	}

	public void clientGetContent(Spiel s) {

		connect();
		// Client IN
		ServerPackage sp = null; // get this package

		// Empfange sp hier mit getObjectInputStream_inFromClient
		try {
			sp = (ServerPackage) getObjectInputStream_inFromClient.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		s.setMap(sp.getMap());

		s.setSpielstand(sp.getSpielstand());

		killConnection();
	}

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
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void connect(){
		try {

			if (isHost) {

				serverSocket = new ServerSocket(PORT); // serverSocket startet TCP-Verbindungsaufbau (UDP wäre Datagram Socket)
				client = awaitingConnection(serverSocket); // hier kann sich der Client verbinden

				// displayMyIP();

				//Streams

				// hier müsen wir statt einem OutputStream einen ObjectOutputStream nutzen um ganze objekte zu versenden

				OutputStream outToClient = client.getOutputStream();
				objectOutputStream_outToClient = new ObjectOutputStream(outToClient);

				//PrintWriter writer = new PrintWriter(outToClient);

				InputStream inFromClient = client.getInputStream();
				objectInputStream_inFromClient = new ObjectInputStream(inFromClient);



			} else {    /*in diesem Fall ist unsere Instanz der Client*/

				client = new Socket(ip, PORT);

				//Streams
				// werden zu Objekt Streams umgebaut

				OutputStream outToServer = client.getOutputStream();
				objectOutputStream_outToServer = new ObjectOutputStream(outToServer);
				//PrintWriter writer = new PrintWriter(outToServer);

				InputStream inFromServer = client.getInputStream();
				objectInputStream_inFromClient = new ObjectInputStream(inFromServer);
				//BufferedReader reader = new BufferedReader(new InputStreamReader(inFromServer));

			}


		}catch(IOException e){e.printStackTrace();}
	}

}