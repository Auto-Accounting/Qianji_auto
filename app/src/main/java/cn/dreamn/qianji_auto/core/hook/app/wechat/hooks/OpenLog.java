package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import android.util.Log;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class OpenLog {
    public static void init(Utils utils) {
        hookXLogSet(utils);
        hookAllFunctions(utils, "i");

        hookAllFunctions(utils, "d");

        hookAllFunctions(utils, "e");

        hookAllFunctions(utils, "f");

        hookAllFunctions(utils, "v");

        hookAllFunctions(utils, "w");
    }

    private static void hookXLogSet(Utils utils) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.xlog.app.XLogSetup", utils.getClassLoader(), "keep_setupXLog",
                boolean.class, String.class, String.class, Integer.class, Boolean.class,
                Boolean.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[5] = true;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.args[5] = true;
                        Log.i("微信", "keep_setupXLog参数isLogcatOpen：" + param.args[5]);
                    }
                });
    }

    public static void hookAllFunctions(Utils utils, String functionName) {

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.Log", utils.getClassLoader(), functionName,
                String.class, String.class, Object[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String) param.args[0];
                        String str2 = (String) param.args[1];
                        Object[] objArr = (Object[]) param.args[2];
                        String format = objArr == null ? str2 : String.format(str2, objArr);
                        Log.i("微信[" + functionName + "] " + str, str2);
                        super.beforeHookedMethod(param);
                    }
                });
    }


}
