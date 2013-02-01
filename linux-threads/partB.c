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

/* NOTE: error handling omitted. */


typedef struct{
	int count;
	char output;
} Data;

void yappy(void *arg){
	while(1){
		//TODO: error check instance type of arg
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

void sleepy(void *arg){
	while(1){
//		 rtdm_printk("s");

		//TODO: error check instance type ofsleepTime arg
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


	rt_task_create(&yappy_task, "yappy", 0, 0, 0); //"new Thread()"
	rt_task_create(&sleepy_task, "sleepy", 0, 0, 0); //"new Thread()"

	rt_printf("DONE");
	Data yappyData;
	yappyData.output = 'y';
	yappyData.count = 999999;

	Data sleepyData;
	sleepyData.output = 's';
	sleepyData.count = 1000000;

	rt_task_start(&yappy_task, &yappy, &yappyData);  // "Thread.start()"
	rt_task_start(&sleepy_task, &sleepy, &sleepyData);  // "Thread.start()"

	pause();

	cleanUp();

	return 0;
}
