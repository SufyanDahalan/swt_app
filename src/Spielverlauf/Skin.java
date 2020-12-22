package Spielverlauf;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
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

	public BufferedImage getImage(String name, int fs) {

		JSONArray pic_val= skin_catalog.getJSONArray(name);

		// read catalog infos

		int x_off = pic_val.getInt(0);
		int y_off = pic_val.getInt(1);
		int width = pic_val.getInt(2);
		int hight = pic_val.getInt(3);

		// crop requested image from skin

		BufferedImage dest = null;
		try {
			dest = ImageIO.read(skin_graphic_file).getSubimage(x_off,y_off,width,hight);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// scale cropped image

		double scale_factor = fs/skin_catalog.getInt("reference"); // calculate teh scaling factor by referece of skin

		int new_width = (int)Math.round(scale_factor * dest.getWidth());
		int new_height = (int)Math.round(scale_factor * dest.getHeight());

		BufferedImage resizedImage = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(dest, 0, 0, new_width, new_height, null);
		g.dispose();

		return resizedImage;
	}
}