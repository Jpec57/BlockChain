package mainPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;

public class ClientThread {
    public static final int PORT_NUMBER = 8081;
    private Wallet my_wallet;
	private PrintWriter out;
	private int id = 0;


    public static void main(String args[]) {
        String host = "127.0.0.1";
        new ClientThread(host, PORT_NUMBER);
    }

    public ClientThread(String host, int port) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    	my_wallet = new Wallet();
		   try {
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
	            String clientName = "Unknown client";
	            
	            while (true) { 
	                System.out.print(clientName+": ");
	                String userInput = stdIn.readLine();
	                if ("close".equals(userInput)) {
	                    break;
	                }	                
	                out.println(userInput);
	                String response = in.readLine();
	                System.out.println("server: " + response);
	                
	                String arr[] = response.split(" ", 3);
	                if (arr[0].equals("NUM")) 
	                {
	                	System.out.println(arr[1]);
	                	this.id = Integer.parseInt(arr[1]);
	                	clientName= "Client "+ this.id;
	                }
	            }
	            out.close();
	            in.close();
	            stdIn.close();
	            echoSocket.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		/*
        try {
        	my_wallet = new Wallet();
            String serverHostname = new String("127.0.0.1");

            System.out.println("Connecting to host " + serverHostname + " on port " + PORT_NUMBER + ".");

            Socket echoSocket = null;
            out = null;
            BufferedReader in = null;

            try {
                echoSocket = new Socket(serverHostname, 8081);
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + serverHostname);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
                System.exit(1);
            }

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            //System.out.println("serverNb: " + in.readLine());
            while (true) {
                System.out.print("client: ");
                String userInput = stdIn.readLine();
                if ("close".equals(userInput)) {
                    break;
                }
                try {
                out.println(userInput);
                }catch(NullPointerException n)
                {
                	System.out.println("Problem with stream");
                }
                
                
                System.out.println("server: " + in.readLine());
            }
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
