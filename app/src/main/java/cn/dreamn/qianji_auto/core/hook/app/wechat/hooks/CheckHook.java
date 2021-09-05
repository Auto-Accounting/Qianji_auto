package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class CheckHook {
    //xp检测关闭
    public static void init(Utils utils) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.app.ag", utils.getClassLoader(), "b", StackTraceElement[].class, XC_MethodReplacement.returnConstant(false));

    }

}
