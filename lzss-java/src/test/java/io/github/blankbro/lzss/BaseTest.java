package io.github.blankbro.lzss;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.blankbro.lzss.util.ByteUtil;
import io.github.blankbro.lzss.util.FileUtil;
import org.apache.commons.codec.DecoderException;

import java.io.*;
import java.util.*;

public class BaseTest {

    public byte[] toByteArray(Integer[] intArr) {
        byte[] byteArr = new byte[intArr.length];
        for (int i = 0; i < intArr.length; i++) {
            Integer obj = intArr[i];
            byteArr[i] = obj.byteValue();
        }
        return byteArr;
    }

    public byte[] generate(int length, int blockLength) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (i % blockLength);
        }
        return result;
    }

    public byte[] loadFromFile() throws IOException, DecoderException {
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

    public void writeFile(String fileName, byte[] byteArr) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(byteArr);
        fos.close();
    }

    public static class CaseInfo {
        byte[] originBytes;
        Integer singleDataPackageByteLength;
        Integer bytePositionByteLength;
        Integer noCompressionByteLength;


        public CaseInfo(Integer noCompressionByteLength, Integer singleDataPackageByteLength, byte[] originBytes) {
            this.originBytes = originBytes;
            this.singleDataPackageByteLength = singleDataPackageByteLength;
            if (singleDataPackageByteLength != null) {
                int bitLength = (originBytes.length - singleDataPackageByteLength) / 8 + ((originBytes.length - singleDataPackageByteLength) % 8 == 0 ? 0 : 1);
                // int byteLength = bitLength / 8 + bitLength % 8;
                this.bytePositionByteLength = bitLength;
            }
            this.noCompressionByteLength = noCompressionByteLength;
        }

        public CaseInfo(Integer singleDataPackageByteLength, byte[] originBytes) {
            this(null, singleDataPackageByteLength, originBytes);
        }

        static final int sizeFor(int cap) {
            int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
            return (n < 0) ? 1 : (n >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
        }

        public CaseInfo(byte[] originBytes) {
            this.originBytes = originBytes;
        }
    }

    public Map<String, CaseInfo> allCase() throws DecoderException, IOException {
        Map<String, CaseInfo> caseList = new LinkedHashMap<>();

        caseList.put("preheat", new CaseInfo(toByteArray(new Integer[]{0x00})));
        caseList.put("current_change_large", new CaseInfo(51, toByteArray(new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xf4, 0x46, 0x45, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x8c, 0x0e, 0x9b, 0x0e, 0x95, 0x0e, 0x93, 0x0e, 0x92, 0x0e, 0x92, 0x0e, 0x93, 0x0e, 0x96, 0x0e, 0x92, 0x0e, 0x97, 0x0e, 0x98, 0x0e, 0x95, 0x0e, 0x97, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x47, 0x4a, 0x35, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xcf, 0x0e, 0xd8, 0x0e, 0xd3, 0x0e, 0xd4, 0x0e, 0xd3, 0x0e, 0xd2, 0x0e, 0xd4, 0x0e, 0xd6, 0x0e, 0xd3, 0x0e, 0xd6, 0x0e, 0xd7, 0x0e, 0xd4, 0x0e, 0xd7, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x79, 0x4c, 0x32, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xf7, 0x0e, 0xfe, 0x0e, 0xf9, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xf7, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfa, 0x0e, 0xfb, 0x0e, 0xfe, 0x0e, 0xfb, 0x0e, 0xfe, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xcb, 0x42, 0x61, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x69, 0x0e, 0x7d, 0x0e, 0x75, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x72, 0x0e, 0x78, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x78, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x05, 0x46, 0x51, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9a, 0x0e, 0xa6, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0x9f, 0x0e, 0xa1, 0x0e, 0xa2, 0x0e, 0xa0, 0x0e, 0xa4, 0x0e, 0xa5, 0x0e, 0xa1, 0x0e, 0xa4, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x06, 0x46, 0x4b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x9b, 0x0e, 0xa7, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa1, 0x0e, 0xa0, 0x0e, 0xa2, 0x0e, 0xa4, 0x0e, 0xa0, 0x0e, 0xa5, 0x0e, 0xa6, 0x0e, 0xa2, 0x0e, 0xa5, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x81, 0x4c, 0x97, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xfe, 0x0f, 0x03, 0x0e, 0xff, 0x0f, 0x01, 0x0f, 0x00, 0x0e, 0xfe, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x00, 0x0f, 0x01, 0x0f, 0x03, 0x0f, 0x01, 0x0f, 0x01, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x13, 0x15, 0x46, 0x51, 0x4f, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0xa6, 0x0e, 0xb3, 0x0e, 0xaf, 0x0e, 0xae, 0x0e, 0xae, 0x0e, 0xab, 0x0e, 0xaf, 0x0e, 0xb0, 0x0e, 0xab, 0x0e, 0xb0, 0x0e, 0xb1, 0x0e, 0xaf, 0x0e, 0xb0, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xc8, 0x42, 0x67, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x68, 0x0e, 0x79, 0x0e, 0x74, 0x0e, 0x73, 0x0e, 0x73, 0x0e, 0x70, 0x0e, 0x74, 0x0e, 0x75, 0x0e, 0x6f, 0x0e, 0x75, 0x0e, 0x77, 0x0e, 0x73, 0x0e, 0x74, 0x03, 0x45, 0x45, 0x46, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x12, 0xbc, 0x42, 0x5b, 0x50, 0xff, 0xff, 0xff, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0d, 0x0e, 0x5e, 0x0e, 0x70, 0x0e, 0x6a, 0x0e, 0x69, 0x0e, 0x6a, 0x0e, 0x68, 0x0e, 0x6a, 0x0e, 0x6b, 0x0e, 0x66, 0x0e, 0x6d, 0x0e, 0x6e, 0x0e, 0x69, 0x0e, 0x6b, 0x03, 0x45, 0x45, 0x46})));
        caseList.put("current_change_small", new CaseInfo(51, toByteArray(new Integer[]{0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x0F, 0x48, 0xA1, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x64, 0x0F, 0x6F, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x71, 0x0F, 0x6D, 0x0F, 0x6E, 0x0F, 0x73, 0x0F, 0x6D, 0x0F, 0x6F, 0x0F, 0x72, 0x0F, 0x6D, 0x0F, 0x6C, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x12, 0x49, 0x0C, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x68, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x73, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x6F, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x6F, 0x0F, 0x6E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x18, 0x49, 0x77, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6D, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x74, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x74, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x17, 0x49, 0x71, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x6C, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x77, 0x0F, 0x73, 0x0F, 0x76, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x73, 0x0F, 0x73, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1C, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7C, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x76, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x1D, 0x49, 0xD6, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x7B, 0x0F, 0x7B, 0x0F, 0x77, 0x0F, 0x79, 0x0F, 0x7D, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x77, 0x0F, 0x77, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x40, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7D, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7D, 0x0F, 0x82, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x22, 0x4A, 0x3A, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x74, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7E, 0x0F, 0x7E, 0x0F, 0x7B, 0x0F, 0x7D, 0x0F, 0x81, 0x0F, 0x7C, 0x0F, 0x7E, 0x0F, 0x81, 0x0F, 0x7B, 0x0F, 0x7B, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x27, 0x4A, 0xA5, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x79, 0x0F, 0x82, 0x0F, 0x7E, 0x0F, 0x83, 0x0F, 0x82, 0x0F, 0x80, 0x0F, 0x82, 0x0F, 0x86, 0x0F, 0x81, 0x0F, 0x82, 0x0F, 0x85, 0x0F, 0x80, 0x0F, 0x7E, 0x03, 0x4E, 0x4F, 0x4F, 0x00, 0x35, 0x00, 0x02, 0x23, 0x24, 0x14, 0x52, 0x4E, 0x1E, 0x52, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x9E, 0x0F, 0xA1, 0x0F, 0x9F, 0x0F, 0xA4, 0x0F, 0xA3, 0x0F, 0xA0, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x0F, 0xA5, 0x0F, 0xA1, 0x0F, 0xA1, 0x03, 0x4E, 0x4F, 0x4F})));
        caseList.put("hexString", new CaseInfo(ByteUtil.hexStringToByteArray("018D2EED773816AB11005700055D00303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303000E4D1")));
        List<Integer> generateBlockCaseList = Arrays.asList(255, 223, 200, 150, 132, 100, 80, 64, 56, 55, 50, 48, 32);
        for (Integer blockLength : generateBlockCaseList) {
            caseList.put("generate_" + blockLength, new CaseInfo(generate(255, blockLength)));
        }
        caseList.put("loadFromFile", new CaseInfo(loadFromFile()));
        caseList.put("custom", new CaseInfo(new byte[]{0x35, 0x35, 0x35, 0x35, 0x35, 0x35, 0x35, 0x35, 0x35, 0x35}));
        // 181 + 49 * 10
        caseList.put("dynamics_671", new CaseInfo(181, 49, toByteArray(new Integer[]{0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xEA, 0x01, 0xEA, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xF1, 0x0C, 0xF1, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x27, 0x53, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x53, 0x54, 0x0F, 0x66, 0x0F, 0x79, 0x0F, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x4F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x06, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x43, 0x0F, 0x70, 0x0F, 0x79, 0x0F, 0x64, 0x0F, 0x76, 0x0F, 0x67, 0x0F, 0x6E, 0x0F, 0x68, 0x0F, 0x6B, 0x0F, 0x68, 0x0F, 0x5E, 0x0F, 0x6A, 0x0F, 0x5F, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xFA, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3A, 0x0F, 0x67, 0x0F, 0x71, 0x0F, 0x5B, 0x0F, 0x6D, 0x0F, 0x5E, 0x0F, 0x65, 0x0F, 0x5F, 0x0F, 0x62, 0x0F, 0x5F, 0x0F, 0x55, 0x0F, 0x61, 0x0F, 0x56, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xF5, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x36, 0x0F, 0x63, 0x0F, 0x6D, 0x0F, 0x57, 0x0F, 0x69, 0x0F, 0x59, 0x0F, 0x61, 0x0F, 0x5B, 0x0F, 0x5E, 0x0F, 0x5B, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x52, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x19, 0x48, 0x3F, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x50, 0x0F, 0x82, 0x0F, 0x87, 0x0F, 0x76, 0x0F, 0x84, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x6B, 0x0F, 0x7B, 0x0F, 0x6C, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xDF, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x27, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x46, 0x0F, 0x59, 0x0F, 0x48, 0x0F, 0x51, 0x0F, 0x49, 0x0F, 0x4F, 0x0F, 0x4A, 0x0F, 0x42, 0x0F, 0x4C, 0x0F, 0x42, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xD7, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x21, 0x0F, 0x4B, 0x0F, 0x57, 0x0F, 0x3F, 0x0F, 0x53, 0x0F, 0x41, 0x0F, 0x4B, 0x0F, 0x43, 0x0F, 0x48, 0x0F, 0x43, 0x0F, 0x3C, 0x0F, 0x45, 0x0F, 0x3D, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x01, 0x47, 0x14, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3F, 0x0F, 0x6E, 0x0F, 0x75, 0x0F, 0x62, 0x0F, 0x71, 0x0F, 0x64, 0x0F, 0x69, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x65, 0x0F, 0x59, 0x0F, 0x68, 0x0F, 0x59, 0x03, 0x54, 0x53, 0x540, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x01, 0x47, 0x12, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3F, 0x0F, 0x6E, 0x0F, 0x75, 0x0F, 0x62, 0x0F, 0x71, 0x0F, 0x64, 0x0F, 0x69, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x59, 0x0F, 0x68, 0x0F, 0x59, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xC6, 0x41, 0x98, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x14, 0x0F, 0x3D, 0x0F, 0x4A, 0x0F, 0x31, 0x0F, 0x45, 0x0F, 0x33, 0x0F, 0x3E, 0x0F, 0x35, 0x0F, 0x3B, 0x0F, 0x35, 0x0F, 0x2F, 0x0F, 0x37, 0x0F, 0x2F, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x2F, 0x4C, 0x2B, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x60, 0x0F, 0x95, 0x0F, 0x96, 0x0F, 0x89, 0x0F, 0x93, 0x0F, 0x8B, 0x0F, 0x8A, 0x0F, 0x8C, 0x0F, 0x87, 0x0F, 0x8C, 0x0F, 0x79, 0x0F, 0x8E, 0x0F, 0x79, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x07, 0x47, 0xDB, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x43, 0x0F, 0x73, 0x0F, 0x79, 0x0F, 0x68, 0x0F, 0x76, 0x0F, 0x6A, 0x0F, 0x6E, 0x0F, 0x6B, 0x0F, 0x6B, 0x0F, 0x6B, 0x0F, 0x5E, 0x0F, 0x6D, 0x0F, 0x5E, 0x03, 0x54, 0x53, 0x54})));
        caseList.put("dynamics_362", new CaseInfo(181, toByteArray(new Integer[]{0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xEA, 0x01, 0xEA, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xF1, 0x0C, 0xF1, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x27, 0x53, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x53, 0x54, 0x0F, 0x66, 0x0F, 0x79, 0x0F, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x4F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x06, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x43, 0x0F, 0x70, 0x0F, 0x79, 0x0F, 0x64, 0x0F, 0x76, 0x0F, 0x67, 0x0F, 0x6E, 0x0F, 0x68, 0x0F, 0x6B, 0x0F, 0x68, 0x0F, 0x5E, 0x0F, 0x6A, 0x0F, 0x5F, 0x03, 0x54, 0x53, 0x54, 0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xE0, 0x01, 0xE0, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xC8, 0x0C, 0xC8, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x07, 0x53, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x53, 0x54, 0x0F, 0x98, 0x0F, 0xA8, 0x0F, 0x71, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x50, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x45, 0x4E, 0x20, 0x50, 0x4F, 0x00, 0x00, 0xC1, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x71, 0x0F, 0xA8, 0x0F, 0xA6, 0x0F, 0x9B, 0x0F, 0xA2, 0x0F, 0x9E, 0x0F, 0x9A, 0x0F, 0x9F, 0x0F, 0x97, 0x0F, 0x9E, 0x0F, 0x89, 0x0F, 0xA0, 0x0F, 0x88, 0x03, 0x54, 0x53, 0x54})));
        // 181 + 49 * 10
        caseList.put("static_671", new CaseInfo(181, 49, toByteArray(new Integer[]{0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xEA, 0x01, 0xEA, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xF5, 0x0C, 0xF5, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x27, 0x54, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x54, 0x54, 0x0F, 0xBA, 0x0F, 0xCA, 0x0F, 0x91, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x50, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0B, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xCA, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC4, 0x0F, 0xC0, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAC, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0C, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC4, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0B, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x90, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0B, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0C, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC4, 0x0F, 0xBF, 0x0F, 0xBB, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0B, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC4, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xBF, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x15, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC4, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x15, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x70, 0x4E, 0x0E, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBB, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xBF, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0E, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC4, 0x0F, 0xBF, 0x0F, 0xBB, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x70, 0x4E, 0x10, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x90, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBB, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xBF, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAB, 0x03, 0x54, 0x54, 0x54})));
        caseList.put("static_362", new CaseInfo(181, toByteArray(new Integer[]{0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xEA, 0x01, 0xEA, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xF5, 0x0C, 0xF5, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x27, 0x54, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x54, 0x54, 0x0F, 0xBA, 0x0F, 0xCA, 0x0F, 0x91, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x50, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x71, 0x4E, 0x0B, 0x50, 0x50, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x91, 0x0F, 0xCA, 0x0F, 0xC7, 0x0F, 0xBE, 0x0F, 0xC4, 0x0F, 0xC0, 0x0F, 0xBC, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xC0, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAC, 0x03, 0x54, 0x54, 0x54, 0x00, 0xB3, 0x4C, 0x4C, 0x4B, 0x43, 0x56, 0x31, 0x39, 0x43, 0x4E, 0x41, 0x38, 0x38, 0x38, 0x39, 0x00, 0x00, 0x30, 0x37, 0x34, 0x31, 0x01, 0xEA, 0x01, 0xEA, 0xFF, 0xFF, 0xFF, 0xFF, 0x0C, 0xF5, 0x0C, 0xF5, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x06, 0x27, 0x54, 0x64, 0x02, 0x8A, 0x02, 0x8A, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01, 0x6C, 0x54, 0x54, 0x0F, 0xB9, 0x0F, 0xC9, 0x0F, 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x50, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x70, 0x4E, 0x10, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x90, 0x0F, 0xC9, 0x0F, 0xC7, 0x0F, 0xBD, 0x0F, 0xC3, 0x0F, 0xBF, 0x0F, 0xBB, 0x0F, 0xC0, 0x0F, 0xB9, 0x0F, 0xBF, 0x0F, 0xAA, 0x0F, 0xC1, 0x0F, 0xAA, 0x03, 0x54, 0x54, 0x54})));
        // 51 * 1
        caseList.put("dynamics_51", new CaseInfo(51, toByteArray(new Integer[]{0x00, 0x2F, 0x01, 0xCA, 0x13, 0xFA, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3A, 0x0F, 0x67, 0x0F, 0x71, 0x0F, 0x5B, 0x0F, 0x6D, 0x0F, 0x5E, 0x0F, 0x65, 0x0F, 0x5F, 0x0F, 0x62, 0x0F, 0x5F, 0x0F, 0x55, 0x0F, 0x61, 0x0F, 0x56, 0x03, 0x54, 0x53, 0x54})));
        // 51 * 5
        caseList.put("dynamics_255", new CaseInfo(51, toByteArray(new Integer[]{0x00, 0x2F, 0x01, 0xCA, 0x13, 0xFA, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3A, 0x0F, 0x67, 0x0F, 0x71, 0x0F, 0x5B, 0x0F, 0x6D, 0x0F, 0x5E, 0x0F, 0x65, 0x0F, 0x5F, 0x0F, 0x62, 0x0F, 0x5F, 0x0F, 0x55, 0x0F, 0x61, 0x0F, 0x56, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xF5, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x36, 0x0F, 0x63, 0x0F, 0x6D, 0x0F, 0x57, 0x0F, 0x69, 0x0F, 0x59, 0x0F, 0x61, 0x0F, 0x5B, 0x0F, 0x5E, 0x0F, 0x5B, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x52, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x19, 0x48, 0x3F, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x50, 0x0F, 0x82, 0x0F, 0x87, 0x0F, 0x76, 0x0F, 0x84, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x6B, 0x0F, 0x7B, 0x0F, 0x6C, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xDF, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x27, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x46, 0x0F, 0x59, 0x0F, 0x48, 0x0F, 0x51, 0x0F, 0x49, 0x0F, 0x4F, 0x0F, 0x4A, 0x0F, 0x42, 0x0F, 0x4C, 0x0F, 0x42, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xD7, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x21, 0x0F, 0x4B, 0x0F, 0x57, 0x0F, 0x3F, 0x0F, 0x53, 0x0F, 0x41, 0x0F, 0x4B, 0x0F, 0x43, 0x0F, 0x48, 0x0F, 0x43, 0x0F, 0x3C, 0x0F, 0x45, 0x0F, 0x3D, 0x03, 0x54, 0x53, 0x54})));
        // 51 * 10
        caseList.put("dynamics_510", new CaseInfo(51, toByteArray(new Integer[]{0x00, 0x2F, 0x01, 0xCA, 0x13, 0xFA, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3A, 0x0F, 0x67, 0x0F, 0x71, 0x0F, 0x5B, 0x0F, 0x6D, 0x0F, 0x5E, 0x0F, 0x65, 0x0F, 0x5F, 0x0F, 0x62, 0x0F, 0x5F, 0x0F, 0x55, 0x0F, 0x61, 0x0F, 0x56, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xF5, 0x44, 0x54, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x36, 0x0F, 0x63, 0x0F, 0x6D, 0x0F, 0x57, 0x0F, 0x69, 0x0F, 0x59, 0x0F, 0x61, 0x0F, 0x5B, 0x0F, 0x5E, 0x0F, 0x5B, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x52, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x19, 0x48, 0x3F, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x50, 0x0F, 0x82, 0x0F, 0x87, 0x0F, 0x76, 0x0F, 0x84, 0x0F, 0x78, 0x0F, 0x7B, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x79, 0x0F, 0x6B, 0x0F, 0x7B, 0x0F, 0x6C, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xDF, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x27, 0x0F, 0x51, 0x0F, 0x5D, 0x0F, 0x46, 0x0F, 0x59, 0x0F, 0x48, 0x0F, 0x51, 0x0F, 0x49, 0x0F, 0x4F, 0x0F, 0x4A, 0x0F, 0x42, 0x0F, 0x4C, 0x0F, 0x42, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xD7, 0x42, 0x60, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x21, 0x0F, 0x4B, 0x0F, 0x57, 0x0F, 0x3F, 0x0F, 0x53, 0x0F, 0x41, 0x0F, 0x4B, 0x0F, 0x43, 0x0F, 0x48, 0x0F, 0x43, 0x0F, 0x3C, 0x0F, 0x45, 0x0F, 0x3D, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x01, 0x47, 0x14, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3F, 0x0F, 0x6E, 0x0F, 0x75, 0x0F, 0x62, 0x0F, 0x71, 0x0F, 0x64, 0x0F, 0x69, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x65, 0x0F, 0x59, 0x0F, 0x68, 0x0F, 0x59, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x01, 0x47, 0x12, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x3F, 0x0F, 0x6E, 0x0F, 0x75, 0x0F, 0x62, 0x0F, 0x71, 0x0F, 0x64, 0x0F, 0x69, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x66, 0x0F, 0x59, 0x0F, 0x68, 0x0F, 0x59, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x13, 0xC6, 0x41, 0x98, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x14, 0x0F, 0x3D, 0x0F, 0x4A, 0x0F, 0x31, 0x0F, 0x45, 0x0F, 0x33, 0x0F, 0x3E, 0x0F, 0x35, 0x0F, 0x3B, 0x0F, 0x35, 0x0F, 0x2F, 0x0F, 0x37, 0x0F, 0x2F, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x2F, 0x4C, 0x2B, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x60, 0x0F, 0x95, 0x0F, 0x96, 0x0F, 0x89, 0x0F, 0x93, 0x0F, 0x8B, 0x0F, 0x8A, 0x0F, 0x8C, 0x0F, 0x87, 0x0F, 0x8C, 0x0F, 0x79, 0x0F, 0x8E, 0x0F, 0x79, 0x03, 0x54, 0x53, 0x54, 0x00, 0x2F, 0x01, 0xCA, 0x14, 0x07, 0x47, 0xDB, 0x50, 0x4F, 0x00, 0x00, 0xC4, 0x00, 0x21, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x0D, 0x0F, 0x43, 0x0F, 0x73, 0x0F, 0x79, 0x0F, 0x68, 0x0F, 0x76, 0x0F, 0x6A, 0x0F, 0x6E, 0x0F, 0x6B, 0x0F, 0x6B, 0x0F, 0x6B, 0x0F, 0x5E, 0x0F, 0x6D, 0x0F, 0x5E, 0x03, 0x54, 0x53, 0x54})));
        return caseList;
    }
}
