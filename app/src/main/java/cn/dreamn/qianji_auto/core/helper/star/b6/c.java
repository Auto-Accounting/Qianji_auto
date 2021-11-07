//
// Decompiled by Procyon - 3301ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;


public abstract class c {
    public boolean isSetMoney;

    public c() {
        this.isSetMoney = false;
    }


    public static boolean isInApp(final String s) {
        return "com.unionpay.activity.UPActivityMain".equals(s) || "com.alipay.mobile.bill.list.ui.BillMainListActivity".equals(s) || "com.tencent.mm.ui.LauncherUI".equals(s) || "com.eg.android.AlipayGphone.AlipayLogin".equals(s) || "com.jd.jrapp.bm.mainbox.main.MainActivity".equals(s) || "com.jingdong.app.mall.MainFrameActivity".equals(s) || "com.sankuai.waimai.business.page.homepage.MainActivity".equals(s) || "com.meituan.android.pt.homepage.activity.MainActivity".equals(s) || "com.xunmeng.pinduoduo.ui.activity.HomeActivity".equals(s);
    }

    public BillInfo createBillInfo() {
        this.isSetMoney = false;
        return new BillInfo();
    }


    public void setMoney(BillInfo billInfo, final String money) {
        try {
            billInfo.setMoney(money);
            this.isSetMoney = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isMoney(final String money) {
        return BillTools.isMoney(money);
    }

    public String replace(String s) {
        if (s == null) return "";
        return s.replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
    }
}

