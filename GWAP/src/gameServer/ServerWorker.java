/**
 * ServerWorker.java
 * 
 * This is a worker thread from the Server. It is spawned for each and every 
 * newly connected client to the server.It is responsible for handling the
 * client's request prior to a client hosting or joining a game     
 * However, this thread will be killed once a client has decided to join or
 * host a game. 
 *
 * @author Osazuwa Omigie
 */

package gameServer;

import gameModel.GameSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerWorker extends Thread{
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	Server mainServer;
	
	public ServerWorker(Socket socket,Server parentServer) {
		clientSocket = socket;
		mainServer = parentServer;
		if(mainServer == null){System.out.println("mainServer is null!!");}
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));			
		}catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
		catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
		this.setName("ServerWorker");
	}
	
	@Override
	public void run() {
		try {
			   boolean optionsStage = true; 
			   while ((optionsStage)&&(clientSocket.isConnected())){
			       String message = in.readLine();
			       optionsStage = processClientInput(message);
			   }
  
		   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
		   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
//		System.out.println("Server worker dead!");
	}

	/**
	 * Processes the message from the client
	 * @param message
	 * @return true if the client is still at the menu stage
	 */
	private boolean processClientInput(String message) {
		
		if(message==null) return false;
		
		String [] messageParam = message.split(" ");
		
		if (messageParam.length < 1) {
			//TODO launch 'USAGE' OR HELP MENU
			return true;
		}
		
		if(messageParam[0].contains("@join")){		
			//The specific session was requested
			if(messageParam.length > 1){
				//TODO search for a game session with id messageParam[1]
				
				int gameId =  Integer.parseInt(messageParam[1]);
				//TODO ensure 2 parameter (the joinId is valid)
				GameSession session = mainServer.getGameSession(gameId);
				
				if(session == null) {
					out.println("game session not found ID:" + gameId);
					return true;
				}
				session.joinGame(clientSocket); 
				//add client to the gameSession
				return false;
			}
			else{
				out.println("Please specify game session ID");
				return true;
			}
					
			//join client with a random game session
			//TODO determine a game session or let the user decide via the message 
		}
		else if(messageParam[0].contains("@host")){
			
			//create a new gameSession with the generated gameID
	       GameSession session = new GameSession(clientSocket,mainServer);
	       
	       //add <gameID,session> pair to the server's gameSessionMap
	       mainServer.addToMap(session.getGameID(), session);
	       
	       session.start();//start the game thread
//	       session.joinGame(clientSocket);//add itself to the session
	       
	       return false;
		}
		
		else if(messageParam[0].contains("@list")){
			out.println("Game sessions:"+mainServer.listGameSessions());
			return true;
		}
		else{
			System.out.println(messageParam[0]);
			out.println("server could not interpret message");
			return true;
		}
		
	}
}

/**
 * Manages the session before the game starts
 * 
 *
 */
class SessionHandler extends Thread{
	@Override
	public void run() {
		
	}
}
