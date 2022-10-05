package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import Server.User.User;

public class Server {
    
    private final HashMap<String, User> users = new HashMap<String, User>();
    private final HashMap<String, BattleShip> games = new HashMap<String, BattleShip>();

    public Server(String port) throws IOException {
        try (ServerSocket socket = new ServerSocket(Integer.parseInt(port))) {
			System.out.println("Serveur en écoute sur le port 1234");
                
			while(true) {
				Socket client = socket.accept();
                new User(this, client).start();
			}
		}catch(Exception e) {
            System.out.println(e);
        }
        
    }


    public HashMap<String, BattleShip> getGames() {
        return this.games;
    }
    public HashMap<String, User> getUsers() {
        return this.users;
    }
}
