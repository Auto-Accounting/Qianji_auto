package cn.dreamn.qianji_auto.core.broadcast;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.hjq.toast.ToastUtils;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;

public class NotificationMonitor extends NotificationListenerService {
    public static void start(Context ctx) {
        PermissionUtils permissionUtils = new PermissionUtils(ctx);
        if (permissionUtils.isNotificationListenersEnabled()) {
            PackageManager pm = ctx.getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(ctx, NotificationMonitor.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            pm.setComponentEnabledSetting(new ComponentName(ctx, NotificationMonitor.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            ToastUtils.show("通知监听服务已启动，请保持自动记账后台运行");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String aPackage = sbn.getPackageName();
        // 获取接收消息的抬头
        String title = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String text = extras.getString(Notification.EXTRA_TEXT);


        //不在监控范围不转发
        if (!AppStatus.isDebug()) {//非Debug模式
            SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getApplicationContext(), "apps", Context.MODE_PRIVATE);

            String[] s2 = sharedPreferences.getString("apps", "").split(",");
            if (!isIn(s2, aPackage)) return;
        }


        Log.i("包名:" + aPackage + "\n标题:" + title + "\n主体" + text);

        String s = "title=" + title + ",body=" + text;


        Bundle bundle = new Bundle();
        bundle.putString("data", s);
        bundle.putString("app_identify", "notice");
        //"cn.dreamn.qianji_auto.XPOSED"
        bundle.putString("app_package", aPackage);
        Intent intent = new Intent("cn.dreamn.qianji_auto.XPOSED");
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        getApplicationContext().sendBroadcast(intent, null);

    }

    private boolean isIn(String[] packages, String pack) {
        boolean flag = false;
        for (String package1 : packages) {
            if (pack.equals(package1)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
