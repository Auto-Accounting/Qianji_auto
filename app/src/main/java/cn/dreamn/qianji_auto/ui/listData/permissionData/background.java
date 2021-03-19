package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class background extends BaseList {

    private static background p;

    public static background getInstance(){
        if(p==null)
            p=new background();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Battery;
    }

    @Override
    public String getName() {
        return "后台运行无限制";
    }

    @Override
    public String getIcon() {
        return "&#xe6b1;";
    }

    @Override
    public String getSubName() {
        return "用于后台保活";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
      //  WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.learnUrl));
    }

}
