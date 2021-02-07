package Menuefuehrung;

/***
 * Interface für häufig genutzte Speicherorte, enthält die Pfade für: Skins, Level, Musik und Bilder
 */
public interface Filesystem {

    String rootDir = "bin/";

    //String skinName = "witch_skin"; // Skinnname
    String skinName = "original_skin"; // Skinnname

    String skinfolder_name = rootDir + "skins/"; // ./skin/sink_original.json,...

    String levelfolder_name = rootDir+"level/"; // ./level/level-01.json ...

    String musicDir = rootDir+"music/";

    String imageDir = rootDir+"images/";
}
