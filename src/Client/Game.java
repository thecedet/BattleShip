package Client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Game {
    private HashMap<String, String> maps = new HashMap<>();
    private HashMap<String, String> symboles = new HashMap<>();
    private String username;
    private String dataMaps;
    private LinkedList<String> messages;

    public Game() {
        String[] array = new String[26];
        Arrays.fill(array, "");

        this.messages = new LinkedList<>(
            Arrays.asList(array)
        );
    }

    public void setUsername(String username) {
        this.username = username;
    } 

    public void addElement(String coord) {
        String[] temp = coord.split("\\|");
        int intCoord = Integer.parseInt(temp[0]);
        String oldMap = this.maps.get(temp[2]);
        String newMap = oldMap.substring(0, intCoord) + this.symboles.get(temp[1]) + oldMap.substring(intCoord+1);

        this.maps.put(temp[2], newMap);
        this.update();
    }

    public void init(String initMap) {
        String[] temp = initMap.split("\\|");
        this.maps.put(temp[0], temp[1]);
    }

    public String getSymbole(String key) {
        return this.symboles.get(key);
    }
    public void addSymbole(String data) {
        String[] temp = data.split("\\|");
        this.symboles.put(temp[0], temp[1]);
    }

    public String getMap(String maps) {
        
        this.dataMaps = maps;

        String[] mapsName = maps.split("\\|"); 
        String[] mapsNameShow = maps
            .replace(username+"_WORK", "Votre brouillon")
            .replace(username, "Votre grille")
            .replace("_WORK", " brouillon")
            .split("\\|");

        if(mapsName.length > 1) {
            String result = "";

            for(int i = 0; i < mapsName.length; i++) {
                if(i % 2 == 0) {
                    
                    String header = "";

                    int spaceCount = (36 - (int) mapsNameShow[i].length()) / 2;
                    int spaceCount2 = (37 - (int) mapsNameShow[i+1].length()) / 2;
                    
                    header += " ".repeat(spaceCount) + mapsNameShow[i] + " ".repeat(spaceCount+1);
                    header += " ".repeat(spaceCount2) + mapsNameShow[i+1];
                    header += " ".repeat(71 - header.length() + 1) + "\n";

                    result += header;

                    for(String line : this.maps.get(mapsName[i]).split("\n")) {
                        result += line + " |  %s\n";
                    }
                }else {
                    result = String.format(result, Arrays.asList(
                        this.maps.get(mapsName[i]).split("\n")
                    ).toArray()) + " ".repeat(36*2) +"\n";
                }
            }

            return mapsName.length == 2 ? result + (" ".repeat(36*2) +"\n").repeat(13) : result;
        }else return this.maps.get(mapsName[0]);
    }

    public void tchat(String message) {
        this.messages.remove();
        this.messages.add(message);
        this.update();
    }

    public void update() {

        System.out.print("\033[s\033[H");

        String result = "";
        for(String line : this.getMap(this.dataMaps).split("\n")) {
            result += line + "   | %s \n";
        }
        /* inverser l'arret
        List<Object> list = Arrays.asList(this.messages.toArray());
        Collections.reverse(list);
         */
        System.out.println(String.format(result, this.messages.toArray()));
        System.out.print("\033[u");
    }

}
