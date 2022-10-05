
import java.util.Scanner;

import Server.Server;
import Client.Client;

public class App {

    public static void main(String[] args) throws Exception {

        String ip = "127.0.0.1";
        String port = "1234";
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Port: ");
        port = scanner.nextLine();
        
        System.out.print("[1] Server \n[2] Client \nMon choix: ");
        String choose = scanner.nextLine();

        if(choose.equals("1")) {
            new Server(port);
        }else if(choose.equals("2")) {
            System.out.print("IP: ");
            ip = scanner.nextLine();
            new Client(scanner, ip, port);
        }else {
            System.exit(0);
        }    
    }
}
