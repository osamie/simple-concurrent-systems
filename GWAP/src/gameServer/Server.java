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
   private RandomData randomizer;
   HashMap<Integer,GameSession> sessionMap;
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;

   public Server()
   {
	   randomizer = new RandomDataImpl();
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
		   while (!serverSocket.isClosed()){
			 //wait for game host connection 
		       clientSocket = serverSocket.accept(); 
		       Integer gameID = randomizer.nextInt(0, 999999);
		       
		       while(sessionMap.containsKey(gameID)){
		    	 //keep generating gameIDs until a unique ID not in the gameSessionMap has been found
		    	   gameID = randomizer.nextInt(0, 999999);
		       }
		       
		       //create a new gameSession with the generated gameID
		       GameSession session = new GameSession(clientSocket,this,gameID);
		       
		       //add <gameID,session> pair to the gameSessionMap
		       sessionMap.put(gameID, session);
		       session.start();
		   }
		   
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