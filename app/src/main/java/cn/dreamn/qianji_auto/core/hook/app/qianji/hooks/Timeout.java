package cn.dreamn.qianji_auto.core.hook.app.qianji.hooks;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class Timeout {
    //去掉钱迹的10秒校验
    public static void init(Utils utils) {
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.a", utils.getClassLoader(), "timeoutApp", String.class, long.class, XC_MethodReplacement.returnConstant(true));
    }
}
