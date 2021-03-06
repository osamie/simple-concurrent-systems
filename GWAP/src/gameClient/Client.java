/**
 * Client.java
 * 
 * Each client is composed of 2 threads, namely the 'InputConsoleListener' 
 * and its main thread. The client uses the 'InputConsoleListener' thread 
 * in receiving and validating user inputs (from the console). It is also 
 * responsible for sending these validated inputs to the server.
 * 
 *  On the other hand, its main thread is responsible for receiving and 
 *  processing signals from the server.
 *  
 *  The Client may react differently to server signals or user inputs 
 *  depending on its current state: 
 *  	NORMAL, WAITING, or GAME_STARTED  
 *   
 * @author Osazuwa Omigie
 */
package gameClient;

import java.io.*;
import java.net.*;

public class Client {

   Socket streamSocket;
   private BufferedReader in;
   InputConsoleListener consoleListener;
   
   //client's modes
   public static final int NORMAL = 0;
   public static final int WAITING = 1;
   public static final int GAME_STARTED = 2;
   
   private int mode;

   /**
    * Clients hosting a game session
    * @param port
    */
   public Client(int port)
   {
	   System.out.print("\n CONNECTING TO SERVER...");
	   init(port);  
	   listenToServer();
   }
   
   private void init(int port){
	   try {
	         // Bind a socket to any available port on the local host machine. 
	    	 streamSocket = new Socket("127.0.0.1",port);
	    
	    	 in = new BufferedReader( new InputStreamReader( streamSocket.getInputStream()));
	    	 mode = NORMAL;
	    	 
	      } catch (UnknownHostException e1) {
	          System.err.println("Don't know about host");
	          System.exit(1);
	     } catch (IOException e2) {
	          System.err.println("ERROR: Couldn't get port "+port+". \nCheck that game server is running.");
	          System.exit(1);
	     }
   }
   
   public int getMode(){
	   return mode;
   }
   
   public void close()
   {
	   mode = NORMAL;
	   try {
		   in.close();
		   streamSocket.close(); 
		   
	   } catch (IOException e) { 
		   System.err.println("Couldn't get I/O for the connection");
           System.exit(1);
	   }	   
   }
   
   /**
    * 
    * This method is called only after the client has successfully connected to
    * the server. 
    */
   public void start(){   
	   System.out.print("DONE\n"); //done connecting to server
	   System.out.println("\t********************************\n" +
			   "\t\tGWAP Client\n"+
	       	"\t********************************");
	   help();
	   consoleListener = new InputConsoleListener(this);
	   consoleListener.start();
   }
   
   /**
    * keeps listening for signal or message from server
    */
   private void listenToServer() {
	   while(streamSocket.isConnected() && !streamSocket.isClosed()){
			try {
				//error check acknowledgment messages
				   String serverMessage = in.readLine();//message from server
				   while(processServerMsg(serverMessage)){
					   serverMessage = in.readLine(); //get message from server
				   }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
   }
    
   /**
    * Process message from server
    * @param serverMessage
    * @return  True if game starts. False otherwise.
    */
   private boolean processServerMsg(String serverMessage) {
	   if (serverMessage == null) return false;
	   
	   if(serverMessage.contains("@CONNECTED#")){
		   start();
		   return false;
	   }
	   else if(serverMessage.contains("@joinAck")){
		   mode = WAITING; 
		   String [] params = serverMessage.split(" ");
		   
		   if(params.length < 2){
			   //could not join game. Disonnect.
			   System.out.println("Could not join game");
			   close();
			   System.exit(-1);
		   }
		   String message = String.format("(Session id: %d)Waiting for other players...",Integer.parseInt(params[1]));
		   System.out.println(message);
		   return true;
	   }
	   else if(serverMessage.equals("@startGame")){
		   mode = GAME_STARTED;
		   return true;
	   }
	   else if(serverMessage.equals("@quitGame")){
		   mode=NORMAL;
		   close();
		   System.out.println("\n\t****************\n");
		   System.out.println("\t GAME OVER");
		   System.out.println("\n\t****************\n");
		   //TODO kill the input console listener OR take the client back to menu stage
		   return false;
	   }
	   else{
//		   if(mode==GAME_STARTED){
			   System.out.println("\n**"+serverMessage.toUpperCase()+"**");
//		   }
		   
		   return true;
	   }
   }
   
   /*
    * Handle for automating user input
    */
   public InputConsoleListener getConsole(){
	   return consoleListener;
   }

   /**
    * display client usage on the console
    */
   public static void help(){
	   System.out.println("USAGE:");
	   System.out.println("@host - creates a new gameSession");
	   System.out.println("@list - show list of existing gameSessions on the specified server");
	   System.out.println("@join <session-ID> - join an existing game session. The session is identified with <session-ID>\n");
   }
   
   public static void main(String args[])
   { 
      if(args.length > 0){
    	  Client c = new Client(Integer.parseInt(args[0]));
          c.close();
    	  return;
      }
      else{
    	  System.out.println("Please pass in the server port number as an argument");
      }  
   }
   
   @Override
	protected void finalize() throws Throwable {
	   System.out.println("test!");
		in.close();
		streamSocket.close();
	}
}

