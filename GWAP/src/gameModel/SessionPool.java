/**
 * SessionPool.java
 * 
 * This is a worker queue or a pool of GameSession threads. 
 * When a client requests to host a new game session ('@host' request), the server worker thread 
 * adds this client's socket (i.e the host's socket) to the gameHostQueue(a message queue) .
 * There is also a  
 */
package gameModel;

import gameServer.Server;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;


public class SessionPool { 
	
	//pre threaded game sessions;
	private final GameSession[] sessions;
	
	public SessionPool(Server mainServer, int nSessions) {
		
		
		sessions = new GameSession[nSessions];
		
		for(int i=0;i<nSessions;i++){
			sessions[i]=new GameSession(mainServer);
			sessions[i].start();
		}
	}
}
