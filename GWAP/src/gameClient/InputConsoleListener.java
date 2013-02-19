/**
 * 
 */
package gameClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author osamie
 *
 */
public class InputConsoleListener extends Thread {
	Client client; //this is the parent client
	PrintWriter out;
	private InputStreamReader converter;
	private BufferedReader clientConsole;
	private ArrayList<String> legalCommands;
	
	public InputConsoleListener(Client parent) {
		client = parent;
		int commandsCount = 4;
		legalCommands = new ArrayList<String>(commandsCount);
		legalCommands.add("@list");
		legalCommands.add("@join");
		legalCommands.add("@host");
		
		try {
			out = new PrintWriter(client.streamSocket.getOutputStream(), true);
			converter = new InputStreamReader(System.in);
	        clientConsole = new BufferedReader(converter);
	        
		} catch (IOException e) {
			System.err.println("Problem creating output stream");
	        System.exit(1);
		}
	}
	
	@Override
	public void run() {
		Socket socket = client.streamSocket;
		while(socket.isConnected() && !socket.isClosed()){
			String str;
			try {
				str = clientConsole.readLine(); //read user input from client console
				//process input here
				validateConsoleInput(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	* validates user's console input before sending to server 
	* @param str
	*/
	public void validateConsoleInput(String str){
	   if (str == null) return;
	   
	   if(str.equals("#exit") || (str.equals("#quit"))){
		   client.close();
		   System.exit(1);
	   }
	   
	   if(str.equals("#help")){
		   Client.help();
	   }
	   else if(str.startsWith("@")){
		   //other utility commands: @join,@list,@host 
		   String[] command = str.split(" ");
		   
		   if(!legalCommands.contains(command[0])){
			   //invalid utility command
			   System.out.println("invalid '@' command");
			   Client.help();
			   return;
		   }
		   
		   if(command[0].equals("@join")){
			   if(command.length < 2){
				   System.out.println("Please specify game session ID. \nUSAGE:@join <sessionID> \ntype '@list' to list current sessions");
				   return;
			   }
		   }
		   out.println(str); //send request to server
	   }
	   else{
		   if(client.getMode() == Client.GAME_STARTED){
			   //if game has started then allow sending of 
			   //messages without the '@' or '#' prefixes 
			   out.println(str);
		   }
	   }	
   }

}
