package cn.dreamn.qianji_auto.core.hook;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.hooks.auto.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.hooks.notice.Notice;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.Qianji;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.QQ;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.Sms;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.Wechat;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    private final List<hookBase> mHookList;

    {
        mHookList = new ArrayList<>();

        mHookList.add(QianjiAuto.getInstance());
        mHookList.add(Qianji.getInstance());
        mHookList.add(Alipay.getInstance());
        mHookList.add(Wechat.getInstance());
        mHookList.add(Sms.getInstance());
        mHookList.add(Notice.getInstance());
      //  mHookList.add(Permission.getInstance());
        mHookList.add(QQ.getInstance());
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        for (hookBase hook : mHookList) {
            hook.onLoadPackage(lpparam);
        }
    }
}