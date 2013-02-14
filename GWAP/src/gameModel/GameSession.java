package gameModel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import gameServer.Server;
import gameServer.ServerWorker;

public class GameSession extends Thread {
	
	private ServerSocket sessionSocket;  //game session socket
	private Socket gameHostSocket;   //game host's socket
	private Server gameServer;
	private Vector<Socket> connectedClientSockets;
	private int gameID,currentQIndex;
	private static final int MIN_PLAYERS = 2; //maximum number of players per game
	private ArrayList<ClientListener> clientListeners;
	
	private PrintWriter outToClient;
	
	
	Vector<String> currentQuestion;
	HashMap<String,Vector<String>> resultsMap;
	
	public GameSession(Socket hostClientSocket, Server server){
		gameHostSocket = hostClientSocket;
		gameServer = server;
		connectedClientSockets = new Vector<Socket>(MIN_PLAYERS);
		clientListeners = new ArrayList<ClientListener>(MIN_PLAYERS); 
		currentQIndex = 0;
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
	
	
	public void startGame(){
		//initialize the results map
		resultsMap = new HashMap<String, Vector<String>>(connectedClientSockets.size());
		
		int count = 4;
		while(count > 0){
			String word = gameServer.getAword();
			//get a random word (results does not contain)
			while (resultsMap.get(word) != null){
				//TODO this could loop forever 
				word = gameServer.getAword();
			}
			
			Vector<String> results = new Vector<String>();
	//		results.put(word, value)
			//broadcast random word to clients
			for (Socket s : connectedClientSockets){
				sendMsgToSocket(word, s);
			}
			
			try {
				Thread.sleep(4000);  //timer or wait for clients to reply their entries
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
		}
		
	}
	
	/**
	 * adds a client to the gameSession
	 */
	public void joinGame(Socket clientSocket){
		//add socket to collection of connected clientSockets
		connectedClientSockets.add(clientSocket);
		
		//spawn a new thread that would listen to this client's requests
		clientListeners.add(new ClientListener(clientSocket));
		
		String msg = "You are now connected to gameSessionID:"+gameID;
		sendMsgToSocket(msg, clientSocket);		
//		System.out.println("added new client to sessionID:" + gameID);
		System.out.println("random word:"+gameServer.getAword());
	}
	
	/**
	 * send a message to a given client
	 * @param message
	 * @param client
	 */
	public void sendMsgToSocket(String message,Socket client){
		Socket clientSocket = client;
		try {
			PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(),true);
			outToClient.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	
	
	public void endSession(){
		try {
			gameHostSocket.close();
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
		joinGame(gameHostSocket); //add host client to game
		sendMsgToSocket("game session port: "+sessionSocket.getLocalPort(), gameHostSocket);
		
		while(connectedClientSockets.size() <= MIN_PLAYERS){ 
			Thread.yield(); //keep waiting for other players to join
		}
		
		//players are ready, now start game
		startGame();
		
		System.out.println("GAME OVER");//print result
		
	}
}

/**
 * Handle user input when responding to questions
 *
 */
class ClientListener extends Thread{
	Socket clientSocket;
//	GameSession gameSession;
	private String answer="";
	BufferedReader inFromClient;
	
	public ClientListener(Socket socket) {
		clientSocket = socket;
//		gameSession = session;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	//returns a null string if no answer have been submitted by the client 
	//or returns the most recent entry by the the client ;
	public String getAnswer(){
		return answer;
	}
	
	
	/**
	 * expects only answers to questions for now
	 */
	public void listenForWords(){
		
		try {
			answer = inFromClient.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		
	}
	
	
	
	@Override
	public void run() {
		while(true){
			listenForWords();
		}
	}
}
