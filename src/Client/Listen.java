package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Listen extends Thread{

    private BufferedReader input;
    private Game game = new Game();

    public Listen(Socket socket) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void run() {
        try {
            while(true) {
                String message = this.input.readLine().replace("\\n", "\n");
                if(message.matches("^[^ >]+>(.|\n)*$")) {
                    String[] messages = message.split(">", 2);
                    switch(messages[0]) {
                        case "InitUsername" -> this.game.setUsername(messages[1]);
                        case "InitMap" -> this.game.init(messages[1]);
                        case "AddElement" -> this.game.addElement(messages[1]);
                        case "InitElement" -> this.game.addSymbole(messages[1]);
                        case "ShowMap" -> System.out.println(this.game.getMap(messages[1]));
                        case "Chat" -> {
                            if(!messages[1].split(":")[1].equals(" ")) this.game.tchat(messages[1]);
                        }
                        case "Clean" -> {
                            System.out.print("\033[H\033[2J"); 
                            System.out.flush();
                        }
                        case "Update" -> this.game.update();
                        case "Exit" -> System.exit(0);
                    }
                }else System.out.print(message);
                
            }
        }catch(Exception e) {
            System.out.println(e);
        }
    }

}
