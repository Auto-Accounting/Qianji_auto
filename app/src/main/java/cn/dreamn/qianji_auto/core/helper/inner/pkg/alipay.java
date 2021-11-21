//
// Decompiled by Procyon - 3561ms
//
package cn.dreamn.qianji_auto.core.helper.inner.pkg;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.dreamn.qianji_auto.core.helper.inner.Analyze;

public class alipay extends baseHelper {


    public static boolean l(final String s, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return "com.eg.android.AlipayGphone".equals(s) && accessibilityNodeInfo != null && (accessibilityNodeInfo.findAccessibilityNodeInfosByText("账单详情").size() > 0 || accessibilityNodeInfo.findAccessibilityNodeInfosByText("结果详情").size() > 0);
    }

    public static boolean m(final String s, final String s2) {
        if (!"com.xunmeng.pinduoduo.activity.NewPageActivity".equals(s2)) {
            if ("com.xunmeng.pinduoduo".equals(s)) {
                if ("android.webkit.WebView".equals(s2) || "android.view.View".equals(s2)) {
                    return true;
                }
                return "android.widget.FrameLayout".equals(s2);
            }
            return false;
        }
        return true;
    }

    public static boolean n(final List<String> list) {
        final int size = list.size();
        final boolean b = true;
        final boolean b2 = size > 10 && Analyze.checkNode(list, "账单详情", true) && Analyze.checkNode(list, "单号", false);
        boolean b3 = b;
        if (!b2) {
            b3 = (list.size() > 6 && (Analyze.checkNode(list, "充值成功", true) || Analyze.checkNode(list, "提现发起成功", true)));
        }
        return b3;
    }

    public static boolean o(final String s, final String s2, final AccessibilityNodeInfo accessibilityNodeInfo) {
        if (!"com.alipay.android.msp.ui.views.MspContainerActivity".equals(s2) && !"com.alipay.android.msp.ui.views.MspUniRenderActivity".equals(s2) && !"com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity".equals(s2)) {
            if ("com.eg.android.AlipayGphone".equals(s) && accessibilityNodeInfo != null) {
                if (accessibilityNodeInfo.findAccessibilityNodeInfosByText("向商家付款").size() > 0) {
                    return true;
                }
                return accessibilityNodeInfo.findAccessibilityNodeInfosByText("向商家付钱").size() > 0;
            }
            return false;
        }
        return true;
    }


}

