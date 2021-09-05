package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Bundle;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;

public class YiMu implements IApp {
    private static YiMu qianJi;

    public static YiMu getInstance() {
        if (qianJi == null)
            qianJi = new YiMu();
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
    public void sendToApp(Context context, BillInfo str) {

    }

    @Override
    public void asyncDataBefore(Context context) {

    }

    @Override
    public void asyncDataAfter(Context context, Bundle billInfo) {

    }

    @Override
    public String getAsyncDesc(Context context) {
        return null;
    }

}
