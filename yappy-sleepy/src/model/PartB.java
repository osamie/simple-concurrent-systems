/**
 * 
 */
package model;

public class PartB {
	
	/**Logging mechanism:
	 * Rather than using the method below, 
	 * 1) We could make log a string object and simply append to it.
	 * 2) Use the java.util.logging.logger   
	 * */
	private static int logCapacity = 2000;
	public static char[] log = new char[logCapacity];
	public static int logIndex = 0;
	
	public void createThreads(){
		Yappy yappyThread = new Yappy();
		Thread sleepyThread = new Thread(new Sleepy());
		
		try {
			sleepyThread.start();	
			yappyThread.start(); //initialize thread resources and run/execute thread
			
			sleepyThread.join();  //waits for sleepy thread to terminate
			yappyThread.join();  //waits for yappy thread to terminate
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PartB partB = new PartB();
		partB.createThreads(); //create the sleepy and yappy threads
	
		
		System.out.print("Log: " + new String(log));
	}
	
	
	/*  Inner classes */
	class Sleepy implements Runnable{
		
		@Override
		public void run() {
			int i = 10;
			while(i > 0){
				log[logIndex++] = 's'; //write to log file
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i--;
			}	
		}	
	}
	
	class Yappy extends Thread{
		
		@Override
		public void run() {
			int i = 100;
			while(i > 0){
				log[logIndex++] = 'y'; //write to log file 

				/*spin this thread*/
				int var;
				for(var=2;var<99999999;var++){
					var *=1;
					var /=1;
				}
				i--;
			}
		}	
	}

}
