import org.apache.commons.lang3.concurrent.TimedSemaphore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        // COUNTER
        AtomicInteger counter = new AtomicInteger(0);

        // TIME QUANTUM ms
        int TimeQuantum = 50;

        // CREATE SEMAPHORE
        TimedSemaphore sem = new TimedSemaphore(TimeQuantum, TimeUnit.MILLISECONDS, 1);
        Semaphore sem1 = new Semaphore(1);

        // CREATE RESULTS
        List<String> Results = new ArrayList<>();

        // THREAD STATES
        List<AtomicBoolean> ThreadStates = new ArrayList<>();
        IntStream.range(0,4).forEach(iteration -> ThreadStates.add(new AtomicBoolean(true)));

        // CREATE NEW WORKER THREADS
        List<Worker>  ThreadList = new ArrayList<>();
        Worker W1 = new Worker(1, sem1, Results, ThreadStates.get(0));
        W1.setPriority(9);
        ThreadList.add(W1);
        Worker W2 = new Worker(2, sem1, Results, ThreadStates.get(1));
        W2.setPriority(8);
        ThreadList.add(W2);
        Worker W3 = new Worker(4, sem1, Results, ThreadStates.get(2));
        W3.setPriority(7);
        ThreadList.add(W3);
        Worker W4 = new Worker(16, sem1, Results, ThreadStates.get(3));
        W4.setPriority(6);
        ThreadList.add(W4);

        // CREATE NEW SCHEDULER
        Scheduler Scheduler = new Scheduler( ThreadList, sem, counter, Results, TimeQuantum );
        Scheduler.setPriority(10);
        Scheduler.start();

        // FINISH THE JOB
        try {
            Scheduler.join();
            for (String line : Results) {
                System.out.println(line);
            }
        }   catch (InterruptedException e)  {
            e.printStackTrace();
        }

    }
}
