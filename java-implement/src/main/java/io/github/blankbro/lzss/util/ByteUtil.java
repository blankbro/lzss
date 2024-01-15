package io.github.blankbro.lzss.util;

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

    private ByteUtil() {
    }
}
