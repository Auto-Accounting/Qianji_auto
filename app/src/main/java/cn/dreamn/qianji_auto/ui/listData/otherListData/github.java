package cn.dreamn.qianji_auto.ui.listData.otherListData;

import android.util.Log;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class github extends BaseList {

    private static github p;

    public static github getInstance(){
        if(p==null)
            p=new github();
        return p;
    }

    @Override
    public String getName() {
        return "Github";
    }

    @Override
    public String getIcon() {
        return "&#xe885;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openPage()
        WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.githubUrl));
    }
}
