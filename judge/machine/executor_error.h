//
// Created by kmh on 2022/5/23.
//

#ifndef MACHINE_EXECUTOR_ERROR_H
#define MACHINE_EXECUTOR_ERROR_H

enum ExecutorError {
    SETRLIMIT_FAILED = -1,
    FORK_FAILED = -2,
    SET_SECCOMP_FAILED = -3,
};

#endif //MACHINE_EXECUTOR_ERROR_H
