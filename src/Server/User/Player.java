package Server.User;

import Server.BattleShip;
import Server.Communication.Choice;
import Server.Communication.Question;
import Server.Map.Map;
import Server.Map.Ship;

public class Player {
    
    private final User user;
    private int life = 0;
    private Map map = new Map(BattleShip.SIZE);
    private Map mapWork = new Map(BattleShip.SIZE);

    public Player(User user) {
        this.user = user;

        this.init();
        
    }

    public int getLife() {
        return this.life;
    }

    public void hit() {
        this.life--;
    }

    private void AskplaceShip(Ship ship) {
        new Question("Les coordonnées du bateau " + ship.getName() + " taille:"+ ship.getSize()+" (ex: B4): ", user, (coord -> {
            if(coord.toUpperCase().matches("[A-Z]+[0-9]+")) {
                new Choice(new String[] {
                    "Vertical",
                    "Horizontal"
                }, this.user, (direction -> {
                    if(this.getMap().checkShip(coord, direction,ship.getSize())) {
                        int[] newCoord = Map.convert(coord);

                        for(int i = 0; i < ship.getSize(); i++) {
                            int[] newCoordWithDirection = this.getMap().coordWithDirection(newCoord, i, direction);
                            this.getMap().setShip(this.user, newCoordWithDirection, ship);
                        }
                    }else {
                        this.user.send("[INFO] Impossible de poser le bateau\\n");
                        this.AskplaceShip(ship);
                    }
                }));
            }else this.AskplaceShip(ship);
        }));
        this.user.send("Clean>");
        this.user.send("ShowMap>"+ this.user.getUsername());
    }
     
    private void init() {
        this.user.send("InitMap>"+this.user.getUsername()+"|"+this.getMap().show());
        this.user.send("InitMap>"+this.user.getUsername()+"_WORK|"+this.getMapWork().show());

        this.user.send("ShowMap>"+this.user.getUsername());

        this.user.send("[INFO] Merci de positionner les bateaux\\n");

        Ship[] ships = new Ship[] {
            new Ship("porte avion", 5),
            new Ship("torpilleur", 4),
            new Ship("croisseur", 3),
            new Ship("contre-torpilleur", 3),
            new Ship("contre-torpilleur", 3)
        };

        
        for(Ship ship : ships) {
            this.life += ship.getLife();
            this.AskplaceShip(ship);
        }
        
    }
    
    public Map getMap() {
        return this.map;
    }
    
    public Map getMapWork() {
        return this.mapWork;
    }
 
}
