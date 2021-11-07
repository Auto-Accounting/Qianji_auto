//
// Decompiled by Procyon - 3561ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.dreamn.qianji_auto.core.helper.star.Analyze;

public class a extends c {
    public final /* synthetic */ int b;

    public a(final int b) {
        this.b = b;
        if (b != 1) {
            return;
        }
    }

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

    public f p(final List list, int i) {
        final int b = this.b;
        String s = "";
        String s2 = "¥";
        final String s3 = ",";
      /*  switch (b) {
            default: {
                final String s4 = ",";
                if (list.size() != 0) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("[auto] PddDetailParser parse type ");
                    sb.append(i);
                    sb.append(" ");
                    sb.append(list.toString());
                    k.c(sb.toString());
                    final f f = new f();
                    final DetailEntity a = this.createBillInfo();
                Label_3586_Outer:
                    for (i = 0; i < list.size(); ++i) {
                        final String s5 = list.get(i);
                        final boolean b2 = i < list.size() - 1;
                        String string = null;
                    Label_3694:
                        while (true) {
                            Label_3589: {
                                if (i != 4 && i != 5) {
                                    break Label_3589;
                                }
                                final String replace = s5.replace("+", "").replace("-", "").replace(s4, "");
                                if (!this.isMoney(replace)) {
                                    break Label_3589;
                                }
                                this.setMoney(a, replace);
                                if (s5.contains("+")) {
                                    a.detailType = 1;
                                    f.a = "多多钱包";
                                }
                                final String remark = list.get(i - 1);
                                a.remark = remark;
                                if ("余额提现".equals(remark)) {
                                    a.detailType = 2;
                                    f.a = "多多钱包";
                                    a.remark = "拼多多余额提现";
                                    continue Label_3586_Outer;
                                }
                                if (!"余额充值".equals(a.remark)) {
                                    continue Label_3586_Outer;
                                }
                                a.detailType = 2;
                                f.b = "多多钱包";
                                string = "拼多多余额充值";
                                break Label_3694;
                            }
                            if (("商品详情".equals(s5) || "关联商品".equals(s5)) && b2) {
                                if (a.remark != null) {
                                    final StringBuilder sb2 = new StringBuilder();
                                    sb2.append(a.remark);
                                    sb2.append("-");
                                    sb2.append(list.get(i + 1));
                                    string = sb2.toString();
                                }
                                else {
                                    string = list.get(i + 1);
                                }
                            }
                            else {
                                if (a.time == null && s5.endsWith("时间") && b2) {
                                    a.time = e.u((String)list.get(i + 1));
                                    continue Label_3586_Outer;
                                }
                                String a2;
                                if ("支付方式".equals(s5) && b2) {
                                    a2 = list.get(i + 1);
                                    if (a2.contains(":")) {
                                        a2 = a2.split(":")[0];
                                    }
                                }
                                else {
                                    if ("提现至".equals(s5) && b2) {
                                        f.b = (String) list.get(i + 1);
                                        continue Label_3586_Outer;
                                    }
                                    if ("充值金额".equals(s5) && b2) {
                                        final String replace2 = list.get(i + 1).replace("¥", "");
                                        if (this.isMoney(replace2)) {
                                            this.setMoney(a, replace2);
                                            continue;
                                        }
                                        continue Label_3586_Outer;
                                    }
                                    else if ("提现金额".equals(s5) && b2) {
                                        final String replace3 = list.get(i + 1).replace("¥", "");
                                        if (this.isMoney(replace3)) {
                                            this.setMoney(a, replace3);
                                            a.detailType = 2;
                                            f.a = "多多钱包";
                                            a.remark = "拼多多余额提现";
                                        }
                                        continue Label_3586_Outer;
                                    }
                                    else if ("充值方式".equals(s5) && b2) {
                                        a2 = list.get(i + 1);
                                    }
                                    else {
                                        if ("退款至".equals(s5) && b2) {
                                            f.a = list.get(i + 1);
                                            String string2;
                                            if (TextUtils.isEmpty((CharSequence)a.remark)) {
                                                string2 = "退款";
                                            }
                                            else {
                                                final StringBuilder sb3 = new StringBuilder();
                                                sb3.append("退款-");
                                                sb3.append(a.remark);
                                                string2 = sb3.toString();
                                            }
                                            a.remark = string2;
                                        }
                                        continue Label_3586_Outer;
                                    }
                                }
                                f.a = a2;
                                continue Label_3586_Outer;
                            }
                            break;
                        }
                        a.remark = string;
                    }
                    if (super.isSetMoney) {
                        if ("多多钱包余额".equals(f.a)) {
                            f.a = "多多钱包";
                        }
                        f.c = a.remark;
                        f.d = a;
                        return f;
                    }
                }
                return null;
            }
            case 0: {
                if (list.size() != 0) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("[auto] AlipayDetailParser parse type ");
                    sb4.append(i);
                    sb4.append(" ");
                    sb4.append(list.toString());
                    k.c(sb4.toString());
                    final String s6 = "收款方";
                    final String s7 = "余额";
                    String s8 = "付款方式";
                    final String s9 = "账户余额";
                    final String s10 = "元";
                    final String s11 = "提现金额";
                    final String a3 = "支付宝";
                    String s12 = "-";
                    f f4;
                    if (i == 2) {
                        k.c("[auto] AlipayDetailParser parseTransfer");
                        final f f3 = new f();
                        final DetailEntity a4 = this.createBillInfo();
                        i = 0;
                        final String s13 = s6;
                        while (i < list.size()) {
                            final String s14 = list.get(i);
                            if (s13.equals(s14) && i < list.size() - 1) {
                                final StringBuilder sb5 = new StringBuilder();
                                sb5.append("转账给");
                                sb5.append(list.get(i + 1));
                                a4.remark = sb5.toString();
                            }
                            else if ("转账成功".equals(s14) && i < list.size() - 2) {
                                String s15;
                                if (!this.isMoney(s15 = list.get(i + 1).replace("￥", "").replace("¥", "").replace("元", "").replace(",", ""))) {
                                    s15 = list.get(i + 2).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                                }
                                if (this.isMoney(s15)) {
                                    this.setMoney(a4, s15);
                                }
                            }
                            else if ("付款方式".equals(s14)) {
                                f3.a = list.get(i + 1);
                            }
                            ++i;
                        }
                        if (!super.isSetMoney) {
                            return null;
                        }
                        if (!TextUtils.isEmpty((CharSequence)f3.a) && TextUtils.isEmpty((CharSequence)a4.remark)) {
                            a4.remark = "支付宝转账";
                        }
                        if ("账户余额".equals(f3.a) || "余额".equals(f3.a)) {
                            f3.a = "支付宝";
                        }
                        f3.c = a4.remark;
                        f3.d = a4;
                        f4 = f3;
                    }
                    else {
                        String s16 = "收款方";
                        final f f5 = new f();
                        final DetailEntity a5 = this.createBillInfo();
                        String s17 = "->";
                        int j = 0;
                        i = 0;
                        while (j < list.size()) {
                            final String s18 = list.get(j);
                            final boolean b3 = j < list.size() - 1;
                            String s35 = null;
                            int n4 = 0;
                            String s36 = null;
                            String s37 = null;
                            String s38 = null;
                            String s39 = null;
                            Label_3014: {
                                String s21 = null;
                                String s31 = null;
                                Label_2766: {
                                    String s19 = null;
                                    int n = 0;
                                    Label_0969: {
                                        String string3;
                                        if ("转账备注".equals(s18)) {
                                            s19 = s16;
                                            n = i;
                                            if (!b3) {
                                                break Label_0969;
                                            }
                                            final String s20 = list.get(j + 1);
                                            if ("转账".equals(s20)) {
                                                n = 1;
                                                s19 = s16;
                                                break Label_0969;
                                            }
                                            final StringBuilder sb6 = new StringBuilder();
                                            sb6.append("转账-");
                                            sb6.append(s20);
                                            string3 = sb6.toString();
                                        }
                                        else {
                                            if (s16.equals(s18) && b3) {
                                                a5.remark = list.get(j + 1);
                                                s19 = s16;
                                                n = i;
                                                break Label_0969;
                                            }
                                            s21 = s16;
                                            if ((!"支付成功".equals(s18) && !"代付成功".equals(s18)) || j >= list.size() - 2) {
                                                final boolean equals = "交易成功".equals(s18);
                                                final String s22 = s8;
                                                String s25 = null;
                                                String s26 = null;
                                                String s32 = null;
                                                String s33 = null;
                                                Label_2746: {
                                                    String s23 = null;
                                                    String s24 = null;
                                                    Label_1998: {
                                                        String remark3 = null;
                                                        Label_1870: {
                                                            int detailType = 0;
                                                            Label_1773: {
                                                                if ((equals || "有退款".equals(s18) || "放款成功".equals(s18) || "自动扣款成功".equals(s18) || "已全额退款".equals(s18) || "退款成功".equals(s18) || "还款成功".equals(s18) || "亲情卡付款成功".equals(s18) || "等待对方发货".equals(s18) || "等待确认收货".equals(s18) || s18.contains("付款成功")) && j > 0) {
                                                                    final int n2 = j - 1;
                                                                    final String replace4 = list.get(n2).replace("￥", s).replace(s2, s).replace(s3, s).replace("+", s).replace(s10, s).replace(s12, s);
                                                                    Label_1516: {
                                                                        if (this.isMoney(replace4)) {
                                                                            this.setMoney(a5, replace4);
                                                                            if ("还款成功".equals(s18) && j > 1) {
                                                                                final String b4 = list.get(j - 2);
                                                                                f5.b = b4;
                                                                                if (!"备用金".equals(b4)) {
                                                                                    detailType = 2;
                                                                                    s23 = s;
                                                                                    break Label_1773;
                                                                                }
                                                                                f5.a = a3;
                                                                                a5.detailType = 3;
                                                                                final double amount = a5.amount;
                                                                                if (amount > 500.0 && amount < 510.0) {
                                                                                    f5.e = amount - 500.0;
                                                                                    f5.f = true;
                                                                                    a5.amount = 500.0;
                                                                                }
                                                                                break Label_1516;
                                                                            }
                                                                            else if ("放款成功".equals(s18) && j > 1) {
                                                                                a5.detailType = 3;
                                                                                f5.a = list.get(j - 2);
                                                                                f5.b = a3;
                                                                                break Label_1516;
                                                                            }
                                                                        }
                                                                        Label_1446: {
                                                                            if (list.get(n2).contains("+")) {
                                                                                if (a5.remark != null) {
                                                                                    a5.remark = "支付宝收款";
                                                                                }
                                                                                a5.detailType = 1;
                                                                            }
                                                                            else {
                                                                                String remark2;
                                                                                if (j > 1) {
                                                                                    if (!TextUtils.isEmpty((CharSequence)a5.remark)) {
                                                                                        break Label_1446;
                                                                                    }
                                                                                    remark2 = list.get(j - 2);
                                                                                }
                                                                                else {
                                                                                    remark2 = "支付宝付款";
                                                                                }
                                                                                a5.remark = remark2;
                                                                            }
                                                                        }
                                                                        if ("退款成功".equals(s18)) {
                                                                            a5.detailType = 1;
                                                                            final StringBuilder sb7 = new StringBuilder();
                                                                            sb7.append(a5.remark);
                                                                            sb7.append("-退款");
                                                                            final String string4 = sb7.toString();
                                                                            s23 = s;
                                                                            s24 = s2;
                                                                            remark3 = string4;
                                                                            s25 = s12;
                                                                            break Label_1870;
                                                                        }
                                                                    }
                                                                    s23 = s;
                                                                    s24 = s2;
                                                                    s25 = s12;
                                                                    s26 = s22;
                                                                    break Label_1998;
                                                                }
                                                                final String a6 = a3;
                                                                Label_1884: {
                                                                    if (!super.isSetMoney) {
                                                                        if (!s18.contains("￥")) {
                                                                            if (!s18.contains(s2)) {
                                                                                if (!s18.contains(s12)) {
                                                                                    if (!s18.contains("+")) {
                                                                                        break Label_1884;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        final String s27 = s12;
                                                                        final String s28 = s2;
                                                                        final String s29 = s;
                                                                        final String replace5 = s18.replace("￥", s29).replace(s28, s29).replace(s3, s29).replace("+", s29);
                                                                        String s30 = s18;
                                                                        String replace6 = replace5;
                                                                        if (!this.isMoney(replace5)) {
                                                                            s30 = s18;
                                                                            replace6 = replace5;
                                                                            if (b3) {
                                                                                s30 = list.get(j + 1);
                                                                                replace6 = s30.replace("￥", s29).replace(s28, s29).replace(s3, s29).replace("+", s29);
                                                                            }
                                                                        }
                                                                        s24 = s28;
                                                                        s25 = s27;
                                                                        s23 = s29;
                                                                        s26 = s22;
                                                                        if (!this.isMoney(replace6)) {
                                                                            break Label_1998;
                                                                        }
                                                                        this.setMoney(a5, replace6);
                                                                        if (s30.contains("+")) {
                                                                            a5.remark = "支付宝收款";
                                                                            detailType = 1;
                                                                            s23 = s29;
                                                                            break Label_1773;
                                                                        }
                                                                        s24 = s28;
                                                                        s25 = s27;
                                                                        s23 = s29;
                                                                        s26 = s22;
                                                                        if (!TextUtils.isEmpty((CharSequence)a5.remark)) {
                                                                            break Label_1998;
                                                                        }
                                                                        s24 = s28;
                                                                        s25 = s27;
                                                                        s23 = s29;
                                                                        s26 = s22;
                                                                        if (j > 0) {
                                                                            remark3 = list.get(j - 1);
                                                                            s23 = s29;
                                                                            s25 = s27;
                                                                            s24 = s28;
                                                                            break Label_1870;
                                                                        }
                                                                        break Label_1998;
                                                                    }
                                                                }
                                                                if ((s8.equals(s18) || "退款方式".equals(s18) || "付款信息".equals(s18)) && b3) {
                                                                    f5.a = list.get(j + 1);
                                                                }
                                                                else if ("创建时间".equals(s18) && b3) {
                                                                    a5.time = e.t((String)list.get(j + 1), "yyyy-MM-dd HH:mm:ss");
                                                                }
                                                                else {
                                                                    if (("商品说明".equals(s18) || "红包说明".equals(s18) || "红包来自".equals(s18) || "放款说明".equals(s18) || "还款说明".equals(s18)) && b3) {
                                                                        if ("商品说明".equals(s18)) {
                                                                            final int n3 = j + 1;
                                                                            if (list.get(n3).contains("-转出到")) {
                                                                                a5.detailType = 2;
                                                                                final String[] split = list.get(n3).split("-转出到");
                                                                                f5.a = split[0];
                                                                                f5.b = split[1];
                                                                                s31 = "转出到";
                                                                                break Label_2766;
                                                                            }
                                                                        }
                                                                        final String remark4 = a5.remark;
                                                                        String string5;
                                                                        if (remark4 != null && !"支付宝收款".equals(remark4)) {
                                                                            final StringBuilder sb8 = new StringBuilder();
                                                                            sb8.append(list.get(j + 1));
                                                                            sb8.append(s12);
                                                                            sb8.append(a5.remark);
                                                                            string5 = sb8.toString();
                                                                        }
                                                                        else {
                                                                            string5 = list.get(j + 1);
                                                                        }
                                                                        a5.remark = string5;
                                                                        s32 = s2;
                                                                        s25 = s12;
                                                                        s33 = s;
                                                                        s26 = s8;
                                                                        break Label_2746;
                                                                    }
                                                                    if (("还款到".equals(s18) || "提现到".equals(s18) || "转入账户".equals(s18) || "到账银行卡".equals(s18)) && b3) {
                                                                        a5.detailType = 2;
                                                                        f5.b = list.get(j + 1);
                                                                        s31 = s18;
                                                                        break Label_2766;
                                                                    }
                                                                    if ("提现说明".equals(s18)) {
                                                                        f5.a = a6;
                                                                        s32 = s2;
                                                                        s25 = s12;
                                                                        s33 = s;
                                                                        s26 = s8;
                                                                        break Label_2746;
                                                                    }
                                                                    if ("转出说明".equals(s18) && b3) {
                                                                        final String s34 = list.get(j + 1);
                                                                        s32 = s2;
                                                                        s25 = s12;
                                                                        s33 = s;
                                                                        s26 = s8;
                                                                        if (s34.contains("-转出到")) {
                                                                            f5.a = s34.split("-转出到")[0];
                                                                            s32 = s2;
                                                                            s25 = s12;
                                                                            s33 = s;
                                                                            s26 = s8;
                                                                        }
                                                                        break Label_2746;
                                                                    }
                                                                    else {
                                                                        if (s18.contains("收益发放") || s18.contains("奖励发放") || s18.contains("理财赎回") || s18.contains("卖出至")) {
                                                                            a5.detailType = 1;
                                                                            s35 = s8;
                                                                            n4 = i;
                                                                            s16 = s21;
                                                                            s36 = s17;
                                                                            s37 = s;
                                                                            s38 = s12;
                                                                            s39 = s2;
                                                                            break Label_3014;
                                                                        }
                                                                        if ("充值成功".equals(s18) && b3) {
                                                                            final int n5 = j + 1;
                                                                            s32 = s2;
                                                                            s25 = s12;
                                                                            s33 = s;
                                                                            s26 = s8;
                                                                            if (this.isMoney((String)list.get(n5))) {
                                                                                this.setMoney(a5, (String)list.get(n5));
                                                                                a5.detailType = 2;
                                                                                f5.b = a6;
                                                                                s32 = s2;
                                                                                s25 = s12;
                                                                                s33 = s;
                                                                                s26 = s8;
                                                                            }
                                                                            break Label_2746;
                                                                        }
                                                                        else if ("服务费".equals(s18) && b3) {
                                                                            final String replace7 = list.get(j + 1).replace("￥", s).replace(s2, s);
                                                                            s32 = s2;
                                                                            s25 = s12;
                                                                            s33 = s;
                                                                            s26 = s8;
                                                                            if (this.isMoney(replace7)) {
                                                                                f5.e = e3.b.H(replace7);
                                                                                f5.f = true;
                                                                                s26 = s8;
                                                                                s33 = s;
                                                                                s25 = s12;
                                                                                s32 = s2;
                                                                            }
                                                                            break Label_2746;
                                                                        }
                                                                        else {
                                                                            s39 = s2;
                                                                            s38 = s12;
                                                                            s37 = s;
                                                                            s36 = s17;
                                                                            s16 = s21;
                                                                            n4 = i;
                                                                            s35 = s8;
                                                                            if (!s11.equals(s18)) {
                                                                                break Label_3014;
                                                                            }
                                                                            s39 = s2;
                                                                            s38 = s12;
                                                                            s37 = s;
                                                                            s36 = s17;
                                                                            s16 = s21;
                                                                            n4 = i;
                                                                            s35 = s8;
                                                                            if (!b3) {
                                                                                break Label_3014;
                                                                            }
                                                                            final String replace8 = list.get(j + 1).replace("￥", s).replace(s2, s);
                                                                            s39 = s2;
                                                                            s38 = s12;
                                                                            s37 = s;
                                                                            s36 = s17;
                                                                            s16 = s21;
                                                                            n4 = i;
                                                                            s35 = s8;
                                                                            if (this.isMoney(replace8)) {
                                                                                this.setMoney(a5, replace8);
                                                                                a5.detailType = 2;
                                                                                f5.a = a6;
                                                                                s39 = s2;
                                                                                s38 = s12;
                                                                                s37 = s;
                                                                                s36 = s17;
                                                                                s16 = s21;
                                                                                n4 = i;
                                                                                s35 = s8;
                                                                            }
                                                                            break Label_3014;
                                                                        }
                                                                    }
                                                                }
                                                                s26 = s8;
                                                                s23 = s;
                                                                s25 = s12;
                                                                s24 = s2;
                                                                break Label_1998;
                                                            }
                                                            s24 = s2;
                                                            a5.detailType = detailType;
                                                            s25 = s12;
                                                            s26 = s22;
                                                            break Label_1998;
                                                        }
                                                        a5.remark = remark3;
                                                        s26 = s22;
                                                    }
                                                    s32 = s24;
                                                    s33 = s23;
                                                }
                                                s8 = s26;
                                                s = s33;
                                                s12 = s25;
                                                s2 = s32;
                                                s31 = s17;
                                                break Label_2766;
                                            }
                                            final String replace9 = list.get(j + 1).replace("￥", s).replace(s2, s).replace(s10, s).replace(s3, s);
                                            if (this.isMoney(replace9)) {
                                                this.setMoney(a5, replace9);
                                                if ("代付成功".equals(s18)) {
                                                    a5.remark = "支付宝代付";
                                                }
                                            }
                                            final String replace10 = list.get(j + 2).replace("￥", s).replace(s2, s).replace(s10, s).replace(s3, s);
                                            if (!this.isMoney(replace9) && this.isMoney(replace10)) {
                                                this.setMoney(a5, replace10);
                                                s19 = s21;
                                                n = i;
                                                break Label_0969;
                                            }
                                            s19 = s21;
                                            n = i;
                                            if (s8.equals(replace10)) {
                                                break Label_0969;
                                            }
                                            s16 = s21;
                                            string3 = replace10;
                                        }
                                        a5.remark = string3;
                                        n = i;
                                        s19 = s16;
                                    }
                                    s31 = s17;
                                    s21 = s19;
                                    i = n;
                                }
                                s39 = s2;
                                s38 = s12;
                                s37 = s;
                                s36 = s31;
                                s16 = s21;
                                n4 = i;
                                s35 = s8;
                            }
                            ++j;
                            s2 = s39;
                            s12 = s38;
                            s8 = s35;
                            s = s37;
                            s17 = s36;
                            i = n4;
                        }
                        if (!super.isSetMoney) {
                            return null;
                        }
                        if (s9.equals(f5.a) || s7.equals(f5.a)) {
                            f5.a = a3;
                        }
                        if (s9.equals(f5.b) || s7.equals(f5.b)) {
                            f5.b = a3;
                        }
                        if (i != 0 && !"支付宝收款".equals(a5.remark)) {
                            final StringBuilder sb9 = new StringBuilder();
                            sb9.append("转账给");
                            sb9.append(a5.remark);
                            a5.remark = sb9.toString();
                        }
                        i = a5.detailType;
                        if (i == 2) {
                            String a7;
                            if ((a7 = f5.a) == null) {
                                a7 = s;
                            }
                            final String b5 = f5.b;
                            if (b5 != null) {
                                s = b5;
                            }
                            final StringBuilder sb10 = new StringBuilder();
                            sb10.append(a7);
                            sb10.append(s17);
                            sb10.append(s);
                            a5.remark = sb10.toString();
                        }
                        else if (i == 1 && f5.a == null) {
                            f5.a = a3;
                        }
                        f5.c = a5.remark;
                        f5.d = a5;
                        f4 = f5;
                    }
                    return f4;
                }
                return null;
            }
        }*/
        return null;
    }
}

