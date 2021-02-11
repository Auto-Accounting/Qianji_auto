package cn.dreamn.qianji_auto.core.hook;

import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;

        for (HookBase hookBase : HookList.getInstance().getmListHook()) {
            hookBase.hook(packageName, processName, 2);
        }
    }
}
