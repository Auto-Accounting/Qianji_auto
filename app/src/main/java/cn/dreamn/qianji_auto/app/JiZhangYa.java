package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Bundle;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;

public class JiZhangYa implements IApp {
    private static JiZhangYa qianJi;

    public static JiZhangYa getInstance() {
        if (qianJi == null)
            qianJi = new JiZhangYa();
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
    public void sendToApp(Context context, BillInfo str) {

    }

    @Override
    public void asyncDataBefore(Context context) {

    }

    @Override
    public void asyncDataAfter(Context context, Bundle billInfo) {

    }

    @Override
    public String getAsyncDesc() {
        return null;
    }


}
