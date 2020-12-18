package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Skin {

	private File skin_graphic_file; // Grafikdatei
	private JSONObject skin_catalog; // Grafikaktalog

	// Load Skin
	public Skin(File skinDir, String skinname){

		skin_graphic_file = new File(skinDir, skinname+".png");
		File skin_catalog_file = new File(skinDir, skinname+".json");

		try {
			skin_catalog = new JSONObject(new String(Files.readAllBytes(Paths.get(skin_catalog_file.getPath()))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage(String name) {

		JSONArray pic_val= skin_catalog.getJSONArray(name);

		BufferedImage dest = null;
		try {
			dest = ImageIO.read(skin_graphic_file).getSubimage(pic_val.getInt(0), pic_val.getInt(1), pic_val.getInt(2), pic_val.getInt(3));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return dest;
	}

}