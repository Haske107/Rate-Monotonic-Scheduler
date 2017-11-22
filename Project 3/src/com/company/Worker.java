package com.company;



import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Callable<Worker> {

    private AtomicInteger currPriority;
    private int priority;
    private Semaphore s1;
    private int period;
    public int iterations = 0;
    public boolean done = false;
    private AtomicInteger counter;

    public int periodcounter = 0;
    private double[][] matrix;

    Worker(int Period, Semaphore semaphore, AtomicInteger counter, AtomicInteger currPriority, int priority)   {
        this.period = Period;
        this.s1 = semaphore;
        this.counter = counter;
        this.currPriority = currPriority;
        this.priority = priority;
        matrix = new double[100][100];
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                matrix[i][j] = 1;
            }
        }

    }
    /*
    @Override
    public void start() {
        System.out.println("Worker " + period + " starting:");
        run();
    }

    @Override
    public void run() {
        try {
            s1.tryAcquire(1);
            System.out.println("Worker "+ period +" acquired:");
            for (int i = 0; i < period; ++i) {
                if(counter.get() >= 16)   {
                    return;
                }
                doWork();
                counter.incrementAndGet();
            }
        } catch (Exception e) {

        }
    }*/

    @Override
    public Worker call() throws Exception {
        if(priority >= currPriority.get())    {
            s1.tryAcquire();
            for (int i = 0; i < period; ++i) {
                doWork();
            }
            currPriority.decrementAndGet();
        }
        return null;
    }

    private void doWork()    {
        for (int i = 0; i < 10000; ++i) {
            matrix[new Random().nextInt(100)][new Random().nextInt(100)]
                    = matrix[new Random().nextInt(100)][new Random().nextInt(100)] * 5.00191839191383;
        }
        periodcounter++;
    }
}






   /*
*/