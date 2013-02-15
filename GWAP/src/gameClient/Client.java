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
	    			processInput(str);
	    		} catch (IOException e) {
	    			System.err.println("I/O exception. Cause: " + e.getCause());
	    			continue;
	    		}
	      }
   }
   
   /**
    * Processed user input from the console 
    * @param str
    */
   public void processInput(String str){
	   if (str == null) return;
	   
	   if(str.equals("#help")){
		   help();
	   }
	   else if(str.startsWith("@")){
		   //other utility commands
		   
		   if(str.contains("@join")){
			   out.println(str);
			   try {
					//wait for reply from server 
					System.out.println(in.readLine());
			   } catch (IOException e) {
					e.printStackTrace();
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
					//wait for reply from server 
					System.out.println(in.readLine());
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
	   /*
	    * You can connect directly to a port (a host_game port or an existing game session port)
	    * OR
	    * 
	    */
	   
	  Client c;
      if(args.length > 0){
    	  c = new Client(Integer.parseInt(args[0]));
    	  c.start();
          c.close();
    	  return;
      }
      else{
    	  help();
      }
      
   }
}
