package cn.dreamn.qianji_auto.ui.listData.otherListData;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.about.AboutFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class backup extends BaseList {

    private static backup p;

    public static backup getInstance() {
        if (p == null)
            p = new backup();
        return p;
    }

    @Override
    public String getName() {
        return "备份恢复";
    }

    @Override
    public String getIcon() {
        return "&#xe63a;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
        // baseFragment.openNewPage()
        baseFragment.openNewPage(AboutFragment.class);
    }
}
