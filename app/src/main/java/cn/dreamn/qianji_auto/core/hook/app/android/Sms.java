package cn.dreamn.qianji_auto.core.hook.app.android;

import android.content.Intent;
import android.util.Log;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Sms extends HookBase {
    //TODO 此处通过hook短信获取，等我看完源码再说
    @Override
    public void hookBefore() {

    }

    @Override
    public void hookFirst() throws Error {
        final Class<?> aClass = XposedHelpers.findClass(

                "com.android.messaging.receiver.SmsReceiver", utils.getClassLoader());

        XposedBridge.hookAllMethods(aClass, "deliverSmsIntent", new XC_MethodHook() {

            @Override

            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                super.beforeHookedMethod(param);

                Log.e("hock_sms", "hookAllMethods--------开始劫持接收到的短信");

                Intent it = (Intent) param.args[1];

                byte[] pdu = new byte[0];

                try {

                    String sender = "null";

                    try {

                        sender = it.getStringExtra("sender");

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                    String receiver = "null";

                    try {

                        receiver = it.getStringExtra("receiver");

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                    String message = "null";

                    try {

                        message = it.getStringExtra("message");

                    } catch (Exception e) {

                        return;

                    }
                    utils.log(sender);
                    utils.log(receiver);
                    utils.log(message);

                } catch (Exception e) {

                    e.printStackTrace();

                }


            }

        });
    }

    @Override
    public String getPackPageName() {
        return "android";
    }

    @Override
    public String getAppName() {
        return "短信";
    }

    @Override
    public String[] getAppVer() {
        return new String[0];
    }

    @Override
    public Integer getHookIndex() {
        return 2;
    }

    @Override
    public boolean isAndroid() {
        return false;
    }
}
