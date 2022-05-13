package cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks;

import java.util.HashMap;
import java.util.Map;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class Timeout {
    //去掉钱迹的10秒校验
    public static void init(Utils utils) {
        HashMap<String, String> clazz = new HashMap<>();
        clazz.put("com.mutangtech.qianji.a", "timeoutApp");
        clazz.put("c6.a", "timeoutApp");  // 钱迹3.2.1.4版本

        for (Map.Entry entry : clazz.entrySet()) {
            String cls = (String) entry.getKey();
            String method = (String) entry.getValue();
            try {
                utils.log("钱迹 Timeout.init Hook <" + cls + "." + method + "> ");
                XposedHelpers.findAndHookMethod(cls, utils.getClassLoader(), method, String.class, long.class, XC_MethodReplacement.returnConstant(true));
                break;
            } catch (Exception e) {
                utils.log("钱迹 Timeout.init Hook <" + cls + "." + method + "> HookError " + e);
            }
        }
    }
}
