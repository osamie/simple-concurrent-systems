package gameServer;

import gameModel.GameSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

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
		if(mainServer == null){System.out.println("mainServer's null!!");}
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
//				   out.println("connected as "+clientSocket.getPort());
//			       out.println("What would you like to do? \n To create a new session enter 'host' \n" +
//			       		"To join any existing sessions enter 'join' \n");
			       
			       String message = in.readLine();
			       optionsStage = processClientInput(message);
			   }

			   
		   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
		   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
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
				System.out.println("session appeded");
				System.out.println(session.getGameID());
				session.joinGame(clientSocket); 
				//add client to the gameSession
				out.println("@join received");
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
	       
	       session.start();
	       
	       //inform client of new game session
	       out.println("New game session:"+ session.getGameID()); 
	       return false;
		}
		
		else if(messageParam[0].contains("@list")){
			System.out.println("sessionlist count:" + mainServer.sessionMap.size());
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
