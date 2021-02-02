package Spielbereitstellug;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; // Create a simple GUI window


/***
 * Klasse für den Multiplayer-Chat
 * erstellt GUI zum chatten
 * arbeitet zusammen mit der Netzwerksteuerung zum verschicken und empfangen von Textnachrichten
 */
public class Chat {

	// Variablen Deklaration für praktischen Zugriff
	private JFrame frame = new JFrame("Chat");
	private javax.swing.JTextArea textfeld; // Zeigt Chatnachrichten an
	private javax.swing.JTextArea eingabeFeld; // hier kann der Nutzer tippen
	private Netzwerksteuerung netConnect;

	/***
	 * Getter fürs Textfeld (aka. Ausgabefeld)
	 * @return JTextArea textfeld
	 */
	public JTextArea getTextfeld() {
		return textfeld;
	}

	/***
	 * Getter fürs Eingabedeld
	 * @return JTextArea eingabeFeld
	 */
	public JTextArea getEingabeFeld() {
		return eingabeFeld;
	}

	/***
	 * zum festlegen der Netzwerksteuerung
	 * @param n Netzwerksteuerung
	 */
	public void setConnection(Netzwerksteuerung n){ netConnect = n; }

	// JButton sendButt;

	/***
	 * erstellt das Chatfenster (GUI)
	 * bestehend aus: Ausgabefeld, Eingabefeld, Senden-Button
	 */
	private void createWindow() { // Create and set up the window.

		frame = new JFrame("Chat");


		// Wenn der Nutzer das Fenster Schließt, verschwindet der Chat
		// Wenn der Nutzer das Fenster nur minimiert, öffnet sich dieses, bei erhalt einer Nachricht automatisch
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		// Denn HIDE_ON_CLOSE is the same as JFrame.setVisible(false)
		// Leider ist der Unischtbare Frame nicht ohne weiteres wieder SetVisible


		JLabel textLabel = new JLabel("I'm a label in the window", SwingConstants.CENTER);
		textLabel.setPreferredSize(new Dimension(300, 100));

		textfeld = new JTextArea(5, 20);
		textfeld.setEditable(false);
		textfeld.setFont(new java.awt.Font("Times New Roman", 0, 22));

		// textfeld.setText("Hier ist ein Teststring ");

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
				// getTextfeld().append(" Der Button wurde geklickt");
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

	/***
	 * Konstruktor für den Chat, bekommt eine Netzwerksteuerung übergeben, über welche die Nachrichten ausgetauscht werden
	 * @param netCon Netzwerksteuerung i.d.R. wird diese auch zum Austasch von Map und Steuerungsinformationen genutzt
	 */
	public Chat( Netzwerksteuerung netCon ){
		createWindow();
		setConnection( netCon );
	}

	/***
	 * Methode zum verschicken von Nachrichten,
	 * wird mit drücken der "Senden"-Schaltfläche aufgerufen
	 * verschiebt Inhalt des Eingabefeldes in die versandQueue der Netzwerksteuerung
	 */
	public void senden(){
		String versandString = getEingabeFeld().getText();
		netConnect.sendMsg(versandString);
		getEingabeFeld().setText("");
		getTextfeld().append("Ich: " + versandString + "\n");
	}

	/***
	 * Methode zum empfangen eines Text-Strings,
	 * Wird von der Netzwerksteuerung aufgerufen,
	 * Zeigt den text im Ausgabefeld an,
	 * Außerdem wird ein Minimiertes Fenster wieder sichtbar
	 * @param text Chatnachricht (ggf Mehrere aus der versandQueue des Absenders)
	 */
	public void empfangen(String text){
		if( ! text.equals("") ){
			getTextfeld().append("Dein Mitspieler: " + text + "\n");
			// Falls das Fenster minimiert wurde, wird es bei erhalt einer Nachricht wieder sichtbar
			// Achtung: Minimiert /= geschlossen !
			frame.setState(JFrame.NORMAL);
		}
	}


}