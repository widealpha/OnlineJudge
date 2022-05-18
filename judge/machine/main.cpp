#include <iostream>
#include "executor.h"
#include "cmdline.h"

bool match_arguments(int argc, char *argv[], Config &config);

int main(int argc, char *argv[]) {
    Config config{};
    if (!match_arguments(argc, argv, config)) {
        return 0;
    }
    Limit limit{};
    limit.max_real_time = 2000;
    limit.max_cpu_time = 1000;
    config.limit = limit;
    Result result{};
    Executor executor(config, result);
    executor.run();
}

bool match_arguments(int argc, char *argv[], Config &config) {
    cmdline::parser parser;
    parser.add<std::string>("bin-file", 'b', "binary file to run", true);
    parser.add<std::string>("input-file", 'i', "binary input redirect from this file", false);
    parser.add<std::string>("output-file", 'o', "binary output redirect to this file", false);
    parser.add<std::string>("error-file", 'e', "binary error redirect to this file", false);
    parser.add<int>("max-real-time", '\0', "max real time use(ms)", false);
    parser.add<int>("max-cpu-time", '\0', "max cpu time use(ms)", false);
    parser.add<int>("max-memory", '\0', "max memory use(KB)", false, 2048, cmdline::range(0, 65535));
    parser.add<int>("max-stack-size", '\0', "max stack use(KB)", false, 2048, cmdline::range(0, 65535));
    parser.add<int>("max-output-size", '\0', "max stack use(KB)", false, 2048, cmdline::range(0, 65535));
    parser.parse_check(argc, argv);
    if (!parser.parse(argc, argv)) {
        return false;
    }
    Limit limit = Limit{
            parser.get<int>("max-real-time"),
            parser.get<int>("max-cpu-time"),
            parser.get<int>("max-memory"),
            parser.get<int>("max-stack-size"),
            parser.get<int>("max-output-size"),
    };
    config.limit = limit;
    config.bin_file = parser.get<std::string>("bin-file");
    config.input_file = parser.get<std::string>("input-file");
    config.output_file = parser.get<std::string>("output-file");
    config.error_file = parser.get<std::string>("error-file");
    return true;
}
