package io.github.blankbro.lzss;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 此例是Dr. Seuss所著《Green Eggs and Ham》的开头，每行开头的已有字符总数是为方便所设。
 *   0: I am Sam
 *   9:
 *  10: Sam I am
 *  19:
 *  20: That Sam-I-am!
 *  35: That Sam-I-am!
 *  50: I do not like
 *  64: that Sam-I-am!
 *  79:
 *  80: Do you like green eggs and ham?
 * 112:
 * 113: I do not like them, Sam-I-am.
 * 143: I do not like green eggs and ham.
 * <p>
 * 这是该段文本在未压缩形式的177字节。假设盈亏平衡点是2字节（并因此是2字节的指针/偏移对），那么加上一字节的新行字符，此文本使用LZSS压缩后将变为94字节：
 *  0: I am Sam
 *  9:
 * 10: (5,3) (0,4)
 * 16:
 * 17: That(4,4)-I-am!(19,16)I do not like
 * 45: t(21,14)
 * 49: Do you(58,5) green eggs and ham?
 * 78: (49,14) them,(24,9).(112,15)(93,18).
 * <p>
 * 详情请看<a href="https://zh.wikipedia.org/wiki/LZSS">维基百科LZSS</a>
 */
@Slf4j
public class Lzss {
    // 索引需要的位数
    private static final int INDEX_BITS = 6; // typically 10..13
    // 长度需要的位数
    private static final int LENGTH_BITS = 3;  // typically 4..5
    // 匹配长度阈值
    private static final int MATCH_LENGTH_THRESHOLD = 1;   // If match length <= MATCH_LENGTH_THRESHOLD then output one character
    // 缓冲区大小
    private static final int BUFFER_SIZE = (1 << INDEX_BITS);  // buffer size
    // 前瞻缓冲区大小
    private static final int LOOKAHEAD_BUFFER_SIZE = ((1 << LENGTH_BITS) + 1);  // lookahead buffer size
    private static final int SPACE_BUFFER_SIZE = BUFFER_SIZE - LOOKAHEAD_BUFFER_SIZE;  // space buffer size

    private static class EncodeBuffer {
        static final int INIT_BIT_BUFFER = 0;
        static final int INIT_BIT_MASK = 1 << 7;
        private int bitBuffer = INIT_BIT_BUFFER;
        private int bitMask = INIT_BIT_MASK;
        private List<Byte> encodeByteList = new ArrayList<>();

