package cn.dreamn.qianji_auto.core.hook;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.hooks.auto.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.hooks.notice.Notice;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.Qianji;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.Sms;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.Wechat;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private final List<hookBase> mHookList;

    {
        mHookList = new ArrayList<>();

        mHookList.add(new QianjiAuto());
        mHookList.add(new Qianji());
        mHookList.add(new Wechat());
        mHookList.add(new Alipay());
        mHookList.add(new Notice());
        mHookList.add(new Sms());
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        for (hookBase hook : mHookList) {
            hook.initZygote(startupParam);
        }

    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        for (hookBase hook : mHookList) {
            hook.onLoadPackage(lpparam);
        }
    }
}