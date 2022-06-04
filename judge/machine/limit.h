//
// Created by kmh on 2022/5/15.
//

#ifndef MACHINE_LIMIT_H
#define MACHINE_LIMIT_H

#include <ostream>

#define UNLIMITED -1

struct Limit {
    int max_real_time; //ms
    int max_cpu_time; //ms
    int max_memory; //KB
    int max_stack_size; //KB
    int max_output_size; //KB
    int num_thread;

    friend std::ostream &operator<<(std::ostream &os, const Limit &limit) {
        os << "max_real_time: " << limit.max_real_time << " max_cpu_time: " << limit.max_cpu_time << " max_memory: "
           << limit.max_memory << " max_stack_size: " << limit.max_stack_size << " max_output_size: "
           << limit.max_output_size << " num_thread: " << limit.num_thread;
        return os;
    }
};


#endif //MACHINE_LIMIT_H
