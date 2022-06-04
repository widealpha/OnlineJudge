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
    int sys_call_whitelist[] = {SCMP_SYS(read), SCMP_SYS(fstat),
                                SCMP_SYS(mmap), SCMP_SYS(mprotect),
                                SCMP_SYS(munmap), SCMP_SYS(uname),
                                SCMP_SYS(arch_prctl), SCMP_SYS(brk),
                                SCMP_SYS(access), SCMP_SYS(exit_group),
                                SCMP_SYS(close), SCMP_SYS(readlink),
                                SCMP_SYS(sysinfo), SCMP_SYS(write),
                                SCMP_SYS(writev), SCMP_SYS(lseek),
                                SCMP_SYS(clock_gettime), SCMP_SYS(pread64)};
    for (int sys_call: sys_call_whitelist) {
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, sys_call, 0)) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
    }
    return 0;
}