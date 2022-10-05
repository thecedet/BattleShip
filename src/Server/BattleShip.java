package Server;

import java.util.ArrayList;
import java.util.Random;

import Server.Map.Map;
import Server.Map.Symbol;
import Server.User.Player;
import Server.User.Role;
import Server.User.User;
import Server.User.User.PStatus;

public class BattleShip {
 
    public static final int[] SIZE = {10,10};
    
    private ArrayList<User> users = new ArrayList<>();
    private final String lobby;
    
    public enum Status {
        PREPARE, START, END
    }

    private Status status = Status.PREPARE;
    private int player = 0;

    public BattleShip(Server server) {
        this.lobby = String.format("%03d", new Random().nextInt(999));
        server.getGames().put(lobby, this);
    }
 

    public ArrayList<User> getUser() {
        return this.users;
    }
    
    public void addPlayer(User user) {
        user.setLobby(this.lobby);
        this.users.add(user);
        user.send("Clean>");
        if(this.users.size() <= 2) {
            user.setRole(Role.PLAYER, this);
        }else {
            user.setRole(Role.SPECTATOR, this);
        }
    }

    public void removePlayer(User user) {
        user.setRole(Role.NEW);
        this.users.remove(user);
    }

    public void start(User user) {
               
        if(this.status == Status.START) return;

                    
        this.broadcast("Clean>");
        this.broadcastPlayer((User player) -> {
            player.send("ShowMap>"+player.getUsername()+"|"+player.getUsername()+"_WORK");
            player.send("Update>");
        });

        if(this.users.size() < 2) {
            user.send("[INFO] Il manque un joueur...\\n\\n\\n\\n");
        }else {
            for(int i = 0; i < 2; i++) {
                if(this.users.get(i).getPlayer() == null) {
                    user.send("Un joueur n'est pas prêt..\\n\\n\\n\\n");
                    return;
                }
            }

            this.setStatus(Status.START);
        }
    }

    public String[] specInit() {
        String[] results = new String[4];
        for(int i=0;i<2;i++) {
            results[i*2] = "InitMap>"+
                this.getUser().get(i).getUsername()+"|"+
                this.getUser().get(i).getPlayer().getMap().show()
            ;
            results[i*2+1] = "InitMap>"+
                this.getUser().get(i).getUsername()+"_WORK|"+
                this.getUser().get(i).getPlayer().getMapWork().show()
            ;
        }
        return results;
    }

    private void switchPlayer() {
        this.player = this.getEnemy(this.player);
    }

    private int getEnemy(int player) {
        return (player+1) % 2;
    }

    public void play(User user, String message) {
        if(this.status == Status.START && this.users.indexOf(user) == this.player) {
            if(message.toUpperCase().matches("[A-Z]+[0-9]+")) {
                
                int[] coord = Map.convert(message);
                
                if(!user.getPlayer().getMapWork().checkSymbol(coord, Symbol.UNKNOW)) {
                    user.send("\033[s\033[2A\033[K[INFO] Vous avez déjà joué sur cette case.\033[u");
                    return;
                }

                Player enemy = this.users.get(this.getEnemy(this.player)).getPlayer(); 
                
                enemy.getMap().getPiece(coord).target((Symbol symbol,String response) -> {
                    Symbol newSymbol;
                    if(symbol == Symbol.SHIP) {
                        newSymbol = Symbol.HIT;
                        enemy.hit();
                    }else {
                        newSymbol = Symbol.OCEAN;
                    }
                    user.getPlayer().getMapWork().editPiece(user, coord, newSymbol);
                    this.broadcastSpec("AddElement>"+ Map.convertString(coord)+"|"+newSymbol.name()+"|"+user.getUsername()+"_WORK");
                    
    
                    this.users.get(this.getEnemy(this.player)).send("\033[s\033[2A\033[1K\033[K --- mode jeu --- \033[u");
                    this.users.get(this.player).send("\033[s\033[3A\033[1K\033[K --- mode tchat --- \033[u");

                    String result = "[INFO] "+user.getUsername()+" à joué en "+message.toUpperCase()+" => "+response;

                    this.users.get(this.getEnemy(this.player)).send("\033[s\033[A\033[K\033[1K"+result+" \033[u");
                    this.users.get(this.player).send("\033[s\033[2A\033[K\033[1K"+result+" \033[u");
                    this.broadcastSpec("\033[s\033[A\033[K\033[1K"+result+"\033[u");
                });
                 
                if(user.getPlayer().getLife() == 0 || enemy.getLife() == 0) {
                    this.setStatus(Status.END);
                    return;
                }

                this.switchPlayer();               
                
            }else user.send("\033[s\033[2A\033[K\033[1K[INFO] Saisie incorrecte (ex: B4)\033[u");
        }else {
            this.broadcast("Chat>\033[K"+user.getUsername()+": "+ message);
        }
    }
 
    public interface Callback {
        void callback(User user);
    }

    public void broadcastSpec(String message) {
        for(int i=2;i<this.users.size();i++) {
            users.get(i).send(message);
        }
    }

    public void broadcastPlayer(Callback callback) {
        int size = this.users.size() > 2 ? 2 : this.users.size();
        for(int i=0;i<size;i++) {
            User user = this.users.get(i);
            if(user.getPStatus() == PStatus.PLAY) {
                callback.callback(this.users.get(i));
            }
        }
    }

    public void broadcast(String message) {
        for(User user : this.users) {
            if(user.getPStatus() == PStatus.PLAY) {
                user.send(message);
            }
        }
    }

    public Status getSatus() {
        return this.status;
    }

    private void setStatus(Status status) {
        this.status = status;
        switch(status) {
            case START -> {
                
                for(String specInit : this.specInit()) {
                    this.broadcastSpec(specInit);
                }

                this.broadcast("Clean>");
                this.broadcastSpec("ShowMap>"+
                    this.getUser().get(0).getUsername()+"|"+
                    this.getUser().get(0).getUsername()+"_WORK|"+
                    this.getUser().get(1).getUsername()+"|"+
                    this.getUser().get(1).getUsername()+"_WORK"
                );
                this.broadcastPlayer((User player) -> {
                    player.send("ShowMap>"+player.getUsername()+"|"+player.getUsername()+"_WORK");
                });
                this.broadcast("Update>");
                this.broadcast("[INFO] La partie commence\\n");
                this.users.get(0).send("[INFO] C'est à vous de jouer.\\n>>");
                this.users.get(1).send("[INFO] Votre adversaire joue\\n\\n");
            }
            case END -> {
                this.broadcast("\\n\\nLa partie est terminée !!\\n");
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + status);
        }
    }

}
