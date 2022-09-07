import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

class DynamicThread extends Thread {
    public void run() {
        long startTime = System.currentTimeMillis();
        for (int num;;) {
            synchronized (pc_dynamic.queue) {
                if (pc_dynamic.queue.isEmpty())
                    break;
                else
                    num = pc_dynamic.queue.poll();
            }
            if (pc_dynamic.isPrime(num)) {
                pc_dynamic.counter.incrementAndGet();
            }
        }
        long endTime = System.currentTimeMillis();
        
        printThreadRunTime(this.getName(), endTime - startTime);
    }

    synchronized private static void printThreadRunTime(final String threadName, final long threadRunTime) {
        System.out.println(threadName + " execution time: " + threadRunTime + "ms");
    }
}

public class pc_dynamic {
    private static int NUM_END = 200000;
    private static int NUM_THREADS = 4;

    public static Queue<Integer> queue = new LinkedList<>();
    public static AtomicInteger counter = new AtomicInteger(0);
    private static DynamicThread[] dynamicThread;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }

        initQueue();

        // making and running of threads
        dynamicThread = new DynamicThread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            dynamicThread[i] = new DynamicThread();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            dynamicThread[i].start();
        }
        
        try {
            for(int i = 0; i < NUM_THREADS; i++) {
                dynamicThread[i].join();
            }
        } catch (InterruptedException e) {
        }
        long endTime = System.currentTimeMillis();

        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        System.out.println("1..." + (NUM_END - 1) + " prime# counter=" + counter);
    }

    private static void initQueue() {
        for (int i = 0; i < NUM_END; i++) {
            queue.add(i);
        }
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