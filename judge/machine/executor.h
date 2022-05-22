//
// Created by kmh on 2022/5/16.
//

#ifndef MACHINE_EXECUTOR_H
#define MACHINE_EXECUTOR_H

#include <seccomp.h>
#include <unistd.h>
#include <sys/resource.h>
#include <wait.h>
#include <ctime>
#include <iostream>
#include <ctime>


#include "config.h"
#include "result.h"

enum ExecutorError {
    SETRLIMIT_FAILED = -1,

};


class Executor {
private:
    Config config{};
    Result result{};
    int child_pid = 0;

    /**
     * 创建超时守护线程
     * @param seconds
     */
    void start_timeout_guard(int seconds);

    static void *timeout_killer(void *killer_args);

    void applyConfig() const;

    void applySeccompRule();

    void generateSignal();

public:
    Executor(Config &config, Result &result);

    void run();

};

struct KillerArgs {
    pid_t child_pid;
    int seconds;
};


#endif //MACHINE_EXECUTOR_H
