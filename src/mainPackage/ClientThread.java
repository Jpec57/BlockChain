package mainPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;

public class ClientThread {
    public static final int PORT_NUMBER = 8081;
    private Wallet my_wallet;
	private static int id = 0;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String clientName = "Unknown client";
    String response;
    String userInput;
    String arr[];
    Socket echoSocket = null;
    private PrintWriter out = null;
    BufferedReader in = null;


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

	            boolean logged = false;
	            
	            //Connection
	            
	            while(logged == false)
	            {
	                System.out.print(clientName+": ");
	                userInput = stdIn.readLine(); 
	                if ("close".equals(userInput)) {
	    	            out.close();
	    	            in.close();
	    	            stdIn.close();
	    	            echoSocket.close();
	                    System.exit(0);
	                }	                
	                out.println(userInput);
	                response = in.readLine();
	                System.out.println("server1: " + response);
	                
	                arr = response.split(" ", 3);
	                if (arr[0].equals("NUM")) 
	                {
	                	this.id = Integer.parseInt(arr[1]);
	                	clientName= "Client "+ this.id;
	                	logged = true;
	                }
	            }
	            System.out.println("You have successfully logged in!");

	            ObjectOutputStream outToServer = null;
	            try 
	            {
		            outToServer = new ObjectOutputStream(echoSocket.getOutputStream());
	            }
	            catch(Exception e)
	            {
	            	System.out.println(e.getMessage());
	            }
	            //ObjectInputStream inFromServer = new ObjectInputStream(echoSocket.getInputStream());
	            //System.out.println("DONE !");
	            
	            
	            TransactionServer t = null;
	            //Transaction handling

	            try {

	                while (true) { 
	                    System.out.print(clientName+": ");
	                    userInput = stdIn.readLine(); 
	                    if ("close".equals(userInput)) {
	                        break;
	                    }	  
	                    arr = userInput.split(" ", 3);

	                    out.println(userInput);
	                    if (arr[0].equals("transaction"))
	                    {
	        	            //System.out.println("SENDING INFO TO THE SERVER...");
	        	            t = new TransactionServer(this.id, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
//	        	            response = in.readLine();
//		                    System.out.println(response);
	    		            outToServer.writeObject((Object)t);
	    		            outToServer.flush();
//		                    ObjectInputStream inFromServer = new ObjectInputStream(echoSocket.getInputStream());
//		                    inFromServer.readObject();
		                    System.out.println("OBJECT SENT");
	                    } 
	                    response = in.readLine();


	                    System.out.println("server: " + response);
	                    String arr[] = response.split(" ", 3);
	                    if (arr[0].equals("TRANSACTION"))
	                    {
		                    System.out.println(arr[1]);
		                    System.out.println(arr[2]);
		                    System.out.println(arr[3]);
		                    outToServer.writeObject(t);
	                    }
	                    
	                    //

	                    
	                    //out.write(userInput);

	                    //
	            	}	
	                out.close();
	                in.close();
	                stdIn.close();
	                echoSocket.close();
	            } catch (Exception e) {
	                e.printStackTrace();
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
    class ServerSender implements Runnable
    {
		@Override
		public void run() {
            try {
            while (true) { 
                System.out.print(clientName+": ");
                userInput = stdIn.readLine(); 
                if ("close".equals(userInput)) {
                    break;
                }	                
                out.println(userInput);
                response = in.readLine();
                System.out.println("server: " + response);
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
    class ServerListener implements Runnable
    {
		@Override
		public void run() {
			String serverResponse = null;
			while (true) { 
					try {
						System.out.println("LISTEN...");
						serverResponse = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
                System.out.println("READ: " + serverResponse);
        	}
		}	
    }*/
}

