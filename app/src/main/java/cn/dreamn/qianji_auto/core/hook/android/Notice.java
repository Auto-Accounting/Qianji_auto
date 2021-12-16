package cn.dreamn.qianji_auto.core.hook.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import cn.dreamn.qianji_auto.core.hook.template.android.AndroidBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Notice extends AndroidBase {

    @Override
    public void hookBefore() {

    }


    @Override
    public void hookFirst() {
        //public void onNotificationPosted(StatusBarNotification sbn) {
        //        // optional
        //    }
        /**/
        // com.android.server.notification.NotificationManagerService
        // void enqueueNotificationInternal(final String pkg, final String opPkg, final int callingUid,final int callingPid, final String tag, final int id, final Notification notification,int incomingUserId) {

     /*   try{


            Class<?> NotificationManagerService = Class.forName("com.android.server.notification.NotificationManagerService");
            XposedHelpers.findAndHookMethod(NotificationManagerService, "enqueueNotificationInternal", String.class, String.class, int.class, int.class, String.class, int.class, Notification.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    final String pkg = (String)param.args[0];
                    final String opPkg = (String)param.args[1];
                    final int callingUid = (int)param.args[2];
                    final int callingPid = (int)param.args[3];
                    final String tag = (String)param.args[4];
                    final int id = (int)param.args[5];
                    final Notification notification = (Notification)param.args[6];
                    int incomingUserId = (int)param.args[7];

                    final String noChannelStr = "Notice============> "
                                       + "pkg=" + pkg
                                    //   + ", channelId=" + channelId
                                      + ", id=" + id
                                       + ", tag=" + tag
                                    + ", opPkg=" + opPkg
                                 + ", callingUid=" + callingUid
                                    + ", callingPid=" + callingPid
                                    + ", incomingUserId=" + incomingUserId
                                 //     + ", notificationUid=" + notificationUid
                                     + ", notification=" + notification;
                    utils.log(noChannelStr);
                }
            });
        }catch (Exception e){
            utils.log("ERROR:"+e.toString());
            e.printStackTrace();
        }*/

        try {
            XposedHelpers.findAndHookMethod(NotificationManager.class, "notify"
                    , String.class, int.class, Notification.class
                    , new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            detailNotice((Notification) param.args[2], null);

                        }
                    });
        } catch (Throwable e) {
            try {
                XposedHelpers.findAndHookMethod(NotificationManager.class, "notify", int.class, Notification.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        detailNotice((Notification) param.args[1], null);
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


    private void detailNotice(Notification notification, String pkg) {
        //获得包名
        String aPackage = pkg;
        utils.log("pkg:" + pkg + ",notification:" + notification.toString());
        Bundle bundle = notification.extras;
        if (bundle == null) {
            utils.log("捕获到了通知数据，但是通知内容为空");
            return;
        }
        String title = bundle.getString(Notification.EXTRA_TITLE);
        String text = bundle.getString(Notification.EXTRA_TEXT);
        if (title == null || text == null) {
            utils.log("捕获到了通知数据，但是通知内容为空");
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


        utils.log("包名:" + aPackage + "\n标题:" + title + "\n主体" + text, true);

        String s = "title=" + title + ",body=" + text;
        utils.sendString(s, "notice", aPackage);
        //转发数据给自动记账
    }

    @Override
    public String getAppName() {
        return "系统通知";
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