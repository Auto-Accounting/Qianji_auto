package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class autostart extends BaseList {

    private static autostart p;

    public static autostart getInstance(){
        if(p==null)
            p=new autostart();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Start;
    }

    @Override
    public String getName() {
        return "自启动权限";
    }

    @Override
    public String getIcon() {
        return "&#xe6e2;";
    }

    @Override
    public String getSubName() {
        return "启动自动记账进行记账";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
      //  WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.learnUrl));
    }

}
