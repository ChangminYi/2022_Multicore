#include <stdlib.h>
#include <math.h>
#include <cuda_runtime.h>
#include <chrono>
#include <iostream>

#define SPHERES 20
#define BLOCK_SIZE 32
#define GRID_SIZE 64

#define rnd(x) (x * rand() / RAND_MAX)
#define INF 2e10f
#define DIM 2048

using namespace std;
using namespace std::chrono;

struct Sphere {
    float r, b, g;
    float radius;
    float x, y, z;

    // hit function will be executed in gpu
    __device__ float hit(float ox, float oy, float *n) {
        float dx = ox - this->x;
        float dy = oy - this->y;
        if (dx * dx + dy * dy < this->radius * this->radius) {
            float dz = sqrtf(this->radius * this->radius - dx * dx - dy * dy);
            *n = dz / sqrtf(this->radius * this->radius);
            return dz + this->z;
        }
        return -INF;
    }
};

extern const uint3 threadIdx, blockIdx;

__global__ void kernel(Sphere *s, unsigned char *ptr) {
    int tx = threadIdx.x + blockIdx.x * BLOCK_SIZE;
    int ty = threadIdx.y + blockIdx.y * BLOCK_SIZE;
    int tid = tx + ty * DIM;
    float ox = (tx - DIM / 2);
    float oy = (ty - DIM / 2);

    float r = 0, g = 0, b = 0;
    float maxz = -INF;
    for (int i = 0; i < SPHERES; i++) {
        float n;
        float t = s[i].hit(ox, oy, &n);
        if (t > maxz) {
            float fscale = n;
            r = s[i].r * fscale;
            g = s[i].g * fscale;
            b = s[i].b * fscale;
            maxz = t;
        }
    }

    ptr[tid * 4 + 0] = (int)(r * 255);
    ptr[tid * 4 + 1] = (int)(g * 255);
    ptr[tid * 4 + 2] = (int)(b * 255);
    ptr[tid * 4 + 3] = 255;
}

void ppm_write(unsigned char *bitmap, int xdim, int ydim, FILE *fp) {
    int i, x, y;
    fprintf(fp, "P3\n");
    fprintf(fp, "%d %d\n", xdim, ydim);
    fprintf(fp, "255\n");
    for (y = 0; y < ydim; y++) {
        for (x = 0; x < xdim; x++) {
            i = x + y * xdim;
            fprintf(fp, "%d %d %d ", bitmap[4 * i], bitmap[4 * i + 1], bitmap[4 * i + 2]);
        }
        fprintf(fp, "\n");
    }
}

int main(void) {
    FILE *fp;
    Sphere *temp_s, *dev_temp_s;
    unsigned char *bitmap, *dev_bitmap;
    // dimension size of cuda processing
    dim3 grid_size = { GRID_SIZE, GRID_SIZE, 1 };
    dim3 block_size = { BLOCK_SIZE, BLOCK_SIZE, 1 };

    srand(time(NULL));

    fp = fopen("result.ppm", "w");
    temp_s = (Sphere *)malloc(sizeof(Sphere) * SPHERES);
    for (int i = 0; i < SPHERES; i++) {
        temp_s[i].r = rnd(1.0f), temp_s[i].g = rnd(1.0f), temp_s[i].b = rnd(1.0f);
        temp_s[i].x = rnd(2000.0f) - 1000, temp_s[i].y = rnd(2000.0f) - 1000, temp_s[i].z = rnd(2000.0f) - 1000;
        temp_s[i].radius = rnd(200.0f) + 40;
    }
    bitmap = (unsigned char *)malloc(sizeof(unsigned char) * DIM * DIM * 4);

    auto start_t = high_resolution_clock::now();
    cudaMalloc((void**)&dev_temp_s, sizeof(Sphere) * SPHERES);
    cudaMalloc((void**)&dev_bitmap, sizeof(unsigned char) * DIM * DIM * 4);
    cudaMemcpy(dev_temp_s, temp_s, sizeof(Sphere) * SPHERES, cudaMemcpyHostToDevice);
    kernel<<<grid_size, block_size>>>(dev_temp_s, dev_bitmap);
    cudaMemcpy(bitmap, dev_bitmap, sizeof(unsigned char) * DIM * DIM * 4, cudaMemcpyDeviceToHost);
    auto interval = high_resolution_clock::now() - start_t;
    
    ppm_write(bitmap, DIM, DIM, fp);
    fclose(fp);

    cudaFree(dev_temp_s); cudaFree(dev_bitmap);
    free(bitmap); free(temp_s);

    cout << "CUDA ray tracing: " << duration_cast<milliseconds>(interval).count() << " millisec\n";
    cout << "[result.ppm] was generated.\n";

    return 0;
}