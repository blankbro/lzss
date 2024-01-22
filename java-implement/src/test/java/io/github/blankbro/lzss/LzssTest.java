package io.github.blankbro.lzss;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.blankbro.lzss.util.ByteUtil;
import io.github.blankbro.lzss.util.FileUtil;
import io.github.blankbro.lzss.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.*;

@Slf4j
public class LzssTest {

    private byte[] toByteArray(Integer[] intArr) {
        byte[] byteArr = new byte[intArr.length];
        for (int i = 0; i < intArr.length; i++) {
            Integer obj = intArr[i];
            byteArr[i] = obj.byteValue();
        }
        return byteArr;
    }

    private byte[] generate(int length, int blockLength) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (i % blockLength);
        }
        return result;
    }

    private byte[] loadFromFile() throws IOException, DecoderException {
        InputStream inputStream = FileUtil.getInputStream("test-data");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<byte[]> playloadList = new ArrayList<>();
        int totalLength = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            JSONObject data = JSON.parseObject(line);
            byte[] bytes = ByteUtil.hexStringToByteArray(data.getString("playload"));
            playloadList.add(bytes);
            totalLength += bytes.length;
        }
        byte[] result = new byte[totalLength];
        int currentIndex = 0;

        for (byte[] playload : playloadList) {
            System.arraycopy(playload, 0, result, currentIndex, playload.length);
            currentIndex += playload.length;
        }
        return result;
    }

    @Test
    public void test202401081731() throws IOException, InterruptedException, DecoderException {
        Map<String, byte[]> caseList = new LinkedHashMap<>();

        caseList.put("preheat", toByteArray(new Integer[]{0x00}));
        caseList.put("current_change_large", toByteArray(new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xf4, 0x46, 0x45, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x8c, 0x0e, 0x9b, 0x0e, 0x95, 0x0e, 0x93, 0x0e, 0x92, 0x0e, 0x92, 0x0e, 0x93, 0x0e, 0x96, 0x0e, 0x92, 0x0e, 0x97, 0x0e, 0x98, 0x0e, 0x95, 0x0e, 0x97, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x47, 0x4a, 0x35, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xcf, 0x0e, 0xd8, 0x0e, 0xd3, 0x0e, 0xd4, 0x0e, 0xd3, 0x0e, 0xd2, 0x0e, 0xd4, 0x0e, 0xd6, 0x0e, 0xd3, 0x0e, 0xd6, 0x0e, 0xd7, 0x0e, 0xd4, 0x0e, 0xd7, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x79, 0x4c, 0x32, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xf7, 0x0e, 0xfe, 0x0e, 0xf9, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xf7, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfb, 0x0e, 0xfe, 0x0e, 0xfb, 0x0e, 0xfe, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xcb, 0x42, 0x61, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x69, 0x0e, 0x7d, 0x0e, 0x75, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x72, 0x0e, 0x78, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x78, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x05, 0x46, 0x51, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9a, 0x0e, 0xa6, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0x9f, 0x0e, 0xa1, 0x0e, 0xa2, 0x0e, 0xa0, 0x0e, 0xa4, 0x0e, 0xa5, 0x0e, 0xa1, 0x0e, 0xa4, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x06, 0x46, 0x4b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9b, 0x0e, 0xa7, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0xa2, 0x0e, 0xa4, 0x0e, 0xa0, 0x0e, 0xa5, 0x0e, 0xa6, 0x0e, 0xa2, 0x0e, 0xa5, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x81, 0x4c, 0x97, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xfe, 0x0f, 0x03, 0x0e, 0xff, 0x0f, 0x01, 0x0f, 0x00, 0x0e, 0xfe, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x03, 0x0f, 0x01, 0x0f, 0x01, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x15, 0x46, 0x51, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xa6, 0x0e, 0xb3, 0x0e, 0xaf, 0x0e, 0xae, 0x0e, 0xae, 0x0e, 0xab, 0x0e, 0xaf, 0x0e, 0xb0, 0x0e, 0xab, 0x0e, 0xb0, 0x0e, 0xb1, 0x0e, 0xaf, 0x0e, 0xb0, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xc8, 0x42, 0x67, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x68, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x73, 0x0e, 0x73, 0x0e, 0x70, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x6f, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x73, 0x0e, 0x74, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xbc, 0x42, 0x5b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x5e, 0x0e, 0x70, 0x0e, 0x6a, 0x0e, 0x69, 0x0e, 0x6a, 0x0e, 0x68, 0x0e, 0x6a, 0x0e, 0x6b, 0x0e, 0x66, 0x0e, 0x6d, 0x0e, 0x6e, 0x0e, 0x69, 0x0e, 0x6b, 0x03, 0x45, 0x45, 0x46}));
        caseList.put("current_change_small", toByteArray(new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x0F, 0x48, 0xA1, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x64, 0x0F, 0x6F, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x71, 0x0F, 0x6D, 0x0F, 0x6E, 0x0F, 0x73, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x72, 0x0F, 0x6D, 0x0F, 0x6C, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x12, 0x49, 0x0C, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x68, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x6F, 0x0F, 0x6E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x18, 0x49, 0x77, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6D, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x74, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x74, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x17, 0x49, 0x71, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6C, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1C, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7C, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x76, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1D, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x77, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x40, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7D, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7D, 0x0F, 0x82, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x3A, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7E, 0x0F, 0x81, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x27, 0x4A, 0xA5, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x79, 0x0F, 0x82, 0x0F, 0x7E, 0x0F, 0x83, 0x0F, 0x82, 0x0F, 0x80, 0x0F, 0x82, 0x0F, 0x86, 0x0F, 0x81, 0x0F, 0x82, 0x0F, 0x85, 0x0F, 0x80, 0x0F, 0x7E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x52, 0x4E, 0x1E, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x9E, 0x0F, 0xA1, 0x0F, 0x9F, 0x0F, 0xA4, 0x0F, 0xA3, 0x0F, 0xA0, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x03, 0x4E, 0x4F, 0x4F}));
        caseList.put("hexString", ByteUtil.hexStringToByteArray("018D2EED773816AB11005700055D00303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303000E4D1"));
        List<Integer> generateBlockCaseList = Arrays.asList(255, 223, 200, 150, 132, 100, 80, 64, 56, 55, 50, 48, 32);
        for (Integer blockLength : generateBlockCaseList) {
            caseList.put("generate_" + blockLength, generate(255, blockLength));
        }
        caseList.put("loadFromFile", loadFromFile());

        for (Map.Entry<String, byte[]> entry : caseList.entrySet()) {
            byte[] byteArr = entry.getValue();
            // FileOutputStream fos = new FileOutputStream(new File(entry.getKey()));
            // fos.write(byteArr);
            // fos.close();
            log.info("=========>>>{}", entry.getKey());

            long encodeTotalTime = 0;
            long decodeTotalTime = 0;
            long totalCount = 1;
            for (int i = 0; i < totalCount; i++) {
                // 增加随机值，避免JVM优化
                for (int j = 0; j < 0; j++) {
                    int randomIndex = new Random().nextInt(byteArr.length);
                    byteArr[randomIndex] = (byte) ((byteArr[randomIndex] << 1) & (1 << 7));
                }
                // Thread.sleep(1000);

                long start = System.nanoTime();
                byte[] encodeBytes = Lzss.encode(byteArr);
                long end = System.nanoTime();
                long encodeHandleTime = end - start;
                encodeTotalTime += encodeHandleTime;

                start = System.nanoTime();
                byte[] decodeBytes = Lzss.decode(encodeBytes);
                end = System.nanoTime();
                long decodeHandleTime = end - start;
                decodeTotalTime += decodeHandleTime;

                for (int j = 0; j < decodeBytes.length; j++) {
                    if (decodeBytes[j] != byteArr[j]) {
                        System.out.println("解压不一致");
                        break;
                    }
                }

                log.info("originByteArray:  {} bytes", byteArr.length);
                log.info("encodeByteArray:  {} bytes ({}%)", encodeBytes.length, encodeBytes.length * 100.0 / byteArr.length);
                log.info("压缩耗时：{}", TimeUtil.formatDuration(Duration.ofNanos(encodeHandleTime)));
                log.info("解压耗时：{}", TimeUtil.formatDuration(Duration.ofNanos(decodeHandleTime)));
            }

            // 742μs 483μs, 301μs 137μs
            // 745μs 586μs, 293μs 142μs
            // 708μs 376μs, 306μs 119μs
            log.info("最终结果 压缩 {} 次，平均耗时：{}", totalCount, TimeUtil.formatDuration(Duration.ofNanos(encodeTotalTime / totalCount)));
            log.info("最终结果 解压 {} 次，平均耗时：{}", totalCount, TimeUtil.formatDuration(Duration.ofNanos(decodeTotalTime / totalCount)));


        }
    }
}
