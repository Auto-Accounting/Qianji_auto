package cn.dreamn.qianji_auto.core.hook.template.app;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.app.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.app.auto.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.app.mipush.MiPush;
import cn.dreamn.qianji_auto.core.hook.app.qianji.Qianji;
import cn.dreamn.qianji_auto.core.hook.app.wechat.Wechat;


public class AppList {
    private static AppList mHookList;
    private final List<Class<?>> mListHook = new ArrayList<>();

    public AppList() {
        mListHook.clear();
        mListHook.add(QianjiAuto.class);
        mListHook.add(Qianji.class);
        mListHook.add(Wechat.class);
        mListHook.add(Alipay.class);
        mListHook.add(MiPush.class);
    }

    public synchronized static AppList getInstance() {
        if (mHookList == null) {
            mHookList = new AppList();
        }
        return mHookList;
    }

    public List<Class<?>> getmListHook() {
        return mListHook;
    }
}
