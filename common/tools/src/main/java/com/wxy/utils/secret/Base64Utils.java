package com.wxy.utils.secret;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.charset.StandardCharsets;

/** */

/**
 * <p>
 * BASE64编码解码工具包
 * </p>
 * <p>
 * 依赖javabase64-1.3.1.jar
 * </p>
 */
public class Base64Utils {

    /** *//**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /** *//**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        Base64 base64Decoder = new Base64();
        return base64Decoder.decode(base64);
    }

    /** *//**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        Base64 base64Decoder = new Base64();
        return new String(base64Decoder.encode(bytes));
    }

    /** *//**
     * <p>
     * 将文件编码为BASE64字符串
     * </p>
     * <p>
     * 大文件慎用，可能会导致内存溢出
     * </p>
     *
     * @param filePath 文件绝对路径
     * @return
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /** *//**
     * <p>
     * BASE64字符串转回文件
     * </p>
     *
     * @param filePath 文件绝对路径
     * @param base64 编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /** *//**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /** *//**
     * <p>
     * 二进制数据写文件
     * </p>
     *
     * @param bytes 二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }

    public static void main(String[] args) {
        String url = "https://www.baidu.com";
        System.out.println("url:" + url);
        // 标准的 Base64 编码、解码
        byte[] encoded = java.util.Base64.getEncoder().encode(url.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
        System.out.println("Url Safe Base64 encoded:" + new String(encoded));
        System.out.println("Url Safe Base64 decoded:" + new String(decoded));
        // URL 安全的 Base64 编码、解码
        byte[] encoded2 = java.util.Base64.getUrlEncoder().encode(url.getBytes(StandardCharsets.UTF_8));
        byte[] decoded2 = java.util.Base64.getUrlDecoder().decode(encoded2);
        System.out.println("Base64 encoded:" + new String(encoded2));
        System.out.println("Base64 decoded:" + new String(decoded2));
    }

}
