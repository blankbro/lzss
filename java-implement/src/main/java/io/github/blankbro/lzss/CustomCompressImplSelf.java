package io.github.blankbro.lzss;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CustomCompressImplSelf {

    public static byte[] encode(byte[] originDataBytes, int singleDataPackageByteLength, Integer bytePositionByteLength) {
        if (originDataBytes.length % singleDataPackageByteLength != 0) {
            throw new RuntimeException("originDataBytes.length % singleDataPackageByteLength != 0");
        }

        // 数据包个数
        int dataPackageCount = originDataBytes.length / singleDataPackageByteLength;
        // 除第一包外，其它包的字节数
        int otherDataPackageByteLength = originDataBytes.length - singleDataPackageByteLength;
        // bytePosition 最小字节长度
        int minBytePositionByteLength = otherDataPackageByteLength / Byte.SIZE + (otherDataPackageByteLength % Byte.SIZE);
        if (bytePositionByteLength == null) {
            bytePositionByteLength = minBytePositionByteLength;
        } else if (bytePositionByteLength < minBytePositionByteLength) {
            throw new RuntimeException("bytePositionByteLength < minBytePositionByteLength");
        }

        // 拷贝一份进行操作
        originDataBytes = Arrays.copyOf(originDataBytes, originDataBytes.length);

        // 将每包数据和前一包进行异或
        for (int packageIndex = dataPackageCount - 1; packageIndex > 0; packageIndex--) {
            for (int packageByteIndex = 0; packageByteIndex < singleDataPackageByteLength; packageByteIndex++) {
                originDataBytes[singleDataPackageByteLength * packageIndex + packageByteIndex] ^= originDataBytes[singleDataPackageByteLength * (packageIndex - 1) + packageByteIndex];
            }
        }

        // 用 bit 记录 0 和 非0字节 的位置
        int bytePositionIndex = 0;
        byte[] bytePositions = new byte[bytePositionByteLength];
        // 记录除异或之后，从第二包开始，所有非0的字节
        List<Byte> unzeroByteList = new ArrayList<>();

        int INIT_BIT_MASK = 0b1000_0000;
        int INIT_BIT_BUFFER = 0b0000_0000;
        int bitBuffer = INIT_BIT_BUFFER;
        int bitMask = INIT_BIT_MASK;
        for (int i = singleDataPackageByteLength; i < originDataBytes.length; i++) {
            if (originDataBytes[i] == 0) {
                // putbit 0
                if ((bitMask >>= 1) == 0) {
                    bytePositions[bytePositionIndex++] = (byte) bitBuffer;
                    bitBuffer = INIT_BIT_BUFFER;
                    bitMask = INIT_BIT_MASK;
                }
            } else {
                unzeroByteList.add(originDataBytes[i]);
                // putbit 1
                bitBuffer |= bitMask;
                if ((bitMask >>= 1) == 0) {
                    bytePositions[bytePositionIndex++] = (byte) bitBuffer;
                    bitBuffer = INIT_BIT_BUFFER;
                    bitMask = INIT_BIT_MASK;
                }
            }
        }

        // flush bit buffer
        if (bitMask != INIT_BIT_MASK) {
            bytePositions[bytePositionIndex++] = (byte) bitBuffer;
            bitBuffer = INIT_BIT_BUFFER;
            bitMask = INIT_BIT_MASK;
        }

        byte[] encodeBytes = new byte[singleDataPackageByteLength + bytePositionByteLength + unzeroByteList.size()];
        // 拷贝第一包数据
        System.arraycopy(originDataBytes, 0, encodeBytes, 0, singleDataPackageByteLength);
        // 填充 0和非0字节 对应的bit位置
        for (int i = 0; i < bytePositionByteLength; i++) {
            encodeBytes[singleDataPackageByteLength + i] = bytePositions[i];
        }
        // 填充非0的字节
        for (int i = 0; i < unzeroByteList.size(); i++) {
            encodeBytes[singleDataPackageByteLength + bytePositionByteLength + i] = unzeroByteList.get(i);
        }

        // log.info("{}/{} = {}%", encodeBytes.length + 2 + 2, originByteArray.length, (encodeBytes.length + 2 + 2) * 100.0 / originByteArray.length);
        return encodeBytes;
    }

    public static byte[] decode(byte[] encodeDataBytes, int singleDataPackageByteLength, int bytePositionByteLength) {

        // 解压第二包之后的数据
        List<Byte> afterFirstPackageByteList = new ArrayList<>();
        int unzeroByteIndex = 0;
        int INIT_BIT_MASK = 0b1000_0000;
        int bitMask;
        for (int bytePositionIndex = 0; bytePositionIndex < bytePositionByteLength; bytePositionIndex++) {
            bitMask = INIT_BIT_MASK;
            byte byteBitPosition = encodeDataBytes[singleDataPackageByteLength + bytePositionIndex];
            while (bitMask != 0) {
                if ((byteBitPosition & bitMask) == 0) {
                    afterFirstPackageByteList.add((byte) 0);
                } else {
                    afterFirstPackageByteList.add(encodeDataBytes[singleDataPackageByteLength + bytePositionByteLength + unzeroByteIndex]);
                    unzeroByteIndex++;
                }
                bitMask >>= 1;
            }
        }

        // 计算确切的数据包的数量（ byteBitPosition 最后可能有几个多余的0，通过 / 运算就可以把他们省略掉了）
        int packageCount = afterFirstPackageByteList.size() / singleDataPackageByteLength;
        byte[] decodeResult = new byte[packageCount * singleDataPackageByteLength];

        // 填充第一包
        System.arraycopy(encodeDataBytes, 0, decodeResult, 0, singleDataPackageByteLength);

        // 填充第二包及之后的数据
        for (int packageNumber = 1; packageNumber < packageCount; packageNumber++) {
            for (int packageIndex = 0; packageIndex < singleDataPackageByteLength; packageIndex++) {
                decodeResult[packageNumber * singleDataPackageByteLength + packageIndex] =
                        (byte) (decodeResult[(packageNumber - 1) * singleDataPackageByteLength + packageIndex] ^ afterFirstPackageByteList.get((packageNumber - 1) * singleDataPackageByteLength + packageIndex));
            }
        }

        return decodeResult;
    }

}
