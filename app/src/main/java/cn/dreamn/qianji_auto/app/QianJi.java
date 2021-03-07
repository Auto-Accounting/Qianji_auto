package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;

public class QianJi implements IApp {
    private static QianJi qianJi;

    public static QianJi getInstance(){
        if(qianJi==null)
            qianJi=new QianJi();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return "com.mutangtech.qianji";
    }

    @Override
    public String getAppName() {
        return "钱迹";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_qianji;
    }

    @Override
    public void sendToApp(String str) {
        //实现发送给钱迹
    }

    @Override
    public void asyncData() {

    }
}
