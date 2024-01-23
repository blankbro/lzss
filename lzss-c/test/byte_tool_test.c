//
// Created by 李泽鑫 on 2023/11/2.
//

#include "../myimplement/byte_tool.c"

int main() {
    printf("\nhexString -> byteArray: ");
    ByteArray *byteArray = hexStringToByteArray("0123456789ABCDEF00");
    for (size_t i = 0; i < byteArray->size; i++) {
        printf("%02X ", byteArray->bytes[i]);
    }
    printf("\nbyteArray size：%d byte", byteArray->size);

    freeByteArray(byteArray);
    return EXIT_SUCCESS;
}