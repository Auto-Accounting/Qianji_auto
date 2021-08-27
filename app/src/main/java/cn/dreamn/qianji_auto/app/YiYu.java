package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Bundle;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;

public class YiYu implements IApp {
    private static YiYu qianJi;

    public static YiYu getInstance() {
        if (qianJi == null)
            qianJi = new YiYu();
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
