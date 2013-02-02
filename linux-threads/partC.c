/*
 * CONCURRENCY EXPERIMENT WITH REAL TIME LINUX THREADS (XENOMAI)
 *
 *	ANALYSIS:
   	   The yappy thread ran first.

	It is possible to use printf in main. However, printf uses linux syscalls and this
	significantly slows down the real-time thread. The rt_printf is preferred because it is
	a cheaper printing call (no syscalls or locks).

   What was the pattern of the numbers printed out?
   	   'yyyyyyyyyyyyyyyyyyyyyyyyysyyyysyyyysyy'. Unlike partB, yappy threads keeps outputting to the
   	   console for a while before both threads eventually start interleaving.

   Run your program several more times. Is the same thread always run first?
   	   Yes, but the number of outputs are not consistent.

   Run your program several more times, changing the sleep time and the spin time. What
   changes do you see?
	    Reducing the spin time causes the yappy thread to output to the console more frequently.
		By increasing the sleep time, the sleepy thread prints to the console less frequently.

 *
 * Author: OSAZUWA OMIGIE (100764733)
 */

#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>

RT_TASK yappy_task;
RT_TASK sleepy_task;

#define MAX_OUTPUT 1000

static char log[MAX_OUTPUT]; //shared memory with child thread
static int log_index = 0;


typedef struct{
	int count; //used to indicate sleep time (in nanoseconds) or spin count
	char output;
} Data;

void yappyRun(void *arg){
	while(log_index < (MAX_OUTPUT-1)){
		Data * data = (Data *) arg;

		log[log_index++] = data->output;

		/*spinning*/
		int var;
		for(var=2;var<data->count;var++){
			var *=1;
			var /=1;
		}
	}
}

void sleepyRun(void *arg){
	while(log_index < (MAX_OUTPUT-1)){
		Data * data = (Data *) arg;
		log[log_index++] = data->output; //write to log variable
		rt_task_sleep(data->count); //sleep
	}

}

void cleanUp(){
	rt_task_delete(&yappy_task);
	rt_task_delete(&sleepy_task);
}

void catch_signal(int sig)
{
	cleanUp();
	return;
}



int main(int argc, char* argv[])
{
	signal(SIGTERM, catch_signal);
	signal(SIGINT, catch_signal);

	rt_print_auto_init(1);

	/* Avoids memory swapping for this program */
	mlockall(MCL_CURRENT|MCL_FUTURE);


	if(rt_task_create(&yappy_task, NULL, 0, 0, T_JOINABLE)==0){
	}else{
		printf("error creating yappy thread");
		return -1;
	}
	if(rt_task_create(&sleepy_task, "sleepy", 0, 0, T_JOINABLE)==0){}
	else{
		printf("error creating sleepy thread");
		return -1;
	}

	Data yappyData;
		yappyData.output = 'y';
		yappyData.count = 100000;  //set the spin count

		Data sleepyData;
		sleepyData.output = 's';
		sleepyData.count = 1000000; //set the sleep time

	if(rt_task_start(&yappy_task, &yappyRun, &yappyData)==0){}
	else{
		printf("error starting yappy thread");
		return -1;
	}
	if(rt_task_start(&sleepy_task, &sleepyRun, &sleepyData)==0){}
	else {
		printf("error starting sleepy thread");
		return -1;
	}

	//wait for the children thread to terminate
	rt_task_join(&yappy_task);
	rt_task_join(&sleepy_task);

	log[MAX_OUTPUT] = '\0'; //null ending character

	rt_printf("%s",log);

	return 0;
}
