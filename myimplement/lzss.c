//
// Created by 李泽鑫 on 2023/11/1.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "byte_tool.c"

#define EI 11  /* typically 10..13 */
#define EJ  4  /* typically 4..5 */
#define P   1  /* If match length <= P then output one character */
#define BUFFER_SIZE (1 << EI)  /* buffer size */
#define LOOKAHEAD_BUFFER_SIZE ((1 << EJ) + 1)  /* lookahead buffer size */
#define SPACE_BUFFER_SIZE (BUFFER_SIZE - LOOKAHEAD_BUFFER_SIZE)  /* space buffer size */

typedef unsigned char Byte;

typedef struct Result {
    Byte *bytes;
    int size;
} Result;

typedef struct ByteNode {
    Byte byte;
    struct ByteNode *next;
} ByteNode;

typedef struct EncodeBuffer {
    Byte bit_buffer;
    Byte bit_mask;
    ByteNode *byteLinkedHead;
    ByteNode *byteLinkedTail;
    int byteLinkedSize;
} EncodeBuffer;

void appendEncodeByteNode(EncodeBuffer *encodeBuffer, ByteNode *byteNode) {
    encodeBuffer->byteLinkedTail->next = byteNode;
    encodeBuffer->byteLinkedTail = byteNode;
    encodeBuffer->byteLinkedSize++;
    encodeBuffer->bit_buffer = 0;
    encodeBuffer->bit_mask = 128;
}

void putbit1(EncodeBuffer *encodeBuffer) {
    encodeBuffer->bit_buffer |= encodeBuffer->bit_mask;
    if ((encodeBuffer->bit_mask >>= 1) == 0) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bit_buffer;
        newByteLinkedTail->next = NULL;
        appendEncodeByteNode(encodeBuffer, newByteLinkedTail);
    }
}

void putbit0(EncodeBuffer *encodeBuffer) {
    if ((encodeBuffer->bit_mask >>= 1) == 0) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bit_buffer;
        newByteLinkedTail->next = NULL;
        appendEncodeByteNode(encodeBuffer, newByteLinkedTail);
    }
}

void flush_bit_buffer(EncodeBuffer *encodeBuffer) {
    if (encodeBuffer->bit_mask != 128) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bit_buffer;
        newByteLinkedTail->next = NULL;
        appendEncodeByteNode(encodeBuffer, newByteLinkedTail);
    }
}

void output1(EncodeBuffer *encodeBuffer, int c) {
    int mask;

    putbit1(encodeBuffer);
    mask = 256;
    while (mask >>= 1) {
        if (c & mask) {
            putbit1(encodeBuffer);
        } else {
            putbit0(encodeBuffer);
        }
    }
}

void output2(EncodeBuffer *encodeBuffer, int x, int y) {
    int mask;

    putbit0(encodeBuffer);
    mask = BUFFER_SIZE;
    while (mask >>= 1) {
        if (x & mask) {
            putbit1(encodeBuffer);
        } else {
            putbit0(encodeBuffer);
        }
    }
    mask = (1 << EJ);
    while (mask >>= 1) {
        if (y & mask) {
            putbit1(encodeBuffer);
        } else {
            putbit0(encodeBuffer);
        }
    }
}

Result *encode(const Byte *origin_bytes, int origin_bytes_size) {
    Byte buffer[BUFFER_SIZE * 2];
    unsigned long origin_bytes_count = 0;

    int buffer_index = 0;
    int origin_bytes_index = 0;
    while (buffer_index < BUFFER_SIZE * 2) {
        if (buffer_index < SPACE_BUFFER_SIZE) {
            buffer[buffer_index++] = ' ';
        } else if (origin_bytes_index < origin_bytes_size) {
            buffer[buffer_index++] = origin_bytes[origin_bytes_index++];
            origin_bytes_count++;
        } else {
            break;
        }
    }

    ByteNode *rootNode = malloc(sizeof(ByteNode));
    rootNode->byte = 0;
    rootNode->next = NULL;
    EncodeBuffer encodeBuffer = {0, 128, rootNode, rootNode, 0};
    int buffer_end = buffer_index;
    int r = SPACE_BUFFER_SIZE;
    int s = 0;
    while (r < buffer_end) {
        int f1 = (LOOKAHEAD_BUFFER_SIZE <= buffer_end - r) ? LOOKAHEAD_BUFFER_SIZE : buffer_end - r;
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
            output1(&encodeBuffer, c);
        } else {
            output2(&encodeBuffer, x & (BUFFER_SIZE - 1), y - 2);
        }
        r += y;
        s += y;
        if (r >= BUFFER_SIZE * 2 - LOOKAHEAD_BUFFER_SIZE) {
            for (int i = 0; i < BUFFER_SIZE; i++) buffer[i] = buffer[i + BUFFER_SIZE];
            buffer_end -= BUFFER_SIZE;
            r -= BUFFER_SIZE;
            s -= BUFFER_SIZE;
            while (buffer_end < BUFFER_SIZE * 2) {
                if (origin_bytes_index >= origin_bytes_size) {
                    break;
                }
                buffer[buffer_end++] = origin_bytes[origin_bytes_index++];
            }
        }
    }
    flush_bit_buffer(&encodeBuffer);
    printf("origin_bytes:  %ld bytes\n", origin_bytes_count);
    printf("encode_bytes:  %d bytes (%ld%%)\n", encodeBuffer.byteLinkedSize,
           (encodeBuffer.byteLinkedSize * 100) / origin_bytes_count);

    Result *result = malloc(sizeof(Result));
    result->bytes = malloc(sizeof(Byte) * encodeBuffer.byteLinkedSize);
    result->size = encodeBuffer.byteLinkedSize;

    ByteNode *current_node = encodeBuffer.byteLinkedHead->next;
    for (int i = 0; i < encodeBuffer.byteLinkedSize && current_node != NULL; ++i, current_node = current_node->next) {
        result->bytes[i] = current_node->byte;
    }
    result->bytes[encodeBuffer.byteLinkedSize] = '\0';

    free(rootNode);
    return result;
}

