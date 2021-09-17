package cn.dreamn.qianji_auto.core.hook.android;

import android.content.Intent;

import cn.dreamn.qianji_auto.core.hook.template.android.AndroidBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Sms extends AndroidBase {

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

                utils.log("hock_sms hookAllMethods--------开始劫持接收到的短信", true);

                Intent it = (Intent) param.args[1];
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

                    utils.log(sender, true);
                    utils.log(receiver, true);
                    utils.log(message, true);

                    String str = message;
                    if (message == null || sender == null) return;
                    utils.sendString(str, "sms", sender);

                } catch (Exception e) {

                    e.printStackTrace();

                }


            }

        });
    }


    @Override
    public String getAppName() {
        return "短信";
    }


}
