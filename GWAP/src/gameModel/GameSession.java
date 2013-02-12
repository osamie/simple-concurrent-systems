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
	private long gameID;
	
	public GameSession(Socket hostClientSocket,Server server,long id){
		mainServer = server;
		gameID = id;
		connectedClientSockets = new Vector<Socket>();
		
		try{
			sessionSocket = new ServerSocket(0);
			joinGame(hostClientSocket); //join game
			System.out.println("started new session");
		}catch(IOException e){
			System.out.println("problem creating session socket!");
		}
		
		
	}
	
	/**
	 * adds a client to the gameSession
	 */
	public void joinGame(Socket clientSocket){
		//add socket to collection of connected clientSockets
		connectedClientSockets.add(clientSocket);
		try {
			PrintWriter outToGuest = new PrintWriter(clientSocket.getOutputStream(),true);
			outToGuest.write("You are now connected to gameSessionID:"+gameID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("added new client to sessionID:" + gameID);
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
		while(connectedClientSockets.size()>0){
			try {
				Socket newGuest = sessionSocket.accept();
				joinGame(newGuest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
