package cn.dreamn.qianji_auto.core.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.dreamn.qianji_auto.core.hook.template.android.AndroidList;
import cn.dreamn.qianji_auto.core.hook.template.app.AppList;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    //此处用户hook app
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;

        for (Class<?> hookBase : AppList.getInstance().getmListHook()) {

            Object hookFunc = hookBase.newInstance();
            Method method = hookBase.getMethod("hook", String.class, String.class);
            method.invoke(hookFunc, packageName, processName);
        }

    }

    //此处用于hook系统类函数
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        for (Class<?> hookBase : AndroidList.getInstance().getmListHook()) {

            Object hookFunc = hookBase.newInstance();
            Method method = hookBase.getMethod("hook", ClassLoader.class);
            method.invoke(hookFunc, getClass().getClassLoader());
        }

    }
}
