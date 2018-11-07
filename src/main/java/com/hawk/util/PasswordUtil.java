package com.hawk.util;

import java.security.MessageDigest;
import java.util.UUID;

public class PasswordUtil {

    private static String MD5(String pwd) {
        // 用于加密的字符
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            // 使用平台的默认字符集将此 String 编码为 byte 序列，并将结果存储到一个新的 byte 数组中
            byte[] btInput = pwd.getBytes();
            // 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // MessageDigest 对象通过使用 update 方法处理数据，使用指定的 byte 数组更新摘要
            mdInst.update(btInput);
            // 摘要更新之后，通过调用 digest() 执行哈希计算，获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = md5String[byte0 >>> 4 & 0xf];
                str[k++] = md5String[byte0 & 0xf];
            }
            // 返回经过加密后的字符串
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String get5UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    }

    public static String get10UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    public static String getPassword(String password, String salt) {
        return PasswordUtil.MD5(PasswordUtil.MD5(password) + salt);
    }
}
