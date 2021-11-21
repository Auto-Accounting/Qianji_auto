//
// Decompiled by Procyon - 3012ms
//
package cn.dreamn.qianji_auto.core.helper.inner.pkg;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.dreamn.qianji_auto.core.helper.inner.Analyze;


public class wechat extends baseHelper {
    public String b;
    public boolean c;

    public static boolean findWechatPayDetail(final String s, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return "com.tencent.mm".equals(s) && accessibilityNodeInfo != null && ((accessibilityNodeInfo.findAccessibilityNodeInfosByText("账单详情").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("查看账单详情").size() == 0) || (accessibilityNodeInfo.findAccessibilityNodeInfosByText("零钱提现").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("到账成功").size() > 0));
    }

    public static boolean findWechatPayDetail2(final String s, final String className) {
        return "com.tencent.mm".equals(s) && ("com.tencent.mm.plugin.webview.ui.tools.WebViewUI".equals(className) || "android.webkit.WebView".equals(className));
    }

    public static boolean findWechatPayBill(final String s) {
        return "com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishProgressNewUI".equals(s);
    }

    public static boolean isUseful(final List<String> list) {
        final int size = list.size();
        boolean b2 = false;
        if (size > 8) {
            if (Analyze.checkNode(list, "状态", false)) {
                if (Analyze.checkNode(list, "单号", false)) {
                    b2 = true;
                }
            }
        }
        return b2;
    }

    public static boolean findWechatPayUi(final String s, final String className, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return "com.tencent.mm.plugin.remittance.ui.RemittanceBusiUI".equals(className) || "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.WalletBrandUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI".equals(className) || "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiReceiveUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiDetailUI".equals(className) || "com.tencent.mm.plugin.aa.ui.PaylistAAUI".equals(className) || "com.tencent.mm.plugin.wallet.balance.ui.WalletBalanceResultUI".equals(className) || ("com.tencent.mm".equals(s) && accessibilityNodeInfo != null && accessibilityNodeInfo.findAccessibilityNodeInfosByText("支付成功").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("浮窗").size() == 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("查看账单详情").size() == 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("订单支付成功通知").size() == 0);
    }

    public static boolean findWechatPayUi2(final String s) {
        return "com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishUI".equals(s);
    }

}

