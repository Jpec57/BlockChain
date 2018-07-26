package mainPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread {
    public static final int PORT_NUMBER = 8081;
    private Wallet my_wallet;


    public static void main(String args[]) {
        String host = "127.0.0.1";
        new ClientThread(host, PORT_NUMBER);
    }

    public ClientThread(String host, int port) {
        try {
        	my_wallet = new Wallet();
            String serverHostname = new String("127.0.0.1");

            System.out.println("Connecting to host " + serverHostname + " on port " + PORT_NUMBER + ".");

            Socket echoSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                echoSocket = new Socket(serverHostname, 8081);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + serverHostname);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
                System.exit(1);
            }

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("serverNb: " + in.readLine());
            while (true) {
                System.out.print("client: ");
                String userInput = stdIn.readLine();
                if ("close".equals(userInput)) {
                    break;
                }
                out.println(userInput);
                
                System.out.println("server: " + in.readLine());
            }
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
