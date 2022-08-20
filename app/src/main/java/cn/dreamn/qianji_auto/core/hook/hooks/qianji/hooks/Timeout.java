package cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class Timeout {
    //去掉钱迹的10秒校验
    public static void init(Utils utils, JSONArray jsonArray) {



        String cls = jsonArray.getString(0);
        String method = jsonArray.getString(1);
        try {
            utils.log("钱迹 Timeout.init Hook <" + cls + "." + method + "> ");
            XposedHelpers.findAndHookMethod(cls, utils.getClassLoader(), method, String.class, long.class, XC_MethodReplacement.returnConstant(true));

        } catch (Exception e) {
            utils.log("钱迹 Timeout.init Hook <" + cls + "." + method + "> HookError " + e);
        }

    }
}
