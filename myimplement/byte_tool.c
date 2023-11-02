//
// Created by 李泽鑫 on 2023/11/2.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int hexCharToInt(char c) {
    if (c >= '0' && c <= '9')
        return c - '0';
    else if (c >= 'A' && c <= 'F')
        return c - 'A' + 10;
    else if (c >= 'a' && c <= 'f')
        return c - 'a' + 10;
    else
        return -1; // 非法字符
}

int hexStringToByteArray(const char *hexString, unsigned char *byteArray, size_t byteArraySize) {
    size_t hexLen = strlen(hexString);
    if (hexLen % 2 != 0 || hexLen / 2 > byteArraySize)
        return EXIT_FAILURE; // 非法十六进制字符串或字节数组容量不足

    for (size_t i = 0; i < hexLen / 2; i++) {
        int highNibble = hexCharToInt(hexString[i * 2]);
        int lowNibble = hexCharToInt(hexString[i * 2 + 1]);

        if (highNibble == -1 || lowNibble == -1)
            return EXIT_FAILURE; // 非法十六进制字符

        byteArray[i] = (highNibble << 4) | lowNibble;
    }

    return EXIT_SUCCESS; // 转换成功
}

size_t getByteArrayLength(const unsigned char *array) {
    size_t length = 0;
    while (array[length] != '\0') {
        length++;
    }
    return length;
}
