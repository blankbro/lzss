#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../reference/lzss.c"

#define MAX_PATH_LENGTH 256

void generate_full_filename(const char *filename, const char *full_filename, size_t full_filename_size) {
    // 获取当前源文件的路径
    char currentFilePath[MAX_PATH_LENGTH];
    strcpy(currentFilePath, __FILE__);

    // 获取当前源文件的目录
    char *lastSeparator = strrchr(currentFilePath, '/');
    if (lastSeparator != NULL) {
        *lastSeparator = '\0';  // 截断最后一个斜杠后的部分
    }

    // 拼装新文件的路径
    snprintf(full_filename, full_filename_size, "%s/%s", currentFilePath, filename);

    // 输出新文件的路径
    printf("Full file path: %s\n", full_filename);

}

int main_encode(char *input, char *output) {
    // 模拟命令行参数
    char *arguments[] = {"lzss", "e", input, output};

    // 将模拟的命令行参数传递给 main 方法
    int argc = sizeof(arguments) / sizeof(arguments[0]);
    char **argv = arguments;

    // 调用 main 方法
    int result = run(argc, argv);
    return result;
}

int main_decode(char *input, char *output) {
    // 模拟命令行参数
    char *arguments[] = {"lzss", "d", input, output};

    // 将模拟的命令行参数传递给 main 方法
    int argc = sizeof(arguments) / sizeof(arguments[0]);
    char **argv = arguments;

    // 调用 main 方法
    int result = run(argc, argv);
    return result;
}

int main() {
    printf("Hello, World!\n");

    char input_full_name[MAX_PATH_LENGTH];
    generate_full_filename("input.txt", input_full_name, sizeof(input_full_name));

    char output_full_name[MAX_PATH_LENGTH];
    generate_full_filename("output.txt", output_full_name, sizeof(output_full_name));

    char output_output_full_name[MAX_PATH_LENGTH];
    generate_full_filename("output_output.txt", output_output_full_name, sizeof(output_output_full_name));

    main_encode(input_full_name, output_full_name);
    main_decode(output_full_name, output_output_full_name);
}
