package cn.dreamn.qianji_auto.utils.runUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.core.broadcast.NotificationClickReceiver;
import cn.dreamn.qianji_auto.utils.files.FileUtils;

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

    public static void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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
        return getTime(s, 0);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String s, int day) {
        long time = System.currentTimeMillis();
        time = time + (long) day * 24 * 60 * 60 * 1000;
        return (new SimpleDateFormat(s)).format(new Date(time));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getShortTime(long date, String format) {

        return (new SimpleDateFormat(format)).format(new Date(date));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String s, long time) {
        return (new SimpleDateFormat(s)).format(new Date(time));
    }

    public static void restartApp(Activity context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.finish();
    }

    public static void shareFile(Context context, String filePath) {
        Intent shareIntent2 = new Intent();
        Uri uri = FileProvider.getUriForFile(context, "cn.dreamn.qianji_auto.fileprovider.share", new File(filePath));
        // grantUriPermission(getPackageName(),uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent2.putExtra(Intent.EXTRA_STREAM, uri);
        //重点:针对7.0以上的操作

        shareIntent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent2.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
        shareIntent2.setAction(Intent.ACTION_SEND);
        shareIntent2.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent2, "分享到"));
    }

    public static void writeToCache(Context mContext, String fileName, String data) {
        String path = mContext.getExternalCacheDir().getPath() + "/";
        String file = path + fileName;
        FileUtils.makeRootDirectory(path);
        FileUtils.del(file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.i("写入缓存异常！" + e);
        }
    }

    public static void stopApp(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses(packageName);
    }

    public static String getCacheFileName(Context mContext, String fileName) {
        String path = mContext.getExternalCacheDir().getPath() + "/";
        return path + fileName;
    }

    public static String getJson(Context context, String file) {
        String language = Locale.getDefault().getLanguage();
        String path = "json/" + language + "/" + file + ".json";
        String path2 = "json/zh/" + file + ".json";
        return getAssert(context, path, path2);
    }

    public static String getAssert(Context context, String fileName, String fileName2) {
        String ret = "";
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            int len = is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            ret = new String(buffer);
            is.close();
        } catch (Exception e) {
            try {
                InputStream is = context.getResources().getAssets().open(fileName2);
                int len = is.available();
                byte[] buffer = new byte[len];
                is.read(buffer);
                ret = new String(buffer);
                is.close();
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }
        return ret;
    }

//dateTime.getTimeInMillis()

}
