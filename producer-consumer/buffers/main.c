/**
 * EXPERIMENT: CONDITIONAL SYNCHRONIZATION WITH SEMAPHORES ON XENOMAI
 * Two threads (producer and consumer) started together, read and write
 * to two buffers(files) and print a character to a log after each operation.
 *
 *
 *Preventing any race or deadlock conditions between both threads:-
 * Create 4 semaphores of used to indicate the 2 different
 * states for each buffer.
 *
 * States:Semaphor name
 *		Buffer1_Empty	:	buffer1_empty_sem
 * 		Buffer1_Full	:	buffer1_full_sem
 *
 * 		Buffer2_Empty	:	buffer2_empty_sem
 * 		Buffer2_Full	:	buffer2_full_sem
 *
 * 	Using the semaphores, the order of production and consumption
 * 	is regulated. The values of the semaphores are initialized to reflect
 * 	the states of the buffers. The buffers are originally empty, so the
 * 	initial values of 'buffer1_empty_sem' and 'buffer2_empty_sem' are set
 * 	as 1. While the other 2 'Full' states semaphores are set as 0
 * 	(indicating that the semaphores are not full at the onset of the program).
 *
 * 	Solution in summary:
 *  The producer (1) waits for the 'buffer1_empty_sem'.
 *  (2) Once acquired, writes to the buffer 1
 *  (3)It then signals the 'buffer1_full_sem'
 *  (4) Steps 1-3 are repeated for buffer2
 *
 *  The consumer (1) waits for the 'buffer1_empty_sem'.
 *  (2) Once acquired, consumes the buffer 1
 *  (3) It then signals the 'buffer1_empty_sem'
 *  (4) Steps 1-3 are repeated for buffer2
 *
 *	Since only 'buffer1_empty_sem' and 'buffer2_empty_sem' are initialized
 *	values as 1 (reflecting the empty states of the buffers), the producer is
 *	guaranteed to always be the first to run. While the consumer waits for the
 *	first 'full' signal from the producer.
 *
 * 	- Osazuwa Omigie (100764733)
 *
 */

#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/mman.h>

#include <native/task.h>
#include <native/timer.h>
#include <native/sem.h>

#include  <rtdk.h>
#include <sys/io.h>

RT_TASK producer_task;  //producer task
RT_TASK consumer_task;  //consumer task

/* Buffers' EMPTY states*/
RT_SEM buffer1_empty_sem;
RT_SEM buffer2_empty_sem;

/*Buffers' FULL states*/
RT_SEM buffer1_full_sem;
RT_SEM buffer2_full_sem;


#define MAX_OUTPUT 500;
static char log[MAX_OUTPUT]; //shared memory with child thread
static int log_index=0;


//static int buffer1[MAX_OUTPUT];
//static int buffer1_index=0;
//
//static int buffer2[MAX_OUTPUT];
//static int buffer2_index=0;

typedef struct{
	FILE *buffer1;
	FILE *buffer2;
} Data;

/**
 * Producer thread fills up the buffer
 */
void producerRun(void *arg){
	int count = MAX_OUTPUT;
	Data * buffers = (Data *) arg;

	while(count > 0){
		//wait for buffer 1 to signal an empty state
		rt_sem_p(&buffer1_empty_sem,TM_INFINITE);

		//produce - write to buffer 1
		produce('P',buffers.buffer1);

		//signal others that buffer 1 is in full state
		rt_sem_v(&buffer1_full_sem);

		//wait for buffer 2 to signal an empty state
		rt_sem_p(&buffer2_empty_sem,TM_INFINITE);

		//produce - write to buffer 2
		produce('p',buffers.buffer2);

		//signal others that buffer 2 is in full state
		rt_sem_v(&buffer2_full_sem);

		--count;
	}
}


/**
 * Consumer thread consumes the buffers
 */
