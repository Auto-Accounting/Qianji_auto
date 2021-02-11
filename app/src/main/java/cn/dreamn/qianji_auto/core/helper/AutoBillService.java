package cn.dreamn.qianji_auto.core.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.activity.MainActivity;


public class AutoBillService extends Service {

    public static boolean isStart = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int i, int i2) {
        isStart = true;
        stopForeground(true);
        startServer();
        return super.onStartCommand(intent, i, i2);
    }

    public void onDestroy() {
        super.onDestroy();
        isStart = false;
        stopForeground(true);

    }


    private void startServer() {
        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("com.mutangtech.qianji", "com.mutangtech.qianji.bill.add.AddBillActivity");
        intent.setComponent(componentName);

        PendingIntent activity_qianji = PendingIntent.getActivity(this, 0, intent, 0);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notify_auto_bill);
        remoteViews.setOnClickPendingIntent(R.id.icon, activity);
        remoteViews.setOnClickPendingIntent(R.id.btn_qianji, activity_qianji);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_monry);
        builder.setContent(remoteViews);
        builder.setCustomBigContentView(remoteViews);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("AutoBillService", "自动记账", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder.setChannelId("AutoBillService");
        }
        Notification notification = builder.build();
        notification.defaults = 1;
        notification.flags = 2;
        startForeground(6699, notification);
    }


}