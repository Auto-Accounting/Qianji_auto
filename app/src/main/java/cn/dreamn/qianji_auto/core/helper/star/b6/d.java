
package cn.dreamn.qianji_auto.core.helper.star.b6;


import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class d extends c {
    public final /* synthetic */ int b;

    public d(final int b) {
        this.b = b;
        if (b != 1) {
            return;
        }
    }

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

    public f p(final List list, int i) {
        final int b = this.b;
        final f f = null;
        final f f2 = null;
       /* switch (b) {
            default: {
                f f3;
                if (list.size() == 0) {
                    f3 = f;
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("[auto] UnionPayDetailParser parse type ");
                    sb.append(i);
                    sb.append(" ");
                    sb.append(list.toString());
                    k.c(sb.toString());
                    if (i == 1) {
                        f3 = this.r(list);
                    }
                    else {
                        f3 = this.q(list);
                    }
                }
                return f3;
            }
            case 0: {
                f f4 = null;
                if (list.size() == 0) {
                    f4 = f2;
                }
                else if (i == 1) {
                    switch (this.b) {
                        default: {
                            final f f5 = new f();
                            final DetailEntity a = this.createBillInfo();
                            String s;
                            boolean b2;
                            String replace;
                            int n;
                            int n2;
                            StringBuilder sb2;
                            for (i = 0; i < list.size(); i = n + 1) {
                                s = list.get(i);
                                b2 = (i < list.size() - 1);
                                if (!super.isSetMoney && s.contains("¥")) {
                                    replace = s.replace("¥", "");
                                    n = i;
                                    if (this.isMoney(replace)) {
                                        this.setMoney(a, replace);
                                        n = i;
                                    }
                                }
                                else if ("订单信息".equals(s) && b2) {
                                    n = i + 1;
                                    a.remark = list.get(n);
                                }
                                else {
                                    n = i;
                                    if ("付款方式".equals(s)) {
                                        n = i;
                                        if (b2) {
                                            ++i;
                                            f5.a = list.get(i);
                                            n2 = i + 2;
                                            n = i;
                                            if (n2 < list.size()) {
                                                n = i;
                                                if (list.get(n2).endsWith("]")) {
                                                    sb2 = new StringBuilder();
                                                    sb2.append(f5.a);
                                                    sb2.append(list.get(i + 1));
                                                    sb2.append(list.get(n2));
                                                    f5.a = sb2.toString();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            f4 = f2;
                            if (super.isSetMoney) {
                                f5.c = a.remark;
                                f5.d = a;
                                f4 = f5;
                                break;
                            }
                            break;
                        }
                        case 0: {
                            final StringBuilder sb3 = new StringBuilder();
                            sb3.append("[auto] JdDetailParser parsePay ");
                            sb3.append(list.toString());
                            k.c(sb3.toString());
                            final f f6 = new f();
                            final DetailEntity a2 = this.createBillInfo();
                            String remark;
                            boolean b3;
                            String o;
                            for (i = 0; i < list.size(); ++i) {
                                remark = list.get(i);
                                b3 = (i < list.size() - 1);
                                if ("支付成功".equals(remark) && b3) {
                                    o = e3.b.o((String)list.get(i + 1));
                                    if (o != null && this.isMoney(o)) {
                                        this.setMoney(a2, o);
                                    }
                                }
                                else if (remark.contains("白条支付")) {
                                    f6.a = "京东白条";
                                }
                                else if (remark.contains("充值面值")) {
                                    a2.remark = remark;
                                }
                            }
                            f4 = f2;
                            if (super.isSetMoney) {
                                if (this.e(a2.amount)) {
                                    f6.g = true;
                                }
                                if (a2.remark == null) {
                                    a2.remark = "京东订单";
                                }
                                f6.c = a2.remark;
                                f6.d = a2;
                                f4 = f6;
                                break;
                            }
                            break;
                        }
                    }
                }
                else {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("[auto] JdDetailParser parse type ");
                    sb4.append(i);
                    sb4.append(" ");
                    sb4.append(list.toString());
                    k.c(sb4.toString());
                    final f f7 = new f();
                    final DetailEntity a3 = this.createBillInfo();
                    String s2;
                    boolean b4;
                    boolean b5;
                    String s3;
                    String o2;
                    StringBuilder sb5 = null;
                    String remark2 = null;
                    String remark3;
                    String b6;
                    for (i = 0; i < list.size(); ++i) {
                        s2 = list.get(i);
                        b4 = (i < list.size() - 1);
                        b5 = (i > 0);
                        Label_1314: {
                            Label_1299: {
                                if (("交易成功".equals(s2) || "退款成功".equals(s2) || "转出到账".equals(s2)) && b5) {
                                    s3 = list.get(i - 1);
                                    o2 = e3.b.o(s3);
                                    if (!this.isMoney(o2)) {
                                        continue;
                                    }
                                    this.setMoney(a3, o2);
                                    if (i >= 2) {
                                        a3.remark = list.get(i - 2).replace("订单详情", "");
                                    }
                                    if (!s3.contains("+")) {
                                        continue;
                                    }
                                    a3.detailType = 1;
                                    sb5 = new StringBuilder();
                                    sb5.append(s2);
                                    sb5.append("-");
                                    remark2 = a3.remark;
                                }
                                else {
                                    if ("创建时间：".equals(s2) && b4) {
                                        a3.time = e.u((String)list.get(i + 1));
                                        continue;
                                    }
                                    if (("支付方式：".equals(s2) || "退款至：".equals(s2) || "收益转入：".equals(s2)) && b4) {
                                        f7.a = list.get(i + 1).replace("微信-", "");
                                        continue;
                                    }
                                    if (s2.endsWith("说明：") && b4) {
                                        remark3 = list.get(i + 1);
                                        a3.remark = remark3;
                                        if ("京东钱包余额充值".equals(remark3)) {
                                            a3.detailType = 2;
                                            f7.b = "京东钱包";
                                            continue;
                                        }
                                        if ("京东钱包余额提现".equals(a3.remark)) {
                                            a3.detailType = 2;
                                            f7.a = "京东钱包";
                                            continue;
                                        }
                                        if (!"京东小金库-转入".equals(a3.remark)) {
                                            continue;
                                        }
                                        a3.detailType = 2;
                                        b6 = "京东小金库";
                                    }
                                    else if ("还款详情：".equals(s2)) {
                                        a3.detailType = 2;
                                        if (!TextUtils.isEmpty((CharSequence)a3.remark) && a3.remark.contains("白条")) {
                                            f7.b = "京东白条";
                                        }
                                        continue;
                                    }
                                    else if ("商品：".equals(s2) && b4) {
                                        remark2 = list.get(i + 1);
                                        if (TextUtils.isEmpty((CharSequence)remark2)) {
                                            continue;
                                        }
                                        if (Character.isLetterOrDigit(remark2.toCharArray()[0])) {
                                            continue;
                                        }
                                        if (a3.remark == null) {
                                            break Label_1314;
                                        }
                                        sb5 = new StringBuilder();
                                        sb5.append(a3.remark);
                                        sb5.append("-");
                                        break Label_1299;
                                    }
                                    else {
                                        if ((!"提现至：".equals(s2) && !"转出至：".equals(s2)) || !b4) {
                                            continue;
                                        }
                                        a3.detailType = 2;
                                        b6 = list.get(i + 1);
                                    }
                                    f7.b = b6;
                                    continue;
                                }
                            }
                            sb5.append(remark2);
                            remark2 = sb5.toString();
                        }
                        a3.remark = remark2;
                    }
                    f4 = f2;
                    if (super.isSetMoney) {
                        f7.c = a3.remark;
                        f7.d = a3;
                        f4 = f7;
                    }
                }
                return f4;
            }
        }*/
        return null;
    }

    public f q(final List<String> list) {
        final f f = new f();
       /* final DetailEntity a = this.createBillInfo();
        int n2 = 0;
        for (int i = 0; i < list.size(); i = n2 + 1) {
            final String s = list.get(i);
            final boolean b = i < list.size() - 1;
            String s2;
            if (("支出金额".equals(s) || "订单金额".equals(s)) && b) {
                final int n = i + 1;
                s2 = list.get(n).replace("元", "");
                n2 = i;
                if (super.isSetMoney) {
                    continue;
                }
                n2 = i;
                if (!this.isMoney(s2)) {
                    continue;
                }
                n2 = n;
            }
            else if ("实付金额（元）".equals(s) && b) {
                final int n3 = i + 1;
                s2 = list.get(n3).replace("元", "");
                n2 = i;
                if (!this.isMoney(s2)) {
                    continue;
                }
                n2 = n3;
            }
            else {
                if (!"收入金额".equals(s) || !b) {
                    String string = null;
                    Label_0963: {
                        Label_0889: {
                            if ((!"商户名称".equals(s) && !"乘车线路".equals(s)) || !b) {
                                StringBuilder sb;
                                if ("卡号".equals(s) && b) {
                                    ++i;
                                    f.a = list.get(i).replace("尾号", "");
                                    final int n4 = i + 1;
                                    n2 = i;
                                    if (n4 >= list.size()) {
                                        continue;
                                    }
                                    n2 = i;
                                    if (!list.get(n4).contains("****")) {
                                        continue;
                                    }
                                    sb = new StringBuilder();
                                    n2 = n4;
                                }
                                else {
                                    if (!"对方卡号".equals(s) || !b) {
                                        String a2;
                                        if (s.startsWith("付款卡") && b) {
                                            n2 = i + 1;
                                            a2 = list.get(n2);
                                        }
                                        else {
                                            if (("时间".equals(s) || "订单时间".equals(s) || "扣款时间".equals(s)) && b) {
                                                n2 = i + 1;
                                                a.time = e.t((String)list.get(n2), "yyyy-MM-dd HH:mm");
                                                continue;
                                            }
                                            if ("分类".equals(s) && b) {
                                                break Label_0889;
                                            }
                                            if ("附言".equals(s) && b) {
                                                ++i;
                                                final String remark = list.get(i);
                                                a.remark = remark;
                                                n2 = i;
                                                if (!"微信零钱提现".equals(remark)) {
                                                    continue;
                                                }
                                                a2 = "微信钱包";
                                                n2 = i;
                                            }
                                            else if ("交易类型".equals(s) && b) {
                                                ++i;
                                                final String remark2 = list.get(i);
                                                a.remark = remark2;
                                                n2 = i;
                                                if ("信用卡还款".equals(remark2)) {
                                                    a.detailType = 2;
                                                    n2 = i;
                                                }
                                                continue;
                                            }
                                            else if ("信用卡卡号".equals(s) && b) {
                                                ++i;
                                                f.b = list.get(i);
                                                n2 = i;
                                                if (f.a == null) {
                                                    f.a = "";
                                                    n2 = i;
                                                }
                                                continue;
                                            }
                                            else {
                                                n2 = i;
                                                if (!"收款人".equals(s)) {
                                                    continue;
                                                }
                                                n2 = i;
                                                if (!b) {
                                                    continue;
                                                }
                                                if (a.remark == null) {
                                                    break Label_0889;
                                                }
                                                final StringBuilder sb2 = new StringBuilder();
                                                sb2.append(a.remark);
                                                sb2.append("-");
                                                n2 = i + 1;
                                                sb2.append(list.get(n2));
                                                string = sb2.toString();
                                                break Label_0963;
                                            }
                                        }
                                        f.a = a2;
                                        continue;
                                    }
                                    a.detailType = 2;
                                    f.b = f.a;
                                    final int n5 = i + 1;
                                    f.a = list.get(n5).replace("尾号", "");
                                    final int n6 = n5 + 1;
                                    n2 = n5;
                                    if (n6 >= list.size()) {
                                        continue;
                                    }
                                    n2 = n5;
                                    if (!list.get(n6).contains("****")) {
                                        continue;
                                    }
                                    sb = new StringBuilder();
                                    n2 = n6;
                                }
                                sb.append(f.a);
                                sb.append(list.get(n2));
                                f.a = sb.toString();
                                continue;
                            }
                        }
                        n2 = i + 1;
                        string = list.get(n2);
                    }
                    a.remark = string;
                    continue;
                }
                final int n7 = i + 1;
                final String replace = list.get(n7).replace("元", "");
                n2 = i;
                if (this.isMoney(replace)) {
                    this.setMoney(a, replace);
                    a.detailType = 1;
                    n2 = n7;
                }
                continue;
            }
            this.setMoney(a, s2);
        }
        if (super.isSetMoney) {
            f.c = a.remark;
            f.d = a;
            return f;
        }*/
        return null;
    }

    public f r(final List list) {
        final int b = this.b;
        final f f = null;
        final f f2 = null;
        return null;
     /*   switch (b) {
            default: {
                final f f3 = new f();
                final DetailEntity a = this.createBillInfo();
                int n;
                for (int i = 0; i < list.size(); i = n + 1) {
                    final String s = list.get(i);
                    final boolean b2 = i < list.size() - 1;
                    if (!super.isSetMoney && s.contains("¥")) {
                        final String replace = s.replace("¥", "");
                        n = i;
                        if (this.isMoney(replace)) {
                            this.setMoney(a, replace);
                            n = i;
                        }
                    }
                    else if ("订单信息".equals(s) && b2) {
                        n = i + 1;
                        a.remark = list.get(n);
                    }
                    else {
                        n = i;
                        if ("付款方式".equals(s)) {
                            n = i;
                            if (b2) {
                                ++i;
                                f3.a = list.get(i);
                                final int n2 = i + 2;
                                n = i;
                                if (n2 < list.size()) {
                                    n = i;
                                    if (list.get(n2).endsWith("]")) {
                                        final StringBuilder sb = new StringBuilder();
                                        sb.append(f3.a);
                                        sb.append(list.get(i + 1));
                                        sb.append(list.get(n2));
                                        f3.a = sb.toString();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                f f4 = f;
                if (super.isSetMoney) {
                    f3.c = a.remark;
                    f3.d = a;
                    f4 = f3;
                }
                return f4;
            }
            case 0: {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("[auto] JdDetailParser parsePay ");
                sb2.append(list.toString());
                k.c(sb2.toString());
                final f f5 = new f();
                final DetailEntity a2 = this.createBillInfo();
                for (int j = 0; j < list.size(); ++j) {
                    final String remark = list.get(j);
                    final boolean b3 = j < list.size() - 1;
                    if ("支付成功".equals(remark) && b3) {
                        final String o = e3.b.o((String)list.get(j + 1));
                        if (o != null && this.isMoney(o)) {
                            this.setMoney(a2, o);
                        }
                    }
                    else if (remark.contains("白条支付")) {
                        f5.a = "京东白条";
                    }
                    else if (remark.contains("充值面值")) {
                        a2.remark = remark;
                    }
                }
                f f6 = f2;
                if (super.isSetMoney) {
                    if (this.e(a2.amount)) {
                        f5.g = true;
                    }
                    if (a2.remark == null) {
                        a2.remark = "京东订单";
                    }
                    f5.c = a2.remark;
                    f5.d = a2;
                    f6 = f5;
                }
                return f6;
            }
        }*/
    }
}

