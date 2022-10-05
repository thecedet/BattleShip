package Server.Map;

import Server.BattleShip;
import Server.User.User;

public class Map {
    
    IPiece[][] map;

    public Map(int[] size) {
        this.map = new IPiece[size[0]][size[1]];

        for(int i = 0; i < size[0]; i++) {
            for(int j = 0; j < size[1]; j++) {
                this.map[i][j] = new Piece(Symbol.UNKNOW);
            }
        }
    }

    public String show() {
        String result = "";
        String header = " ".repeat(4);

        for(int i = 0; i < this.map.length; i++) {
            
            header += Character.toString((char) 65 + i) + "  ";
            if(i != this.map.length) result += String.format("%02d ", i+1);

            for(int j = 0; j < this.map[0].length; j++) {
                result += "["+this.map[i][j].getSymbol()+"]";
            }

            result += " \\n";
        }

        return header + "\\n" + result;
    }
    
    public static String convertString(int[] coord) {
        return String.valueOf(35*(coord[1]+1) + 3*(coord[0]+2) - 2);
    }

    public static int[] convert(String coord) {
        String[] temp = coord.toUpperCase().split("(?<=[A-Z])(?=[0-9])");
        int x = (int) temp[0].charAt(0) - "A".charAt(0);
        int y = Integer.parseInt(temp[1]) - 1;

        return new int[] {x,y};
    }

    public boolean checkShip(String coord, String direction,int size) {
        
        int[] newCoord = Map.convert(coord);

        for(int i = 0; i < size; i++) {
            if(!this.checkSymbol(this.coordWithDirection(newCoord, i, direction), Symbol.UNKNOW)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkSymbol(int[] coord, Symbol symbol) {
        if(coord[0] >= BattleShip.SIZE[0]) return false;
        if(coord[1] >= BattleShip.SIZE[1]) return false;
        
        return this.getPiece(coord).getSymbol() == symbol;
    }

    public int[] coordWithDirection(int[] coord, int i, String direction) {
        return new int[] {
            coord[0] + (direction.equals("2") ? i : 0),
            coord[1] + (direction.equals("2") ? 0 : i)
        };
    }

    public void setShip(User user, int[] coord, Ship ship) {
        this.map[coord[1]][coord[0]] = ship;
        this.send(user, coord, Symbol.SHIP.name(), user.getUsername());
    }

    private void send(User user, int[] coord, String symbolName, String map) {
        user.send("AddElement>"+
            Map.convertString(coord)+"|"+symbolName+"|"+map);
    }

    public void editPiece(User user, int[] coord, Symbol symbol) {
        this.getPiece(coord).setSymbol(symbol);
        this.send(user, coord, symbol.name(), user.getUsername()+"_WORK");

        /*
        ArrayList<User> users = user.getGame().getUser();
        for(User spec : users.subList(2, users.size())) {
            this.send(spec, coord, symbol.name(), user.getUsername()+"_WORK");
        }
         */
    }

    public IPiece getPiece(int[] coord) {
        return this.map[coord[1]][coord[0]];
    }
}
