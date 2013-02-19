 #include <stdio.h>
 #include <signal.h>
 #include <unistd.h>
 #include <sys/mman.h>

 #include <native/task.h>
 #include <native/timer.h>
 #include <native/sem.h>

 #include  <rtdk.h>
 #include <sys/io.h>

 RT_TASK tasks[3];
 RT_SEM  startup;

 #define PRIORITY 40

 void worker(void *arg)
 {
    int id=*(int *)arg;
 
    printf("Worker%d: Start\n",id);
    rt_sem_p(&startup, TM_INFINITE);
    printf("Worker%d: Endt\n",id);
    rt_task_delete(NULL);
}


int main(int argc, char* argv[])
{
  int i;

  mlockall(MCL_CURRENT|MCL_FUTURE);
  rt_sem_create( &startup, "STARTUP", 0, S_FIFO);

  for (i=0;i<3;i++)
  {
    rt_task_create(&tasks[i], NULL, 0, PRIORITY+i, T_JOINABLE);
    rt_task_start(&tasks[i], &worker, &i);
  }
  rt_sem_broadcast(&startup);

  for (i=0;i<3;i++)  rt_task_join(&tasks[i]);
}
