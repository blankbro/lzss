package io.github.blankbro.lzss.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class FileUtil {

    /**
     * 获取文件流
     * 加载与jar同目录下的文件: filePath写文件件名即可
     *
     * @param filePath 文件路径（相对路径和绝对路径）
     * @return InputStream
     * @throws FileNotFoundException 文件不存在
     */
    public static InputStream getInputStream(String filePath) throws FileNotFoundException {
        URL fileUrl = FileUtil.class.getClassLoader().getResource(filePath);
        if (fileUrl != null) {
            log.info("Starting with file at {}, file normal {}", fileUrl.toExternalForm(), fileUrl);
            return FileUtil.class.getClassLoader().getResourceAsStream(filePath);
        }
        log.warn("No file has been found in the bundled resources. Scanning filesystem...");
        File file = new File(filePath);
        if (file.exists()) {
            log.info("Loading external file. Url = {}.", file.getAbsolutePath());
            return new FileInputStream(file);
        }
        throw new FileNotFoundException("The file does not exist. Url = " + file.getAbsolutePath());
    }

}
