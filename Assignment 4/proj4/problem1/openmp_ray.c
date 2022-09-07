#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <omp.h>

#define SPHERES 20

#define rnd(x) (x * rand() / RAND_MAX)
#define INF 2e10f
#define DIM 2048

struct Sphere {
    float r, b, g;
    float radius;
    float x, y, z;
};

// c language can't define functions in structure, so it has been seperated
float hit(struct Sphere *sp, float ox, float oy, float *n) {
    float dx = ox - sp->x;
    float dy = oy - sp->y;
    if (dx * dx + dy * dy < sp->radius * sp->radius) {
        float dz = sqrtf(sp->radius * sp->radius - dx * dx - dy * dy);
        *n = dz / sqrtf(sp->radius * sp->radius);
        return dz + sp->z;
    }
    return -INF;
}

// calculating function
void kernel(int x, int y, struct Sphere *s, unsigned char *ptr) {
    int offset = x + y * DIM;
    float ox = (x - DIM / 2);
    float oy = (y - DIM / 2);

    float r = 0, g = 0, b = 0;
    float maxz = -INF;
    for (int i = 0; i < SPHERES; i++) {
        float n;
        float t = hit(&s[i], ox, oy, &n);
        if (t > maxz) {
            float fscale = n;
            r = s[i].r * fscale;
            g = s[i].g * fscale;
            b = s[i].b * fscale;
            maxz = t;
        }
    }

    ptr[offset * 4 + 0] = (int)(r * 255);
    ptr[offset * 4 + 1] = (int)(g * 255);
    ptr[offset * 4 + 2] = (int)(b * 255);
    ptr[offset * 4 + 3] = 255;
}

// write result file to disk
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

int main(int argc, char *argv[]) {
    int thread_num;
    unsigned char *bitmap;
    FILE *fp;
    clock_t start_t, interval;

    srand(time(NULL));

    if (argc != 2) {
        printf("> a.out [option]\n");
        printf("[option] 1~16: OpenMP using 1~16 threads\n");
        printf("for example, '> a.out 8' means executing OpenMP with 8 threads\n");
        exit(0);
    }
    fp = fopen("result.ppm", "w");
    thread_num = atoi(argv[1]);
    omp_set_num_threads(thread_num);    // set thread number with given input

    struct Sphere *temp_s = (struct Sphere *)malloc(sizeof(struct Sphere) * SPHERES);
    for (int i = 0; i < SPHERES; i++) {
        temp_s[i].r = rnd(1.0f), temp_s[i].g = rnd(1.0f), temp_s[i].b = rnd(1.0f);
        temp_s[i].x = rnd(2000.0f) - 1000, temp_s[i].y = rnd(2000.0f) - 1000, temp_s[i].z = rnd(2000.0f) - 1000;
        temp_s[i].radius = rnd(200.0f) + 40;
    }
    bitmap = (unsigned char *)malloc(sizeof(unsigned char) * DIM * DIM * 4);
    
    start_t = clock();
    #pragma omp parallel for schedule(guided)   // using guided scheduling & for statement unrolled
    for (int i = 0; i < DIM * DIM; i++) {
        kernel(i / DIM, i % DIM, temp_s, bitmap);
    }
    interval = clock() - start_t;

    ppm_write(bitmap, DIM, DIM, fp);
    fclose(fp);
    
    free(bitmap); free(temp_s);

    printf("OpenMP (%d thread%s) ray tracing: %d milisec\n", thread_num, thread_num == 1 ? "" : "s", (1000 * interval) / CLOCKS_PER_SEC);
    printf("[result.ppm] was generated.\n");

    return 0;
}