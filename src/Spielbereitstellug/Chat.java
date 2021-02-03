package Spielbereitstellug;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; // Create a simple GUI window
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicTextFieldUI;


/***
 * Klasse für den Multiplayer-Chat
 * erstellt GUI zum chatten
 * arbeitet zusammen mit der Netzwerksteuerung zum verschicken und empfangen von Textnachrichten
 */
public class Chat extends JPanel {

	// Variablen Deklaration für praktischen Zugriff
	private JFrame frame = new JFrame("Chat");
	private javax.swing.JTextArea textfeld; // Zeigt Chatnachrichten an
	private javax.swing.JTextField eingabeFeld; // hier kann der Nutzer tippen
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
	public JTextField getEingabeFeld() {
		return eingabeFeld;
	}

	/***
	 * zum festlegen der Netzwerksteuerung
	 * @param n Netzwerksteuerung
	 */
	public void setConnection(Netzwerksteuerung n){ netConnect = n; }

	// JButton sendButt;

	/***
	 * Konstruktor für den Chat, bekommt eine Netzwerksteuerung übergeben, über welche die Nachrichten ausgetauscht werden
	 * @param netCon Netzwerksteuerung i.d.R. wird diese auch zum Austasch von Map und Steuerungsinformationen genutzt
	 */
	public Chat( Netzwerksteuerung netCon ){

		this.setLayout(new BorderLayout());
		this.setBackground(new Color(0,0,0,200));
		this.setBorder( BorderFactory.createEmptyBorder(10,10,10,10));

		textfeld = new JTextArea(5, 20);
		textfeld.setEditable(false);
		textfeld.setOpaque(false);
		textfeld.setBackground(new Color(0,0,0,0));
		textfeld.setFont(new java.awt.Font("Times New Roman", 0, 20));
		textfeld.setForeground(Color.white);

		// textfeld.setText("Hier ist ein Teststring ");

		textfeld.setLineWrap(true); // Zeilenumbruch wird eingeschaltet
		textfeld.setWrapStyleWord(true); // Zeilenumbrüche erfolgen nur nach ganzen Wörtern

		eingabeFeld = new JTextField();
		eingabeFeld.setColumns(20);
		eingabeFeld.setForeground(Color.black);
		eingabeFeld.setBackground(Color.white);
		eingabeFeld.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black),BorderFactory.createEmptyBorder(10,10,10,10)));

		// Ein JScrollPane, der das Textfeld beinhaltet, wird erzeugt
		JScrollPane scrollpane = new JScrollPane(textfeld);
		scrollpane.setViewportView(textfeld);
		scrollpane.setOpaque(false);
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		scrollpane.getViewport().setOpaque(false);
		scrollpane.setViewportBorder(BorderFactory.createEmptyBorder());

		// Hier wird der Sendenbutton erstellt
		/*JButton sendButt = new JButton("senden");
		sendButt.setBackground(Color.RED);

		sendButt.addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// getTextfeld().append(" Der Button wurde geklickt");
				senden();
			}
		});*/


		this.add(scrollpane, BorderLayout.NORTH);
		this.add(eingabeFeld, BorderLayout.CENTER);
		// this.add(sendButt, BorderLayout.SOUTH);
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
		if( !text.equals("") ){
			this.setVisible(true);
			getTextfeld().append("Dein Mitspieler: " + text + "\n");
			// Falls das Fenster minimiert wurde, wird es bei erhalt einer Nachricht wieder sichtbar
			// Achtung: Minimiert /= geschlossen !
			frame.setState(JFrame.NORMAL);
		}
	}

}