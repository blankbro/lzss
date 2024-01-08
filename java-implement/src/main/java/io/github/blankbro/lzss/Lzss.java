package io.github.blankbro.lzss;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Lzss {
    private static final int EI = 11; // typically 10..13
    private static final int EJ = 4;  // typically 4..5
    private static final int P = 1;   // If match length <= P then output one character
    private static final int BUFFER_SIZE = (1 << EI);  // buffer size
    private static final int LOOKAHEAD_BUFFER_SIZE = ((1 << EJ) + 1);  // lookahead buffer size
    private static final int SPACE_BUFFER_SIZE = BUFFER_SIZE - LOOKAHEAD_BUFFER_SIZE;  // space buffer size

    public static class EncodeBuffer {
        public static final int INIT_BIT_BUFFER = 0;
        public static final int INIT_BIT_MASK = 1 << 7;
        int bitBuffer = INIT_BIT_BUFFER;
        int bitMask = INIT_BIT_MASK;
        List<Byte> byteList = new ArrayList<>();

        void putBit1() {
            this.bitBuffer |= this.bitMask;
            if ((this.bitMask >>= 1) == 0) {
                this.byteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }

        void putBit0() {
            if ((this.bitMask >>= 1) == 0) {
                this.byteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }

        void output1(int c) {
            this.putBit1();
            int mask = 256;
            while ((mask >>= 1) != 0) {
                if ((c & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
        }

        void output2(int x, int y) {
            int mask;

            this.putBit0();
            mask = BUFFER_SIZE;
            while ((mask >>= 1) != 0) {
                if ((x & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
            mask = (1 << EJ);
            while ((mask >>= 1) != 0) {
                if ((y & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
        }

        void flushBitBuffer() {
            if (this.bitMask != INIT_BIT_MASK) {
                this.byteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }
    }

    public static byte[] encode(byte[] originByteArray) {
        byte[] buffer = new byte[BUFFER_SIZE * 2];
        int bufferIndex = 0;
        int originByteArrayIndex = 0;
        while (bufferIndex < BUFFER_SIZE * 2) {
            if (bufferIndex < SPACE_BUFFER_SIZE) {
                buffer[bufferIndex++] = ' ';
            } else if (originByteArrayIndex < originByteArray.length) {
                buffer[bufferIndex++] = originByteArray[originByteArrayIndex++];
            } else {
                break;
            }
        }

        EncodeBuffer encodeBuffer = new EncodeBuffer();
        int bufferEnd = bufferIndex;
        int r = SPACE_BUFFER_SIZE;
        int s = 0;
        while (r < bufferEnd) {
            int f1 = Math.min(LOOKAHEAD_BUFFER_SIZE, bufferEnd - r);
            int x = 0;
            int y = 1;
            int c = buffer[r];
            for (int i = r - 1; i >= s; i--) {
                if (buffer[i] == c) {
                    int j;
                    for (j = 1; j < f1; j++) {
                        if (buffer[i + j] != buffer[r + j]) break;
                    }
                    if (j > y) {
                        x = i;
                        y = j;
                    }
                }
            }
            if (y <= 1) {
                y = 1;
                encodeBuffer.output1(c);
            } else {
                encodeBuffer.output2(x & (BUFFER_SIZE - 1), y - 2);
            }
            r += y;
            s += y;
            if (r >= BUFFER_SIZE * 2 - LOOKAHEAD_BUFFER_SIZE) {
                for (int i = 0; i < BUFFER_SIZE; i++) buffer[i] = buffer[i + BUFFER_SIZE];
                bufferEnd -= BUFFER_SIZE;
                r -= BUFFER_SIZE;
                s -= BUFFER_SIZE;
                while (bufferEnd < BUFFER_SIZE * 2) {
                    if (originByteArrayIndex >= originByteArray.length) {
                        break;
                    }
                    buffer[bufferEnd++] = originByteArray[originByteArrayIndex++];
                }
            }
        }

        encodeBuffer.flushBitBuffer();
        log.info("originByteArray:  {} bytes", originByteArray.length);
        log.info("encodeByteArray:  {} bytes ({}%)", encodeBuffer.byteList.size(), encodeBuffer.byteList.size() * 100.0 / originByteArray.length);

        byte[] result = new byte[encodeBuffer.byteList.size()];
        for (int i = 0; i < encodeBuffer.byteList.size(); i++) {
            result[i] = encodeBuffer.byteList.get(i);
        }
        return result;
    }

    public static byte[] decode(byte[] bytes) {
        return null;
    }

}

