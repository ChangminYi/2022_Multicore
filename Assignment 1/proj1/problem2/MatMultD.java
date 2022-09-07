import java.util.Scanner;

class MatMultThread extends Thread {
    private int base;
    private int offset;
    private long startTime, endTime;

    // constructor
    MatMultThread(final int base, final int offset) {
        this.base = base;
        this.offset = offset;
    }

    // cyclic domain decomposition
    public void run() {
        startTime = System.currentTimeMillis();
        for (int i = this.base; i < MatMultD.a.length; i += this.offset) {
            for (int j = 0; j < MatMultD.b[0].length; j++) {
                for (int k = 0; k < MatMultD.a[0].length; k++) {
                    MatMultD.c[i][j] += MatMultD.a[i][k] * MatMultD.b[k][j];
                }
            }
        }
        endTime = System.currentTimeMillis();
        
        printRunTime(this.getName(), this.endTime - this.startTime);
    }

    // printing thread's name and running time
    synchronized private static void printRunTime(final String threadName, final long runTime) {
        System.out.printf("[%s], [Runtime]: %4dms\n", threadName, runTime);
    }
}

public class MatMultD {
    private static Scanner sc = new Scanner(System.in);
    private static MatMultThread[] multThread;
    private static int threadNum;

    public static int[][] a, b, c;

    public static void main(String[] args) {
        // initializing of thread number
        if (args.length == 1)
            threadNum = Integer.valueOf(args[0]);
        else
            threadNum = 1;

        // initializing matrix a, b and allocation of c
        a = new int[sc.nextInt()][sc.nextInt()];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                a[i][j] = sc.nextInt();
            }
        }
        b = new int[sc.nextInt()][sc.nextInt()];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] = sc.nextInt();
            }
        }
        if (a[0].length == b.length)
            c = new int[a.length][b[0].length];
        else
            c = new int[0][0];

        // thread initialization
        multThread = new MatMultThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            multThread[i] = new MatMultThread(i, threadNum);
        }

        // do parallel matrix multiplication
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            multThread[i].start();
        }

        try {
            for(int i = 0; i < threadNum; i++) {
                multThread[i].join();
            }
        } catch (InterruptedException e) {
        }
        long endTime = System.currentTimeMillis();

        // printing out time and sum
        printMatrix(c);
        System.out.printf("[thread_no]:%2d , [Time]:%4d ms\n", threadNum, endTime - startTime);
    }

    private static void printMatrix(int[][] mat) {
        long sum = 0;
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                sum += mat[i][j];
            }
        }

        System.out.println("Matrix[" + mat.length + "][" + mat[0].length + "]");
        System.out.println("\nMatrix Sum = " + sum + "\n");
    }
}