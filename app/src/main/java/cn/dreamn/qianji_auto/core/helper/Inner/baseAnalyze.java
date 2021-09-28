//
// Decompiled by FernFlower - 1903ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import cn.dreamn.qianji_auto.bills.BillInfo;

public abstract class baseAnalyze {
    public boolean isSetMoney = false;

    //Renamed from .e()
    public void setMoney(BillInfo billInfo, String money) {
        isSetMoney = true;
        billInfo.setMoney(money);
    }

    //Renamed from .g()
    public boolean isMoney(String money) {
        return money.matches("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$") || money.matches("^(-?[1-9]\\d*)|0$");

    }
}

