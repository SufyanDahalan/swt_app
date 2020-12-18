package Spielverlauf;

import org.json.JSONArray;

import javax.swing.*;

public abstract class Tunnel{

	JSONArray position;
	ImageIcon picture;


	public Tunnel(JSONArray pos){
        position = pos;
	}



}