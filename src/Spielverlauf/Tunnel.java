package Spielverlauf;

import org.json.JSONArray;

public class Tunnel{

	JSONArray fieldPosition;
	TUNNELTYP typ;


	public Tunnel(JSONArray fp, TUNNELTYP t){
		fieldPosition = fp;
		typ = t;
	}

	public JSONArray getPosition() {
		return null;
	};

	public TUNNELTYP getTyp() {
		return typ;
	}


	public JSONArray getField() {
		return fieldPosition;
	}
}