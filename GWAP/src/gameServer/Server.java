/**
 * Server.java
 * 
 * -Accepts initial connections from clients 
 * -Spawns a new worker thread 'ServerWorker' for each connected client.
 * -Holds the collection/record of all currently available and running game sessions.  
 * -Holds the 'database' of words for the actual game play. 
 * 
 * @author Osazuwa Omigie
 */

package gameServer;

import gameModel.GameSession;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Server {
   private ServerSocket serverSocket;   
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;
   HashMap<Integer,GameSession> sessionMap;
   
   private static String [] dictionary = 
	   {"water","magnitude","house","game","love","kitchen",
	   "hat","skyfall","employment","cosmetics","drama",
	   "lovers","bottle","hat-trick","paradise","nuance","fusion","etymology",
	   "juvenile","factual","fast"}; 
   
   public Server(int portNum)
   {
	   sessionMap = new HashMap<Integer,GameSession>();
       try {
    	   //listen for new gameSession hosts
           serverSocket = new ServerSocket(portNum);
       } catch (IOException e) {
           e.printStackTrace(System.err);
           System.exit(1);
       }
   }
   
   
   /**
    * Iterates through the map of game sessions and returns a string
    * of all game session IDs
    * @return a string of sessionIDs separated by new lines
    */
   public String listGameSessions() {
	  StringBuilder str = new StringBuilder();
	  
	  Iterator<Entry<Integer, GameSession>> iterator = sessionMap.entrySet().iterator();
	  
	  while(iterator.hasNext()){
		  Map.Entry<Integer, GameSession> pairs = (Map.Entry<Integer, GameSession>)iterator.next();
			str.append(pairs.getKey() + String.format("[%d]",pairs.getValue().getPlayerCount() ) +",");
	  }	  
	  if (str.length() == 0) str.append("No game sessions");
	  return str.toString();
   }
   
   
   /**
    * 
    * @return random word from the dictionary	
    */
   public String getAword(){
	   Random generator = new Random();
	   return dictionary[generator.nextInt(dictionary.length)];
   }
   
   public synchronized void addToMap(Integer gameID,GameSession session){
	 //add <gameID,session> pair to the gameSessionMap
       sessionMap.put(gameID, session);
   }
   
   public void launchGameServer()
   {
	   System.out.println("\t*****************");
	   System.out.println("\tGWAP Game Server");
	   System.out.println("\t*****************");
	   try {
		   while (true){
			  //wait for an initial connection from host client 
			   clientSocket = serverSocket.accept();
			   new ServerWorker(clientSocket,this).start();  
		   }
	   
	   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
	   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
	     
   }
   
   @Override
   public void finalize()
   {
	   try {
		   Iterator<Entry<Integer, GameSession>> iterator = sessionMap.entrySet().iterator();
		   
		  //End all game sessions
		   while(iterator.hasNext()){
			   Map.Entry<Integer, GameSession> pairs = (Map.Entry<Integer, GameSession>)iterator.next();
			   pairs.getValue().endSession();
		   }
			  
		   serverSocket.close(); 
		   clientSocket.close();
	   } catch (IOException e) {
		   
	   }
   }
   
   /**
    * Displays server usage on the console
    */
   public static void help(){
	   System.out.println("\nERROR:Invalid port number \nUSAGE:\n\tjava gameServer.Server <server-port#> \n\t(e.g java gameServer.Server 5000)");
   }


   /**
    * returns a specific game session
    * @param sessionID
    * @return
    */
	public GameSession getGameSession(Integer sessionID) {
		return sessionMap.get(sessionID);
	}


	public void removeSession(GameSession gameSession) {
		sessionMap.remove(gameSession);
	}
	
	public static void main( String args[] )
	{
		if(args.length < 1){
	    	  help();
	    	  return;
		} 
		Server c = new Server(Integer.parseInt(args[0]));
		c.launchGameServer();
	}
}
