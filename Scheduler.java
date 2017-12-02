import org.apache.commons.lang3.concurrent.TimedSemaphore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler extends Thread {

    private AtomicInteger counter;
    private TimedSemaphore sem;
    private int TimeQuantum;
    private List<Worker> threads;
    private List<Worker> periodQueue;
    private List<String> Results = new ArrayList<>();
    private int OverrunCounter = 0;
    private boolean OR;

    Scheduler (List<Worker> threads, TimedSemaphore sem, AtomicInteger counter, List<String> Results
            , int TimeQuantum) {
        this.threads = threads;
        this.sem = sem;
        this.counter = counter;
        this.Results = Results;
        this.TimeQuantum = TimeQuantum;
    }

    @Override
    public void start() {
        run();
    }

    @Override
    public void run()    {
        long StartTime = System.nanoTime();
        while(counter.get() < 100) {
            long Duration;
            ExecutorService Executor = Executors.newSingleThreadExecutor();
            try {
                sem.acquire();
                generatePeriodQueue();
                StartTime = System.nanoTime();
                checkAndHandleOverruns();
                for( Worker  worker: periodQueue)   {
                    try {
                        Future<?> Future = Executor.submit(worker);
                        Future.get(TimeQuantum - (System.nanoTime() - StartTime) / 1000000, TimeUnit.MILLISECONDS);
                    }   catch (TimeoutException e)  {
                        Results.add("Worker "+ worker.period +":        Preempted");
                        worker.state.set(false);
                    }
                }
                while((( System.nanoTime() - StartTime) / 1000000) < TimeQuantum)   {}
                Duration = (System.nanoTime() - StartTime) / 1000000;
                Results.add("Period " + counter.get() + " --------------------------------------" + Duration + "ms");
            }   catch (Exception e) {

            }
            finally {
                counter.incrementAndGet();
                if(OR) {
                    Results.add("                                               OVERRUNS: " + OverrunCounter);
                }
            }
        }
    }

    private void generatePeriodQueue()  {
        periodQueue = new ArrayList<>();
        if (counter.get() % 1 == 0) periodQueue.add(threads.get(0));
        if (counter.get() % 2 == 0) periodQueue.add(threads.get(1));
        if (counter.get() % 4 == 0) periodQueue.add(threads.get(2));
        if (counter.get() % 16 == 0) periodQueue.add(threads.get(3));
    }

    private void checkAndHandleOverruns()   {
        OR = false;
        for (Worker worker: periodQueue)   {
            if(!worker.state.get())  {
                OverrunCounter++;
                OR = true;
            }
        }
    }
}
