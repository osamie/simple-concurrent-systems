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


void yappy(void *arg){
	while(1){
		printf("y");

		/*spin this thread*/
		int var;
		for(var=2;var<999999;var++){
			var *=1;
			var /=1;
		}
	}
}

void sleepy(void *arg){
	while(1){
		printf("s");

		rt_task_wait_period(NULL);
	}

}

void catch_signal(int sig)
{
}

int main(int argc, char* argv[])
{
	signal(SIGTERM, catch_signal);
	signal(SIGINT, catch_signal);

	/* Avoids memory swapping for this program */
	mlockall(MCL_CURRENT|MCL_FUTURE);


	rt_task_create(&yappy_task, "yappy", 0, 99, 0); //"new Thread()"
	rt_task_create(&sleepy_task, "sleepy", 0, 99, 0); //"new Thread()"

	/*
	 * Arguments: &task,
	 *            task function,
	 *            function argument
	 */
	rt_task_start(&yappy_task, &yappy, NULL);  // "Thread.start()"
	rt_task_start(&sleepy_task, &sleepy, NULL);  // "Thread.start()"

//	pause();

	rt_task_delete(&yappy_task);
	rt_task_delete(&sleepy_task);

	return 0;
}
