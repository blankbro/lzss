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
    // 滑动窗口长度，需要的位数
    private static final int WINDOW_SIZE_BITS = 10; // typically 10..13
    // 匹配字符串的最大长度，需要的位数
    private static final int MAX_MATCH_LENGTH_BITS = 4;  // typically 4..5
    // 滑动窗口长度
    private static final int WINDOW_SIZE = (1 << WINDOW_SIZE_BITS);
    // 最大匹配字符串的长度
    private static final int MAX_MATCH_LENGTH = ((1 << MAX_MATCH_LENGTH_BITS) + 1);  // lookahead buffer size
    // 搜索字符串的空间大小
    private static final int SEARCH_WINDOW_SIZE = WINDOW_SIZE - MAX_MATCH_LENGTH;

    private static class EncodeBuffer {
        static final int INIT_BIT_BUFFER = 0;
        static final int INIT_BIT_MASK = 1 << 7;
        private int bitBuffer = INIT_BIT_BUFFER;
        private int bitMask = INIT_BIT_MASK;
        private final List<Byte> encodeByteList;

        public EncodeBuffer(int initEncodeByteListSize) {
            this.encodeByteList = new ArrayList<>(initEncodeByteListSize);
        }

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

        private void output2(int matchIndex, int matchLength) {
            this.putBit0();
            int mask = WINDOW_SIZE;
            while ((mask >>= 1) != 0) {
                if ((matchIndex & mask) != 0) {
                    this.putBit1();
                } else {
                    this.putBit0();
                }
            }
            mask = (1 << MAX_MATCH_LENGTH_BITS);
            while ((mask >>= 1) != 0) {
                if ((matchLength & mask) != 0) {
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
        byte[] buffer = new byte[WINDOW_SIZE * 2];
        // 缓存区中当前有效字节的最终位置
        int bufferEndIndex = 0;
        // 原始数据当前索引
        int originByteArrayIndex = 0;

        // 初始化窗口
        while (bufferEndIndex < buffer.length) {
            if (bufferEndIndex < SEARCH_WINDOW_SIZE) {
                buffer[bufferEndIndex++] = ' ';
            } else if (originByteArrayIndex < originByteArray.length) {
                buffer[bufferEndIndex++] = originByteArray[originByteArrayIndex++];
            } else {
                break;
            }
        }

        EncodeBuffer encodeBuffer = new EncodeBuffer(originByteArray.length * 2);
        // 缓冲区中当前搜索匹配的位置
        int currentIndex = SEARCH_WINDOW_SIZE;
        // 缓冲区中开始搜索的位置
        int searchStartIndex = 0;
        while (currentIndex < bufferEndIndex) {
            // 当前最大可匹配的长度
            int maxMatchableLength = Math.min(MAX_MATCH_LENGTH, bufferEndIndex - currentIndex);
            // 匹配字符串在缓冲区中的位置
            int matchIndex = 0;
            // 匹配字符串的长度
            int matchLength = 1;
            // 当前处理的字节
            int currentByte = buffer[currentIndex];
            for (int i = currentIndex - 1; i >= searchStartIndex; i--) {
                if (currentByte == buffer[i]) {
                    int j;
                    for (j = 1; j < maxMatchableLength; j++) {
                        if (buffer[i + j] != buffer[currentIndex + j]) break;
                    }
                    if (j > matchLength) {
                        matchIndex = i;
                        matchLength = j;
                    }
                }
            }
            if (matchLength <= 1) {
                matchLength = 1;
                encodeBuffer.output1(currentByte);
            } else {
                encodeBuffer.output2(matchIndex & (WINDOW_SIZE - 1), matchLength - 2);
            }
            currentIndex += matchLength;
            searchStartIndex += matchLength;
            // 移动窗口
            if (currentIndex >= WINDOW_SIZE * 2 - MAX_MATCH_LENGTH) {
                for (int i = 0; i < WINDOW_SIZE; i++) buffer[i] = buffer[i + WINDOW_SIZE];
                bufferEndIndex -= WINDOW_SIZE;
                currentIndex -= WINDOW_SIZE;
                searchStartIndex -= WINDOW_SIZE;
                while (bufferEndIndex < WINDOW_SIZE * 2) {
                    if (originByteArrayIndex >= originByteArray.length) break;
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
        private static final int INIT_BIT_MASK = 1 << 7;
        private int bitBuffer;
        private int bitMask;
        private final byte[] encodeBytes;
        private int encodeBytesCurrIndex;
        private final List<Byte> decodeByteList;

        public DecodeBuffer(byte[] encodeBytes) {
            this.encodeBytes = encodeBytes;
            this.encodeBytesCurrIndex = 0;
            this.bitBuffer = this.encodeBytes[this.encodeBytesCurrIndex++];
            this.bitMask = INIT_BIT_MASK;
            this.decodeByteList = new ArrayList<>(encodeBytes.length * 2);
        }

        /* get n bits */
        private Integer getBit(int n) {
            int x = 0;
            for (int i = 0; i < n; i++) {
                if (this.bitMask == 0) {
                    if (this.encodeBytesCurrIndex >= this.encodeBytes.length) {
                        return null;
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
        byte[] buffer = new byte[WINDOW_SIZE];

        for (int i = 0; i < SEARCH_WINDOW_SIZE; i++) buffer[i] = ' ';

        DecodeBuffer decodeBuffer = new DecodeBuffer(encodeByteArray);

        int currentIndex = SEARCH_WINDOW_SIZE;
        Integer flagBit, oneByte, matchIndex, matchLength;
        while ((flagBit = decodeBuffer.getBit(1)) != null) {
            if (flagBit != 0) {
                // 取 one byte
                if ((oneByte = decodeBuffer.getBit(8)) == null) break;
                decodeBuffer.appendDecodeByte(oneByte.byteValue());
                buffer[currentIndex++] = oneByte.byteValue();
                currentIndex &= (WINDOW_SIZE - 1);
            } else {
                // 取 multi byte
                if ((matchIndex = decodeBuffer.getBit(WINDOW_SIZE_BITS)) == null) break;
                if ((matchLength = decodeBuffer.getBit(MAX_MATCH_LENGTH_BITS)) == null) break;
                for (int i = 0; i <= matchLength + 1; i++) {
                    byte matchByte = buffer[(matchIndex + i) & (WINDOW_SIZE - 1)];
                    decodeBuffer.appendDecodeByte(matchByte);
                    buffer[currentIndex++] = matchByte;
                    currentIndex &= (WINDOW_SIZE - 1);
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

