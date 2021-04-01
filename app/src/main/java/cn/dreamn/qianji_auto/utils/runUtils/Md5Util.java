package cn.dreamn.qianji_auto.utils.runUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

    /**
     * @param args
     * @return
     */
    public static String main(String args) {
        return encoder(args);
    }

    /**给指定字符串按照md5算法去加密
     * @param psd   需要加密的密码
     * @return
     */
    private static String encoder(String psd) {
        try {
            //1,指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //2,将需要加密的字符串中转换成byte类型的数组,然后进行随机哈希过程
            byte[] bs = digest.digest(psd.getBytes());

            //3,循环遍历bs,然后让其生成32位字符串,固定写法
            //4,拼接字符串过程
            StringBuilder stringBuffer = new StringBuilder();
            for (byte b : bs) {
                int i = b & 0xff;
                //int类型的i需要转换成16机制字符
                String hexString = Integer.toHexString(i);

                if(hexString.length()<2){
                    hexString = "0"+hexString;
                }
                stringBuffer.append(hexString);
            }
            //5,打印测试
            System.out.println(stringBuffer.toString());
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
