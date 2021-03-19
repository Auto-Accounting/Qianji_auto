package cn.dreamn.qianji_auto.ui.listData.permissionData;

import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.BaseList;

public class smsRead extends BaseList {

    private static smsRead p;

    public static smsRead getInstance(){
        if(p==null)
            p=new smsRead();
        return p;
    }

    @Override
    public int getAppId() {
        return PermissionUtils.Sms;
    }

    @Override
    public String getName() {
        return "短信权限";
    }

    @Override
    public String getIcon() {
        return "&#xe61c;";
    }

    @Override
    public String getSubName() {
        return "读取分析短信账单";
    }

    @Override
    public void onClick(BaseFragment baseFragment) {
       // baseFragment.openNewPage()
      //  WebViewFragment.openUrl(baseFragment,baseFragment.getContext().getString(R.string.learnUrl));
    }

}
