package cn.dreamn.qianji_auto.utils.active;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONArray;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.dreamn.qianji_auto.utils.file.Log;

import static com.xuexiang.xui.XUI.getContext;


public class Fun {
    public static final String TYPE_PAY="0";
    public static final String TYPE_GAIN="1";
    private final static String TAG = "Fun-Core";

    /**
     * 将HashMap的String转回来
     * @param str String
     * @return HashMap<String,String>
     */
    public static HashMap<String, String> stringToHashMap(String str){
        String[] strs=str.split(", ");
        HashMap<String, String> map = new HashMap<>();
        for (String string : strs) {
            String[] ms = string.split("=");
            map.put(ms[0],string.replace(ms[0]+"=",""));
        }
        return map;
    }

    public static void sendNotify(Context context, String title, String content, String url){
        Log.i("自动记账", "发送通知");
        Notification mNotification;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context,"1");

            mNotification = builder
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setTicker(content)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();


        }else{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"channelid1");
            builder.setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
            mNotification=builder.build();

        }

        mNotificationManager.notify((int) (Math.random() * 1000), mNotification);
        Log.i("自动记账", "结束通知");
    }

    /*
    * 过滤银行卡信息
    * */
    public static String filter(String s) {
        return s.replaceAll("[^\\u4e00-\\u9fa5]", "").replace("储蓄卡","").replace("信用卡","");
    }

    /**
     * 获取浮点数
     * @param s String
     * @return String
     */
    public static String getMoney(String s){
        String regex =  "\\d*[.]\\d*";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(s);

        while (m.find()) {
            if (!"".equals(m.group()))
                return m.group();
        }

        return "0";
    }



    public static void browser(@NotNull Context context, String url){

        Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url.trim());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(content_url);
        context.startActivity(intent);

    }
    public static void gotoQianji(@NotNull Context context, String url){
        Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url.trim());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static void clipboard(String text){

        ClipboardManager cm = (ClipboardManager) Objects.requireNonNull(getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label",text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


}
