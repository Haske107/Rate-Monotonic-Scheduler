package com.company;

import javafx.concurrent.Task;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler extends Thread {

    private int period;
    public AtomicInteger counter;
    private AtomicInteger currpriority;
    private List<Worker> threads;
    private Semaphore s1;




    Scheduler (int period, List<Worker> threads, Semaphore s1, AtomicInteger counter, AtomicInteger currpriority) {
        this.period = period;
        this.threads = threads;
        this.s1 = s1;
        this.counter = counter;
        this.currpriority = currpriority;
    }

    @Override
    public void start() {
        System.out.println("Scheduler Start:");
        run();
    }

    @Override
    public void run()    {
            try {
                for (int i = 0; i < 10; ++i)    {
                    runFrame();
                    counter.set(0);
                  }
                s1.release();
            } catch  (Exception e)    {

            }
    }

    private void runFrame() throws Exception {
            for (Worker thread: threads) {
                if (counter.get() >= 16) {
                    currpriority.set(9);
                    break;
                }
                runPeriod(thread);
            }
    }

    private void runPeriod(Worker thread) throws Exception {
        System.out.println("---");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Worker> future = executor.submit(thread);
        try {
            future.get(50, TimeUnit.MILLISECONDS);
            System.out.println("Completed!");
        } catch (TimeoutException e) {
            //future.cancel(true);
            System.out.println("Overrun!");
        }

        s1.release();

    }
}
