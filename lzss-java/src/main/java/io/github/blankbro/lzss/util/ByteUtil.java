package io.github.blankbro.lzss.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtil {


    public static byte[] intToByteArray(int value, int capacity) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(value);
        byte[] bytes = buffer.array();
        if (capacity == Integer.BYTES) {
            return bytes;
        }

        for (int i = 0; i < Integer.BYTES - capacity; i++) {
            if (bytes[i] != 0) {
                throw new RuntimeException("capacity is so small");
            }
        }

        return Arrays.copyOfRange(bytes, Integer.BYTES - capacity, Integer.BYTES);
    }

    /**
     * 将字节数组转换成十六进制字符串
     *
     * @param bytes 待转换的字节数组
     * @return 转换之后的十六进制字符串
     */
    public static String byteArrayToHexStr(byte[] bytes) {
        return Hex.encodeHexString(bytes, false);
    }

    public static byte[] hexStringToByteArray(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString);
    }

    private ByteUtil() {
    }
}