        private void putBit1() {
            this.bitBuffer |= this.bitMask;
            if ((this.bitMask >>= 1) == 0) {
                this.encodeByteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }

        private void putBit0() {
            if ((this.bitMask >>= 1) == 0) {
                this.encodeByteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }

        private void output1(int c) {
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

        private void output2(int x, int y) {
            this.putBit0();
            int mask = BUFFER_SIZE;
            while ((mask >>= 1) != 0) {
                if ((x & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
            mask = (1 << LENGTH_BITS);
            while ((mask >>= 1) != 0) {
                if ((y & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
        }

        private void flushBitBuffer() {
            if (this.bitMask != INIT_BIT_MASK) {
                this.encodeByteList.add((byte) this.bitBuffer);
                this.bitBuffer = INIT_BIT_BUFFER;
                this.bitMask = INIT_BIT_MASK;
            }
        }
    }

    public static byte[] encode(byte[] originByteArray) {
        byte[] buffer = new byte[BUFFER_SIZE * 2];
        int bufferIndex = 0;
        int originByteArrayIndex = 0;
        // 构建初始滑动窗口 [SPACE..., originByte...]
        while (bufferIndex < buffer.length) {
            if (bufferIndex < SPACE_BUFFER_SIZE) {
                buffer[bufferIndex++] = ' ';
            } else if (originByteArrayIndex < originByteArray.length) {
                buffer[bufferIndex++] = originByteArray[originByteArrayIndex++];
            } else {
                break;
            }
        }

        EncodeBuffer encodeBuffer = new EncodeBuffer();
        int bufferEndIndex = bufferIndex;
        // 缓冲区中当前搜索匹配的位置
        int currentProcessIndex = SPACE_BUFFER_SIZE;
        // 缓冲区中开始搜索的位置
        int searchStartIndex = 0;
        while (currentProcessIndex < bufferEndIndex) {
            // 前瞻缓冲区可用长度
            int lookaheadBufferValidLength = Math.min(LOOKAHEAD_BUFFER_SIZE, bufferEndIndex - currentProcessIndex);
            // 匹配字符串在缓冲区中的位置
            int matchStartIndex = 0;
            // 匹配字符串的长度
            int matchLength = 1;
            // 当前处理的字符
            int currentChar = buffer[currentProcessIndex];
            for (int i = currentProcessIndex - 1; i >= searchStartIndex; i--) {
                if (buffer[i] == currentChar) {
                    int j;
                    for (j = 1; j < lookaheadBufferValidLength; j++) {
                        if (buffer[i + j] != buffer[currentProcessIndex + j]) break;
                    }
                    if (j > matchLength) {
                        matchStartIndex = i;
                        matchLength = j;
                    }
                }
            }
            if (matchLength <= MATCH_LENGTH_THRESHOLD) {
                matchLength = 1;
                encodeBuffer.output1(currentChar);
            } else {
                encodeBuffer.output2(matchStartIndex & (BUFFER_SIZE - 1), matchLength - 2);
            }
            currentProcessIndex += matchLength;
            searchStartIndex += matchLength;
            // 移动窗口
            if (currentProcessIndex >= BUFFER_SIZE * 2 - LOOKAHEAD_BUFFER_SIZE) {
                for (int i = 0; i < BUFFER_SIZE; i++) buffer[i] = buffer[i + BUFFER_SIZE];
                bufferEndIndex -= BUFFER_SIZE;
                currentProcessIndex -= BUFFER_SIZE;
                searchStartIndex -= BUFFER_SIZE;
                while (bufferEndIndex < BUFFER_SIZE * 2) {
                    if (originByteArrayIndex >= originByteArray.length) {
                        break;
                    }
                    buffer[bufferEndIndex++] = originByteArray[originByteArrayIndex++];
                }
            }
        }

        encodeBuffer.flushBitBuffer();

        byte[] result = new byte[encodeBuffer.encodeByteList.size()];
        for (int i = 0; i < encodeBuffer.encodeByteList.size(); i++) {
            result[i] = encodeBuffer.encodeByteList.get(i);
        }
        return result;
    }

    private static class DecodeBuffer {
        private static final int EOF = -1;
        private static final int INIT_BIT_MASK = 1 << 7;
        private int bitBuffer;
        private int bitMask;
        private byte[] encodeBytes;
        private int encodeBytesCurrIndex;
        private List<Byte> decodeByteList;

        public DecodeBuffer(byte[] encodeBytes) {
            this.encodeBytes = encodeBytes;
            this.encodeBytesCurrIndex = 0;
            this.bitBuffer = this.encodeBytes[this.encodeBytesCurrIndex++];
            this.bitMask = INIT_BIT_MASK;
            this.decodeByteList = new ArrayList<>();
        }

        /* get n bits */
        private int getbit(int n) {
            int x = 0;
            for (int i = 0; i < n; i++) {
                if (this.bitMask == 0) {
                    if (this.encodeBytesCurrIndex >= this.encodeBytes.length) {
                        return EOF;
                    }
                    this.bitBuffer = this.encodeBytes[this.encodeBytesCurrIndex++];
                    this.bitMask = INIT_BIT_MASK;
                }
                x <<= 1;
                if ((this.bitBuffer & this.bitMask) != 0) x++;
                this.bitMask >>= 1;
            }
            return x;
        }

        private void appendDecodeByte(byte b) {
            this.decodeByteList.add(b);
        }
    }

    public static byte[] decode(byte[] encodeByteArray) {
        byte[] buffer = new byte[BUFFER_SIZE * 2];

        for (int i = 0; i < SPACE_BUFFER_SIZE; i++) buffer[i] = ' ';
        int r = SPACE_BUFFER_SIZE;

        DecodeBuffer decodeBuffer = new DecodeBuffer(encodeByteArray);

        int c;
        while ((c = decodeBuffer.getbit(1)) != DecodeBuffer.EOF) {
            if (c != 0) {
                if ((c = decodeBuffer.getbit(8)) == DecodeBuffer.EOF) break;
                decodeBuffer.appendDecodeByte((byte) c);
                buffer[r++] = (byte) c;
                r &= (BUFFER_SIZE - 1);
            } else {
                int i, j, k;
                if ((i = decodeBuffer.getbit(INDEX_BITS)) == DecodeBuffer.EOF) break;
                if ((j = decodeBuffer.getbit(LENGTH_BITS)) == DecodeBuffer.EOF) break;
                for (k = 0; k <= j + 1; k++) {
                    c = buffer[(i + k) & (BUFFER_SIZE - 1)];
                    decodeBuffer.appendDecodeByte((byte) c);
                    buffer[r++] = (byte) c;
                    r &= (BUFFER_SIZE - 1);
                }
            }
        }

        byte[] result = new byte[decodeBuffer.decodeByteList.size()];
        for (int i = 0; i < decodeBuffer.decodeByteList.size(); i++) {
            result[i] = decodeBuffer.decodeByteList.get(i);
        }

        return result;
    }

}

