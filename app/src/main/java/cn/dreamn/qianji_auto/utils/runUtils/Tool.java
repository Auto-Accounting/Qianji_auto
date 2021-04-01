package cn.dreamn.qianji_auto.utils.runUtils;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.broadcasts.NotificationClickReceiver;

public class Tool {
    public static void clipboard(Context context, String text) {

        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static void goUrl(Context context, String url) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static void goToMarket(String packageName){
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception ignored) {
        }
    }

    public static void notice(Context context, String title, String content, BillInfo billInfo) {
        Log.d("发送记账通知");
        Notification mNotification;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent =new Intent (context, NotificationClickReceiver.class);
        intent.putExtra("billinfo",billInfo.toString());
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "记账通知", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context, "1");

            mNotification = builder
                    .setSmallIcon(R.drawable.ic_money)
                    .setTicker(content)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();


        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
            builder.setSmallIcon(R.drawable.ic_money)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            mNotification = builder.build();

        }

        mNotificationManager.notify((int) (Math.random() * 1000), mNotification);

    }
    @SuppressLint("SimpleDateFormat")
    public static String getTime(String s) {
        return getTime(s,0);
    }
    @SuppressLint("SimpleDateFormat")
    public static String getTime(String s,int day) {
        long time=System.currentTimeMillis();
        time=time+day*24*60*60*1000;
        return (new SimpleDateFormat(s)).format(new Date(time));
    }


}
