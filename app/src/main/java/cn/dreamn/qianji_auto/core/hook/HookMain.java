package cn.dreamn.qianji_auto.core.hook;

import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;
        XposedBridge.log("-------hook start---------");
        for (HookBase hookBase : HookList.getInstance().getmListHook()) {
            XposedBridge.log(packageName+"->"+processName);
            hookBase.hook(packageName, processName, 0);
        }
        XposedBridge.log("-------hook end---------");
    }
}
