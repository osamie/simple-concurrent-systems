package gameServer;

import java.io.*;
import java.net.*;

public class Server {

   private ServerSocket serverSocket;
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;

   public Server()
   {
	   serverSocket = null;
       try {
           serverSocket = new ServerSocket(1234);
 
       } catch (IOException e) {
           e.printStackTrace(System.err);
           System.exit(1);
       }
   }

   public void receiveAndEcho()
   {
	   String msg;
	   try {
	       clientSocket = serverSocket.accept();
	       out = new PrintWriter(clientSocket.getOutputStream(), true);
	       in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	       //while (!clientSocket.isOutputShutdown())  // isClosed(), isConnected(),isInputShutdown do not work
		   while ( (msg = in.readLine()) != null) 
		   {
			   System.out.println ("Server Rxd: " + msg );
			   out.println("Echo " + msg);
		   }

		   in.close();
		   out.close();
		   clientSocket.close();
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
      c.receiveAndEcho();
   }
}