package cn.dreamn.qianji_auto.ui.listData.complie;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class sms extends BaseList {

    private static sms p;

    public static sms getInstance(){
        if(p==null)
            p=new sms();
        return p;
    }

    @Override
    public String getName() {
        return "短信识别";
    }

    @Override
    public String getIcon() {
        return "&#xe61c;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        Log.d("plugin","click");
    }
}
