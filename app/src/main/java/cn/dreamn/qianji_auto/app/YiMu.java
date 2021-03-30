package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;

public class YiMu implements IApp {
    private static YiMu qianJi;

    public static YiMu getInstance(){
        if(qianJi==null)
            qianJi=new YiMu();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "一木记账";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_yimu;
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
