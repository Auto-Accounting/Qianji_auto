package cn.dreamn.qianji_auto.app;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;

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
    public void sendToApp(BillInfo billInfo) {
        //实现发送给钱迹

    }

    @Override
    public void asyncData() {

    }

    @Override
    public String getAsyncDesc() {
        return "你可以一键将钱迹中的资产、账本、分类等数据同步到自动记账，期间手机会跳转到钱迹，完成同步后将自动跳回本页面。 \n\n ⚠️ 同步前，请确保开启所有权限，并将钱迹加入XPOSED模块作用域中。";
    }
}
