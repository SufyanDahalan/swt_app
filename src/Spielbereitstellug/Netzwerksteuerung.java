package Spielbereitstellug;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

// import Spielverlauf.Map;


public class Netzwerksteuerung {

	public static final int PORT = 1337;
	// private Spiel spiel;



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

	/*public Map deserializeMap(String mapString){

		return null;
	}*/

	public Netzwerksteuerung( /*Spiel m,*/ boolean isHost) {

		//spiel = m;

		try {

			if (isHost) {

				ServerSocket serverSocket = new ServerSocket(PORT); // serverSocket startet TCP-Verbindungsaufbau (UDP wäre Datagram Socket)
				Socket client = awaitingConnection(serverSocket); // hier kann sich der Client verbinden

				// displayMyIP();

				//Streams

				OutputStream outToClient = client.getOutputStream();
				PrintWriter writer = new PrintWriter(outToClient);

				InputStream inFromClient = client.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient));

				String s = null;

				while ((s = reader.readLine()) != null) {
					writer.write(s + "\n");
					writer.flush();
					System.out.println("Empfangen vom Client:" + s + "\n");
				}

				/*System.out.println("Server sendet nun etwas zurück.\n");

				writer.write("Hallo Client! Hier ist eine lange zahl: 1234567899876563213456789\n"); // s.u.
				writer.flush();*/

				writer.close();
				reader.close();

				serverSocket.close();

			} else {    /*in diesem Fall ist unsere Instanz der Client*/

				Socket client = new Socket("localhost", PORT);


				//Streams

				OutputStream outToServer = client.getOutputStream();
				PrintWriter writer = new PrintWriter(outToServer);

				InputStream inFromServer = client.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inFromServer));

				System.out.println("Client ist online!\n");

				writer.write("Hallo Server!\n"); // ohne das "\n" blockiert die Verbindung, weil der Server auf ein Zeilenende wartet!
				writer.flush(); // damit es auch abgeschickt wird d.h. der Stream wird aktualisiert

				String s = null;

				while ((s = reader.readLine()) != null) {
					System.out.println("Empfangen vom Server:" + s);
					break; // nur so beenden sich beide Seiten wieder. Ansonsten wird weiterhin auf die Leitung gehört.
				}


				reader.close();
				writer.close();

				client.close();

			}


		}catch(IOException e){e.printStackTrace();}


	}


	public void befehlempfangen() {
		// TODO - implement Netzwerksteuerung.befehlempfangen
		throw new UnsupportedOperationException();
	}

	//Main Methode zum Teseten:
	public static void main(String[] args){

		String name;
		System.out.println("Wie heisst du?");
		Scanner sc = new Scanner(System.in);
		String eingabe = sc.next();
		System.out.println("Guten Tag, " + eingabe);



		if(eingabe.equals("1")){ System.out.println("Starte nun den Server.");
			Netzwerksteuerung n1 = new Netzwerksteuerung(true);}
		if(eingabe.equals("2")){ System.out.println("Starte nun den Client.");
			Netzwerksteuerung n1 = new Netzwerksteuerung(false);}

	}


}