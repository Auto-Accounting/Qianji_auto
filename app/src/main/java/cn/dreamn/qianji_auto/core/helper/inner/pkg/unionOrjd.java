
package cn.dreamn.qianji_auto.core.helper.inner.pkg;


import android.view.accessibility.AccessibilityNodeInfo;

public class unionOrjd extends baseHelper {

    public static boolean l(final String s, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return ("com.jingdong.app.mall".equals(s) || "com.jd.jrapp".equals(s)) && accessibilityNodeInfo != null && accessibilityNodeInfo.findAccessibilityNodeInfosByText("账单详情").size() > 0;
    }

    public static boolean m(final String s, final String s2, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return ("com.unionpay".equals(s) && ("com.unionpay.activity.react.UPActivityReactNative".equals(s2) || "android.view.ViewGroup".equals(s2) || "android.widget.ScrollView".equals(s2)) && accessibilityNodeInfo != null && (accessibilityNodeInfo.findAccessibilityNodeInfosByText("交易详情").size() > 0 || (accessibilityNodeInfo.findAccessibilityNodeInfosByText("商户名称").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("卡号").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("时间").size() > 0))) || ("com.unionpay.cordova.UPActivityWeb".equals(s2) && accessibilityNodeInfo != null && accessibilityNodeInfo.findAccessibilityNodeInfosByText("订单详情").size() > 0);
    }

    public static boolean n(final String s) {
        return "com.jd.lib.cashier.complete.view.CashierCompleteActivity".equals(s);
    }

    public static boolean o(final String s, final String s2, final AccessibilityNodeInfo accessibilityNodeInfo) {
        return "com.unionpay.activity.payment.UPActivityScan".equals(s2) || "com.unionpay.activity.payment.UPActivityPaymentQrCodeOut".equals(s2) || ("com.unionpay".equals(s) && "android.widget.FrameLayout".equals(s2) && accessibilityNodeInfo != null && accessibilityNodeInfo.findAccessibilityNodeInfosByText("支付成功").size() > 0 && accessibilityNodeInfo.findAccessibilityNodeInfosByText("付款方式").size() > 0);
    }

}

