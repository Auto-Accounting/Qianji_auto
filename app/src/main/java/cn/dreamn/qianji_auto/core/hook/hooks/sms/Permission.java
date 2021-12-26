package cn.dreamn.qianji_auto.core.hook.hooks.sms;

import android.os.Build;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.PackageManagerServiceHook;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.PermissionManagerServiceHook;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.PermissionManagerServiceHook30;

public class Permission extends hookBase {
    @Override
    public void hookLoadPackage() {


        if (Build.VERSION.SDK_INT >= 30) { // Android 11+
            PermissionManagerServiceHook30.init(utils);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Android 9.0~10
            PermissionManagerServiceHook.init(utils);
        } else { // Android 5.0 ~ 8.1
            PackageManagerServiceHook.init(utils);
        }
    }

    @Override
    public void hookInitZygoteMain() {

    }

    @Override
    public String getPackPageName() {
        return "android";
    }

    @Override
    public String getAppName() {
        return "安卓权限";
    }

    @Override
    public Integer getHookIndex() {
        return 2;
    }


    @Override
    public boolean needHelpFindApplication() {
        return true;
    }
}
