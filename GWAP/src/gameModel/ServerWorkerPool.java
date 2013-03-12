package gameModel;

import gameServer.Server;
import gameServer.ServerWorker;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerWorkerPool {
//	private final int nWorkers; 
	
	private final ServerWorker[] serverWorkers;
	private SessionPool sessionPool;
	
	public ServerWorkerPool(Server mainServer,int nWorkers) {
//		this.nWorkers = nWorkers;
		//initialize the worker threads here
		serverWorkers = new ServerWorker[nWorkers];
		
		
		/*
		 *nSessions is the max number of host requests that can handled at the same time  
		 */
//		int nSessions = Server.NUM_OF_SESSIONS;
//		this.sessionPool = new SessionPool(mainServer, nSessions);
		
		for(int i=0;i<nWorkers;i++){
			serverWorkers[i]=new ServerWorker(mainServer);
			serverWorkers[i].start();
		}
	}
}
