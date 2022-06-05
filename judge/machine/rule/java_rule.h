//
// Created by kmh on 2022/6/3.
//

#ifndef MACHINE_JAVA_RULE_H
#define MACHINE_JAVA_RULE_H

#include "rule.h"

class JavaRule : public Rule {
public:
    JavaRule();

    int applyRule(scmp_filter_ctx ctx, const Config *config) override;
};



#endif //MACHINE_JAVA_RULE_H
