package gameClient;


import java.io.*;
import java.net.*;

public class Client {

   Socket streamSocket;
   private boolean gameStarted;
   private BufferedReader in;
   private PrintWriter out;
   
   //client modes
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
	   init(port);   
	   gameStarted = false;
   }
   
   private void init(int port){
	   try {
	         // Bind a socket to any available port on the local host machine. 
	    	 streamSocket = new Socket("127.0.0.1",port);
	    	 
	    	 out = new PrintWriter(streamSocket.getOutputStream(), true);
	    	 in = new BufferedReader( new InputStreamReader( streamSocket.getInputStream()));
	    	 mode = NORMAL;
	    	 
	      } catch (UnknownHostException e1) {
	          System.err.println("Don't know about host");
	          System.exit(1);
	     } catch (IOException e2) {
	          System.err.println("Couldn't get port "+port);
	          System.exit(1);
	     }
   }
   
   public int getMode(){
	   return mode;
   }
   
   public void close()
   {
	   gameStarted = false; 
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
	   System.out.println("****************\n " +
	   		"To create a new session enter '@host' \n" +
	       	"To join any existing sessions enter '@join <sessionID>' \n" +
	       	"****************");
	   InputConsoleListener consoleListener = new InputConsoleListener(this);
	   consoleListener.start();
	   listenToServer();
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
   
   public boolean hasGameStarted(){
	   return gameStarted;
   }
    
   
   /**
    * Process message from server
    * @param serverMessage
    * @return  True if game starts. False otherwise.
    */
   private boolean processServerMsg(String serverMessage) {
	   if (serverMessage == null) return false;
	   
	   if(serverMessage.contains("@joinAck")){
		   mode = WAITING; 
		   String [] params = serverMessage.split(" ");
		   
		   if(params.length < 2){
			   //could not join game. Disonnect
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
		   return false;
	   }
	   else if(serverMessage.equals("@quitGame")){
		   mode=NORMAL;
		   System.out.println("Game quitted by server");
		   System.exit(-1);
	   }
	   else{
		   System.out.println(serverMessage);
	   }
	   return false;
   }

   /**
    * display client usage on the console
    */
   public static void help(){
	   System.out.println("Invalid input \n");
	   System.out.println("USAGE:");
	   System.out.println("@host - creates a new gameSession");
	   System.out.println("@list - show list of existing gameSessions on the specified server");
	   System.out.println("@join <> - join a game session. This should be followed by the game's sessionID");
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
    	  System.out.println("Please pass in the server port number as an argument");
      }  
   }
   
   @Override
	protected void finalize() throws Throwable {
		in.close();
		out.close();
		streamSocket.close();
	}
}

