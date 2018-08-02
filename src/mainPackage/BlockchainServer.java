package mainPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockchainServer extends Thread {
    public static final int PORT_NUMBER = 8081;
    static int NUM_CLIENT = 0;
    int	id_client;
    
    public static ArrayList<Socket> sockets = new ArrayList<Socket>();
    public static HashMap<Integer, Socket> map = new HashMap<>();
    public static HashMap<Integer, String> publicKeys = new HashMap<>();
    public static ArrayList<TransactionServer> transactionQueue = new ArrayList<TransactionServer>();
    protected Socket socket;


    private BlockchainServer(Socket socket) {
        this.socket = socket;
        System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
        //sockets.add(socket);
    	BlockchainServer.NUM_CLIENT++;
    	id_client = BlockchainServer.NUM_CLIENT;
        map.put(id_client, socket);
        //System.out.println(map);
        System.out.println("NUM: "+ BlockchainServer.NUM_CLIENT);
        start();
    }

    
    public void run() {
        InputStream in = null;
        
        int to = -1;
        Socket toSocket;
        OutputStream toOutputStream;
        
        int amount = 0;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request ="";
            boolean logged = false;
            //Connecting
            while ((request = br.readLine()) != null && logged == false) 
            {
            	String arr[] = request.split(" ", 2); 
            	if (arr[0].equals("login")) 
                {
                	String res = "NUM "+id_client+" ";
                	out.write(res.getBytes());
                	logged = true;
                	request = "";
                }
            	else
            	{
            		request = "You must first log in: just type \"login\"";
            	}
                request += '\n';
                out.write(request.getBytes());
                if (logged == true)
                	break;
            }
            /*
            //Get public key
            String publicKey;
            publicKey = br.readLine();
            publicKeys.put(id_client, publicKey);
            Wallet coinbase = new Wallet();
            
            
    		
    		//create genesis transaction, which sends 100 NoobCoin to walletA: 
    		Transaction genesisTransaction = new Transaction(coinbase.publicKey, publicKey, 100f, null);
    		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
    		genesisTransaction.transactionId = "0"; //manually set the transaction id
    		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
    		Block.UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
    		
    		System.out.println("Creating and Mining Genesis block... ");
    		Block genesis = new Block("0");
    		genesis.addTransaction(genesisTransaction);
    		Block.addBlock(genesis);
    		Block block1 = new Block(genesis.hash);
            */
            System.out.println("You have successfully logged in!\n");
            request = "";

            //Handling transactions
            while ((request = br.readLine()) != null) {
                System.out.println("Message received: " + request);
                String arr[] = request.split(" ", 4); 
                if (arr[0].equals("transaction")) 
                {
                	try
                	{
                		to = Integer.parseInt(arr[1]);
                    	amount = Integer.parseInt(arr[2]);
                	}catch (Exception e)
                	{
                		request = "Usage: transaction receiver amount.";
                	}
                	toSocket = map.get(to);
                	System.out.println("ENTER");
                	ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
                	System.out.println("WAITING FOR AN OBJECT...");
                	try
                	{
                    	TransactionServer t = (TransactionServer)inFromClient.readObject();
                    	System.out.println("Le montant est de... " + t.getAmount());
                    	ObjectOutputStream outToOtherClient = new ObjectOutputStream(toSocket.getOutputStream());
                    	outToOtherClient.writeObject(t);
                    	outToOtherClient.flush();
                	}
                	catch(Exception e)
                	{
                		System.out.println(e.getMessage());
                	}
                	System.out.println("HELLO");

                		
                    transactionQueue.add(new TransactionServer(this.id_client, to, amount));
                    printTransaction(transactionQueue, -1);
                    
                    toOutputStream = toSocket.getOutputStream();
                    toOutputStream.write(("TRANSACTION "+ this.id_client+" "+ to + " "+ amount+ " \n").getBytes());
                    transactionQueue.remove(0);
                    	
                    request = "Cool une transaction :)\n";

                }
                else if (arr[0].equals("list"))
                {
                	request = map.toString();
                }
                else if (arr[0].equals("")) 
                {
                	request = "TRANS: " + transactionQueue.size();
                }
                request += '\n';
                out.write(request.getBytes());
                request = "";
            }
        }catch(Exception e)
        {
        	System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace(); 
            }
        } 
    }
    public void printTransaction(ArrayList<TransactionServer> queue, int num)
    {
		TransactionServer t;

    	if (num == -1)
    	{
    		for (int i = 0; i < queue.size(); i++)
    		{
    			t = queue.get(i);
            	System.out.println("VALUE: "+ t.getAmount() + "FROM: "+ t.getFrom()+" TO: "+ t.getTo());
    		}
    	}
    	else
    	{
    		t = queue.get(num);
        	System.out.println("VALUE: "+ t.getAmount() + "FROM: "+ t.getFrom()+" TO: "+ t.getTo());
    	}
    }
/*
    public static void main(String[] args) {
        System.out.println("SocketServer Example");
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT_NUMBER);
            while (true) {
                new BlockchainServer(server.accept());
            }
        } catch (IOException ex) {
            System.out.println("Unable to start server.");
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }*/
    /*
    public static void main(String[] args) {	
		//add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
			
		Wallet coinbase = new Wallet();
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		//testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		isChainValid();
		
	}*/
}