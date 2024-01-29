package io.github.blankbro.lzss;

import io.github.blankbro.lzss.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Slf4j
public class CustomCompressImplTest extends BaseTest {

    @Test
    public void test202401151734() throws DecoderException, IOException {
        Map<String, CaseInfo> caseList = allCase();

        for (Map.Entry<String, CaseInfo> entry : caseList.entrySet()) {
            log.info("=========>>>{}", entry.getKey());

            CaseInfo caseInfo = entry.getValue();
            byte[] byteArr = caseInfo.originBytes;
            Integer singleDataPackageByteLength = caseInfo.singleDataPackageByteLength;
            if (singleDataPackageByteLength == null) {
                log.info("=========>>> singleDataPackageByteLength is null, continue");
                continue;
            }

            int bytePositionByteLength = caseInfo.bytePositionByteLength;

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
                byte[] encodeBytes = CustomCompressImpl.encode(byteArr, singleDataPackageByteLength, bytePositionByteLength);
                long end = System.nanoTime();
                long encodeHandleTime = end - start;
                encodeTotalTime += encodeHandleTime;

                start = System.nanoTime();
                byte[] decodeBytes = CustomCompressImpl.decode(encodeBytes, singleDataPackageByteLength, bytePositionByteLength);
                end = System.nanoTime();
                long decodeHandleTime = end - start;
                decodeTotalTime += decodeHandleTime;

                for (int j = 0; j < decodeBytes.length; j++) {
                    if (decodeBytes[j] != byteArr[j]) {
                        log.info("解压不一致");
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
