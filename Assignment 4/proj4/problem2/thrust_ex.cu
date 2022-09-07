#include <iostream>
#include <chrono>
#include <thrust/device_vector.h>
#include <thrust/transform.h>
#include <thrust/reduce.h>

#define NUM_STEPS 1000000000
#define CHUNK 2 // I break N into 2 chunks because of lack of GPU memory (6GB)
#define STEP (1.0 / NUM_STEPS)

using namespace std::chrono;

struct calc {
    __device__ double operator()(int val) {
        double x = (val + 0.5) * STEP;
        return 4.0 / (1.0 + x * x);
    }
};

int main(void) {
    double pi = 0.0;
    thrust::counting_iterator<int> seq(0);
    thrust::device_vector<double> d_tmp(NUM_STEPS / CHUNK);

    auto start_t = high_resolution_clock::now();
    for(int i = 0; i < CHUNK; i++) {
        thrust::transform(seq + (NUM_STEPS / CHUNK) * i, seq + (NUM_STEPS / CHUNK) * (i + 1), d_tmp.begin(), calc());
        pi += thrust::reduce(d_tmp.begin(), d_tmp.end());
    }
    pi /= NUM_STEPS;
    auto interval = high_resolution_clock::now() - start_t;

    (std::cout << std::fixed).precision(8);
    std::cout << "pi=" << pi << '\n';
    std::cout << "duration: " << duration_cast<milliseconds>(interval).count() << " millisec\n";

    return 0;
}