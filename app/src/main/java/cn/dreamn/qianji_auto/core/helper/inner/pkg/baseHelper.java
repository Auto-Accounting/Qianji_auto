//
// Decompiled by Procyon - 3301ms
//
package cn.dreamn.qianji_auto.core.helper.inner.pkg;


public abstract class baseHelper {

    public static boolean isInApp(final String s) {
        return "com.unionpay.activity.UPActivityMain".equals(s) || "com.alipay.mobile.bill.list.ui.BillMainListActivity".equals(s) || "com.tencent.mm.ui.LauncherUI".equals(s) || "com.eg.android.AlipayGphone.AlipayLogin".equals(s) || "com.jd.jrapp.bm.mainbox.main.MainActivity".equals(s) || "com.jingdong.app.mall.MainFrameActivity".equals(s) || "com.sankuai.waimai.business.page.homepage.MainActivity".equals(s) || "com.meituan.android.pt.homepage.activity.MainActivity".equals(s) || "com.xunmeng.pinduoduo.ui.activity.HomeActivity".equals(s);
    }

}

