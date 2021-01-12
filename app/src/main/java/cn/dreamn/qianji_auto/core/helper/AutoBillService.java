package cn.dreamn.qianji_auto.core.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.Objects;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.activity.MainActivity;


public class AutoBillService extends Service {

    public static final String check_auto_mode = "check_auto_mode";

    public static boolean isStart = false;

    private RemoteViews remoteViews;

    private Notification notification;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        start();
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

    private void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(check_auto_mode);

    }

    private void startServer() {
        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Intent intent = new Intent();
        intent.setAction(check_auto_mode);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);


        intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("com.mutangtech.qianji", "com.mutangtech.qianji.bill.add.AddBillActivity");
        intent.setComponent(componentName);

        PendingIntent activity_qianji = PendingIntent.getActivity(this, 0, intent, 0);
        

        this.remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notify_auto_bill);
        this.remoteViews.setOnClickPendingIntent(R.id.icon, activity);
        this.remoteViews.setOnClickPendingIntent(R.id.btn_qianji, activity_qianji);
        this.remoteViews.setOnClickPendingIntent(R.id.mode_layout, broadcast);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_monry);
        builder.setContent(this.remoteViews);
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setCustomBigContentView(this.remoteViews);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("AutoBillService", "自动记账",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder.setChannelId("AutoBillService");
        }
        this.notification = builder.build();
        Notification notification = this.notification;
        notification.defaults = 1;
        notification.flags = 2;
        startForeground(6699, notification);
    }

    /* access modifiers changed from: private */
    public void notification() {
        RemoteViews remoteViews = this.remoteViews;
        if (remoteViews != null) {
            remoteViews.setTextViewText(R.id.mode_status, "自动记账运行中");
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(6699, this.notification);
        }
    }


}