typedef struct DecodeBuffer {
    Byte bit_buffer;
    Byte bit_mask;
    Byte *encode_bytes;
    int encode_bytes_size;
    int encode_bytes_curr_index;
    ByteNode *byteLinkedHead;
    ByteNode *byteLinkedTail;
    int byteLinkedSize;
} DecodeBuffer;

/* get n bits */
int getbit(DecodeBuffer *decodeBuffer, int n) {
    int x = 0;
    for (int i = 0; i < n; i++) {
        if (decodeBuffer->bit_mask == 0) {
            if (decodeBuffer->encode_bytes_curr_index >= decodeBuffer->encode_bytes_size) {
                return EOF;
            }
            decodeBuffer->bit_buffer = decodeBuffer->encode_bytes[decodeBuffer->encode_bytes_curr_index++];
            decodeBuffer->bit_mask = 128;
        }
        x <<= 1;
        if (decodeBuffer->bit_buffer & decodeBuffer->bit_mask) x++;
        decodeBuffer->bit_mask >>= 1;
    }
    return x;
}

void appendDecodeByte(DecodeBuffer *decodeBuffer, Byte byte) {
    ByteNode *byteNode = malloc(sizeof(ByteNode));
    byteNode->byte = byte;
    byteNode->next = NULL;
    decodeBuffer->byteLinkedTail->next = byteNode;
    decodeBuffer->byteLinkedTail = byteNode;
    decodeBuffer->byteLinkedSize++;
}

Result *decode(Byte *encode_bytes, int encode_bytes_size) {
    Byte buffer[BUFFER_SIZE * 2];

    for (int i = 0; i < SPACE_BUFFER_SIZE; i++) buffer[i] = ' ';
    int r = SPACE_BUFFER_SIZE;

    ByteNode *rootNode = malloc(sizeof(ByteNode));
    rootNode->byte = 0;
    rootNode->next = NULL;
    DecodeBuffer decodeBuffer = {0, 0, encode_bytes, encode_bytes_size, 0, rootNode, rootNode, 0};

    int c;
    while ((c = getbit(&decodeBuffer, 1)) != EOF) {
        if (c) {
            if ((c = getbit(&decodeBuffer, 8)) == EOF) break;
            appendDecodeByte(&decodeBuffer, c);
            buffer[r++] = c;
            r &= (BUFFER_SIZE - 1);
        } else {
            int i, j, k;
            if ((i = getbit(&decodeBuffer, EI)) == EOF) break;
            if ((j = getbit(&decodeBuffer, EJ)) == EOF) break;
            for (k = 0; k <= j + 1; k++) {
                c = buffer[(i + k) & (BUFFER_SIZE - 1)];
                appendDecodeByte(&decodeBuffer, c);
                buffer[r++] = c;
                r &= (BUFFER_SIZE - 1);
            }
        }
    }

    Result *result = malloc(sizeof(Result));
    result->bytes = malloc(sizeof(Byte) * decodeBuffer.byteLinkedSize);
    result->size = decodeBuffer.byteLinkedSize;

    ByteNode *current_node = decodeBuffer.byteLinkedHead->next;
    for (int i = 0; i < decodeBuffer.byteLinkedSize && current_node != NULL; ++i, current_node = current_node->next) {
        result->bytes[i] = current_node->byte;
    }
    result->bytes[decodeBuffer.byteLinkedSize] = '\0';

    free(rootNode);
    return result;
}