package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;

public class JiZhangYa implements IApp {
    private static JiZhangYa qianJi;

    public static JiZhangYa getInstance(){
        if(qianJi==null)
            qianJi=new JiZhangYa();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "柚子记账";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_youzi;
    }

    @Override
    public void sendToApp(String str) {
        //实现发送给钱迹
    }

    @Override
    public void asyncData() {

    }
}
