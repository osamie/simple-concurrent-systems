package gameClient;
// EchoClient.java
//    TCP Version 
// @version CS January 2009

import gameServer.Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {

   private Socket streamSocket;
   private BufferedReader in;
   private PrintWriter out;
   private InputStreamReader converter;
   private BufferedReader clientConsole;
   private ArrayList<String> adminMessages; 

   /**
    * Clients hosting a game session
    * @param port
    */
   public Client(int port)
   {
	   int numberOfCommands = 3;
	   adminMessages = new ArrayList<String>(numberOfCommands);
       adminMessages.add("@list");
       adminMessages.add("@join");
       adminMessages.add("@host");
	   init(port);
   }
   
   private void init(int port){
	   try {
	         // Bind a socket to any available port on the local host machine. 
	    	 streamSocket = new Socket("127.0.0.1",port);
	    	 
	    	 out = new PrintWriter(streamSocket.getOutputStream(), true);
	    	 in = new BufferedReader( new InputStreamReader( streamSocket.getInputStream()));
	    	 converter = new InputStreamReader(System.in);
	         clientConsole = new BufferedReader(converter);   
	    	 
	      } catch (UnknownHostException e1) {
	          System.err.println("Don't know about host");
	          System.exit(1);
	     } catch (IOException e2) {
	          System.err.println("Couldn't get port "+port);
	          System.exit(1);
	     }
   }
   
   public void close()
   {
	   try {
		   out.close();
		   in.close();
		   streamSocket.close();   
	   } catch (IOException e) { 
		   System.err.println("Couldn't get I/O for the connection");
           System.exit(1);
	   }	   
   }
   
   public void start(){
	   System.out.println("Connected to server");
	   
	   /**
	    * perform setup here
	    */
	   System.out.println("What would you like to do? \n " +
	   		"To create a new session enter 'host' \n" +
	       	"To join any existing sessions enter 'join' \n");
	   
	   
	   while(streamSocket.isBound()){
		   
	          try {
	        	    System.out.print("Type: ");
	    			String str = clientConsole.readLine();
	    			//process input here
	    			if(str.equals("#exit") || (str.equals("#quit"))){
	    				   close();
	    				   return;
	    			 }
	    			validateConsoleInput(str);
	    		} catch (IOException e) {
	    			System.err.println("I/O exception. Cause: " + e.getCause());
	    			continue;
	    		}
	      }
   }
   
//   private boolean process
   
   
   /**
    * validates user's console input before sending to server 
    * @param str
    */
   public void validateConsoleInput(String str){
	   if (str == null) return;
	   
	   if(str.equals("#help")){
		   help();
	   }
	   else if(str.startsWith("@")){
		   //other utility commands
		   
		   if(str.contains("@join")){
			   String[] command = str.split(" ");
			   
			   if(command.length < 2){
				   System.out.println("Please specify game session ID. \nUSAGE:@join <sessionID> \ntype '@list' to list current sessions");
				   return;
			   }
			   else{
				   out.println(str);
				   try {
					   //error check acknowledgment messages
					   
						//join acknowledgment from server 
						System.out.println(in.readLine());
						
						//game start message from server
						System.out.println(in.readLine());
						
						gameStarted();		
				   } catch (IOException e) {
						e.printStackTrace();
				   }
			   }
		   }
		   else if(str.contains("@list")){
			   out.println(str);
			   try {
					//wait for reply from server 
					System.out.println(in.readLine());
			   } catch (IOException e) {
					e.printStackTrace();
			   }
				
		   }
		   else if(str.contains("@host")){
			   out.println(str);
			   try {
				   	//error check acknowledgment messages
				   
					//join acknowledgment from server 
					System.out.println(in.readLine());
					
					//game start message from server
					System.out.println(in.readLine());
					
					gameStarted();		
			   } catch (IOException e) {
					e.printStackTrace();
			   }
				
		   }
		   else{
			   //invalid utility command
			   System.out.println("invalid '@' command");
			   help();
			   return;
		   }
	   }
	   else{
		   out.println(str);
	   }
		  
	
		
		
   }
   
   /**
    * called after getting gameStarted ack from session 
    */
   private void gameStarted() {
	   //keep reading inputs from the 
	   
	   while(!streamSocket.isClosed()){
		  
		   try {
			   //keep processing message from server until process() returns false
			   //
			   
			   //get word from server
			   System.out.println("\n***New Challenge:***" + in.readLine());
			   
			   
			   //read user entry from console and send as response
			   out.println(clientConsole.readLine());
			   
//			   System.out.println("");
			   
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   
		   //wait for clients reply

	   }
	
   }

/**
    * display client usage on the console
    */
   public static void help(){
	   System.out.println("Invalid option number \nUSAGE is java gameClient.Client <server-port#> <option-number> e.g java gameClient.Client 5000 1");
	   System.out.println("OPTION#s:");
	   System.out.println("0 - Opens this menu");
	   System.out.println("1 - create a gameSession");
	   System.out.println("2 - show list of existing gameSessions on the specified server");
	   System.out.println("3 - join a game session. This should be followed by the game's sessionID");
   }
   public static void main(String args[])
   { 
	 
      if(args.length > 0){
    	  Client c = new Client(Integer.parseInt(args[0]));
    	  c.start();
          c.close();
    	  return;
      }
      else{
    	  help();
      }
      
   }
}
