//
// Created by 李泽鑫 on 2023/11/2.
//

#include "../myimplement/byte_tool.c"

int main() {
    const char *hexString = "0123456789ABCDEF00";
    size_t byteArraySize = strlen(hexString) / 2;
    unsigned char *byteArray = (unsigned char *) malloc(byteArraySize);

    if (hexStringToByteArray(hexString, byteArray, byteArraySize) == EXIT_FAILURE) {
        printf("转换失败\n");

        free(byteArray);
        return EXIT_FAILURE;
    }

    printf("转换成功：");
    for (size_t i = 0; i < byteArraySize; i++) {
        printf("%02X ", byteArray[i]);
    }
    printf("\n字节长度：%d byte\n", byteArraySize);

    free(byteArray);
    return EXIT_SUCCESS;
}