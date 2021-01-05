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
//Spieler 1
		BufferedImage[] rbilder = new BufferedImage[6];

		rbilder[0] = getImage("dig_red_rgt_f1");
		rbilder[1] = getImage("dig_red_rgt_f2");
		rbilder[2] = getImage("dig_red_rgt_f3");
		rbilder[3] = getImage("dig_red_rgt_f4");
		rbilder[4] = getImage("dig_red_rgt_f5");
		rbilder[5] = getImage("dig_red_rgt_f6");

		Animation a = new Animation(50, rbilder, this);
		animations.put("digger_red_right", a);

		BufferedImage[] lbilder = new BufferedImage[6];
		lbilder[0] = getImage("dig_red_lft_f1");
		lbilder[1] = getImage("dig_red_lft_f2");
		lbilder[2] = getImage("dig_red_lft_f3");
		lbilder[3] = getImage("dig_red_lft_f4");
		lbilder[4] = getImage("dig_red_lft_f5");
		lbilder[5] = getImage("dig_red_lft_f6");
		Animation b = new Animation(50, lbilder, this);
		animations.put("digger_red_left", b);

		BufferedImage[] ubilder = new BufferedImage[6];
		ubilder[0] = getImage("dig_red_up_f1");
		ubilder[1] = getImage("dig_red_up_f2");
		ubilder[2] = getImage("dig_red_up_f3");
		ubilder[3] = getImage("dig_red_up_f4");
		ubilder[4] = getImage("dig_red_up_f5");
		ubilder[5] = getImage("dig_red_up_f6");
		Animation c = new Animation(50, ubilder, this);
		animations.put("digger_red_up", c);

		BufferedImage[] dbilder = new BufferedImage[6];
		dbilder[0] = getImage("dig_red_dow_f1");
		dbilder[1] = getImage("dig_red_dow_f2");
		dbilder[2] = getImage("dig_red_dow_f3");
		dbilder[3] = getImage("dig_red_dow_f4");
		dbilder[4] = getImage("dig_red_dow_f5");
		dbilder[5] = getImage("dig_red_dow_f6");
		Animation d = new Animation(50, dbilder, this);
		animations.put("digger_red_down", d);

		//Spieler 2
		BufferedImage[] gre_rbilder = new BufferedImage[6];

		gre_rbilder[0] = getImage("dig_gre_rgt_f1");
		gre_rbilder[1] = getImage("dig_gre_rgt_f2");
		gre_rbilder[2] = getImage("dig_gre_rgt_f3");
		gre_rbilder[3] = getImage("dig_gre_rgt_f4");
		gre_rbilder[4] = getImage("dig_gre_rgt_f5");
		gre_rbilder[5] = getImage("dig_gre_rgt_f6");

		Animation e = new Animation(50, gre_rbilder, this);
		animations.put("digger_gre_right", e);

		BufferedImage[] gre_lbilder = new BufferedImage[6];
		gre_lbilder[0] = getImage("dig_gre_lft_f1");
		gre_lbilder[1] = getImage("dig_gre_lft_f2");
		gre_lbilder[2] = getImage("dig_gre_lft_f3");
		gre_lbilder[3] = getImage("dig_gre_lft_f4");
		gre_lbilder[4] = getImage("dig_gre_lft_f5");
		gre_lbilder[5] = getImage("dig_gre_lft_f6");
		Animation f = new Animation(50, gre_lbilder, this);
		animations.put("digger_gre_left", f);

		BufferedImage[] gre_ubilder = new BufferedImage[6];
		gre_ubilder[0] = getImage("dig_gre_up_f1");
		gre_ubilder[1] = getImage("dig_gre_up_f2");
		gre_ubilder[2] = getImage("dig_gre_up_f3");
		gre_ubilder[3] = getImage("dig_gre_up_f4");
		gre_ubilder[4] = getImage("dig_gre_up_f5");
		gre_ubilder[5] = getImage("dig_gre_up_f6");
		Animation g = new Animation(50, gre_ubilder, this);
		animations.put("digger_gre_up", g);

		BufferedImage[] gre_dbilder = new BufferedImage[6];
		gre_dbilder[0] = getImage("dig_gre_dow_f1");
		gre_dbilder[1] = getImage("dig_gre_dow_f2");
		gre_dbilder[2] = getImage("dig_gre_dow_f3");
		gre_dbilder[3] = getImage("dig_gre_dow_f4");
		gre_dbilder[4] = getImage("dig_gre_dow_f5");
		gre_dbilder[5] = getImage("dig_gre_dow_f6");
		Animation h = new Animation(50, gre_dbilder, this);
		animations.put("digger_gre_down", h);
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