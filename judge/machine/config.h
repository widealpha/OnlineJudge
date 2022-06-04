//
// Created by kmh on 2022/5/16.
//

#ifndef MACHINE_CONFIG_H
#define MACHINE_CONFIG_H

#define MAX_ARGS_COUNT 255
#define MAX_ENV_COUNT 255

#include "limit.h"
#include <string>

struct Config {
    Limit limit;
    std::string bin_file;
    std::string input_file;
    std::string output_file;
    std::string error_file;
    int rule;
    char *args[MAX_ARGS_COUNT] = {};
    char *env[MAX_ENV_COUNT] = {};
};

#endif //MACHINE_CONFIG_H
