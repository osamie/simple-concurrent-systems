package gameServer;

import gameModel.GameSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;

/**
 * 
 *Handles 'host' or 'join' request from client
 */
public class ServerWorker extends Thread{
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	Server mainServer;
	
	
	public ServerWorker(Socket socket,Server parentServer) {
		clientSocket = socket;
		mainServer = parentServer;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
		}catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
		catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
		
		
	}
	
	@Override
	public void run() {
		try {
			   
			   boolean optionsStage = true; 
			   while ((optionsStage)&&(clientSocket.isConnected())){ 
				   
				   
				   //TODO add more features like view existing sessions
				   out.println("connected as "+clientSocket.getPort());
			       out.println("What would you like to do? \n " +
			       		"To create a new session enter 'host' \n" +
			       		"To join any existing sessions enter 'join' \n");
			       
			       String message = in.readLine();
			       optionsStage = processClientInput(message);
			   }

			   
		   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
		   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
	}

	/**
	 * 
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
		if(messageParam[0].equals("@join")){
			//if the specific session was requested
			if(messageParam.length > 1){
				//TODO search for a game session with id messageParam[1]
				//add client to the gameSession
			}
			
			//join client with a random game session
			//TODO determine a game session or let the user decide via the message 
			return true;
		}
		else if(messageParam[0].contains("@host")){
			
			
			//create a new gameSession with the generated gameID
	       GameSession session = new GameSession(clientSocket,mainServer);
	       
	       //add <gameID,session> pair to the server's gameSessionMap
	       mainServer.addToMap(session.getGameID(), session);
	       
	       session.start();
	       return false;
		}
		else{
			return true;
		}
		
	}
}
