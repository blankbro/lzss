package io.github.blankbro.lzss;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Selfdev {

    @Getter
    public static class EncodeResult {
        private byte[] bytes;
        // 可用无符号的2字节，意味着最大为65535
        private int singlePackageLength;
        private int byteBitPositionLength;
    }

    public static byte[] decode(EncodeResult encodeResult) {

        // 解压第二包之后的数据
        List<Byte> afterFirstPackageByteList = new ArrayList<>();
        int unzeroByteIndex = 0;
        int INIT_BIT_MASK = 0b1000_0000;
        int bitMask;
        for (int byteBitPositionIndex = 0; byteBitPositionIndex < encodeResult.byteBitPositionLength; byteBitPositionIndex++) {
            bitMask = INIT_BIT_MASK;
            byte byteBitPosition = encodeResult.bytes[encodeResult.singlePackageLength + byteBitPositionIndex];
            while (bitMask != 0) {
                if ((byteBitPosition & bitMask) == 0) {
                    afterFirstPackageByteList.add((byte) 0);
                } else {
                    afterFirstPackageByteList.add(encodeResult.bytes[encodeResult.singlePackageLength + encodeResult.byteBitPositionLength + unzeroByteIndex]);
                    unzeroByteIndex++;
                }
                bitMask >>= 1;
            }
        }

        // 计算确切的数据包的数量（ byteBitPosition 最后可能有几个多余的0，通过 / 运算就可以把他们省略掉了）
        int packageCount = afterFirstPackageByteList.size() / encodeResult.singlePackageLength + 1;
        byte[] decodeResult = new byte[packageCount * encodeResult.singlePackageLength];

        // 填充第一包
        System.arraycopy(encodeResult.bytes, 0, decodeResult, 0, encodeResult.singlePackageLength);

        // 填充第二包及之后的数据
        for (int packageNumber = 1; packageNumber < packageCount; packageNumber++) {
            for (int packageIndex = 0; packageIndex < encodeResult.singlePackageLength; packageIndex++) {
                decodeResult[packageNumber * encodeResult.singlePackageLength + packageIndex] =
                        (byte) (decodeResult[(packageNumber - 1) * encodeResult.singlePackageLength + packageIndex] ^ afterFirstPackageByteList.get((packageNumber - 1) * encodeResult.singlePackageLength + packageIndex));
            }
        }

        return decodeResult;
    }

    public static EncodeResult encode(byte[] inputBytes, int packageCount) {
        if (inputBytes.length % packageCount != 0) {
            throw new RuntimeException("原始字节数组长度 和 数据包个数 不相符");
        }

        // 拷贝一份进行操作
        byte[] originByteArray = Arrays.copyOf(inputBytes, inputBytes.length);


        // 获取单包长度
        int singlePackageLength = originByteArray.length / packageCount;

        // 将每包数据和前一包进行异或
        for (int packageNumber = packageCount - 1; packageNumber > 0; packageNumber--) {
            for (int packageIndex = 0; packageIndex < singlePackageLength; packageIndex++) {
                originByteArray[singlePackageLength * packageNumber + packageIndex] ^= originByteArray[singlePackageLength * (packageNumber - 1) + packageIndex];
            }
        }

        // 用 bit 记录 0 和 非0字节 的位置
        List<Byte> byteBitPositionList = new ArrayList<>();
        // 记录除异或之后，从第二包开始，所有非0的字节
        List<Byte> unzeroByteList = new ArrayList<>();

        int INIT_BIT_MASK = 0b1000_0000;
        int INIT_BIT_BUFFER = 0b0000_0000;
        int bitBuffer = INIT_BIT_BUFFER;
        int bitMask = INIT_BIT_MASK;
        for (int i = singlePackageLength; i < originByteArray.length; i++) {
            if (originByteArray[i] == 0) {
                // putbit 0
                if ((bitMask >>= 1) == 0) {
                    byteBitPositionList.add((byte) bitBuffer);
                    bitBuffer = INIT_BIT_BUFFER;
                    bitMask = INIT_BIT_MASK;
                }
            } else {
                unzeroByteList.add(originByteArray[i]);
                // putbit 1
                bitBuffer |= bitMask;
                if ((bitMask >>= 1) == 0) {
                    byteBitPositionList.add((byte) bitBuffer);
                    bitBuffer = INIT_BIT_BUFFER;
                    bitMask = INIT_BIT_MASK;
                }
            }
        }

        // flush bit buffer
        if (bitMask != INIT_BIT_MASK) {
            byteBitPositionList.add((byte) bitBuffer);
            bitBuffer = INIT_BIT_BUFFER;
            bitMask = INIT_BIT_MASK;
        }


        byte[] encodeBytes = new byte[singlePackageLength + byteBitPositionList.size() + unzeroByteList.size()];
        // 拷贝第一包数据
        System.arraycopy(originByteArray, 0, encodeBytes, 0, singlePackageLength);
        // 填充 0和非0字节 对应的bit位置
        for (int i = 0; i < byteBitPositionList.size(); i++) {
            encodeBytes[singlePackageLength + i] = byteBitPositionList.get(i);
        }
        // 填充非0的字节
        for (int i = 0; i < unzeroByteList.size(); i++) {
            encodeBytes[singlePackageLength + byteBitPositionList.size() + i] = unzeroByteList.get(i);
        }

        EncodeResult encodeResult = new EncodeResult();
        encodeResult.singlePackageLength = singlePackageLength;
        encodeResult.bytes = encodeBytes;
        encodeResult.byteBitPositionLength = byteBitPositionList.size();

        // log.info("{}/{} = {}%", encodeBytes.length + 2 + 2, originByteArray.length, (encodeBytes.length + 2 + 2) * 100.0 / originByteArray.length);
        return encodeResult;
    }
}
