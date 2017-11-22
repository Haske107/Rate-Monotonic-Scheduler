package com.company;


import vanilla.java.affinity.AffinityLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        // COUNTER
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger curpriority = new AtomicInteger(9);

        // CREATE SEMAPHORES FOR EACH WORKER
        Semaphore s1 = new Semaphore(1);

        // CREATE NEW WORKER THREADS
        List<Worker>  ThreadList = new ArrayList<>();
        Worker W1 = new Worker(1, s1, counter, curpriority, 9);
        //W1.setPriority(9);
        ThreadList.add(W1);
        Worker W2 = new Worker(2, s1, counter, curpriority, 8);
        //W2.setPriority(8);
        ThreadList.add(W2);
        Worker W3 = new Worker(4, s1, counter, curpriority, 7);
        //W3.setPriority(7);
        ThreadList.add(W3);
        Worker W4 = new Worker(16, s1, counter, curpriority, 6);
        //W4.setPriority(6);
        ThreadList.add(W4);





        // CREATE NEW SCHEDULER
        Scheduler Scheduler = new Scheduler(16, ThreadList, s1, counter, curpriority);
        Scheduler.setPriority(10);
        Scheduler.start();

        // FINISH THE JOB
        try {
            Scheduler.join();
        }   catch (InterruptedException e)  {
            e.printStackTrace();
        }
    }
}


/*

Main thread kicks off scheduler and four threads
Thread should know if it misses its deadline

loop()
    sm.wait()
    for loop:
        dowork()
        counter ++

 */