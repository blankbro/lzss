package io.github.blankbro.lzss;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LzssTest {

    private static String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;
        long micros = duration.toNanos() / 1_000 % 1_000;

        return String.format("%d天 %d小时 %d分钟 %d秒 %d毫秒 %d微秒",
                days, hours, minutes, seconds, millis, micros);
    }

    @Test
    public void test202401081731() {
        Map<String, Integer[]> caseList = new HashMap<>();
        caseList.put("Suphighbuf", new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x0F, 0x48, 0xA1, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x64, 0x0F, 0x6F, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x71, 0x0F, 0x6D, 0x0F, 0x6E, 0x0F, 0x73, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x72, 0x0F, 0x6D, 0x0F, 0x6C, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x12, 0x49, 0x0C, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x68, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x6F, 0x0F, 0x6E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x18, 0x49, 0x77, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6D, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x74, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x74, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x17, 0x49, 0x71, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6C, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1C, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7C, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x76, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1D, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x77, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x40, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7D, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7D, 0x0F, 0x82, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x3A, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7E, 0x0F, 0x81, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x27, 0x4A, 0xA5, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x79, 0x0F, 0x82, 0x0F, 0x7E, 0x0F, 0x83, 0x0F, 0x82, 0x0F, 0x80, 0x0F, 0x82, 0x0F, 0x86, 0x0F, 0x81, 0x0F, 0x82, 0x0F, 0x85, 0x0F, 0x80, 0x0F, 0x7E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x52, 0x4E, 0x1E, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x9E, 0x0F, 0xA1, 0x0F, 0x9F, 0x0F, 0xA4, 0x0F, 0xA3, 0x0F, 0xA0, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x03, 0x4E, 0x4F, 0x4F});
        caseList.put("Supbuf", new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xf4, 0x46, 0x45, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x8c, 0x0e, 0x9b, 0x0e, 0x95, 0x0e, 0x93, 0x0e, 0x92, 0x0e, 0x92, 0x0e, 0x93, 0x0e, 0x96, 0x0e, 0x92, 0x0e, 0x97, 0x0e, 0x98, 0x0e, 0x95, 0x0e, 0x97, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x47, 0x4a, 0x35, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xcf, 0x0e, 0xd8, 0x0e, 0xd3, 0x0e, 0xd4, 0x0e, 0xd3, 0x0e, 0xd2, 0x0e, 0xd4, 0x0e, 0xd6, 0x0e, 0xd3, 0x0e, 0xd6, 0x0e, 0xd7, 0x0e, 0xd4, 0x0e, 0xd7, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x79, 0x4c, 0x32, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xf7, 0x0e, 0xfe, 0x0e, 0xf9, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xf7, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfb, 0x0e, 0xfe, 0x0e, 0xfb, 0x0e, 0xfe, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xcb, 0x42, 0x61, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x69, 0x0e, 0x7d, 0x0e, 0x75, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x72, 0x0e, 0x78, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x78, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x05, 0x46, 0x51, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9a, 0x0e, 0xa6, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0x9f, 0x0e, 0xa1, 0x0e, 0xa2, 0x0e, 0xa0, 0x0e, 0xa4, 0x0e, 0xa5, 0x0e, 0xa1, 0x0e, 0xa4, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x06, 0x46, 0x4b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9b, 0x0e, 0xa7, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0xa2, 0x0e, 0xa4, 0x0e, 0xa0, 0x0e, 0xa5, 0x0e, 0xa6, 0x0e, 0xa2, 0x0e, 0xa5, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x81, 0x4c, 0x97, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xfe, 0x0f, 0x03, 0x0e, 0xff, 0x0f, 0x01, 0x0f, 0x00, 0x0e, 0xfe, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x03, 0x0f, 0x01, 0x0f, 0x01, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x15, 0x46, 0x51, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xa6, 0x0e, 0xb3, 0x0e, 0xaf, 0x0e, 0xae, 0x0e, 0xae, 0x0e, 0xab, 0x0e, 0xaf, 0x0e, 0xb0, 0x0e, 0xab, 0x0e, 0xb0, 0x0e, 0xb1, 0x0e, 0xaf, 0x0e, 0xb0, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xc8, 0x42, 0x67, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x68, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x73, 0x0e, 0x73, 0x0e, 0x70, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x6f, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x73, 0x0e, 0x74, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xbc, 0x42, 0x5b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x5e, 0x0e, 0x70, 0x0e, 0x6a, 0x0e, 0x69, 0x0e, 0x6a, 0x0e, 0x68, 0x0e, 0x6a, 0x0e, 0x6b, 0x0e, 0x66, 0x0e, 0x6d, 0x0e, 0x6e, 0x0e, 0x69, 0x0e, 0x6b, 0x03, 0x45, 0x45, 0x46});

        for (Map.Entry<String, Integer[]> entry : caseList.entrySet()) {
            Integer[] intArr = entry.getValue();
            byte[] byteArr = new byte[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                byteArr[i] = intArr[i].byteValue();
            }

            long start = System.nanoTime();
            byte[] encodeBytes = Lzss.encode(byteArr);
            long end = System.nanoTime();
            log.info("{} 压缩耗时：{}", entry.getKey(), formatDuration(Duration.ofNanos(end - start)));

            start = System.nanoTime();
            byte[] decodeBytes = Lzss.decode(encodeBytes);
            end = System.nanoTime();
            log.info("{} 解压耗时：{}", entry.getKey(), formatDuration(Duration.ofNanos(end - start)));
            for (int i = 0; i < decodeBytes.length; i++) {
                if (decodeBytes[i] != byteArr[i]) {
                    System.out.println("解压不一致");
                    break;
                }
            }

        }
    }
}
