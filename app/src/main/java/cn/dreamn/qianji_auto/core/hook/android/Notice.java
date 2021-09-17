package cn.dreamn.qianji_auto.core.hook.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import java.util.Arrays;

import cn.dreamn.qianji_auto.core.hook.template.android.AndroidBase;
import cn.dreamn.qianji_auto.utils.runUtils.Cmd;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Notice extends AndroidBase {

    @Override
    public void hookBefore() {

    }

    @Override
    public void hookFirst() {
        XposedHelpers.findAndHookMethod(NotificationManager.class, "notify"
                , String.class, int.class, Notification.class
                , new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        utils.log("notice:beforeHookedMethod", true);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        utils.log("notice:afterHookedMethod", true);
                        utils.log("methodHookParam.args:  " + Arrays.toString(param.args), true);
                        //通过param拿到第三个入参notification对象
                        Notification notification = (Notification) param.args[2];
                        utils.log("loadpackage:" + notification.toString(), true);
                        //获得包名
                        String aPackage = "";
                        Bundle bundle = notification.extras;
                        if (bundle == null) {
                            utils.log("通知数据：describeContents->" + notification.describeContents(), true);
                            utils.log("通知数据：tickerText->" + notification.tickerText, true);
                            utils.log("通知数据：null", true);
                            return;
                        }
                        //空数据不要
                        String title = bundle.get("android.title").toString();
                        String text = bundle.get("android.text").toString();
                        ApplicationInfo applicationInfo = (ApplicationInfo) bundle.get("android.appInfo");
                        aPackage = applicationInfo.packageName;

                        utils.log("通知数据：" + bundle.toString());

                        //收到支付宝支付通知后,自动拉起支付宝
                        if (aPackage.contains("com.eg.android.AlipayGphone")) {
                            Cmd.exec(new String[]{
                                    "am force-stop com.eg.android.AlipayGphone",
                                    "sleep 1",
                                    "am start -n com.eg.android.AlipayGphone/com.eg.android.AlipayGphone.AlipayLogin"
                            });
                        }
                        //不在监控范围不转发
                        String[] s2 = utils.readDataByApp("apps", "apps").split(",");
                        utils.log("通知范围：" + Arrays.toString(s2) + "app" + aPackage);
                        if (!isIn(s2, aPackage)) return;

                        utils.log("包名:" + aPackage, true);
                        utils.log("标题:" + title, true);
                        utils.log("主体" + text, true);
                        String s = "title=" + title + ",body=" + text;
                        utils.sendString(s, "notice", aPackage);
                        //转发数据给自动记账
                    }
                });
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