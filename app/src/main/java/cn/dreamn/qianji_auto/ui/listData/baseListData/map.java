package cn.dreamn.qianji_auto.ui.listData.baseListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainMapFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class map extends BaseList {

    private static map p;

    public static map getInstance(){
        if(p==null)
            p=new map();
        return p;
    }

    @Override
    public String getName() {
        return "资产映射";
    }

    @Override
    public String getIcon() {
        return "&#xe612;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        baseFragment.openNewPage(MainMapFragment.class);
    }
}
