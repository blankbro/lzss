cmake_minimum_required(VERSION 3.26)
project(lzss C)

set(CMAKE_C_STANDARD 11)

add_executable(lzss main.c
        my_lzss.c)
