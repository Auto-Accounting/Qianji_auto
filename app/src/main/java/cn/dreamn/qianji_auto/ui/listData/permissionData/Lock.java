package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class Lock extends BaseList {

    private static Lock p;

    public static Lock getInstance(){
        if(p==null)
            p=new Lock();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Lock;
    }

    @Override
    public String getName() {
        return "多任务锁定";
    }

    @Override
    public String getIcon() {
        return "&#xe654;";
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
