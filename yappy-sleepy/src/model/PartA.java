/*
 * Basic Java thread concurrency  
 * Two thread classes will both loop for the given number of times, and each time 
 * through the loop, it will print out its one-letter name. 
 * 
 * Author: Osazuwa Omigie
 */

package model;

public class PartA {

	
	public void createThreads(){
		
		Yappy yappyThread = new Yappy();
		Sleepy sleepyRunnable = new Sleepy();
		
		new Thread(sleepyRunnable).start();
		
		yappyThread.start(); //initialize thread resources and run/execute thread
		System.out.print("DONE");
	}
	public static void main(String[] args) 
	{
		PartA partA = new PartA();
		partA.createThreads(); //create the sleepy and yappy threads
	}
	
	class Sleepy implements Runnable{
		
		@Override
		public void run() {
			while(1==1){
				System.out.print("s");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}	
	}
	
	class Yappy extends Thread{
		
		@Override
		public void run() {
			while(1==1){
				System.out.print("y");
						
				/*spin this thread*/
				int var;
				for(var=2;var<99999999;var++){
					var *=1;
					var /=1;
				}
			}
		}	
	}
}


