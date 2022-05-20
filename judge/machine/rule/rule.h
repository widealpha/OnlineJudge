//
// Created by kmh on 2022/5/18.
//

#ifndef MACHINE_RULE_H
#define MACHINE_RULE_H

#include <seccomp.h>

enum Language {
    CPP,
    C,
    PYTHON,
    GO,
    JAVA
};

class Rule {
private:
    Language language;

};


#endif //MACHINE_RULE_H
