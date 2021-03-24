package cn.dreamn.qianji_auto.ui.listData.baseListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.sorts.MainSortFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class sort extends BaseList {

    private static sort p;

    public static sort getInstance(){
        if(p==null)
            p=new sort();
        return p;
    }

    @Override
    public String getName() {
        return "分类管理";
    }

    @Override
    public String getIcon() {
        return "&#xe6a7;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        baseFragment.openNewPage(MainSortFragment.class);
    }
}
