package Spielverlauf;

import org.json.JSONArray;

import Spielverlauf.*;

import javax.swing.*;

public class Tunnel{

	JSONArray position;
	TUNNELTYP typ;


	public Tunnel(JSONArray pos, TUNNELTYP t){
        position = pos;
        typ = t;
	}

	public JSONArray getPosition() {
		return position;
	};



}