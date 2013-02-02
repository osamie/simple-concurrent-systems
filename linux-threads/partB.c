/*
 * CONCURRENCY EXPERIMENT WITH REAL TIME LINUX THREADS (XENOMAI)
 *
   ANALYSIS:
   Run your program. What got printed out first?
   	   The parent gets printed out first

   Which of the two created threads ran first?
   	   The yappy thread ran first.

   What was the pattern of the numbers printed out?
   	   'DONEyyyysssyssy' Parent, then yappy a few times, and then subsequent
   	   outputs are interleaved between both child threads

   Run your program several more times. Is the same thread always run first?
   	   Yes, but the number of outputs are not consistent.

   Run your program several more times, changing the sleep time and the spin time. What
   changes do you see?
	    Reducing the spin time causes the yappy thread to output to the console more frequently.
		By increasing the sleep time, the sleepy thread prints to the console less frequently.


  Author: OSAZUWA OMIGIE (100764733)
 */

#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>

RT_TASK yappy_task;
RT_TASK sleepy_task;


typedef struct{
	int count;
	char output;
} Data;

void yappyRun(void *arg){
	while(1){
		Data * data = (Data *) arg;
		rt_printf("%c",data->output);

		/*spin this thread*/
		int var;
		for(var=2;var<data->count;var++){
			var *=1;
			var /=1;
		}
	}
}

void sleepyRun(void *arg){
	while(1){
		Data * data = (Data *) arg;
		rt_printf("%c",data->output);
		rt_task_sleep(data->count);
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

	/* Avoids memory swapping for this program */
	mlockall(MCL_CURRENT|MCL_FUTURE);

	rt_print_auto_init(1);

	if(rt_task_create(&yappy_task, "yappy", 0, 0, 0) == 0){}
	else {
		printf("error creating yappy thread");
		return -1;
	}
	if(rt_task_create(&sleepy_task, "sleepy", 0, 0, 0)==0){}
	else{
		printf("error creating sleepy thread");
		return -1;
	}

	rt_printf("DONE");
	Data yappyData;
	yappyData.output = 'y';
	yappyData.count = 99999;  //set spin time for yappy thread

	Data sleepyData;
	sleepyData.output = 's';
	sleepyData.count = 1000000;  //set sleep time for sleepy thread

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

	pause();

	cleanUp();

	return 0;
}
