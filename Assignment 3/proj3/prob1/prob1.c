#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <omp.h>

int isprime(int x);

int main(int argc, char *args[]) {
    int option_num, thread_num;
    int prime_cnt = 0;
    clock_t start_time, end_time;

    if (argc != 3) {    // argument format check
        printf("incorrect argument format");
        return 0;
    }
    else {
        option_num = atoi(args[1]);
        thread_num = atoi(args[2]);
    }

    start_time = clock();
    omp_set_num_threads(thread_num);
    if(option_num == 1) {   // static, default chunk size
        #pragma omp parallel for schedule(static)
        for(int i = 1; i <= 200000; i++) {
            if(isprime(i)) {
                #pragma omp atomic
                prime_cnt++;
            }
        }
    }
    else if(option_num == 2) {  // dynamic, default chunk size
        #pragma omp parallel for schedule(dynamic)
        for(int i = 1; i <= 200000; i++) {
            if(isprime(i)) {
                #pragma omp atomic
                prime_cnt++;
            }
        }
    }
    else if(option_num == 3) { // static, chunk size 10
        #pragma omp parallel for schedule(static, 10)
        for(int i = 1; i <= 200000; i++) {
            if(isprime(i)) {
                #pragma omp atomic
                prime_cnt++;
            }
        }
    }
    else if(option_num == 4) {  // dynamic, chunk size 10
        #pragma omp parallel for schedule(dynamic, 10)
        for(int i = 1; i <= 200000; i++) {
            if(isprime(i)) {
                #pragma omp atomic
                prime_cnt++;
            }
        }
    }
    else {
        printf("Wrong option number\n");
        return 0;
    }
    end_time = clock();

    printf("Total %d prime numbers in 1 ~ 200000\n", prime_cnt);
    printf("Time = %ldms\n", (end_time - start_time) * 1000 / CLOCKS_PER_SEC);

    return 0;
}

int isprime(int x) {
    if(x >= 2) {
        for(int i = 2; i < x; i++) {
            if(x % i == 0) return 0;
        }
        return 1;
    }
    else {
        return 0;
    }
}