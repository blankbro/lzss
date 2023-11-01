//
// Created by 李泽鑫 on 2023/11/1.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define EI 11  /* typically 10..13 */
#define EJ  4  /* typically 4..5 */
#define P   1  /* If match length <= P then output one character */
#define BUFFER_SIZE (1 << EI)  /* buffer size */
#define LOOKAHEAD_BUFFER_SIZE ((1 << EJ) + 1)  /* lookahead buffer size */

typedef struct EncodeBuffer {
    int bit_buffer;
    int bit_mask;
    unsigned char *code;
    int code_index;
} EncodeBuffer;

void putbit1(EncodeBuffer encodeBuffer) {
    encodeBuffer.bit_buffer |= encodeBuffer.bit_mask;
    if ((encodeBuffer.bit_mask >>= 1) == 0) {
        encodeBuffer.code[encodeBuffer.code_index++] = encodeBuffer.bit_buffer;
        encodeBuffer.bit_buffer = 0;
        encodeBuffer.bit_mask = 128;
    }
}

void putbit0(EncodeBuffer encodeBuffer) {
    if ((encodeBuffer.bit_mask >>= 1) == 0) {
        encodeBuffer.code[encodeBuffer.code_index++] = encodeBuffer.bit_buffer;
        encodeBuffer.bit_buffer = 0;
        encodeBuffer.bit_mask = 128;
    }
}

void output1(EncodeBuffer encodeBuffer, int c) {
    int mask;

    putbit1(encodeBuffer);
    mask = 256;
    while (mask >>= 1) {
        if (c & mask) putbit1(encodeBuffer);
        else putbit0(encodeBuffer);
    }
}

void output2(EncodeBuffer encodeBuffer, int x, int y) {
    int mask;

    putbit0(encodeBuffer);
    mask = BUFFER_SIZE;
    while (mask >>= 1) {
        if (x & mask) putbit1(encodeBuffer);
        else putbit0(encodeBuffer);
    }
    mask = (1 << EJ);
    while (mask >>= 1) {
        if (y & mask) putbit1(encodeBuffer);
        else putbit0(encodeBuffer);
    }
}

unsigned char *encode(const char *text) {
    unsigned char buffer[BUFFER_SIZE * 2];
    unsigned long text_count = strlen(text);
    unsigned long code_count = 0;

    int buffer_index = 0;
    int text_index = 0;
    int what_buffer_size = BUFFER_SIZE - LOOKAHEAD_BUFFER_SIZE;
    while (buffer_index < BUFFER_SIZE * 2) {
        if (buffer_index < what_buffer_size) {
            buffer[buffer_index] = ' ';
        } else if (text_index < text_count) {
            buffer[buffer_index++] = text[text_index++];
        } else {
            break;
        }
    }

    EncodeBuffer encodeBuffer = {
            0,
            128,
            malloc(sizeof(unsigned char) * text_count),
            0
    };
    int buffer_end = buffer_index;
    int r = what_buffer_size;
    int s = 0;
    while (r < buffer_end) {
        int f1 = (LOOKAHEAD_BUFFER_SIZE <= buffer_end - r) ? LOOKAHEAD_BUFFER_SIZE : buffer_end - r;
        int x = 0;
        int y = 1;
        int c = buffer[r];
        for (int i = r - 1; i >= s; i--)
            if (buffer[i] == c) {
                int j;
                for (j = 1; j < f1; j++)
                    if (buffer[i + j] != buffer[r + j]) break;
                if (j > y) {
                    x = i;
                    y = j;
                }
            }
        if (y <= 1) {
            y = 1;
            output1(encodeBuffer, c);
        } else output2(encodeBuffer, x & (BUFFER_SIZE - 1), y - 2);
        r += y;
        s += y;
        if (r >= BUFFER_SIZE * 2 - LOOKAHEAD_BUFFER_SIZE) {
            for (int i = 0; i < BUFFER_SIZE; i++) buffer[i] = buffer[i + BUFFER_SIZE];
            buffer_end -= BUFFER_SIZE;
            r -= BUFFER_SIZE;
            s -= BUFFER_SIZE;
            while (buffer_end < BUFFER_SIZE * 2) {
                if ((c = fgetc(infile)) == EOF) break;
                buffer[buffer_end++] = c;
                text_count++;
            }
        }
    }
    flush_bit_buffer();
    printf("text:  %ld bytes\n", text_count);
    printf("code:  %ld bytes (%ld%%)\n", code_count, (code_count * 100) / text_count);
    return "";
}

char *decode(const char *input) {
    return "";
}