void consumerRun(void *arg){
	int count = MAX_OUTPUT;
	Data * buffers = (Data *) arg;
	while(count > 0){
		//wait for buffer1 to be in a full state
		rt_sem_p(&buffer1_full_sem,TM_INFINITE);

		//consume - get buffer 1 contents
		consume('C',buffers->buffer1);

		//signal others that buffer 1 is in empty state
		rt_sem_v(&buffer1_empty_sem);

		//wait for buffer2 to be in a full state
		rt_sem_p(&buffer2_full_sem,TM_INFINITE);

		//consume - get buffer 2 contents
		consume('c',buffers->buffer2);

		//signal others that buffer 2 is in empty state
		rt_sem_v(&buffer2_empty_sem);

		--count;
	}
}

void produce(char c,FILE file){
	log(c,file);
}

void consume(char c,FILE file){
	log(c,file);
}

/**
 * Used by a thread in outputting to the log
 */
void log(char output){
	printf(output);
	//only one thread can access the log variable at a time
//	if(log_index < MAX_OUTPUT){
//		//add to the log variable
//	}
}

/**
 * Produce/add to the contents of a buffer.
 * Changes the state of the buffer to FULL
 */
void add(char c){
	printf("adding %c",c);
}

/**
 * Consume/get the contents of a buffer
 * Changes the state of the buffer to EMPTY
 */
void get(char c){
	printf("getting %c",c);
}
void catch_signal(int sig)
{
	cleanUp();
	return;
}

/*
 * cleanup: remove the semaphores and mutexes
 */
void cleanUp(){

}

int main(int argc, char **argv) {
	signal(SIGTERM, catch_signal);
	signal(SIGINT, catch_signal);

	rt_print_auto_init(1);

	/* Avoids memory swapping for this program */
	mlockall(MCL_CURRENT|MCL_FUTURE);


	/**
	 * Create the buffer files
	 */
	Data buffers;
	buffers.buffer1 = fopen("bufferOne.txt","W+");
	buffers.buffer2 = fopen("bufferTwo.txt","W+");



	/**
	 * Create all 4 semaphores to represent the 'full' and 'empty'
	 * states of the buffers
	 */

	rt_sem_create( &buffer1_full_sem, "buffer1_full", 0, S_FIFO);
	rt_sem_create( &buffer1_empty_sem, "buffer1_empty", 0, S_FIFO);
	rt_sem_create( &buffer2_full_sem, "buffer2_full", 0, S_FIFO);
	rt_sem_create( &buffer2_empty_sem, "buffer2_full", 0, S_FIFO);


	/*
	 * Both buffers are empty initially. So initialize the 'empty'
	 * semaphores to reflect the state of the buffers.
	 */
	rt_sem_broadcast(&buffer1_empty_sem);
	rt_sem_broadcast(&buffer2_empty_sem);char output;

	char output;
	/**
	 * Create the mutex
	 */






	/**
	 * Create the threads
	 */
	if(rt_task_create(&producer_task, NULL, 0, 0, T_JOINABLE)==0){
		//producer thread has been created without any issues
	}
	else{
		printf("error creating pro thread");
		return -1;
	}
	if(rt_task_create(&consumer_task, NULL, 0, 0, T_JOINABLE)==0){
		//consumer thread has been created without any issues
	}
	else{
			printf("error creating sleepy thread");
			return -1;
	}


	/**
	 * Start the threads
	 */
	if(rt_task_start(&producer_task, &producerRun, &buffers)==0){}
	else{
		printf("error starting producer thread");
		return -1;
	}
	if(rt_task_start(&consumer_task, &consumerRun, &buffers)==0){}
	else {
		printf("error starting consumer thread");
		return -1;
	}


	//Wait for the producer and consumer threads to terminate
	rt_task_join(&producer_task);
	rt_task_join(&consumer_task);

	log[MAX_OUTPUT] = '\0'; //null ending character

	rt_printf("%s",log); //print the log

	/**
	 * CLEAN UPS
	 */
	fclose(buffers.buffer1);
	fclose(buffers.buffer2);

//	cleanUp();

	return 0;

}
