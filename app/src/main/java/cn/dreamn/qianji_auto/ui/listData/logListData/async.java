package cn.dreamn.qianji_auto.ui.listData.logListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class async extends BaseList {

    private static async p;

    public static async getInstance(){
        if(p==null)
            p=new async();
        return p;
    }

    @Override
    public String getName() {
        return "数据同步";
    }

    @Override
    public String getIcon() {
        return "&#xe68e;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        Log.d("plugin","click");
    }
}
