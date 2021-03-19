package cn.dreamn.qianji_auto.ui.listData.logListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class logs extends BaseList {

    private static logs p;

    public static logs getInstance(){
        if(p==null)
            p=new logs();
        return p;
    }

    @Override
    public String getName() {
        return "日志列表";
    }

    @Override
    public String getIcon() {
        return "&#xe6ad;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        Log.d("plugin","click");
    }
}
