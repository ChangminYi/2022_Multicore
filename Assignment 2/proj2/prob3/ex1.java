import java.util.concurrent.ArrayBlockingQueue;

class ex1ThreadConsumer extends Thread {
    ex1ThreadConsumer() {
    }

    public void run() {
        for (int i = 0, res = -1; i < ex1.ITER_SIZE; i++) {
            try {
                Thread.sleep((long) (Math.random() * 1500));
            } catch (InterruptedException e) {
            }

            System.out.println("\t\t\t\t\t\t" + this.getName() + " try to take element");
            try {
                res = ex1.queue.take();
            } catch (InterruptedException e) {
            }
            System.out.println("\t\t\t\t\t\t" + this.getName() + " took element " + res);
        }
    }
}

class ex1ThreadProducer extends Thread {
    private int base;

    ex1ThreadProducer(int base) {
        this.base = base * 100;
    }

    public void run() {
        for (int i = 1; i <= ex1.ITER_SIZE; i++) {
            try {
                Thread.sleep((long) (Math.random() * 1500));
            } catch (InterruptedException e) {
            }

            System.out.println(this.getName() + " try to put element");
            try {
                ex1.queue.put(base + i);
            } catch (InterruptedException e) {
            }
            System.out.println(this.getName() + " put element " + (base + i));
        }
    }
}

public class ex1 {
    final private static int THREAD_NUM = 4;
    final private static int MAX_QUEUE_SIZE = 3;
    final public static int ITER_SIZE = 5;

    public static ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    private static ex1ThreadConsumer[] consumer = new ex1ThreadConsumer[THREAD_NUM];
    private static ex1ThreadProducer[] producer = new ex1ThreadProducer[THREAD_NUM];

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_NUM; i++) {
            producer[i] = new ex1ThreadProducer(i + 1);
            producer[i].setName("Producer " + (i + 1));
            consumer[i] = new ex1ThreadConsumer();
            consumer[i].setName("Consumer " + (i + 1));
        }

        for (int i = 0; i < THREAD_NUM; i++) {
            producer[i].start();
            consumer[i].start();
        }

        try {
            for(int i = 0; i < THREAD_NUM; i++) {
                producer[i].join();
                consumer[i].join();
            }
        } catch (InterruptedException e) {
        }
    }
}
