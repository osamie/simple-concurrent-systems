/*
 * CONCURRENCY EXPERIMENT WITH REAL TIME LINUX THREADS (XENOMAI)
 *
 *
 * Author: Osazuwa Omigie
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
	while(log_index < MAX_OUTPUT){
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
	while(log_index < MAX_OUTPUT){
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

//	rt_printf("here");
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


	rt_task_join(&yappy_task); //wait for the child thread to terminate
	rt_task_join(&sleepy_task);

	rt_printf("%s",log);

	return 0;
}
