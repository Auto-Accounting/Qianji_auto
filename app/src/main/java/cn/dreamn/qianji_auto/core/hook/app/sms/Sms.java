package cn.dreamn.qianji_auto.core.hook.app.sms;

import cn.dreamn.qianji_auto.core.hook.template.app.AppBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Sms extends AppBase {
    @Override
    public void hookBefore() {

    }

    @Override
    public void hookFirst() throws Error {
        Class<?> mSmsMessageClass = XposedHelpers.findClass("com.android.internal.telephony.gsm.SmsMessage", utils.getClassLoader());

        XposedHelpers.findAndHookMethod(mSmsMessageClass, "createFromPdu", byte[].class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        try {
                            Object smsMessage = param.getResult();
                            if (null != smsMessage) {
                                String from = (String) XposedHelpers.callMethod(smsMessage, "getOriginatingAddress");
                                String msgBody = (String) XposedHelpers.callMethod(smsMessage, "getMessageBody");
                                String s = "test_sms: 收到短信---->" + "from:" + from + " msgBody:" + msgBody;
                                if (utils.readData("lastSMS", false).equals(s)) {
                                    return;
                                }
                                utils.writeData("lastSMS", s);

                                utils.log(s, true);

                                utils.sendString(msgBody, "sms", from);
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                            utils.log("SMS listen error" + e.getMessage());
                        }

                    }
                });
    }

    @Override
    public String getPackPageName() {
        return "com.android.phone";
    }

    @Override
    public String getAppName() {
        return "短信";
    }

    @Override
    public Integer getHookIndex() {
        return 1;
    }
}
