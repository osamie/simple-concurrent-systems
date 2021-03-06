package gameModel;

import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;

/**
 * Each index position in the buffer contains a string array. 
 * These would be the resulting words gathered from players 
 * after a specific game session. These buffers however, will 
 * continue to be reused by the filled by the sessions and
 * emptied by the merger.  
 *
 */
public class BufferManager {
	
	ArrayBlockingQueue<String []> fullBuffers;
	ArrayBlockingQueue<String []> emptyBuffers;
	
	public BufferManager() {
		fullBuffers = new ArrayBlockingQueue<String[]>(20,true);
		emptyBuffers = new ArrayBlockingQueue<String[]>(20,true);
	}
	
	
	/**
	 * Gets an empty buffer from the empty buffer queue. 
	 * This operation will be used the game sessions.
	 * @return a reference to the empty buffer
	 */
	public String[] getEmptyBuffer(){
		String [] emptyBuffer = null;
		try {
			emptyBuffer = emptyBuffers.take(); //synchronized take
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return emptyBuffer;
	}
	
	/**
	 * This operation will be used by the merger in getting 
	 * buffers that have been filled by the a finished game session. 
	 * @return a reference to the full buffer
	 */
	public String[] getFullBuffer(){
		String [] fullBuffer = null;
		try {
			fullBuffer = fullBuffers.take(); //blocking take
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return fullBuffer;
	}
	
	
	/**
	 * This operation would be used by the game sessions (gameSession threads)
	 * in putting back the filled buffer in the fullBuffers queue.
	 * @param buffer
	 * @return
	 */
	public boolean putFullBuffer(String [] buffer){
		try {
			fullBuffers.put(buffer); //synchronized put
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * Puts an empty buffer into the empty buffer queue. 
	 * This operation will be used the merger in putting 
	 * back buffers that have already been emptied and stored in the 
	 * word database.
	 * @return a reference to the empty buffer
	 */
	public boolean putEmptyBuffer(String [] emptiedBuffer){
		try {
			emptyBuffers.put(emptiedBuffer); //block put
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

}