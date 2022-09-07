import java.util.concurrent.atomic.AtomicInteger;

class BlockThread extends Thread {
    private int base = 0, top = 0;

    BlockThread(int base, int top) {
        this.base = base;
        this.top = top;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        for (int i = base; i <= top; i++) {
            if (pc_static_block.isPrime(i))
                pc_static_block.counter.incrementAndGet();
        }
        long endTime = System.currentTimeMillis();

        printThreadRunTime(this.getName(), endTime - startTime);
    }

    synchronized private static void printThreadRunTime(final String threadName, final long threadRunTime) {
        System.out.println(threadName + " execution time: " + threadRunTime + "ms");
    }
}

public class pc_static_block {
    public static int NUM_END = 200000;
    private static int NUM_THREADS = 4;

    private static BlockThread[] blockThread;
    public static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        
        // making and running of threads
        blockThread = new BlockThread[NUM_THREADS];
        for (int i = 0, blockSize = NUM_END / NUM_THREADS, base, top; i < NUM_THREADS; i++) {
            base = i * blockSize;
            top = (i == NUM_THREADS - 1 ? NUM_END - 1 : (i + 1) * blockSize - 1);
            blockThread[i] = new BlockThread(base, top);
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            blockThread[i].start();
        }

        try {
            for(int i = 0; i < NUM_THREADS; i++) {
                blockThread[i].join();
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