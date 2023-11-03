//
// Created by 李泽鑫 on 2023/11/2.
//

#include "../myimplement/lzss.c"

void test(const char *hexString) {
    size_t byteArraySize = strlen(hexString) / 2;
    unsigned char *byteArray = (unsigned char *) malloc(byteArraySize);

    if (hexStringToByteArray(hexString, byteArray, byteArraySize) == EXIT_SUCCESS) {
        printf("转换成功：");
        for (size_t i = 0; i < byteArraySize; i++) {
            printf("%02X ", byteArray[i]);
        }
        printf("\n字节长度：%zu byte\n", byteArraySize);

        encode(byteArray, byteArraySize);
    } else {
        printf("转换失败\n");
    }

    free(byteArray);
    printf("=============================================\n");
}

int main() {
    test("018B8F3898A504E2074B004902D000C800C8000005A0000005A0640003072710286E27D84A494E200096000004031D7402DF0A4700BF0148FFFF374445435632314A37433030343330303032303137350800000000");
    test("018B8F38949A04E1144B0101374445435632314A37433030343300084A4A4A4A4948494A0307271000140000140F240F290F280F270F250F2C0F2F0F2B0F300F2F0F220F220F230F250F220F190F1A0F1E0F1A0F1B");
    test("018B8F39167600451D040A018B8F391676004504003136393839313439353035303420462042696173203131343032383320302E30333830363720302E303032303137202D302E303030383634202D302E30323639333420302E30333636353920332E31323631303120302E3236343332350A31363938393134393531323936204620424D53204F75743A3130302E302C39312E3920496E3A32343530302C31302C32332E3020433A343539382C3435393020493A37362520322042617420342046736F6320302E300A3136393839313439353137393720462053454E522062617420626F6F6C3A3078342C2072633A343634312C2072703A39312C20637572723A312C20766F6C743A323435302C2074656D703A34330A3136393839313439353137393820462053454E52206D6F64653A3078312C206C617374206576656E743A342C20626174536F633A3130302C206D6F775F74656D703A302C204D43555F74656D703A3532302C20494D555F74656D703A323236342C2072656D61696E5F686561703A31363131343339322C206865696768743A36300A0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003136393839313439363037303520462042696173203131353034383320302E30333830353820302E303032303234202D302E303030383633202D302E30323639363420302E30333638353920332E31323537383020302E3236353932340A31363938393134393631333937204620424D53204F75743A3130302E302C39312E3920496E3A32343530302C302C32332E3020433A343539382C3435393020493A37362520322042617420342046736F6320302E300A3136393839313439363137393720462053454E522062617420626F6F6C3A3078342C2072633A343634312C2072703A39312C20637572723A302C20766F6C743A323435302C2074656D703A34330A3136393839313439363137393720462053454E52206D6F64653A3078312C206C617374206576656E743A342C20626174536F633A3130302C206D6F775F74656D703A302C204D43555F74656D703A3532302C20494D555F74656D703A323236362C2072656D61696E5F686561703A31363131343339322C206865696768743A36300A00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    return EXIT_SUCCESS;
}