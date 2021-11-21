package cn.dreamn.qianji_auto.core.hook.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.dreamn.qianji_auto.core.hook.template.android.AndroidBase;

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
        utils.log("Hook notice");

        try {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);            // 得到系统的 sService
            Method getService = NotificationManager.class.getDeclaredMethod("getService");
            getService.setAccessible(true);
            final Object sService = getService.invoke(notificationManager);

            Class<?> iNotiMngClz = Class.forName("android.app.INotificationManager");            // 动态代理 INotificationManager
            Object proxyNotiMng = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{iNotiMngClz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // utils.log("invoke(). method:{}" + method);
                    if (method.toString().contains("enqueueNotificationWithTag")) {
                        //java.lang.String,java.lang.String,java.lang.String,int,android.app.Notification,int
                        int index = 0;
                        String pkg = null;
                        Notification notification = null;
                        if (args != null && args.length > 0) {
                            for (Object arg : args) {

                                if (arg != null) {
                                    if (index == 0) {
                                        pkg = (String) arg;
                                        continue;
                                    }
                                    if (arg.getClass().toString().contains("Notification")) {
                                        notification = (Notification) arg;
                                        continue;
                                    }
                                }
                                utils.log("type: " + (arg != null ? arg.getClass() : null) + "arg:" + arg);
                                index++;
                            }
                            if (notification == null) {
                                utils.log("通知为空");
                            } else {
                                detailNotice(notification, pkg);
                            }


                        }
                    }
                    // 操作交由 sService 处理，不拦截通知
                    // return method.invoke(sService, args);
                    // 拦截通知，什么也不做
                    return method.invoke(sService, args);                 // 或者是根据通知的 Tag 和 ID 进行筛选
                }
            });            // 替换 sService
            Field sServiceField = NotificationManager.class.getDeclaredField("sService");
            sServiceField.setAccessible(true);
            sServiceField.set(notificationManager, proxyNotiMng);
        } catch (Exception e) {
            utils.log("Hook NotificationManager failed!" + e);
        }
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

      /*  try {
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
*/

    }


    private void detailNotice(Notification notification, String pkg) {
        //获得包名
        String aPackage = pkg;
        utils.log("pkg:" + pkg + ",notification:" + notification.toString());
        Bundle bundle = notification.extras;
        if (bundle == null) {
            utils.log("通知数据：describeContents->" + notification.describeContents(), true);
            utils.log("通知数据：tickerText->" + notification.tickerText, true);
            utils.log("通知数据：null", true);
            return;
        } else {
            if (utils.isDebug()) utils.log("通知数据：" + bundle.toString());
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