package gameServer;

import gameModel.GameSession;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;

public class Server {

   private ServerSocket serverSocket;
   public static final int HOST_LISTENING_PORT = 5555;
   
//   private RandomData randomizer;
   HashMap<Integer,GameSession> sessionMap;
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;

   public Server()
   {
//	   randomizer = new RandomDataImpl();
	   sessionMap = new HashMap<Integer,GameSession>();
       try {
    	   //listen for new gameSession hosts
           serverSocket = new ServerSocket(HOST_LISTENING_PORT);
       } catch (IOException e) {
           e.printStackTrace(System.err);
           System.exit(1);
       }
   }

   public void launchGameServer()
   {   
	   try {
		   PrintWriter out;
		   Socket copySocket;
		   while (true){
			  //wait for an initial connection from host client 
			   clientSocket = serverSocket.accept();
			   System.out.println("connected "+clientSocket.getPort());
//		       Integer gameID = randomizer.nextInt(0, 999999);
		       
		       copySocket = clientSocket;
		       out = new PrintWriter(copySocket.getOutputStream(),true);
		       
		       System.out.println("connected "+clientSocket.getPort());
		       for(int i =0;i<99;i++){
		    	   out.println("connected "+clientSocket.getPort());
		       }
		       
		       //create a new gameSession with the generated gameID
		       GameSession session = new GameSession(clientSocket,this);
		       
		       //add <gameID,session> pair to the gameSessionMap
		       sessionMap.put(session.getGameID(), session);
		       session.start();
		   }
//		   System.out.println("here");
		   
	   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
	   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
	     
   }
   public void finalize()
   {
	   System.out.println("CLOSING!");
	   try { 
		   serverSocket.close(); 
		   clientSocket.close();
	   } catch (IOException e) {
		   
	   }
   }

   public static void main( String args[] )
   {
      Server c = new Server();
      c.launchGameServer();
   }
}