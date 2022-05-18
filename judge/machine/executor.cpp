//
// Created by kmh on 2022/5/16.
//

#include "executor.h"

Executor::Executor(Config &config, Result &result) {
    this->config = config;
    this->result = result;
}

void Executor::run() {
    //fork当前进程,进行资源约束和跟踪
    child_pid = fork();
    //parent process
    if (child_pid != 0) {
        int status;
        rusage resource_usage{};
        start_timeout_guard(config.limit.max_real_time / 1000 + 1);
        if (wait4(child_pid, &status, WSTOPPED, &resource_usage) == -1) {
            kill(child_pid, SIGKILL);
        }
        this->result.update(resource_usage);
        this->result.real_time = 0;
        this->result.exit_code = status;
        this->result.signal = Signal::SUCCESS;
        std::cout << result << std::endl;
        return;
    } else {
        //redirect input/output/error file
        if (!config.input_file.empty()) {
            freopen(config.input_file.c_str(), "r", stdin);
        }
        if (!config.output_file.empty()) {
            freopen(config.output_file.c_str(), "w", stdout);
        }
        if (!config.error_file.empty()) {
            freopen(config.error_file.c_str(), "w", stderr);
        }
        execve(config.bin_file.c_str(), config.argv, config.env);
    }
}

void *Executor::timeout_killer(void *killer_args) {
    sleep(((KillerArgs *) killer_args)->seconds);
    kill(((KillerArgs *) killer_args)->child_pid, SIGKILL);
    return nullptr;
}

void Executor::start_timeout_guard(int seconds) {
    pthread_t tid;
    KillerArgs killer_args = {child_pid, seconds};
    //创建超时守护线程失败,直接杀死子进程
    if (pthread_create(&tid, nullptr, timeout_killer, &killer_args) != 0) {
        kill(child_pid, SIGKILL);
    }
}
