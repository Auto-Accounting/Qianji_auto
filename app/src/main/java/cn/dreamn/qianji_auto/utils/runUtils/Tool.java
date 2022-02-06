package cn.dreamn.qianji_auto.utils.runUtils;

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
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.broadcast.NotificationClickReceiver;
import cn.dreamn.qianji_auto.data.local.FileUtils;

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

    public static void goToCoolMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //跳转酷市场
        intent.setClassName("com.coolapk.market", "com.coolapk.market.view.app.AppViewV8Activity");
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            goUrl(context, "https://www.coolapk.com/apk/" + packageName);
        }
    }

    public static void notice(Context context, String title, String content) {
        Log.d("发送记账通知");
        Notification mNotification;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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

        mNotificationManager.notify(86922, mNotification);

    }


    public static void restartApp(Activity context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.finish();
    }

    public static void shareFile(Context context, String filePath) {
        Intent shareIntent2 = new Intent();
        Uri uri = FileProvider.getUriForFile(context, "cn.dreamn.qianji_auto.fileprovider", new File(filePath));
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
            Log.d("写入缓存异常！" + e);
        }
    }

    public static void stopApp(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses(packageName);
    }

    public static String getCacheFileName(Context mContext, String fileName) {
        String path = mContext.getExternalCacheDir().getPath() + "/";
        Log.d(path + fileName);
        return path + fileName;
    }

    public static String getJson(Context context, String file) {
        String language = Locale.getDefault().getLanguage();
        String path = "json/" + language + "/" + file + ".json";
        String path2 = "json/zh/" + file + ".json";
        return getAssert(context, path, path2);
    }

    public static String getAssert(Context context, String fileName, String fileName2) {
        Log.d("assert_path:" + fileName + "\nassert_path2:" + fileName2);
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


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static Bundle class2Bundle(Object cls) {
        if (cls == null) return null;
        Field[] fields = cls.getClass().getDeclaredFields();
        Bundle bundle = new Bundle();
        for (Field field : fields) {

            try {
                String name = field.getName();    //获取属性的名字
                String type = field.getGenericType().toString();    //获取属性的类型
                if (type.equals("class java.lang.String")) {   //如果type是类类型，则前面包含"class "，后面跟类名
                    //  java.lang.reflect.Method m = cls.getClass().getMethod("get" + name);
                    String value = (String) field.get(cls); //调用getter方法获取属性值
                    if (value != null) {
                        bundle.putString(name, value);
                    }
                }
                if (type.equals("int")) {
                    // java.lang.reflect.Method m = cls.getClass().getMethod("get" + name);
                    Integer value = (Integer) field.get(cls);
                    if (value != null) {
                        bundle.putInt(name, value);
                    }
                }
            } catch (Throwable e) {

                Log.d(e.toString());
            }
        }
        return bundle;
    }

    public static JSONObject bundle2JSONObject(Bundle bundle) {
        Set<String> keySet = bundle.keySet();
        JSONObject jsonObject = new JSONObject();
        for (String key : keySet) {
            jsonObject.put(key, bundle.get(key));
        }
        return jsonObject;
    }

    public static JSONObject class2JSONObject(Object cls) {
        if (cls == null) return null;
        Field[] fields = cls.getClass().getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        for (Field field : fields) {

            try {
                String name = field.getName();    //获取属性的名字
                String type = field.getGenericType().toString();    //获取属性的类型
                if (type.equals("class java.lang.String")) {   //如果type是类类型，则前面包含"class "，后面跟类名
                    //  java.lang.reflect.Method m = cls.getClass().getMethod("get" + name);
                    String value = (String) field.get(cls); //调用getter方法获取属性值
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("int")) {
                    // java.lang.reflect.Method m = cls.getClass().getMethod("get" + name);
                    Integer value = (Integer) field.get(cls);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
            } catch (Throwable e) {

                Log.d(e.toString());
            }
        }
       // Log.d(jsonObject.toString());
        return jsonObject;
    }


}
