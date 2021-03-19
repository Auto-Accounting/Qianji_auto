package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class floatData extends BaseList {

    private static floatData p;

    public static floatData getInstance(){
        if(p==null)
            p=new floatData();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Float;
    }

    @Override
    public String getName() {
        return "悬浮窗权限";
    }

    @Override
    public String getIcon() {
        return "&#xe603;";
    }

    @Override
    public String getSubName() {
        return "用于弹出悬浮窗记账";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
      //  WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.learnUrl));
    }

}
