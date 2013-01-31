/* 
 * BASIC JAVA THREAD CONCURRENCY EXPERIMENT
 * The thread classes will now simply "log" its name, instead of the 
 * expensive system call involved in printing to the console. 
 * The log is a simple global variable shared by the two threads. 
 * The parent thread would also wait for termination of the child threads
 	Author: OSAZUWA OMIGIE
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
		Yappy yappyThread = new Yappy('y',15);
		Thread sleepyThread = new Thread(new Sleepy('s',5));
		
		try {
			//initialize thread resources and run/execute thread
			sleepyThread.start();	
			yappyThread.start(); 
			
			//Parent thread needs to wait for child threads to terminate
			yappyThread.join();  
			sleepyThread.join(); 
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PartB partB = new PartB();
		System.out.println("Running...");
		partB.createThreads(); //create and execute the sleepy and yappy threads
	
		
		System.out.println("Log: " + new String(log));
	}
	
	
	
	/*  Thread classes */
	
	class Sleepy implements Runnable{
		int loops;
		char output;
		
		public Sleepy(char c, int loops){
			this.loops = loops;
			this.output = c;
		}
		
		@Override
		public void run() {
			int i = loops;
			while(i > 0){
				log[logIndex++] = output; //write to log file
				try {
					Thread.sleep(500); //sleep for 500 milliseconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i--;
			}	
		}	
	}
	
	class Yappy extends Thread{
		int loops; //number of output prints 
		char output;  //output character
		
		public Yappy(char c, int loopCount) {
			this.loops = loopCount;
			this.output = c;
		}
		
		@Override
		public void run() {
			int i = loops;
			while(i > 0){
				log[logIndex++] = output; //write to log file 
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
