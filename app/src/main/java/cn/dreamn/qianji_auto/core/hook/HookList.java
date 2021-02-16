package cn.dreamn.qianji_auto.core.hook;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.app.AlipayHook;
import cn.dreamn.qianji_auto.core.hook.app.QianjiAuto;
import cn.dreamn.qianji_auto.core.hook.app.QianjiHook;
import cn.dreamn.qianji_auto.core.hook.app.WechatHook;


public class HookList {
    private static HookList mHookList;
    private final List<HookBase> mListHook = new ArrayList<>();

    public HookList() {
        mListHook.clear();
        mListHook.add(QianjiAuto.getInstance());
        mListHook.add(AlipayHook.getInstance());
        mListHook.add(WechatHook.getInstance());
        //  mListHook.add(QianjiHook.getInstance());
    }

    public synchronized static HookList getInstance() {
        if (mHookList == null) {
            mHookList = new HookList();
        }
        return mHookList;
    }

    public List<HookBase> getmListHook() {
        return mListHook;
    }
}
