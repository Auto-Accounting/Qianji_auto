package cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.Normalizer;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SmsIntent {
    private static final String TELEPHONY_PACKAGE = "com.android.internal.telephony";
    private static final String SMS_HANDLER_CLASS = TELEPHONY_PACKAGE + ".InboundSmsHandler";

    public static void init(Utils utils) {
        hookSmsHandler(utils.getClassLoader(), utils);
    }

    private static void hookSmsHandler(ClassLoader classloader, Utils utils) {
        utils.log("Hooking dispatchIntent() for Android v29+");
        // 实际上这是一个通用的方式，不再使用精确匹配来找到对应的 Method，而使用模糊搜索的方式
        // 但是之前分 API 匹配的逻辑在以往 Android 版本的系统之中已经验证通过，故而保留原有逻辑
        Class<?> inboundSmsHandlerClass = XposedHelpers.findClass(SMS_HANDLER_CLASS, classloader);
        if (inboundSmsHandlerClass == null) {
            utils.log("Class: %s cannot found", SMS_HANDLER_CLASS);
            return;
        }

        Method[] methods = inboundSmsHandlerClass.getDeclaredMethods();
        Method exactMethod = null;
        final String DISPATCH_INTENT = "dispatchIntent";
        for (Method method : methods) {
            String methodName = method.getName();
            if (DISPATCH_INTENT.equals(methodName)) {
                exactMethod = method;
            }
        }
        Constructor<?>[] constructors = inboundSmsHandlerClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] clss = constructor.getParameterTypes();
            for (Class<?> cls : clss) {
                if (cls == Context.class) {
                    XposedBridge.hookMethod(constructor, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Context context = (Context) param.args[1];
                            if (utils.getContext() == null) {
                                utils.setContext(context);
                            }
                        }
                    });
                    break;
                }
            }
        }
        if (exactMethod == null) {
            utils.log("Method %s for Class %s cannot found", DISPATCH_INTENT, SMS_HANDLER_CLASS);
            return;
        }
        XposedBridge.hookMethod(exactMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                String action = intent.getAction();

                if (!Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(action)) {
                    return;
                }
                SmsMessage[] smsMessageParts = SmsMessageUtils.fromIntent(intent);
                String sender = smsMessageParts[0].getDisplayOriginatingAddress();
                String body = SmsMessageUtils.getMessageBody(smsMessageParts);
                sender = Normalizer.normalize(sender, Normalizer.Form.NFC);
                body = Normalizer.normalize(body, Normalizer.Form.NFC);
                utils.sendString(body, "sms", sender);
            }
        });
    }


    public static class SmsMessageUtils {

        private static final int SMS_CHARACTER_LIMIT = 160;

        private SmsMessageUtils() {
        }

        public static SmsMessage[] fromIntent(Intent intent) {
            return Telephony.Sms.Intents.getMessagesFromIntent(intent);
        }

        public static String getMessageBody(SmsMessage[] messageParts) {
            if (messageParts.length == 1) {
                return messageParts[0].getDisplayMessageBody();
            } else {
                StringBuilder sb = new StringBuilder(SMS_CHARACTER_LIMIT * messageParts.length);
                for (SmsMessage messagePart : messageParts) {
                    sb.append(messagePart.getDisplayMessageBody());
                }
                return sb.toString();
            }
        }

    }
}
