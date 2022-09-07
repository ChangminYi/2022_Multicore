import java.util.concurrent.atomic.AtomicInteger;

class ex3ThreadAdder extends Thread {
    private int offset;

    ex3ThreadAdder(int offset) {
        this.offset = offset;
    }

    public void run() {
        if(offset % 2 == 0)
            incFirst();
        else
            getFirst();
    }

    private void incFirst() {
        while(true) {
            try {
                Thread.sleep((long)(Math.random() * 500));
            } catch(InterruptedException e) {}

            if(ex3.finishFlag.get() == 0)
                System.out.println(this.getName() + " added " + offset + ", Sum becomes " + ex3.sum.addAndGet(offset));
            else
                break;
        }
    }

    private void getFirst() {
        while(true) {
            try {
                Thread.sleep((long)(Math.random() * 500));
            } catch(InterruptedException e) {}

            if(ex3.finishFlag.get() == 0)
                System.out.println(this.getName() + " adding " + offset + ", Sum was " + ex3.sum.getAndAdd(offset));
            else
                break;
        }
    }
}

class ex3ThreadInitializer extends Thread {
    ex3ThreadInitializer() {
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
            }

            if (ex3.sum.get() % 2 == 1) {
                System.out.println("\t\t\t\t\tSum is odd number. Return to 0.");
                ex3.sum.set(0);
            } else {
                ex3.finishFlag.set(1);
                System.out.println("\t\t\t\t\tSum is even number. Finishing threads");
                break;
            }
        }
    }
}

public class ex3 {
    final public static int THREAD_NUM = 4;

    public static AtomicInteger sum = new AtomicInteger(0);
    public static AtomicInteger finishFlag = new AtomicInteger(0);
    private static ex3ThreadAdder[] adder = new ex3ThreadAdder[THREAD_NUM];
    private static ex3ThreadInitializer restarter = new ex3ThreadInitializer();

    public static void main(String[] args) {
        restarter.start();
        for (int i = 0; i < THREAD_NUM; i++) {
            adder[i] = new ex3ThreadAdder(i + 1);
            adder[i].start();
        }

        try {
            restarter.join();
            for (int i = 0; i < THREAD_NUM; i++) {
                adder[i].join();
            }
        } catch (InterruptedException e) {
        }
    }
}
