package cn.dreamn.qianji_auto.ui.listData.otherListData;

import android.util.Log;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class videoTeach extends BaseList {

    private static videoTeach p;

    public static videoTeach getInstance(){
        if(p==null)
            p=new videoTeach();
        return p;
    }

    @Override
    public String getName() {
        return "视频教程";
    }

    @Override
    public String getIcon() {
        return "&#xe60c;";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
        WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.biliUrl));
    }
    @Override
    public int getFontSize() {
        return 32;
    }
}
