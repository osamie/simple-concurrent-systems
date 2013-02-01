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

#define MAX_OUTPUT 100
//maximumOutput = 100;
static char log[MAX_OUTPUT]; //shared memory with child thread
static int log_index = 0;


typedef struct{
	int count; //used to indicate sleep time (in nanoseconds) or spin count
	char output;
} Data;

void yappy(void *arg){
	while(log_index < MAX_OUTPUT){
		//TODO: error check instance type of arg
		Data * data = (Data *) arg;
//		rt_printf("%c\n",data->output);
		log[log_index++] = data->output;

		/*spin this thread*/
		int var;
		for(var=2;var<data->count;var++){
			var *=1;
			var /=1;
		}
	}


}

void sleepy(void *arg){
	while(log_index < MAX_OUTPUT){

		//TODO: error check instance type ofsleepTime arg
		Data * data = (Data *) arg;
		log[log_index++] = data->output;
//		rt_printf("%c\n",data->output);
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

//	rt_printf("here");
	rt_print_auto_init(1);

	/* Avoids memory swapping for this program */
	mlockall(MCL_CURRENT|MCL_FUTURE);


	Data yappyData;
	yappyData.output = 'y';
	yappyData.count = 100000;  //set the spin count

	Data sleepyData;
	sleepyData.output = 's';
	sleepyData.count = 1000000; //set the sleep time

	rt_task_create(&yappy_task, NULL, 0, 0, T_JOINABLE); //"new Thread()"
	rt_task_create(&sleepy_task, NULL, 0, 0, T_JOINABLE); //"new Thread()"

	rt_task_start(&yappy_task, &yappy, &yappyData);  // "Thread.start()"
	rt_task_start(&sleepy_task, &sleepy, &sleepyData);  // "Thread.start()"

	rt_task_join(&yappy_task); //wait for the child thread to terminate
	rt_task_join(&sleepy_task);

	rt_printf("%s",log);
//	pause();



	return 0;
}
