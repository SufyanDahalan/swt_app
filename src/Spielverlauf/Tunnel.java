package Spielverlauf;

import java.awt.image.BufferedImage;

public class Tunnel{

	int[] fieldPosition;
	TUNNELTYP typ;
	BufferedImage bild;


	public Tunnel(int[] fp, TUNNELTYP t, Skin sk){
		fieldPosition = fp;
		typ = t;

		if(t == TUNNELTYP.HORIZONTAL)
			bild = sk.getImage("tunnel_hori");
		else if(t == TUNNELTYP.VERTICAL)
			bild = sk.getImage("tunnel_vert");
		else
			bild = sk.getImage("tunnel_space");
	}

	public int[] getPosition() {
		return null;
	};

	public TUNNELTYP getTyp() {
		return typ;
	}

	public int[] getField() {
		return fieldPosition;
	}

    public BufferedImage getImage() {
        return bild;
    }
}