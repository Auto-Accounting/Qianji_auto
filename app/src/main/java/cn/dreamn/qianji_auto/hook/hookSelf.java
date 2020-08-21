package cn.dreamn.qianji_auto.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookSelf implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam paramLoadPackageParam)
    {
        if (paramLoadPackageParam.packageName.equals("cn.dreamn.qianji_auto")) {
            XposedHelpers.findAndHookMethod("cn.dreamn.qianji_auto.func.model", paramLoadPackageParam.classLoader, "xposedActive", XC_MethodReplacement.returnConstant(true));
        }
    }


}
