package cn.dreamn.qianji_auto.core.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.dreamn.qianji_auto.core.hook.app.android.Float;
import cn.dreamn.qianji_auto.core.hook.app.android.Notice2;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;

        for (Class<?> hookBase : HookList.getInstance().getmListHook()) {

            Object hookFunc = hookBase.newInstance();
            Method method = hookBase.getMethod("hook", String.class, String.class);
            method.invoke(hookFunc, packageName, processName);
        }

    }


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Notice2 notice2 = new Notice2();
        notice2.init(startupParam);

        Float.init(getClass().getClassLoader());
    }
}
