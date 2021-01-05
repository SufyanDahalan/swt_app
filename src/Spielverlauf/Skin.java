package Spielverlauf;

import Spielverlauf.Animation;
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
import java.util.HashMap;
import java.util.Iterator;

public class Skin {

	private HashMap<String, BufferedImage> images;
	private HashMap<String, Animation> animations;
	private BufferedImage[] numbers;
	private BufferedImage[] letters;
	private Font font;

	private String name;
	private int reference;

	// Load Skin
	public Skin(File skinDir, String skinname){

		File skin_graphic_file = new File(skinDir, skinname+".png");
		File skin_catalog_file = new File(skinDir, skinname+".json");

		JSONObject obj = null;

		try {
			obj = new JSONObject(new String(Files.readAllBytes(Paths.get(skin_catalog_file.getPath()))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		images = new HashMap<>();
		animations = new HashMap<>();
		numbers = new BufferedImage[10];
		letters = new BufferedImage[26];

		name = obj.getString("name");
		reference = obj.getInt("reference");

		JSONObject imageData = obj.getJSONObject("data");

		Iterator<String> keys = imageData.keys();

		while(keys.hasNext()) {
			String key = keys.next();

			JSONArray pic_val = imageData.getJSONArray(key);

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

			images.put(key, dest);


		}

		createAnimations();

		try{
			font = Font.createFont(Font.TRUETYPE_FONT, new File(skinDir, skinname+".ttf"));
		}
		catch (IOException|FontFormatException e){

		}

	}

	private void createAnimations() {

		BufferedImage[] bilder = new BufferedImage[6];

		bilder[0] = getImage("dig_red_rgt_f1");
		bilder[1] = getImage("dig_red_rgt_f2");
		bilder[2] = getImage("dig_red_rgt_f3");
		bilder[3] = getImage("dig_red_rgt_f4");
		bilder[4] = getImage("dig_red_rgt_f5");
		bilder[5] = getImage("dig_red_rgt_f6");

		Animation a = new Animation(2, bilder, this);

		animations.put("digger_red_right", a);
	}

	public BufferedImage getImage(String name, int fs) {

		BufferedImage dest = images.get(name);
		return scale(dest,fs);

	}

	public BufferedImage getImage(String name) {

		BufferedImage dest = images.get(name);
		return images.get(name);

	}

	public BufferedImage scale(BufferedImage bi, int fs){

		BufferedImage dest = bi;

		// scale cropped image

		double scale_factor = fs/reference; // calculate teh scaling factor by referece of skin

		int new_width = (int)Math.round(scale_factor * dest.getWidth());
		int new_height = (int)Math.round(scale_factor * dest.getHeight());

		BufferedImage resizedImage = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(dest, 0, 0, new_width, new_height, null);
		g.dispose();

		return resizedImage;
	}

	public Font getFont() {
		return font;
	}

    public Animation getAnimation(String bez) {
        return animations.get(bez);
    }
}