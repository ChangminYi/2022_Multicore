import java.util.concurrent.atomic.AtomicInteger;

class CyclicThread extends Thread {
    private int offset;

    CyclicThread(int offset) {
        this.offset = offset;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        for (int i = offset; i < pc_static_cyclic.NUM_END; i += pc_static_cyclic.NUM_THREADS) {
            if (pc_static_cyclic.isPrime(i))
                pc_static_cyclic.counter.incrementAndGet();
        }
        long endTime = System.currentTimeMillis();

        printThreadRunTime(this.getName(), endTime - startTime);
    }

    synchronized private static void printThreadRunTime(final String threadName, final long threadRunTime) {
        System.out.println(threadName + " execution time: " + threadRunTime + "ms");
    }
}

public class pc_static_cyclic {
    public static int NUM_END = 200000;
    public static int NUM_THREADS = 4;

    private static CyclicThread[] cyclicThread;
    public static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }

        // making and running of threads
        cyclicThread = new CyclicThread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            cyclicThread[i] = new CyclicThread(i);
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            cyclicThread[i].start();
        }

        try {
            for(int i = 0; i < NUM_THREADS; i++) {
                cyclicThread[i].join();
            }
        } catch (InterruptedException e) {
        }
        long endTime = System.currentTimeMillis();

        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        System.out.println("1..." + (NUM_END - 1) + " prime# counter=" + counter);
    }

    public static boolean isPrime(int x) {
        int i;
        if (x <= 1)
            return false;
        for (i = 2; i < x; i++) {
            if (x % i == 0)
                return false;
        }
        return true;
    }
}