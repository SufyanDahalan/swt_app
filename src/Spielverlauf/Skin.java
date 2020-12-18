package Spielverlauf;

public class Skin {

	private int[] grosse;
	private string url;

	BufferedImage image = getImage( "dig_red_usd" );

	private BufferedImage getImage(String name) {

		JSONObject obj = null;
		try {
			obj = new JSONObject(new String(Files.readAllBytes(Paths.get("katalog.json"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray pic_val= obj.getJSONArray(name);

		int factor= 1;

		BufferedImage dest = null;
		try {
			dest = ImageIO.read(new File("img_template.png")).getSubimage(pic_val.getInt(0), pic_val.getInt(1), pic_val.getInt(2), pic_val.getInt(3));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return dest;
	}

}