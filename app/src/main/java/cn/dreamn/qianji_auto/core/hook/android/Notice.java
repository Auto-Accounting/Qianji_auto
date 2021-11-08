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
        try {
            XposedHelpers.findAndHookMethod(NotificationManager.class, "notify"
                    , String.class, int.class, Notification.class
                    , new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            detailNotice((Notification) param.args[2], null);

                        }
                    });
        } catch (Throwable e) {
            try {
                XposedHelpers.findAndHookMethod(NotificationManager.class, "notify", int.class, Notification.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        detailNotice((Notification) param.args[1], null);
                    }
                });
            } catch (Throwable e3) {
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

        }


    }


    private void detailNotice(Notification notification, String pkg) {
        //获得包名
        String aPackage = pkg;
        Bundle bundle = notification.extras;
        if (bundle == null) {
            utils.log("通知数据：describeContents->" + notification.describeContents(), true);
            utils.log("通知数据：tickerText->" + notification.tickerText, true);
            utils.log("通知数据：null", true);
            return;
        } else {
            if (utils.isDebug())
                utils.log("通知数据：" + bundle.toString());
            else {
                utils.log("通知数据：非调试模式");
            }
        }
        //空数据不要
        String title = bundle.get("android.title").toString();
        String text = bundle.get("android.text").toString();
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