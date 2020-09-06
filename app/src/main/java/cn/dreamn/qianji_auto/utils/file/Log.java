package cn.dreamn.qianji_auto.utils.file;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.Integer.parseInt;

/**
 * 一个将log保存到txt的工具类
 * Created by CrystalChen on 2016/4/19.
 */
public class Log {
    @SuppressLint("SdCardPath")
    static private boolean androidLogOn = true;
    static private boolean myLogOn = true;


    public static final int INFO = 2;
    public static final int ERROR = 3;
    public static final int FAIL = 4;
    public static final int SUCCESS = 5;



    public static void i(String msg)  {

        String defalutTag = "myLog";
        if (androidLogOn)
            android.util.Log.i(defalutTag, msg);
        if (myLogOn)
            printToFile(defalutTag, msg);
    }

    public static void i(String tag, String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            android.util.Log.i(tag, msg);
        if (myLogOn)
            printToFile(tag, msg);
    }





    public static String readFile(String strFile){
        try{
            InputStream is = new FileInputStream(strFile);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);

        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }




    // 将字符串写入到文本文件中
    private static void printToFile(String tag, String strcontent) {


        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        @SuppressLint("DefaultLocale") String timeString = String.format("%02d:%02d:%02d", hour, minute, second);


        int time;
        try{
            time=parseInt(myFile.get("file.ini"));
        }catch(Exception e){
            time=0;
        }
        String time1= year + String.valueOf(month)+ day;
        if(time!=parseInt(time1)){
            myFile.write("file.ini",time1,true);
            String fileName = "Log";
            myFile.del(fileName +".json");
        }

        put(strcontent,tag,timeString);

    }



    public static JSONArray get(){
        return Storage.type(Storage.Log).getJSONArray("log");
    }

    private static void put(String log,String tag,String time){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("log",log);
        jsonObject.put("tag",tag);
        jsonObject.put("time",time);
        JSONArray jsonArray=Storage.type(Storage.Log).getJSONArray("log");
        jsonArray.add(jsonObject);
        Storage.type(Storage.Log).set("log",jsonArray);
    }


}