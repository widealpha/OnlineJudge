//
// Created by kmh on 2022/5/16.
//

#include "executor.h"

Executor::Executor(Config *config, Result *result) {
    this->config = config;
    this->result = result;
}

void Executor::run() {
    //fork当前进程,进行资源约束和跟踪
    child_pid = fork();
    if (child_pid < 0) {
        exit(ExecutorError::FORK_FAILED);
    }
    //parent process
    if (child_pid != 0) {
        int status;
        rusage resource_usage{};
        start_timeout_guard(config->limit.max_real_time / 1000 + 1);
        clock_t start = clock();
        if (wait4(child_pid, &status, WSTOPPED, &resource_usage) == -1) {
            kill(child_pid, SIGKILL);
        }
        this->result->update(resource_usage);
        this->result->real_time = std::max(clock() - start, result->cpu_time);
        this->result->exit_code = status;
        generateSignal();
        return;
    } else {
        applyConfig();
        //todo applySeccompRule
        applySeccompRule();
        execve(config->bin_file.c_str(), config->args, config->env);
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

void Executor::applyConfig() const {
    Limit limit = config->limit;
    if (limit.max_stack_size != UNLIMITED) {
        struct rlimit max_stack{static_cast<rlim_t>(limit.max_stack_size), static_cast<rlim_t>(limit.max_stack_size)};
        if (setrlimit(RLIMIT_STACK, &max_stack) != 0) {
            exit(SETRLIMIT_FAILED);
        }
    }

    if (limit.max_memory != UNLIMITED) {
        struct rlimit max_memory{
                static_cast<rlim_t>(limit.max_memory) * 2,
                static_cast<rlim_t>(limit.max_memory) * 2};
        if (setrlimit(RLIMIT_AS, &max_memory) != 0) {
            exit(SETRLIMIT_FAILED);
        }
    }

    // set cpu time limit (in seconds)
    if (limit.max_cpu_time != UNLIMITED) {
        rlim_t time = limit.max_cpu_time / 1000 + 1;
        struct rlimit max_cpu_time{time, time};
        if (setrlimit(RLIMIT_CPU, &max_cpu_time) != 0) {
            exit(SETRLIMIT_FAILED);
        }
    }

    // set max process number limit
    if (limit.num_thread != UNLIMITED) {
        struct rlimit max_process_number{static_cast<rlim_t>(limit.num_thread), static_cast<rlim_t>(limit.num_thread)};
        if (setrlimit(RLIMIT_NPROC, &max_process_number) != 0) {
            exit(SETRLIMIT_FAILED);
        }
    }

    // set max output size limit
    if (limit.max_output_size != UNLIMITED) {
        struct rlimit max_output_size{static_cast<rlim_t>(limit.max_output_size),
                                      static_cast<rlim_t>(limit.max_output_size)};
        if (setrlimit(RLIMIT_FSIZE, &max_output_size) != 0) {
            exit(SETRLIMIT_FAILED);
        }
    }
    //redirect input/output/error file
    if (!config->input_file.empty()) {
        freopen(config->input_file.c_str(), "r", stdin);
    }
    if (!config->output_file.empty()) {
        freopen(config->output_file.c_str(), "w", stdout);
    }
    if (!config->error_file.empty()) {
        freopen(config->error_file.c_str(), "w", stderr);
    }
}

void Executor::applySeccompRule() {
    Rule *rule_config;
    switch (config->rule) {
        case UNLIMITED:
            return;
        case 0:
            rule_config = new CppRule();
            break;
        default:
            rule_config = new CppRule();
    }
    //kill program if it uses deny syscall
    scmp_filter_ctx ctx = seccomp_init(SCMP_ACT_KILL);
    if (rule_config->applyRule(ctx, config) != 0 || seccomp_load(ctx) != 0) {
        exit(ExecutorError::SET_SECCOMP_FAILED);
    }
    seccomp_release(ctx);
    delete rule_config;
}

void Executor::generateSignal() {
    if (config->limit.max_memory != UNLIMITED && result->memory > config->limit.max_memory) {
        result->signal = Signal::MEMORY_LIMIT_EXCEEDED;
    } else if (config->limit.max_cpu_time != UNLIMITED && result->cpu_time > config->limit.max_cpu_time) {
        result->signal = Signal::CPU_TIME_LIMIT_EXCEEDED;
    } else if (config->limit.max_output_size != UNLIMITED && result->output > config->limit.max_output_size) {
        result->signal = Signal::OUTPUT_LIMIT_EXCEEDED;
    } else if (result->exit_code == SIGKILL) {
        result->signal = Signal::REAL_TIME_LIMIT_EXCEEDED;
    } else if (result->exit_code == 0) {
        result->signal = Signal::SUCCESS;
    } else if (result->exit_code < 0) {
        result->signal = Signal::RUNTIME_ERROR;
    } else {
        result->signal = Signal::SYSTEM_ERROR;
    }
}


