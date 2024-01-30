package io.github.blankbro.lzss;

import java.util.Arrays;

/**
 * 自定义压缩算法（四不像，乱七八糟，烦死了）
 * 限制：相同类型数据，相同数据长度
 */
public class CustomCompressImpl2 {

    /**
     * 压缩
     *
     * @param originDataBytes             原始数据
     * @param noCompressionByteLength     不需要压缩的字节长度（这一部分必须在最开始）
     * @param singleDataPackageByteLength 单个数据包字节长度
     * @param bytePositionByteLength      字节位置所占字节长度（可以为空，默认用最小的字节数）
     * @return
     */
    public static byte[] encode(byte[] originDataBytes, int noCompressionByteLength, int singleDataPackageByteLength, Integer bytePositionByteLength) {
        int compressionByteLength = originDataBytes.length - noCompressionByteLength;
        if (compressionByteLength < 0) {
            throw new RuntimeException("noCompressionByteLength > originDataBytes.length");
        }
        if (compressionByteLength == 0) {
            return originDataBytes;
        }
        if (compressionByteLength % singleDataPackageByteLength != 0) {
            throw new RuntimeException("compressedByteLength % singleDataPackageByteLength != 0");
        }

        int dataPackageCount = compressionByteLength / singleDataPackageByteLength;
        int otherDataPackageByteLength = compressionByteLength - singleDataPackageByteLength;
        int minBytePositionByteLength = otherDataPackageByteLength / Byte.SIZE + (otherDataPackageByteLength % Byte.SIZE == 0 ? 0 : 1);
        if (bytePositionByteLength == null) {
            bytePositionByteLength = minBytePositionByteLength;
        } else if (bytePositionByteLength < minBytePositionByteLength) {
            throw new RuntimeException("bytePositionByteLength < minBytePositionByteLength");
        }

        // 创建压缩后的数组
        byte[] encodeDataBytes = new byte[noCompressionByteLength + compressionByteLength * 2];
        // 拷贝不需要压缩的部分和第一包数据
        System.arraycopy(originDataBytes, 0, encodeDataBytes, 0, noCompressionByteLength + singleDataPackageByteLength);

        // 将需要压缩的数据拷贝出来进行操作，不在原来的数据中进行操作
        byte[] compressionOriginDataBytes = new byte[compressionByteLength];
        System.arraycopy(originDataBytes, noCompressionByteLength, compressionOriginDataBytes, 0, compressionByteLength);

        // 异或第二包及之后的数据
        for (int packageNumber = dataPackageCount - 1; packageNumber > 0; packageNumber--) {
            for (int packageIndex = 0; packageIndex < singleDataPackageByteLength; packageIndex++)
                compressionOriginDataBytes[packageIndex + singleDataPackageByteLength * packageNumber] ^= compressionOriginDataBytes[packageIndex + singleDataPackageByteLength * (packageNumber - 1)];
        }

        // 计算非0字节数量，并用bytePositions记录位置，最后把非0字节放入 encodeDataBytes
        byte[] bytePositions = new byte[bytePositionByteLength];
        int unzeroByteCount = 0;
        for (int i = 0; i < otherDataPackageByteLength; i++) {
            if (compressionOriginDataBytes[singleDataPackageByteLength + i] != 0) {
                bytePositions[i / Byte.SIZE] = (byte) (bytePositions[i / Byte.SIZE] + 0x01);
                encodeDataBytes[noCompressionByteLength + singleDataPackageByteLength + bytePositionByteLength + unzeroByteCount] = compressionOriginDataBytes[singleDataPackageByteLength + i];
                unzeroByteCount++;
            }
            if ((i % Byte.SIZE) != 7)
                bytePositions[i / Byte.SIZE] <<= 1;
        }

        // 把 bytePositions 放入 encodeDataBytes
        System.arraycopy(bytePositions, 0, encodeDataBytes, noCompressionByteLength + singleDataPackageByteLength, bytePositionByteLength);

        // 返回有效字节
        return Arrays.copyOfRange(encodeDataBytes, 0, noCompressionByteLength + singleDataPackageByteLength + bytePositionByteLength + unzeroByteCount);
    }

    /**
     * 解压缩
     *
     * @param encodeDataBytes             压缩数据
     * @param noCompressionByteLength     不需要压缩的字节长度（这一部分必须在最开始）
     * @param singleDataPackageByteLength 单个数据包字节长度
     * @param bytePositionByteLength      字节位置所占字节长度
     * @return
     */
    public static byte[] decode(byte[] encodeDataBytes, int noCompressionByteLength, int singleDataPackageByteLength, int bytePositionByteLength) {
        if (encodeDataBytes.length == noCompressionByteLength + singleDataPackageByteLength) {
            return encodeDataBytes;
        }
        int dataPackageCount = bytePositionByteLength * Byte.SIZE / singleDataPackageByteLength;
        int otherDataPackageByteLength = (dataPackageCount - 1) * singleDataPackageByteLength;

        byte[] originDataBytes = new byte[noCompressionByteLength + singleDataPackageByteLength + otherDataPackageByteLength];

        // 拷贝不需要压缩部分和第一包数据
        System.arraycopy(encodeDataBytes, 0, originDataBytes, 0, noCompressionByteLength + singleDataPackageByteLength);

        int unzeroByteIndex = 0;
        for (int i = 0; i < otherDataPackageByteLength; i++) {
            if ((encodeDataBytes[noCompressionByteLength + singleDataPackageByteLength + i / Byte.SIZE] & 0x80) == 0x80) {
                originDataBytes[noCompressionByteLength + singleDataPackageByteLength + i] = (byte) (originDataBytes[noCompressionByteLength + i] ^ encodeDataBytes[noCompressionByteLength + singleDataPackageByteLength + bytePositionByteLength + unzeroByteIndex]);
                unzeroByteIndex++;
            } else {
                originDataBytes[noCompressionByteLength + singleDataPackageByteLength + i] = originDataBytes[noCompressionByteLength + i];
            }
            encodeDataBytes[noCompressionByteLength + singleDataPackageByteLength + i / Byte.SIZE] <<= 1;
        }

        return originDataBytes;
    }

}
