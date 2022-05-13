package cn.dreamn.qianji_auto.core.hook.core;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class AnkioHook extends XC_MethodHook {
    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {

        XC_MethodHook.Unhook xposedHooked = XposedHelpers.findAndHookMethod(clazz,methodName,parameterTypesAndCallback);

        return xposedHooked;
    }
}
