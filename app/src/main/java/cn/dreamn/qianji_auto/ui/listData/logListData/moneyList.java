package cn.dreamn.qianji_auto.ui.listData.logListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class moneyList extends BaseList {

    private static moneyList p;

    public static moneyList getInstance(){
        if(p==null)
            p=new moneyList();
        return p;
    }

    @Override
    public String getName() {
        return "账单列表";
    }

    @Override
    public String getIcon() {
        return "&#xe62b;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        Log.d("plugin","click");
    }
}
