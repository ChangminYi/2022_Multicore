import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ex2ThreadReader extends Thread {
    ex2ThreadReader() {
    }

    public void run() {
        for(int i = 0, res; i < 10; i++) {
            try {
                Thread.sleep((long)(Math.random() * 1000));
            } catch(InterruptedException e) {
            }

            ex2.rwlock.readLock().lock();
            res = ex2.number;
            ex2.rwlock.readLock().unlock();

            System.out.println("\t\t\t\t" + this.getName() + " read result: " + res);
        }
    }
}

class ex2ThreadWriter extends Thread {
    ex2ThreadWriter() {
    }

    public void run() {
        for(int i = 0, res; i < 10; i++) {
            try {
                Thread.sleep((long)(Math.random() * 1000));
            }catch(InterruptedException e) {
            }

            ex2.rwlock.writeLock().lock();
            ex2.number++;
            res = ex2.number;
            ex2.rwlock.writeLock().unlock();

            System.out.println(this.getName() + " write result: " + res);
        }
    }
}

public class ex2 {
    final private static int THREAD_NUM = 3;

    public static ReadWriteLock rwlock = new ReentrantReadWriteLock();

    public static int number = 0;
    private static ex2ThreadReader[] reader = new ex2ThreadReader[THREAD_NUM];
    private static ex2ThreadWriter[] writer = new ex2ThreadWriter[THREAD_NUM];

    public static void main(String[] args) {
        for(int i = 0; i < THREAD_NUM; i++) {
            writer[i] = new ex2ThreadWriter();
            writer[i].setName("Writer " + (i + 1));
            reader[i] = new ex2ThreadReader();
            reader[i].setName("Reader " + (i + 1));
        }

        for(int i = 0;i < THREAD_NUM; i++) {
            writer[i].start();
            reader[i].start();
        }

        try {
            for(int i = 0; i < THREAD_NUM; i++) {
                writer[i].join();
                reader[i].join();
            }
        } catch(InterruptedException e) {
        }
    }
}
