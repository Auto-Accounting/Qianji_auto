package cn.dreamn.qianji_auto.ui.utils;

import java.io.ByteArrayOutputStream;

public class Base16 {
    private static final String hexString = "0123456789ABCDEFabcdef";


    public static String encode(String msg) {
        byte[] bytes = msg.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //转换hex编码
        for (byte b : bytes) {
            sb.append(Integer.toHexString(b + 0x800).substring(1));
        }

//转换后的代码为c7d7a3acc4e3bac3
        msg = sb.toString();
        return msg;
    }


//把hex编码转换为string

    public static String decode(String bytes) {
        bytes = bytes.toUpperCase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return baos.toString();
    }
}