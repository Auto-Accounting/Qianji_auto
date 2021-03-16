package cn.dreamn.qianji_auto.ui.listData.baseListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class cards extends BaseList {

    private static cards p;

    public static cards getInstance(){
        if(p==null)
            p=new cards();
        return p;
    }

    @Override
    public String getName() {
        return "资产管理";
    }

    @Override
    public String getIcon() {
        return "&#xe6b2;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openPage()
        Log.d("plugin","click");
    }
}
