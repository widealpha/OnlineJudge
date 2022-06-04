cmake_minimum_required(VERSION 3.16)
project(machine)

set(CMAKE_CXX_STANDARD 14)

file(GLOB rules "rule/*.cpp")
add_library(rule-lib STATIC ${rules})

add_executable(machine main.cpp executor.cpp)
target_link_libraries(machine pthread seccomp rule-lib)

add_executable(hello demo/hello.cpp)
