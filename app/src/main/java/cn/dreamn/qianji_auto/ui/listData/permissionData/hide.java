package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class hide extends BaseList {

    private static hide p;

    public static hide getInstance(){
        if(p==null)
            p=new hide();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Hide;
    }

    @Override
    public String getName() {
        return "多任务隐藏";
    }

    @Override
    public String getIcon() {
        return "&#xe61f;";
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
