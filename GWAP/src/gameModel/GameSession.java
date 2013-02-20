/**
 * GameSession.java
 * 
 * The creation of a game session is triggered by a client. 
 * The session coordinates the game play. It is responsible for informing all connected 
 * clients of the current state of the game.     
 * 
 * Once a session is created, other clients can join the session. 
 * And once the minimum number of players has been reached, the session broadcasts a signal 
 * to all the connected clients and then begins the game.  
 * It keeps a record of the clients connected to the session.   
 * 
 * @author Osazuwa Omigie
 */

package gameModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import gameServer.Server;

public class GameSession extends Thread {
	
	private ServerSocket sessionSocket;  //game session socket
	private Server gameServer;
	private Vector<Socket> connectedClientSockets;
	private int gameID;
	private static final int MIN_PLAYERS = 1; //minimum number of players per game
	private int timeOut; //question timeout in minute
	private ArrayList<ClientListener> clientListeners;
	PrintWriter outToClient;
	Vector<String> currentQuestion;
	HashMap<String,Vector<String>> resultsMap;
	
	
	public GameSession(Socket hostClientSocket, Server server){
		gameServer = server;
		connectedClientSockets = new Vector<Socket>(MIN_PLAYERS);
		clientListeners = new ArrayList<ClientListener>(MIN_PLAYERS); 
		timeOut = 5000; //in milliseconds
		try{
			sessionSocket = new ServerSocket(0);//create a session socket with any available port
			gameID = sessionSocket.getLocalPort(); //gameID is the port local port number of the session
		}catch(IOException e){
			System.out.println("problem creating session socket!");
		}
	}
	
	public Integer getGameID(){
		return gameID;
	}
	
	/**
	 * Sends a given string to all connect clients
	 * @param message
	 */
	public void broadCastMessage(String message){
		for (Socket s : connectedClientSockets){
			sendMsgToSocket(message, s);
		}
	}
	
	/**
	 * @return The total number of clients on this game session
	 */
	public int getPlayerCount(){
		return connectedClientSockets.size();
	}
	
	/**
	 * Contains the main game logic.
	 * Once this method is called, no more clients can be added to the session 
	 */
	public void startGame(){
		System.out.println("Game started!");
		
		//signal all connected clients that game has started
		broadCastMessage("@startGame"); 
		
		//initialize the results map
		resultsMap = new HashMap<String, Vector<String>>(connectedClientSockets.size());
		
		/*
		 * the number of word challenges/rounds
		 * TODO: this could be defined by the game host 
		 */
		int count = 3;
		while(count > 0){
			String word = gameServer.getAword();
			//get a random word (results does not contain)
			while (resultsMap.get(word) != null){
				//TODO this could loop forever 
				word = gameServer.getAword();
			}
			
			Vector<String> results = new Vector<String>(connectedClientSockets.size());
			broadCastMessage(word);
			
			try {
				Thread.sleep(timeOut);  //timer or wait for clients to reply their entries
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//After timer
			//iterate through listeners collection and store answers in resultsMap
			for(ClientListener listener: clientListeners){
				results.add(listener.getAnswer()); //populate the collection of answers from all clients
			}
			
			//store result in resultsMap
			resultsMap.put(word, results);
			count--;
		}
	}
	
	/**
	 * Prints the results of the game
	 */
	private void printResult() {
		System.out.println(resultsMap.toString());
	}


	/**
	 * adds a client to the gameSession
	 */
	public synchronized void joinGame(Socket clientSocket){
		//spawn a new thread that would listen to this client's requests
		ClientListener listener = new ClientListener(clientSocket);
		
		//Will be used by session to get answers from client
		clientListeners.add(listener); 
		
		listener.start(); //listen for game inputs  
		
		//signal the client that they have joined the gameSession
		String message = String.format("@joinAck %d", this.gameID);
		sendMsgToSocket(message, clientSocket);
		
		//add socket to collection of connected clientSockets
		connectedClientSockets.add(clientSocket);
	}
	
	/**
	 * send a message to a given client
	 * @param message
	 * @param client
	 */
	public void sendMsgToSocket(String message,Socket client){
		Socket clientSocket = client;
		try {
			outToClient = new PrintWriter(clientSocket.getOutputStream(),true);
			outToClient.println(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Signal connected clients that the session has ended.
	 * Remove this game session from list of active sessions on the main server
	 */
	public void endSession(){
		try{
			//remove this session from the server's list of sessions
			gameServer.removeSession(this);  				
			broadCastMessage("@quitGame"); //remove all clients from the session
			sessionSocket.close();
		} catch (IOException e) {
//			System.err.println(String.format("Could not close gameSession on port %i",sessionSocket.getLocalPort()));
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	@Override
	public void finalize(){
		endSession();
	}
	
	@Override
	public void run() {
		//waiting for the required number of players 
		while(connectedClientSockets.size() <= MIN_PLAYERS){ 
			Thread.yield(); //give way to other threads in the meanwhile 
		}
		startGame(); //players are ready, now the start game
		System.out.println("GAME OVER");
		printResult();//print game results
		endSession();
	}
}

/**
 * Handle user input when responding to questions
 *
 */
class ClientListener extends Thread{
	Socket clientSocket;
	String answer;
	BufferedReader inFromClient;
	
	public ClientListener(Socket socket) {
		clientSocket = socket;
		answer="";
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns a null string if no answer have been submitted by the client
	 * or returns the most recent entry by the the client 
	 * @return
	 */
	
	public String getAnswer(){
		String result = new String(answer);
//		System.out.println("answer is:" + result);
		answer = ""; //reset answer after every read
//		System.out.println("answer is:" + result);
		return result;
	}
	
	
	/**
	 * Listens for answers from clients 
	 * new answers will overwrite the answer variable. Only 1 answer per question
	 * Returns false if client has been disconnected/closed 
	 */
	public boolean listenForWords(){
		
		try {
			//wait for answer from client
			answer = inFromClient.readLine();
			if(answer == null){
				return false;
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false; 		
	}
	
	
	@Override
	public void run() {
		while(!clientSocket.isClosed()){
			listenForWords();
		}
	}
}
