package cn.dreamn.qianji_auto.ui.listData.otherListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class about extends BaseList {

    private static about p;

    public static about getInstance(){
        if(p==null)
            p=new about();
        return p;
    }

    @Override
    public String getName() {
        return "关于";
    }

    @Override
    public String getIcon() {
        return "&#xe646;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openPage()
        Log.d("plugin","click");
    }
}
