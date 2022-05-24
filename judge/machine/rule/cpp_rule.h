//
// Created by kmh on 2022/5/23.
//

#ifndef MACHINE_CPP_RULE_H
#define MACHINE_CPP_RULE_H

#include "rule.h"

class CppRule : public Rule {
public:
    CppRule();

    int applyRule(scmp_filter_ctx ctx, const Config *config) override;
};


#endif //MACHINE_CPP_RULE_H
