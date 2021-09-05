package cn.dreamn.qianji_auto.ui.utils;

public class B64 {
    public static String encode(String data) {

        return Base16.encode(data);
 /*       if (data == null) return "";
        try {
            return URLEncoder.encode(new String(Base64.getEncoder().encode(data.getBytes())), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("出现异常" + e.toString());
            return "";
        }*/

    }

    public static String decode(String data) {

        return Base16.decode(data);
       /* if (data == null) return "";
        try {
            return URLDecoder.decode(new String(Base64.getDecoder().decode(URLDecoder.decode(data, "UTF-8").getBytes())), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("出现异常" + e.toString());
            return "";
        }*/

    }
}
