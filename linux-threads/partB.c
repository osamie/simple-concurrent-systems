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
	yappyData.count = 999999;

	Data sleepyData;
	sleepyData.output = 's';
	sleepyData.count = 1000000;

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
