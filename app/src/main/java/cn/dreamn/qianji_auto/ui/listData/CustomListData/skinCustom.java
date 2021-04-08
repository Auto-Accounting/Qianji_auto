package cn.dreamn.qianji_auto.ui.listData.CustomListData;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class skinCustom extends BaseList {

    private static skinCustom p;

    public static skinCustom getInstance() {
        if (p == null)
            p = new skinCustom();
        return p;
    }

    @Override
    public String getName() {
        return "皮肤";
    }

    @Override
    public String getIcon() {
        return "&#xe635;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
        // baseFragment.openNewPage()
        // baseFragment.openNewPage(AboutFragment.class);
    }
}
