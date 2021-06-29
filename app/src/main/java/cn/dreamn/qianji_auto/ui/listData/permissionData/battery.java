package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class battery extends BaseList {

    private static battery p;

    public static battery getInstance(){
        if(p==null)
            p=new battery();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.BatteryIngore;
    }

    @Override
    public String getName() {
        return "忽略电池优化";
    }

    @Override
    public String getIcon() {
        return "&#xe624;";
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
