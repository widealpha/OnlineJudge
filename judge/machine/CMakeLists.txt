cmake_minimum_required(VERSION 3.21)
project(machine)

set(CMAKE_CXX_STANDARD 14)

add_executable(machine main.cpp executor.cpp)
target_link_libraries(machine pthread seccomp)

add_executable(hello demo/hello.cpp)
