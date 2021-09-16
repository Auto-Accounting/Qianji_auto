package cn.dreamn.qianji_auto.core.hook;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.app.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.app.android.Notice;
import cn.dreamn.qianji_auto.core.hook.app.auto.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.app.qianji.Qianji;
import cn.dreamn.qianji_auto.core.hook.app.wechat.Wechat;


public class HookList {
    private static HookList mHookList;
    private final List<Class<?>> mListHook = new ArrayList<>();

    public HookList() {
        mListHook.clear();
        mListHook.add(QianjiAuto.class);
        mListHook.add(Qianji.class);
        mListHook.add(Wechat.class);
        mListHook.add(Alipay.class);
        mListHook.add(Notice.class);
    }

    public synchronized static HookList getInstance() {
        if (mHookList == null) {
            mHookList = new HookList();
        }
        return mHookList;
    }

    public List<Class<?>> getmListHook() {
        return mListHook;
    }
}
