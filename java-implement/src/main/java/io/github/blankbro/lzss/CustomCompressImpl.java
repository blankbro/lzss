package io.github.blankbro.lzss;

import java.util.Arrays;

public class CustomCompressImpl {

    /**
     * 压缩
     *
     * @param originDataBytes             原始数据
     * @param singleDataPackageByteLength 单个数据包字节长度
     * @param bytePositionByteLength      字节位置所占字节长度（可以为空，默认用最小的字节数）
     * @return
     */
    public static byte[] encode(byte[] originDataBytes, int singleDataPackageByteLength, Integer bytePositionByteLength) {
        if (originDataBytes.length % singleDataPackageByteLength != 0) {
            throw new RuntimeException("originDataBytes.length % singleDataPackageByteLength != 0");
        }

        // 拷贝一份进行操作
        originDataBytes = Arrays.copyOf(originDataBytes, originDataBytes.length);

        int dataPackageCount = originDataBytes.length / singleDataPackageByteLength;
        int otherDataPackageByteLength = originDataBytes.length - singleDataPackageByteLength;
        int minBytePositionByteLength = otherDataPackageByteLength / Byte.SIZE + (otherDataPackageByteLength % Byte.SIZE);
        if (bytePositionByteLength == null) {
            bytePositionByteLength = minBytePositionByteLength;
        } else if (bytePositionByteLength < minBytePositionByteLength) {
            throw new RuntimeException("bytePositionByteLength < minBytePositionByteLength");
        }

        byte[] bytePositions = new byte[bytePositionByteLength];
        byte[] encodeDataBytes = new byte[originDataBytes.length * 2];

        for (int packageNumber = dataPackageCount - 1; packageNumber > 0; packageNumber--) {
            for (int packageIndex = 0; packageIndex < singleDataPackageByteLength; packageIndex++)
                originDataBytes[packageIndex + singleDataPackageByteLength * packageNumber] ^= originDataBytes[packageIndex + singleDataPackageByteLength * (packageNumber - 1)];
        }

        // 拷贝一包数据
        System.arraycopy(originDataBytes, 0, encodeDataBytes, 0, singleDataPackageByteLength);

        // 计算非0字节数量，并用bytePositions记录位置，最后把非0字节放入 encodeDataBytes
        int unzeroByteCount = 0;
        for (int i = 0; i < otherDataPackageByteLength; i++) {
            if (originDataBytes[singleDataPackageByteLength + i] != 0) {
                bytePositions[i / Byte.SIZE] = (byte) (bytePositions[i / Byte.SIZE] + 0x01);
                encodeDataBytes[unzeroByteCount + singleDataPackageByteLength + bytePositionByteLength] = originDataBytes[singleDataPackageByteLength + i];
                unzeroByteCount++;
            }
            if ((i % Byte.SIZE) != 7)
                bytePositions[i / Byte.SIZE] <<= 1;
        }

        // 把 bytePositions 放入 encodeDataBytes
        System.arraycopy(bytePositions, 0, encodeDataBytes, singleDataPackageByteLength, bytePositionByteLength);

        // 返回有效字节
        return Arrays.copyOfRange(encodeDataBytes, 0, singleDataPackageByteLength + bytePositionByteLength + unzeroByteCount);
    }

    /**
     * 解压缩
     *
     * @param encodeDataBytes             压缩数据
     * @param singleDataPackageByteLength 单个数据包字节长度
     * @param bytePositionByteLength      字节位置所占字节长度
     * @return
     */
    public static byte[] decode(byte[] encodeDataBytes, int singleDataPackageByteLength, int bytePositionByteLength) {
        int dataPackageCount = bytePositionByteLength * 8 / singleDataPackageByteLength;
        int otherDataPackageByteLength = (dataPackageCount - 1) * singleDataPackageByteLength;

        byte[] originDataBytes = new byte[singleDataPackageByteLength + otherDataPackageByteLength];

        // 拷贝第一包
        System.arraycopy(encodeDataBytes, 0, originDataBytes, 0, singleDataPackageByteLength);

        int unzeroByteIndex = 0;
        for (int i = 0; i < otherDataPackageByteLength; i++) {
            if ((encodeDataBytes[singleDataPackageByteLength + i / Byte.SIZE] & 0x80) == 0x80) {
                originDataBytes[singleDataPackageByteLength + i] = (byte) (originDataBytes[i] ^ encodeDataBytes[singleDataPackageByteLength + bytePositionByteLength + unzeroByteIndex]);
                unzeroByteIndex++;
            } else {
                originDataBytes[singleDataPackageByteLength + i] = originDataBytes[i];
            }
            encodeDataBytes[singleDataPackageByteLength + i / Byte.SIZE] <<= 1;
        }


        return originDataBytes;
    }

}
