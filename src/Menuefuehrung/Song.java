package Menuefuehrung;

import javazoom.jl.player.Player;

import java.io.FileInputStream;



public class Song{
    public static void play (String name) throws Exception{
        FileInputStream popcorn = new FileInputStream(name);
        Player k = new Player(popcorn);
        System.out.println("playing");
        k.play();


    }

    public static void loop (String filepath){ // Das Lied wiederholt sich durch Die Ausführung.
        try
        {

         //AudioData data = new AudioStream(new FileInputStream(filepath)).getData();
         //ContinuiosAudioDataStream sound = new ContinuiosAudioDataStream(data);
         //AudioPlayer.player.start(sound)
        }
        catch (Exception e)
        {

        }
    }

    public static void main(String u[]) throws Exception {}
}