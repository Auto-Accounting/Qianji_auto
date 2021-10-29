package cn.dreamn.qianji_auto.utils.runUtils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import cn.dreamn.qianji_auto.R;

public class NotifyProcess {
    static NotifyProcess notifyProcess;
    Notification.Builder o_builder;
    NotificationCompat.Builder x_builder;

    public static NotifyProcess getInstance(Context context) {
        if (notifyProcess == null) {
            notifyProcess = new NotifyProcess();
            notifyProcess.create(context);
        }
        return notifyProcess;
    }

    private NotifyProcess create(Context context) {
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "同步进度", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context, "1");

            o_builder = builder
                    .setSmallIcon(R.drawable.ic_money);
            // .setTicker(content)
            // .setContentTitle(title)
            // .setContentText(content)
            // .setContentIntent(pendingIntent)

            // .build();


        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
            x_builder = builder.setSmallIcon(R.drawable.ic_money);
        }
        return this;
    }

    public NotifyProcess setTitle(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            o_builder.setContentTitle(title);
        } else {
            x_builder.setContentTitle(title);
        }
        return this;
    }

    public NotifyProcess setBody(String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            o_builder.setContentTitle(body);
        } else {
            x_builder.setContentTitle(body);
        }
        return this;
    }

    public NotifyProcess setProcess(int current, int max) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            o_builder.setProgress(max, current, false);
        } else {
            x_builder.setProgress(max, current, false);
        }
        return this;
    }

    public void send(Context context) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.notify(2000, o_builder.build());
        } else {
            manager.notify(2000, x_builder.build());
        }
    }

    public void clear(Context context) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(2000);
    }

}
