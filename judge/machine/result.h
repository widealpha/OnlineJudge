//
// Created by kmh on 2022/5/16.
//

#ifndef MACHINE_RESULT_H
#define MACHINE_RESULT_H

#include <ostream>

enum Signal {
    SUCCESS = 0,
    CPU_TIME_LIMIT_EXCEEDED = 1,
    REAL_TIME_LIMIT_EXCEEDED = 2,
    MEMORY_LIMIT_EXCEEDED = 3,
    OUTPUT_LIMIT_EXCEEDED = 4,
    RUNTIME_ERROR = 5,
    SYSTEM_ERROR = 6
};

struct Result {
    void update(rusage usage) {
        this->memory = usage.ru_maxrss;
        this->cpu_time = (usage.ru_utime.tv_sec + usage.ru_stime.tv_sec) * 1000
                         + (usage.ru_utime.tv_usec + usage.ru_stime.tv_usec) / 1000;
        this->output = usage.ru_oublock;
    }

    long cpu_time; //ms
    long real_time; //ms
    long memory; //KB
    bool output; //output size
    bool error; //error size
    int signal; //Signal enum
    int exit_code;

    friend std::ostream &operator<<(std::ostream &os, const Result &result) {
        os << '{' <<
           "\"cpu_time\":" << result.cpu_time << ',' <<
           "\"real_time\":" << result.real_time << ',' <<
           "\"memory\":" << result.memory << ',' <<
           "\"output\":" << result.output << ',' <<
           "\"error\":" << result.error << ',' <<
           "\"signal\":" << result.signal << ',' <<
           "\"exit_code\":" << result.exit_code << '}';
        return os;
    }
};


#endif //MACHINE_RESULT_H
