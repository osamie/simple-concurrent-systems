package gameServer;

import gameModel.GameSession;

import java.io.*;
import java.net.*;

public class Server {

   private ServerSocket serverSocket;
   private final int LISTENING_PORT = 9999;
   
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;
   

   public Server()
   {
	   serverSocket = null;
       try {
           serverSocket = new ServerSocket(LISTENING_PORT);
       } catch (IOException e) {
           e.printStackTrace(System.err);
           System.exit(1);
       }
   }

   public void startGame()
   {
	   byte[] receiveData = new byte[1024];
	   byte[] sendData;
	   
	   try {
	       clientSocket = serverSocket.accept();
	       GameSession gameSession = new GameSession(clientSocket);
	       gameSession.start();
	       gameSession.endSession();
	       
	       try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//	       gameSession.endSession();
	       
//	       out = new PrintWriter(clientSocket.getOutputStream(), true);
//	       in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//	       //while (!clientSocket.isOutputShutdown())  // isClosed(), isConnected(),isInputShutdown do not work
//		   while ( (msg = in.readLine()) != null) 
//		   {
//			   System.out.println ("Server Rxd: " + msg );
//			   out.println("Echo " + msg);
//		   }
//
//		   in.close();
//		   out.close();
//		   clientSocket.close();
		   serverSocket.close();
      
	   } catch (SocketException e2) { System.out.println("Done"); System.exit(0); }
	   catch (IOException e) { e.printStackTrace(System.err); System.exit(1);  }
	     
   }
   public void finalize()
   {
	   try { serverSocket.close(); } catch (IOException e) {}
   }

   public static void main( String args[] )
   {
      Server c = new Server();
      c.startGame();
   }
}