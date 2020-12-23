package Spielverlauf;

import org.json.JSONArray;

import Spielverlauf.*;

import javax.swing.*;

public class Tunnel{

	JSONArray fieldPosition;
	TUNNELTYP typ;


	public Tunnel(JSONArray fp, TUNNELTYP t){
		fieldPosition = fp;
        typ = t;
	}

	public JSONArray getPosition() {
		return fieldPosition;
	};



}