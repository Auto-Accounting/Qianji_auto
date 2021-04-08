package cn.dreamn.qianji_auto.ui.listData.CustomListData;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class floatCustom extends BaseList {

    private static floatCustom p;

    public static floatCustom getInstance() {
        if (p == null)
            p = new floatCustom();
        return p;
    }

    @Override
    public String getName() {
        return "悬浮窗";
    }

    @Override
    public String getIcon() {
        return "&#xe6d7;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
        // baseFragment.openNewPage()
        //  baseFragment.openNewPage(AboutFragment.class);
    }
}
