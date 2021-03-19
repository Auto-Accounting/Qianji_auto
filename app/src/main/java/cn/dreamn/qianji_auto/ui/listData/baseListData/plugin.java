package cn.dreamn.qianji_auto.ui.listData.baseListData;

import android.util.Log;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainSetFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class plugin extends BaseList {

    private static plugin p;

    public static plugin getInstance(){
        if(p==null)
            p=new plugin();
        return p;
    }

    @Override
    public String getName() {
        return "插件设置";
    }

    @Override
    public String getIcon() {
        return "&#xe632;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        baseFragment.openNewPage(MainSetFragment.class);
    }
}
