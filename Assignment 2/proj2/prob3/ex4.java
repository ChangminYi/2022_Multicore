import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class ex4Thread extends Thread {
    ex4Thread() {
    }

    public void run() {
        System.out.println(this.getName() + " started");
            try {
                Thread.sleep((long)(Math.random() * 20000));
            } catch(InterruptedException e) {
        }

        synchronized(ex4.barrier) {
            System.out.println(this.getName() + " reached barrier, " + (ex4.THREAD_NUM - ex4.barrier.getNumberWaiting() - 1) + " left");
        }

        try {
            ex4.barrier.await();
        } catch(BrokenBarrierException e) {
        } catch(InterruptedException e) {
        }

        System.out.println(this.getName() + " restarted");
    }
}

public class ex4 {
    final public static int THREAD_NUM = 16;
    
    private static ex4Thread[] threads = new ex4Thread[THREAD_NUM];
    public static CyclicBarrier barrier;

    public static void main(String[] args) {
        barrier = new CyclicBarrier(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i] = new ex4Thread();
            threads[i].start();
        }

        try {
            for(int i = 0; i < THREAD_NUM; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
        }
    }
}
