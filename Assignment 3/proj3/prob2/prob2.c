#include <omp.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>

long num_steps = 10000000; 
double step;

void main(int argc, char* args[]) {
	long i;
    double x, pi, sum = 0.0;
    int idx, sched_type, chunk_size, thr_num;
    clock_t start_time, end_time;

	step = 1.0 / (double)num_steps;
    if(argc != 4) {
        printf("Wrong argument number");
        return;
    }
    else {
        sched_type = atoi(args[1]);
        chunk_size = atoi(args[2]);
        thr_num = atoi(args[3]);
    }

	start_time = clock();
    omp_set_num_threads(thr_num);
    if(sched_type == 1) {   // schedule static
        #pragma omp parallel for    reduction(+:sum)\
                                    schedule(static, chunk_size)\
                                    private(x)
        for(i = 0; i < num_steps; i++) {
            x = (i + 0.5) * step;
            sum = sum + 4.0 / (1.0 + x * x);
        }
    }
    else if(sched_type == 2) {  // schedule dynamic
        #pragma omp parallel for    reduction(+:sum)\
                                    schedule(dynamic, chunk_size)\
                                    private(x)
        for(i = 0; i < num_steps; i++) {
            x = (i + 0.5) * step;
            sum = sum + 4.0 / (1.0 + x * x);
        }
    }
    else if(sched_type == 3) {  // schedule guided
        #pragma omp parallel for    reduction(+:sum)\
                                    schedule(guided, chunk_size)\
                                    private(x)
        for(i = 0; i < num_steps; i++) {
            x = (i + 0.5) * step;
            sum = sum + 4.0 / (1.0 + x * x);
        }
    }
    else {
        printf("wrong schedule type\n");
    }
	
    pi = step * sum;
	end_time = clock();

    printf("Execution Time : %ldms\n", (end_time - start_time) * 1000 / CLOCKS_PER_SEC);
	printf("pi=%.24lf\n", pi);
    
    return;
}