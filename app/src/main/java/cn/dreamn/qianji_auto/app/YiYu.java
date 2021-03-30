package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;

public class YiYu implements IApp {
    private static YiYu qianJi;

    public static YiYu getInstance(){
        if(qianJi==null)
            qianJi=new YiYu();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "一羽记账";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_yiyu;
    }

    @Override
    public void sendToApp(BillInfo billInfo) {
        //实现发送给钱迹
    }

    @Override
    public void asyncData() {

    }

    @Override
    public String getAsyncDesc() {
        return null;
    }
}
