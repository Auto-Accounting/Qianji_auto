package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;

public class YouZi implements IApp {
    private static YouZi qianJi;

    public static YouZi getInstance(){
        if(qianJi==null)
            qianJi=new YouZi();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "记账鸭";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_jizhangya;
    }

    @Override
    public void sendToApp(String str) {
        //实现发送给钱迹
    }

    @Override
    public void asyncData() {

    }
}
