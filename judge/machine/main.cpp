#include <iostream>
#include "executor.h"
#include "cmdline.h"
#include "rule/rule.h"

bool match_arguments(int argc, char *argv[], Config &config);

int main(int argc, char *argv[]) {
    Config config{};
    if (!match_arguments(argc, argv, config)) {
        return 0;
    }
    Result result{};
    Executor executor(&config, &result);
    executor.run();
    std::cout << result << std::endl;
}

bool match_arguments(int argc, char *argv[], Config &config) {
    cmdline::parser parser;
    parser.add<std::string>("bin-file", 'b', "executable file to run", true);
    parser.add<std::string>("input-file", 'i', "binary input redirect from this file", false);
    parser.add<std::string>("output-file", 'o', "binary output redirect to this file", false);
    parser.add<std::string>("error-file", 'e', "binary error redirect to this file", false);
    parser.add<int>("rule", 'r', "seccomp rule", false, UNLIMITED);
    parser.add<int>("max-real-time", '\0', "max real time use(ms)", false, UNLIMITED, cmdline::range(0, 200 * 1000));
    parser.add<int>("max-cpu-time", '\0', "max cpu time use(ms)", false, UNLIMITED, cmdline::range(0, 200 * 1000));
    parser.add<int>("max-memory", '\0', "max memory use(KB)", false, UNLIMITED, cmdline::range(0, 65535));
    parser.add<int>("max-stack-size", '\0', "max stack use(KB)", false, UNLIMITED, cmdline::range(0, 65535));
    parser.add<int>("max-output-size", '\0', "max stack use(KB)", false, UNLIMITED, cmdline::range(0, 65535));
    parser.add<int>("num-threads", '\0', "max thread num", false, 1, cmdline::range(1, 8));
    parser.add<std::string>("args", '\0', "executable arguments", false);
    parser.add<std::string>("env", '\0', "executable environment", false);
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
            parser.get<int>("num-threads"),
    };
    config.limit = limit;
    config.rule = parser.get<int>("rule");
    config.bin_file = parser.get<std::string>("bin-file");
    config.input_file = parser.get<std::string>("input-file");
    config.output_file = parser.get<std::string>("output-file");
    config.error_file = parser.get<std::string>("error-file");
    config.args[0] = (char *) config.bin_file.c_str();
    std::vector<std::string> rest = parser.rest();
    for (int i = 1; i < rest.size() + 1; ++i) {
        config.args[i] = (char *) rest[i].c_str();
    }
    return true;
}
