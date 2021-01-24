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
	// zum Testen: auf dem lokalen Host kann der Client den Port nicht nutzen, da dieser vom Host benutzt wird.
	public static final int CLIENTPORT = 65430;

	public ObjectOutputStream objectOutputStream;
	public ObjectInputStream objectInputStream;
	public boolean isHost;

	private ServerSocket serverSocket;
	private Socket streamSocket;

	private InetAddress ip;

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

    public void serverExchange( Spiel s) {

		connect();
		// Server OUT
		// gibt noch Probleme beim serialisieren vom Bufferedimage
		
		ServerPackage sp = new ServerPackage(s.getLevel().getMap(), s.getSpielstand(), s.sp1, s.sp2);

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


		s.sp2 = cp.getSp();

		// noch nicht sicher wie, aber falls der spieler einen fb abfeuert dann
		if(cp.isFb_try())
			s.spawnFeuerball(s.sp2);

		killConnection();
	}

	public void clientExchange(Spiel s) {

		connect();
		// Client OUT
		boolean try_fb = s.sp2.getFired();
		s.sp2.setFired(false);

		ClientPackage cp = new ClientPackage(s.sp2, try_fb);

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
			s.sp2 = sp.getSp2();
		}

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
				streamSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

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

			/*
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
				String s = null;
				while ((s = reader.readLine()) != null) {
					writer.write(s + "\n");
					writer.flush();
					System.out.println("Empfangen vom Client:" + s + "\n");
				}

				System.out.println("Server sendet nun etwas zurück.\n");

				writer.write("Hallo Client! Hier ist eine lange zahl: 1234567899876563213456789\n"); // s.u.
				writer.flush();

				writer.close();
				reader.close();
			*/

		}catch(IOException e){e.printStackTrace();}
	}

}