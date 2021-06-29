package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class helper extends BaseList {

    private static helper p;

    public static helper getInstance(){
        if(p==null)
            p=new helper();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Assist;
    }

    @Override
    public String getName() {
        return "辅助服务";
    }

    @Override
    public String getIcon() {
        return "&#xe625;";
    }

    @Override
    public String getSubName() {
        return "用于无障碍记账";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
      //  WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.learnUrl));
    }

}
