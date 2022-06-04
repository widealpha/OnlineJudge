#include <iostream>
#include <stack>
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

void string2argv(const std::string &str, char *argv[], int max_length) {
    std::vector<char *> args;
    std::istringstream iss(str);

    std::string token;
    while (iss >> token) {
        char *arg = new char[token.size() + 1];
        copy(token.begin(), token.end(), arg);
        arg[token.size()] = '\0';
        args.push_back(arg);
    }
    args.push_back(nullptr);
    if (args.size() > max_length){
        for (auto  arg: args) {
            delete[] arg;
        }
        return;
    }
    for (int i = 0; i < args.size(); ++i) {
        argv[i] = args.at(i);
    }
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
    string2argv(parser.get<std::string>("args"), &config.args[1], 254);
    string2argv(parser.get<std::string>("env"), config.env, 254);
    return true;
}
