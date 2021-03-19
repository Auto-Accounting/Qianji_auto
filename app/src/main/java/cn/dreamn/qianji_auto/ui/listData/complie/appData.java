package cn.dreamn.qianji_auto.ui.listData.complie;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class appData extends BaseList {

    private static appData p;

    public static appData getInstance(){
        if(p==null)
            p=new appData();
        return p;
    }

    @Override
    public String getName() {
        return "App识别";
    }

    @Override
    public String getIcon() {
        return "&#xe716;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        Log.d("plugin","click");
    }
}
