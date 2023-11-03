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

typedef struct ByteNode {
    Byte byte;
    struct ByteNode *next;
} ByteNode;

typedef struct EncodeBuffer {
    Byte bitBuffer;
    Byte bitMask;
    ByteNode *byteLinkedHead;
    ByteNode *byteLinkedTail;
    int byteLinkedSize;
} EncodeBuffer;

void appendEncodeByteNode(EncodeBuffer *encodeBuffer, ByteNode *byteNode) {
    encodeBuffer->byteLinkedTail->next = byteNode;
    encodeBuffer->byteLinkedTail = byteNode;
    encodeBuffer->byteLinkedSize++;
    encodeBuffer->bitBuffer = 0;
    encodeBuffer->bitMask = 128;
}

void putbit1(EncodeBuffer *encodeBuffer) {
    encodeBuffer->bitBuffer |= encodeBuffer->bitMask;
    if ((encodeBuffer->bitMask >>= 1) == 0) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bitBuffer;
        newByteLinkedTail->next = NULL;
        appendEncodeByteNode(encodeBuffer, newByteLinkedTail);
    }
}

void putbit0(EncodeBuffer *encodeBuffer) {
    if ((encodeBuffer->bitMask >>= 1) == 0) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bitBuffer;
        newByteLinkedTail->next = NULL;
        appendEncodeByteNode(encodeBuffer, newByteLinkedTail);
    }
}

void flushBitBuffer(EncodeBuffer *encodeBuffer) {
    if (encodeBuffer->bitMask != 128) {
        ByteNode *newByteLinkedTail = malloc(sizeof(ByteNode));
        newByteLinkedTail->byte = encodeBuffer->bitBuffer;
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

ByteArray *encode(ByteArray *originByteArray) {
    Byte buffer[BUFFER_SIZE * 2];
    int bufferIndex = 0;
    int originByteArrayIndex = 0;
    while (bufferIndex < BUFFER_SIZE * 2) {
        if (bufferIndex < SPACE_BUFFER_SIZE) {
            buffer[bufferIndex++] = ' ';
        } else if (originByteArrayIndex < originByteArray->size) {
            buffer[bufferIndex++] = originByteArray->bytes[originByteArrayIndex++];
        } else {
            break;
        }
    }

    ByteNode *rootNode = malloc(sizeof(ByteNode));
    rootNode->byte = 0;
    rootNode->next = NULL;
    EncodeBuffer encodeBuffer = {0, 128, rootNode, rootNode, 0};
    int bufferEnd = bufferIndex;
    int r = SPACE_BUFFER_SIZE;
    int s = 0;
    while (r < bufferEnd) {
        int f1 = (LOOKAHEAD_BUFFER_SIZE <= bufferEnd - r) ? LOOKAHEAD_BUFFER_SIZE : bufferEnd - r;
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
            bufferEnd -= BUFFER_SIZE;
            r -= BUFFER_SIZE;
            s -= BUFFER_SIZE;
            while (bufferEnd < BUFFER_SIZE * 2) {
                if (originByteArrayIndex >= originByteArray->size) {
                    break;
                }
                buffer[bufferEnd++] = originByteArray->bytes[originByteArrayIndex++];
            }
        }
    }
    flushBitBuffer(&encodeBuffer);
    printf("\noriginByteArray:  %d bytes", originByteArray->size);
    printf("\nencodeByteArray:  %d bytes (%d%%)", encodeBuffer.byteLinkedSize,
           (encodeBuffer.byteLinkedSize * 100) / originByteArray->size);

    ByteArray *result = malloc(sizeof(ByteArray));
    result->bytes = malloc(sizeof(Byte) * encodeBuffer.byteLinkedSize);
    result->size = encodeBuffer.byteLinkedSize;

    ByteNode *currentNode = encodeBuffer.byteLinkedHead->next;
    free(rootNode);
    for (int i = 0; i < encodeBuffer.byteLinkedSize; ++i) {
        result->bytes[i] = currentNode->byte;
        ByteNode *tempByteNode = currentNode;
        currentNode = currentNode->next;
        free(tempByteNode);
    }
    result->bytes[encodeBuffer.byteLinkedSize] = '\0';

    return result;
}

typedef struct DecodeBuffer {
    Byte bitBuffer;
    Byte bitMask;
    Byte *encodeBytes;
    int encodeBytesSize;
    int encodeBytesCurrIndex;
    ByteNode *byteLinkedHead;
    ByteNode *byteLinkedTail;
    int byteLinkedSize;
} DecodeBuffer;

/* get n bits */
int getbit(DecodeBuffer *decodeBuffer, int n) {
    int x = 0;
    for (int i = 0; i < n; i++) {
        if (decodeBuffer->bitMask == 0) {
            if (decodeBuffer->encodeBytesCurrIndex >= decodeBuffer->encodeBytesSize) {
                return EOF;
            }
            decodeBuffer->bitBuffer = decodeBuffer->encodeBytes[decodeBuffer->encodeBytesCurrIndex++];
            decodeBuffer->bitMask = 128;
        }
        x <<= 1;
        if (decodeBuffer->bitBuffer & decodeBuffer->bitMask) x++;
        decodeBuffer->bitMask >>= 1;
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

ByteArray *decode(ByteArray *encodeByteArray) {
    Byte buffer[BUFFER_SIZE * 2];

    for (int i = 0; i < SPACE_BUFFER_SIZE; i++) buffer[i] = ' ';
    int r = SPACE_BUFFER_SIZE;

    ByteNode *rootNode = malloc(sizeof(ByteNode));
    rootNode->byte = 0;
    rootNode->next = NULL;
    DecodeBuffer decodeBuffer = {0, 0, encodeByteArray->bytes, encodeByteArray->size, 0, rootNode, rootNode, 0};

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

    ByteArray *result = malloc(sizeof(ByteArray));
    result->bytes = malloc(sizeof(Byte) * decodeBuffer.byteLinkedSize);
    result->size = decodeBuffer.byteLinkedSize;

    ByteNode *currentNode = decodeBuffer.byteLinkedHead->next;
    free(rootNode);
    for (int i = 0; i < decodeBuffer.byteLinkedSize; ++i) {
        result->bytes[i] = currentNode->byte;
        ByteNode *tempByteNode = currentNode;
        currentNode = currentNode->next;
        free(tempByteNode);
    }
    result->bytes[decodeBuffer.byteLinkedSize] = '\0';

    return result;
}