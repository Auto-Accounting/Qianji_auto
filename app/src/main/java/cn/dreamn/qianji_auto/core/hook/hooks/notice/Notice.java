package cn.dreamn.qianji_auto.core.hook.hooks.notice;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Notice extends hookBase {

    static final hookBase self = new Notice();

    public static hookBase getInstance() {
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            XposedHelpers.findAndHookMethod(NotificationManager.class, "notify"
                    , String.class, int.class, Notification.class
                    , new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            detailNotice((Notification) param.args[2]);

                        }
                    });
        } catch (Throwable e) {
            try {
                XposedHelpers.findAndHookMethod(NotificationManager.class, "notify", int.class, Notification.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        detailNotice((Notification) param.args[1]);
                    }
                });
            } catch (Throwable e3) {

            }

        }
        try {
            XposedHelpers.findAndHookMethod(NotificationManager.class, "notifyAsPackage", String.class, String.class, int.class, Notification.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    detailNotice((Notification) param.args[3], (String) param.args[0]);
                }
            });
        } catch (Throwable e2) {

        }

    }


    private void detailNotice(Notification notification) {
        detailNotice(notification, null);
    }

    private void detailNotice(Notification notification, String pkg) {

        //获得包名
        String aPackage = pkg;
        utils.log("系统通知 notification:" + notification.toString());

    /*    if (Build.VERSION.SDK_INT >= 28) {
            String ids = notification.getChannelId();
            //mipush|com.campmobile.snowcamera|
            if (ids != null) {
                String[] idss = ids.split("\\|");
                if (idss.length >= 2) {
                    aPackage = idss[1];
                }
            }

        }*/

        Bundle bundle = notification.extras;
        if (bundle == null) {
            utils.log("系统通知 捕获到了通知数据，但是通知内容（Bundle）为空");
            return;
        }

        //去重
        long t = System.currentTimeMillis();
        String last = utils.readData("lastNotification");
        String time = utils.readData("lastNotificationTime");
        utils.writeData("lastNotification", bundle.toString());
        utils.writeData("lastNotificationTime", String.valueOf(t));

        if (time.equals("")) {
            time = "0";
        }

        if (last.equals(bundle.toString())) {
            if (t - Long.parseLong(time) <= 1000) {
                return;
            }
        }

        String title = bundle.getString(Notification.EXTRA_TITLE);
        String text = bundle.getString(Notification.EXTRA_TEXT);
        if (title == null || text == null) {
            utils.log("系统通知 捕获到了通知数据，但是通知内容为空");
            return;
        }
        //空数据不要

        ApplicationInfo applicationInfo = (ApplicationInfo) bundle.get("android.appInfo");
        if (aPackage == null)
            aPackage = applicationInfo.packageName;


        //收到支付宝支付通知后,自动拉起支付宝
        if (aPackage.contains("com.eg.android.AlipayGphone") && title.equals("秋风")) {
            return;
        }
        //不在监控范围不转发
        if (!utils.isDebug()) {//非Debug模式
            String[] s2 = utils.readDataByApp("apps", "apps").split(",");
            if (!isIn(s2, aPackage)) return;
        }


        utils.log("系统通知 包名:" + aPackage + "\n标题:" + title + "\n主体" + text, true);

        String s = "title=" + title + ",body=" + text;
        utils.sendString(s, "notice", aPackage);
        //转发数据给自动记账
    }

    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "系统通知";
    }


    @Override
    public boolean needHelpFindApplication() {
        return true;
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

    @Override
    public int hookIndex() {
        return 1;
    }
}