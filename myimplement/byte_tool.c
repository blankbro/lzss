//
// Created by 李泽鑫 on 2023/11/2.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef unsigned char Byte;

typedef struct ByteArray {
    Byte *bytes;
    int size;
} ByteArray;

int hexCharToInt(char c) {
    if (c >= '0' && c <= '9')
        return c - '0';
    else if (c >= 'A' && c <= 'F')
        return c - 'A' + 10;
    else if (c >= 'a' && c <= 'f')
        return c - 'a' + 10;
    else {
        printf("\n非法十六进制字符: %c", c);
        return -1;
    }
}

ByteArray *hexStringToByteArray(const char *hexString) {
    size_t hexLen = strlen(hexString);
    if (hexLen % 2 != 0) {
        printf("非法十六进制字符串或字节数组容量不足");
        exit(EXIT_FAILURE);
    }

    unsigned char *bytes = malloc(sizeof(char) * hexLen / 2);
    for (size_t i = 0; i < hexLen / 2; i++) {
        int highNibble = hexCharToInt(hexString[i * 2]);
        int lowNibble = hexCharToInt(hexString[i * 2 + 1]);

        if (highNibble == -1 || lowNibble == -1)
            exit(EXIT_FAILURE);

        bytes[i] = (highNibble << 4) | lowNibble;
    }

    ByteArray *byteArray = malloc(sizeof(ByteArray));
    byteArray->bytes = bytes;
    byteArray->size = hexLen / 2;

    return byteArray;
}

char intToHexChar(int i) {
    if (i >= 0 && i <= 9) {
        return (char) ('0' + i);
    } else if (i >= 10 && i <= 15) {
        return (char) ('A' + i - 10);
    } else {
        printf("\n非法十进制数字: %d, 无法转成十六进制", i);
        return '?';
    }
}

char *byteArrayToHexString(ByteArray *byteArray) {
    int stringLength = sizeof(char) * byteArray->size * 2 + 1;
    char *hexString = malloc(stringLength);
    for (int i = 0; i < byteArray->size; ++i) {
        Byte byte = byteArray->bytes[i];
        char c1 = intToHexChar(byte >> 4);
        char c2 = intToHexChar(byte & 0x0F);
        hexString[i * 2] = c1;
        hexString[i * 2 + 1] = c2;
    }
    hexString[stringLength - 1] = '\0';
    return hexString;
}

void freeByteArray(ByteArray *byteArray) {
    free(byteArray->bytes);
    free(byteArray);
}
