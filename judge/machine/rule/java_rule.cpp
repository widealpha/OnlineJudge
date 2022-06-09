//
// Created by kmh on 2022/6/3.
//

#include "java_rule.h"

JavaRule::JavaRule(): Rule(Language::JAVA) {}

int JavaRule::applyRule(scmp_filter_ctx ctx, const Config *config) {
    //允许调用execve方法
    int status = Rule::applyRule(ctx, config);
    if (status != 0) {
        return status;
    }
    int sys_call_whitelist[] = {  SCMP_SYS(access),
                                    SCMP_SYS(arch_prctl),
                                    SCMP_SYS(brk),
                                    SCMP_SYS(clock_getres),
                                    SCMP_SYS(clone),
                                    SCMP_SYS(close),
                                    SCMP_SYS(connect),
                                    SCMP_SYS(execve),
                                    SCMP_SYS(exit_group),
                                    SCMP_SYS(fchdir),
                                    SCMP_SYS(fcntl),
                                    SCMP_SYS(fstat),
                                    SCMP_SYS(ftruncate),
                                    SCMP_SYS(futex),
                                    SCMP_SYS(getcwd),
                                    SCMP_SYS(getdents),
                                    SCMP_SYS(geteuid),
                                    SCMP_SYS(getpid),
                                    SCMP_SYS(gettid),
                                    SCMP_SYS(getuid),
                                    SCMP_SYS(kexec_load),
                                    SCMP_SYS(kill),
                                    SCMP_SYS(lseek),
                                    SCMP_SYS(lstat),
                                    SCMP_SYS(mkdir),
                                    SCMP_SYS(mmap),
                                    SCMP_SYS(mprotect),
                                    SCMP_SYS(munmap),
                                    SCMP_SYS(openat),
                                    SCMP_SYS(prctl),
                                    SCMP_SYS(pread64),
                                    SCMP_SYS(prlimit64),
                                    SCMP_SYS(pselect6),
                                    SCMP_SYS(read),
                                    SCMP_SYS(readlink),
                                    SCMP_SYS(rt_sigaction),
                                    SCMP_SYS(rt_sigprocmask),
                                    SCMP_SYS(rt_sigreturn),
                                    SCMP_SYS(sched_getaffinity),
                                    SCMP_SYS(sched_yield),
                                    SCMP_SYS(set_robust_list),
                                    SCMP_SYS(set_tid_address),
                                    SCMP_SYS(socket),
                                    SCMP_SYS(stat),
                                    SCMP_SYS(sysinfo),
                                    SCMP_SYS(uname),
                                    SCMP_SYS(unlink),
                                    SCMP_SYS(write)};
    for (int sys_call: sys_call_whitelist) {
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, sys_call, 0)) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
    }
    return 0;
}