package gameModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class GameSession extends Thread {
	
	private ServerSocket sessionSocket;  //game session socket
	private Socket connectionSocket;  //guest socket
	private Socket gameHostSocket;   //game host's socket
	
	public GameSession(Socket hostClientSocket){
		//TODO: serverSocket parameter will be used to get the host client's information
		System.out.println("created gamesesseion!");
		gameHostSocket = hostClientSocket;
			try {
				/*create a new session socket and bind hostClient to it*/
				sessionSocket = new ServerSocket();
				
//				hostClientSocket.bind(getSocketAddress());
				
			} catch (IOException e) {
				System.err.println(String.format("Could not connect to gameSession on port %d",sessionSocket.getLocalPort()));
				e.printStackTrace();
				System.exit(-1);
			}

	}
	
	/*
	 * Returns the address for other clients to enter/connect to the 
	 * game session.
	 * Socket address will be used to populate the list of gameSessions as well
	 */
	public SocketAddress getSocketAddress(){
		System.out.println("returning socket addy");
		return sessionSocket.getLocalSocketAddress();
		
	}
	
	public void endSession(){
		try {
			connectionSocket.close();
			gameHostSocket.close();
//			sessionSocket.close();
			
		} catch (IOException e) {
//			System.err.println(String.format("Could not close gameSession on port %i",sessionSocket.getLocalPort()));
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void run() {
		try {
			
			/*
			 * TODO: wait here for guest client to join the connection
			 */
			
//			connectionSocket = sessionSocket.accept(); //wait for a guest client
			
			//bind the new client connection to the game session's socket
//			connectionSocket.bind(getSocketAddress());	
			//broadcast message to all connected clients!
			DataOutputStream outToClient =
			         new DataOutputStream(gameHostSocket.getOutputStream());
			
			outToClient.writeChars("You are now connected to game session %i!\n");
			
		} catch (IOException e) {
//			System.err.println(String.format("Could not connect to gameSession on port %i",sessionSocket.getLocalPort()));
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
