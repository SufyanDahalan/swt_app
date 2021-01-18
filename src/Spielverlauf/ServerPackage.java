package Spielverlauf;

public class ServerPackage {
    private Map map;
    private int spielstand;

    public ServerPackage(Map map, int spielstand) {
        this.map = map;
        this.spielstand = spielstand;
    }

    public Map getMap() {
        return map;
    }

    public int getSpielstand() {
        return spielstand;
    }
}
