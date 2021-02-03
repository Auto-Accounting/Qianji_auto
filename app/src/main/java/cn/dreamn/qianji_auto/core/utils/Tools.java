/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.utils;

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
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.xuexiang.xutil.data.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Tools {
    public static void goUrl(Context context, String url){
        Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static void shareFile(Context context, String filePath){
        Intent shareIntent2 = new Intent();
        Uri uri = FileProvider.getUriForFile(context,"cn.dreamn.qianji_auto.fileprovider", new File(filePath));
        // grantUriPermission(getPackageName(),uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent2.putExtra(Intent.EXTRA_STREAM, uri);
        //重点:针对7.0以上的操作

        shareIntent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent2.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
        shareIntent2.setAction(Intent.ACTION_SEND);
        shareIntent2.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent2, "分享到"));
    }

    public static String writeToCache(Context mContext,String fileName,String data) {
        String path = mContext.getExternalCacheDir().getPath()+"/";
        String file=path+fileName;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
        }catch (Exception e){
            Logs.i("写入缓存异常！"+e);
        }
        return file;
    }

    /**
     * "yyyy-MM-dd HH:mm:ss"
     * @param format
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime(String format){
        return DateUtils.getNowString(new SimpleDateFormat(format));
    }
    public static void sendNotify(Context context, String title, String content, String url){
       Logs.d("自动记账", "发送通知");
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
                    .setSmallIcon(R.drawable.ic_monry)
                    .setTicker(content)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();


        }else{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"1");
            builder.setSmallIcon(R.drawable.ic_monry)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
            mNotification=builder.build();

        }

        mNotificationManager.notify((int) (Math.random() * 1000), mNotification);
       // Log.i("自动记账", "结束通知");
    }

    public static void clipboard(Context context,String text){

        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label",text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


}
