package Spielbereitstellug;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; // Create a simple GUI window



public class Chat {

	// Variablen Deklaration für praktischen Zugriff
	private javax.swing.JTextArea textfeld; // Zeigt Chatnachrichten an
	private javax.swing.JTextArea eingabeFeld; // hier kann der Nutzer tippen
	private Netzwerksteuerung netConnect;

	public JTextArea getTextfeld() {
		return textfeld;
	}

	public JTextArea getEingabeFeld() {
		return eingabeFeld;
	}

	public void setConnection(Netzwerksteuerung n){ netConnect = n; }

	JButton sendButt;

	private void createWindow() { // Create and set up the window.

		JFrame frame = new JFrame("Simple GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JLabel textLabel = new JLabel("I'm a label in the window", SwingConstants.CENTER);
		textLabel.setPreferredSize(new Dimension(300, 100));

		textfeld = new JTextArea(5, 20);
		textfeld.setEditable(false);
		textfeld.setFont(new java.awt.Font("Times New Roman", 0, 22));

		textfeld.setText("Hier ist ein Teststring ");

		textfeld.setLineWrap(true); // Zeilenumbruch wird eingeschaltet
		textfeld.setWrapStyleWord(true); // Zeilenumbrüche erfolgen nur nach ganzen Wörtern

		eingabeFeld = new JTextArea();
		eingabeFeld.setColumns(20);
		eingabeFeld.setLineWrap(true);
		eingabeFeld.setRows(5);

		// Ein JScrollPane, der das Textfeld beinhaltet, wird erzeugt
		JScrollPane scrollpane = new JScrollPane(textfeld);
		scrollpane.setViewportView(textfeld);
		// scrollpane.setFitToWidth(true); // geht nicht weil JscrollPane nicht nur
		// scrollpane

		// Ein JScrollPane, der das EingabeFeld beinhaltet, wird erzeugt
		JScrollPane scrollpane2 = new JScrollPane(eingabeFeld);
		scrollpane2.setViewportView(eingabeFeld);

		// Hier wird der Sendenbutton erstellt
		JButton sendButt = new JButton("senden");

		sendButt.addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTextfeld().append(" Der Button wurde geklickt");
				senden();
			}
		});



		frame.add(scrollpane, BorderLayout.NORTH);
		frame.add(scrollpane2, BorderLayout.CENTER);
		frame.add(sendButt, BorderLayout.SOUTH);

		frame.setLocationRelativeTo(null);
		frame.pack(); // Fenstergröße wird an Textfeld angepasst
		frame.setVisible(true);

	}

	public Chat(){
		createWindow();
		//setConnection(n);
	}

	public void senden(){
		String versandString = getEingabeFeld().getText();
		netConnect.sendeText(versandString);
		getEingabeFeld().setText("");
		getTextfeld().append("Ich: " + versandString + "\n");
	}

	public void empfangen(){
		String empfangsString = netConnect.empfangeText();
		getTextfeld().append("Dein Mitspieler: " + empfangsString + "\n");
	}


	public static void main(String[] args) {       //createWindow();


		/*Netzwerksteuerung netCont1 = null;
		try {
			netCont1 = new Netzwerksteuerung(true, InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Netzwerksteuerung netCont2 = null;
		try {
			netCont2 = new Netzwerksteuerung(false, InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}*/

		Chat Chat1 = new Chat();

		Chat Chat2 = new Chat();

		Chat1.getTextfeld().append("hier ist ein zweiter String, viel Spaß damit");
	}

}