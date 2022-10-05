package Server.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Server.BattleShip;
import Server.Server;
import Server.BattleShip.Status;
import Server.Communication.Choice;
import Server.Communication.Question;
import Server.Map.Symbol;

public class User extends Thread {
    
    private Role role;
    private String username;

    public enum PStatus {
        PREPARE, PLAY
    }
    private PStatus pStatus = PStatus.PREPARE;

    private BattleShip game = null;
    private Player player = null;

    private BufferedReader input;
    private PrintWriter output;

    private final Server server;

    private String lobby;
    private final int TryConnection = 3;



    public User(Server server, Socket client) {
        this.server = server;

        try {
            this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.output = new PrintWriter(client.getOutputStream(), true);
        }catch(Exception err) {
            System.out.println(err);
        }

        this.setRole(Role.NEW);              
    }

    public String lisen() {
        try {
            return this.input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PStatus getPStatus() {
        return this.pStatus;
    }

    public BattleShip getGame() {
        return this.game;
    }

    public void run() {
        this.setUsername();
        this.askJoinGame();       
   
        this.pStatus = PStatus.PLAY;
   
        this.game.start(this);
   
        Question question = new Question(">>", this, (String result) -> {
            this.game.play(this, result);
        }, false);
   
        while(this.game.getSatus() != Status.END) {
            this.send("\033[A\033[J");
            question.ask();
        }
   
        new Choice(new String[] {
            "Nouvelle partie",
            "Quitter"
        }, this, (String result) -> {
            this.game = null;
            this.player = null;
            switch(result) {
                case "1" -> {
                    this.askJoinGame();
                }
                case "2" -> {
                    this.server.getUsers().remove(this.username);
                    this.send("Exit>");
                }
            }
        });
    }

    private void askJoinGame() {
        new Choice(new String[] {
            "Rejoindre une partie",
            "Créer une partie"
        }, this, (String result) -> {
            switch(result) {
                case "1" -> this.joinGame(this.TryConnection);
                case "2" -> new BattleShip(this.server).addPlayer(this);
            }
        });
    }

    private void joinGame(int tries) {
        new Question("Numéro de la partie: ", this, (String lobby) -> {
            if(this.server.getGames().containsKey(lobby)) {
                this.lobby = lobby;
                this.server.getGames().get(lobby).addPlayer(this);
            }else {
                this.send("La partie "+lobby+" n'existe pas\\n");
                if(tries != 1) this.joinGame(tries-1);
                else this.askJoinGame();               
            }
        });
    }

    public String getUsername() {
        return this.username;
    }
    private void setUsername() {
        new Question("Votre username: ",this, (String result) -> {
            if(this.server.getUsers().containsKey(result)) {
                this.send("[INFO] Le pseudo est déjà utilisé !\\n");
                this.setUsername();
            }else {
                this.server.getUsers().put(result, this);
                this.username = result;
                this.send("InitUsername>"+this.username);
            }
        });
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(Role role, BattleShip game) {
        this.role = role;
        this.game = game;

        this.send("[INFO] Vous venez de rejoindre la partie "+ this.lobby +" en tant que "+role.toString()+ "\\n");
        for(Symbol element : Symbol.values()) {
            this.send("InitElement>"+element.name()+"|"+element.toString());
        }
        
        if(role == Role.PLAYER) {
            this.player = new Player(this);
        }else if(role == Role.SPECTATOR && this.game.getSatus() == Status.START) {
            for(String specInit : this.game.specInit()) {
                this.game.broadcastSpec(specInit);
            }
            this.game.broadcastSpec("ShowMap>"+
                this.game.getUser().get(0).getUsername()+"|"+
                this.game.getUser().get(0).getUsername()+"_WORK|"+
                this.game.getUser().get(1).getUsername()+"|"+
                this.game.getUser().get(1).getUsername()+"_WORK"
            );
        }

    }
    public Player getPlayer() {
        return player;
    }
    
    public void send(String message) {
        this.output.println(message);
    } 

    public void setLobby(String lobby) {
        this.lobby = lobby;
    }

}
