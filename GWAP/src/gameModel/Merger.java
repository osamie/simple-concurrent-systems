package gameModel;

import gameServer.Server;

public class Merger extends Thread {
	private Server mainServer;
	
	public Merger(Server mainServer) {
		this.mainServer = mainServer;
	}
	
	
	@Override
	public void run() {
		BufferManager bufferManager = mainServer.getBufferManager();
		
		while(true){
			//Get a full buffer
			String [] fullBuffer = bufferManager.getFullBuffer();
			
			String [] newBuffer = new String [fullBuffer.length];
			for(int i=0;i<fullBuffer.length;i++){
				/*Read and destroy*/
				newBuffer[i] = fullBuffer[i];
				fullBuffer[i] = "";
			}
			
			//store the collected data in the words database
			mainServer.addResultData(newBuffer);
			
			//put back the emptied buffer
			bufferManager.putEmptyBuffer(fullBuffer);
		}
	}
}
