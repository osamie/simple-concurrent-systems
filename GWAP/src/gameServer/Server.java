package gameServer;



import gameModel.GameSession;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
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
   public HashMap<Integer,GameSession> sessionMap;
   
   private static String [] dictionary = 
	   {"whatever","house","game","love","kitchen",
	   "hat","skyfall","employment","cosmetics",
	   "lovers","bottle","hat-trick","skyfalling"}; 
   
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
			str.append(pairs.getKey() + ",");
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
		   serverSocket.close(); 
		   clientSocket.close();
	   } catch (IOException e) {
		   
	   }
   }
   
   /**
    * display server usage on the console
    */
   public static void help(){
	   System.out.println("\nInvalid port number \nUSAGE:\n\tjava gameServer.Server <server-port#> \n\te.g java gameServer.Server 5000");
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

   /**
    * returns a specific game session
    * @param sessionID
    * @return
    */
	public GameSession getGameSession(Integer sessionID) {
		return sessionMap.get(sessionID);
	}
}
