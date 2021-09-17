package cn.dreamn.qianji_auto.core.hook.app.android;

import android.util.Log;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Float {
    public static void init(ClassLoader classLoader) {

        Log.d("hookSysApi", "hook checkAddPermission start");
//hook 最终的权限检查方法:checkAddPermission
        try {
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindowManager", classLoader), "checkAddPermission", new XC_MethodHook() {
                public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                    if (param.args[0] instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) param.args[0];
                        if (params.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR) {
                            param.setResult(0);//当检测到是系统错误对话框时，返回0，即ok！
                        }
                    }
                }
            });
        } catch (Throwable t) {
            Log.d("hookSysApi", Log.getStackTraceString(t));
        }

        Log.d("hookSysApi", "hook checkAddPermission end");
    }
}
