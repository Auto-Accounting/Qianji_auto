package cn.dreamn.qianji_auto.core.hook;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.app.AlipayHook;
import cn.dreamn.qianji_auto.core.hook.app.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.app.SmsHook;
import cn.dreamn.qianji_auto.core.hook.app.WechatHook;


public class HookList {
    private final List<HookBase> mListHook = new ArrayList<>();

    private static HookList mHookList;

    public synchronized static HookList getInstance() {
        if (mHookList == null) {
            mHookList = new HookList();
        }
        return mHookList;
    }

    public HookList() {
        mListHook.clear();
        mListHook.add(QianjiAuto.getInstance());
        mListHook.add(AlipayHook.getInstance());
        mListHook.add(SmsHook.getInstance());
        mListHook.add(WechatHook.getInstance());
    }

    public List<HookBase> getmListHook() {
        return mListHook;
    }
}
