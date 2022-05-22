//
// Created by kmh on 2022/5/18.
//

#ifndef MACHINE_RULE_H
#define MACHINE_RULE_H

#include <seccomp.h>

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

};


#endif //MACHINE_RULE_H
