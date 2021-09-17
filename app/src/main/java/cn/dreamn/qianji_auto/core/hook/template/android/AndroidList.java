package cn.dreamn.qianji_auto.core.hook.template.android;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.android.Notice;


public class AndroidList {
    private static AndroidList mAndroidList;
    private final List<Class<?>> mListHook = new ArrayList<>();

    public AndroidList() {
        mListHook.clear();
        mListHook.add(Notice.class);
        //  mListHook.add(Sms.class);
    }

    public synchronized static AndroidList getInstance() {
        if (mAndroidList == null) {
            mAndroidList = new AndroidList();
        }
        return mAndroidList;
    }

    public List<Class<?>> getmListHook() {
        return mListHook;
    }
}
