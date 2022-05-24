//
// Created by kmh on 2022/5/15.
//

#ifndef MACHINE_LIMIT_H
#define MACHINE_LIMIT_H
#define UNLIMITED -1

struct Limit {
    int max_real_time; //ms
    int max_cpu_time; //ms
    int max_memory; //KB
    int max_stack_size; //KB
    int max_output_size; //KB
    int num_thread;
};


#endif //MACHINE_LIMIT_H
