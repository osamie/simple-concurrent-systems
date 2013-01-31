/*
 * BASIC JAVA THREAD CONCURRENCY EXPERIMENT  
 * Two thread classes will both loop for the given number of times, and each time 
 * through the loop, it will print out its one-letter name. 
 * 
 	OUTPUTS:
 		main thread output: "DONE"
 		sleepy thread: 's'
 		yappy thread: 'y'
 		
    OBSERVATIONS:
 1. 
 	-Run your program. What got printed out first? 
 	Which of the two created threads ran first? 
 	
 	The first output was 'y'. YappyThread.start() was called before sleepyThread.start(). 
 	The first output 'y' was from the yappy thread.  
 	
 	
 	-What was the pattern of the numbers printed out?
 	"ysDONEyyyyyyysyyyyyyys" or "yDONEsyyyyyyysyyyyyyys"
 	yappy thread always output first, but sleepy and the parent interchange for 
 	second place.   
 
 2. 
 	Run your program several more times. Is the same thread always run first?
    The same thread is always the first to run.
    
 3. 
	Run your program several more times, changing the sleep time and the spin time. What 
	changes do you see?
	By decreasing the spin time, the yappy thread prints to the console more frequently.
	By increasing the sleep time, the sleepy thread prints to the console less frequently.      

During the yappy thread's spin time, the thread is been 'executed' or CPU bound (doing nothing).
Since the I/O is not in use at this time, the other thread is able use I/O in printing (if it is not asleep).  
 
 Author: OSAZUWA OMIGIE
 */

package model;

public class PartA {

	
	public void createThreads(){
		
		Yappy yappyThread = new Yappy('y',99999999);
		Sleepy sleepyRunnable = new Sleepy('s',1000);
		
		yappyThread.start(); //initialize thread resources and run/execute thread
		new Thread(sleepyRunnable).start();
		System.out.print("DONE");
	}
	public static void main(String[] args) 
	{
		PartA partA = new PartA();
		partA.createThreads(); //create the sleepy and yappy threads
	}
	
	
	/*Thread classes*/
	
	class Sleepy implements Runnable{
		int sleepTime;
		char output;
		public Sleepy(char c, int delay){
			this.sleepTime = delay;
			this.output = c;
		}
		
		@Override
		public void run() {
			while(1==1){
				System.out.print(output);
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}	
	}
	
	class Yappy extends Thread{
		int loops;  //spin count
		char output;  //output character 
		
		public Yappy(char c, int loopCount) {
			this.loops = loopCount; //set the spin count
			this.output = c;
		}
		
		@Override
		public void run() {
			while(1==1){
				System.out.print(output);
						
				/*spin this thread*/
				int var;
				for(var=2;var<loops;var++){
					var *=1;
					var /=1;
				}
			}
		}	
	}
}


