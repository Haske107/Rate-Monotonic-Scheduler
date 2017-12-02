import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread implements Runnable {

    private Semaphore sem1;
    public int period;
    public AtomicBoolean state;
    private List<String> Results;
    private double[][] matrix;

    Worker(int Period, Semaphore semaphore, List<String> Results, AtomicBoolean state)   {
        this.period = Period;
        this.sem1 = semaphore;
        this.Results = Results;
        this.state = state;
        matrix = new double[100][100];
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                matrix[i][j] = 1;
            }
        }
    }

    @Override
    public void start() {

        run();
    }

    @Override
    public void run() {
        Results.add("Worker " + period + ": Scheduled");
        state.set(false);
        try {
            sem1.acquire();
            Results.add("Worker "+ period +":        Acquired");
            for (int i = 0; i < period; ++i) {
                doWork();
            }
            state.set(true);
            Results.add("Worker " + period + ":                      Done");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Results.add("Worker "+ period +":                        Released");
            sem1.release();
        }
    }



    private void doWork()    {
        Results.add("Worker " + period + ":              Working");
        for (int i = 0; i < 10000; ++i) {
            matrix[new Random().nextInt(100)][new Random().nextInt(100)]
                    = matrix[new Random().nextInt(100)][new Random().nextInt(100)] * 5;
        }
    }
}