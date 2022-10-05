package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public Client(Scanner scanner, String ip, String port) throws UnknownHostException, IOException {
        Socket socket = new Socket(ip, Integer.parseInt(port));

        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        new Listen(socket).start();
        
        String message = "";

        while(!message.equals("quit")) {
            if(scanner.hasNextLine()) {
                message = scanner.nextLine();
                output.println(message);
            }
            
        }

        scanner.close();

    }
}
