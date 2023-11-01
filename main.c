#include <stdio.h>
#include "lzss.c"

int main_encode() {
    // 模拟命令行参数
    char *arguments[] = {"lzss", "e", "/Users/lizexin/projects/github/other/lzss/input.txt",
                         "/Users/lizexin/projects/github/other/lzss/output.txt"};

    // 将模拟的命令行参数传递给 main 方法
    int argc = sizeof(arguments) / sizeof(arguments[0]);
    char **argv = arguments;

    // 调用 main 方法
    int result = run(argc, argv);
    return result;
}

int main_decode() {
    // 模拟命令行参数
    char *arguments[] = {"lzss", "d", "/Users/lizexin/projects/github/other/lzss/output.txt",
                         "/Users/lizexin/projects/github/other/lzss/output_output.txt"};

    // 将模拟的命令行参数传递给 main 方法
    int argc = sizeof(arguments) / sizeof(arguments[0]);
    char **argv = arguments;

    // 调用 main 方法
    int result = run(argc, argv);
    return result;
}

int main() {
    printf("Hello, World!\n");

    main_encode();
    main_decode();
}
