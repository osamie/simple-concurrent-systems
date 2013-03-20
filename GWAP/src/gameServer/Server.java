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

import gameModel.BufferManager;
import gameModel.GameSession;
import gameModel.Merger;
import gameModel.ServerWorkerPool;
import gameModel.SessionPool;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;


public class Server {
   private ServerSocket serverSocket;   
   private Socket clientSocket;
   private ConcurrentHashMap<Integer,GameSession> sessionMap;
   private ServerWorkerPool workerPool;
   private SessionPool sessionPool;
   private BufferManager bufferManager;
   private LinkedList<String []> wordsDB;
   
   /*
    * NUM_OF_SESSIONS is the maximum number of game sessions  
    * at a time. 
    */
   public static final int NUM_OF_SESSIONS = 10;
   
  /*
   * NUM_OF_WORKERS is the maximum number of serverWorkers (i.e maximum number 
   * of clients you can have in the menu stage at a time.) 
   */
   public static final int NUM_OF_WORKERS = 10;
   
 //message queue for server - serverWorker communication
   private final ArrayBlockingQueue<Socket> newClientsQueue;
   
   //message queue for serverWorker - sessionPool communication
 	private final ArrayBlockingQueue<Socket> gameHostQueue;
   
   //read-only word list
   private final static String [] dictionary = 
	   {"water","magnitude","house","game","love","kitchen",
	   "hat","skyfall","employment","cosmetics","drama",
	   "lovers","bottle","hat-trick","paradise","nuance","fusion","etymology",
	   "juvenile","factual","fast"}; 
   
   public Server(int portNum)
   { 
	   sessionMap = new ConcurrentHashMap<Integer,GameSession>();
	   
	   newClientsQueue = new ArrayBlockingQueue<Socket>(NUM_OF_WORKERS,true);
	   gameHostQueue = new ArrayBlockingQueue<Socket>(NUM_OF_SESSIONS,true);
	   wordsDB = new LinkedList<String[]>();
	   
	   
       try {
    	   //listen for new gameSession hosts
           serverSocket = new ServerSocket(portNum);
       } catch (IOException e) {
           e.printStackTrace(System.err);
           System.exit(1);
       }
       
       bufferManager = new BufferManager();
       workerPool = new ServerWorkerPool(this,NUM_OF_WORKERS); 
       sessionPool = new SessionPool(this,NUM_OF_SESSIONS);
       new Merger(this).start(); //start the merger
       
   }
   
   /**
    * Add a new word to the database of words  
    * @param data
    */
   public void addResultData(String [] data){
	   synchronized (wordsDB) {
		   wordsDB.add(data);
	   }
   }
   
   public BufferManager getBufferManager(){
	   return this.bufferManager;
   }
   
   /**
    * 
    * @return
    */
   public Socket takeNewClient(){
	   Socket socket = null;
	   try {
		socket = newClientsQueue.take();
	   } catch (InterruptedException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   return socket;
   }
   
   public Socket takeNewGameHostClient(){
	   Socket socket = null;
	   try {
		socket = gameHostQueue.take();
	   } catch (InterruptedException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   return socket;
   }
   
   public void putNewClient(Socket socket){
	   try {
		newClientsQueue.put(socket);
	   } catch (InterruptedException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
   
   public void putNewGameHostClient(Socket socket){
	   try {
		   gameHostQueue.put(socket);
	   } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
   
   public void addToMap(Integer gameID,GameSession session){
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
			   
			  //puts the client socket in the newClient message queue 
			  putNewClient(clientSocket);
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


	public void removeSession(Integer sessionID) {
		sessionMap.remove(sessionID);
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
