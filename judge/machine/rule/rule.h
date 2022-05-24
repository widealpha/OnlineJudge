//
// Created by kmh on 2022/5/18.
//

#ifndef MACHINE_RULE_CPP
#define MACHINE_RULE_CPP

#include <seccomp.h>
#include <stdio.h>
#include "../executor_error.h"
#include "../config.h"

enum Language {
    CPP = 0,
    C = 1,
    PYTHON = 2,
    GO = 3,
    JAVA = 4
};

class Rule {
private:
    Language language;
public:
    Rule() : language(Language::CPP) {};

    explicit Rule(Language language) : language(language) {}

    virtual int applyRule(scmp_filter_ctx ctx, const Config *config) {
        printf("%p", ctx);
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(execve), 1,
                             SCMP_A0(SCMP_CMP_EQ, (scmp_datum_t) (config->bin_file.c_str()))) != 0) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(open), 0) != 0) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(dup), 0) != 0) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(dup2), 0) != 0) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
        if (seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(dup3), 0) != 0) {
            return ExecutorError::SET_SECCOMP_FAILED;
        }
        return 0;
    }

};

#endif //MACHINE_RULE_CPP
