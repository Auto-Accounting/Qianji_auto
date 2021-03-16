package cn.dreamn.qianji_auto.ui.listData.logListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class notice extends BaseList {

    private static notice p;

    public static notice getInstance(){
        if(p==null)
            p=new notice();
        return p;
    }

    @Override
    public String getName() {
        return "通知列表";
    }

    @Override
    public String getIcon() {
        return "&#xe61d;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openPage()
        Log.d("plugin","click");
    }
}
