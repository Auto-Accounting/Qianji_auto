package cn.dreamn.qianji_auto.core.hook.app.android;

import android.app.Notification;
import android.content.Context;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Notice2 {
    public HashMap<Notification.Builder, String> builderlist = new HashMap<>();
    public HashMap<Notification.Builder, Context> contextlist = new HashMap<>();

    public void init(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        Class<?> findClass = XposedHelpers.findClass("android.app.Notification$Builder", null);
        XposedHelpers.findAndHookConstructor(findClass, Context.class, new XC_MethodHook() {
            @Override
            public void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                Notice2.this.contextlist.put((Notification.Builder) methodHookParam.thisObject, (Context) methodHookParam.args[0]);
            }
        });
        XposedHelpers.findAndHookMethod(findClass, "setContentText", CharSequence.class, new XC_MethodHook() {
            @Override // de.robv.android.xposed.XC_MethodHook
            public void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                Notice2.this.builderlist.put((Notification.Builder) methodHookParam.thisObject, ((CharSequence) methodHookParam.args[0]).toString());
            }
        });
        XposedHelpers.findAndHookMethod(findClass, "build", new XC_MethodHook() {
            @Override // de.robv.android.xposed.XC_MethodHook
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                Notification.Builder builder = (Notification.Builder) methodHookParam.thisObject;
                Notice2.this.builderlist.remove(builder);
                Context context = (Context) Notice2.this.contextlist.get(builder);
                Notice2.this.contextlist.remove(builder);

                String s = Notice2.this.builderlist.get(builder);

                XposedBridge.log("通知消息：" + s);
            }
        });
    }

}