//
// Created by kmh on 2022/5/16.
//

#ifndef MACHINE_CONFIG_H
#define MACHINE_CONFIG_H

#include "limit.h"

struct Config {
    Limit limit;
    std::string bin_file;
    std::string input_file;
    std::string output_file;
    std::string error_file;
    char **argv;
    char **env;
};

#endif //MACHINE_CONFIG_H
