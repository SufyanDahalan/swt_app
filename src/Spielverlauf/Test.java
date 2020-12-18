import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.*;

import Spielverlauf.Skin;
import org.json.*;

public class Test {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test::new);
    }

    public Test() {

        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        File skin_graphic_file = new File("skins");
        Skin s = new Skin(skin_graphic_file, "original_skin");

        JSONObject obj = null;
        try {
             obj =  new JSONObject(new String(Files.readAllBytes(Paths.get("../bin/level/level_01.json"))));
        } catch (Exception e) {
            e.printStackTrace();
        }


        int[] size = {300,300};

        Map map = new Map(obj, size ,s);

        frame.add((Component) map);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}