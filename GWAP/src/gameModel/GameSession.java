package gameModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

import com.sun.tools.hat.internal.server.FinalizerObjectsQuery;

import gameServer.Server;

public class GameSession extends Thread {
	
	private ServerSocket sessionSocket;  //game session socket
	private Socket gameHostSocket;   //game host's socket
	private Server mainServer;
	private Vector<Socket> connectedClientSockets;
	private int gameID;
	PrintWriter outToClient;
	
	public GameSession(Socket hostClientSocket,Server server){
		gameHostSocket = hostClientSocket;
		mainServer = server;
		connectedClientSockets = new Vector<Socket>();
		
		
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
	 * adds a client to the gameSession
	 */
	public void joinGame(Socket clientSocket){
		//add socket to collection of connected clientSockets
		connectedClientSockets.add(clientSocket);
			String msg = "You are now connected to gameSessionID:"+gameID;
			sendMsgToSocket(msg, clientSocket);		
		System.out.println("added new client to sessionID:" + gameID);
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
			System.out.println("sent message");
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
	
	
	
	public void finalize(){
		endSession();
	}
	
	@Override
	public void run() {
		joinGame(gameHostSocket); //add host client to game
		sendMsgToSocket("game session port:"+sessionSocket.getLocalPort(), gameHostSocket);
		while(connectedClientSockets.size()>0){ 
			//there is at least one client in the game session
			try {
				Socket newGuest = sessionSocket.accept(); //accept more guests
				joinGame(newGuest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
