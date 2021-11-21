//
// Decompiled by Procyon - 2200ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;

import java.util.List;

import cn.dreamn.qianji_auto.core.helper.star.Analyze;

public class e extends c {
    public static boolean l(final String s, final String s2) {
        return "com.sankuai.waimai.business.knb.KNBWebViewActivity".equals(s2) || "com.meituan.android.hybridcashier.HybridCashierActivity".equals(s2) || "com.sankuai.eh.framework.EHContainerActivity".equals(s2) || "com.meituan.android.pay.activity.PayActivity".equals(s2) || (("com.sankuai.meituan.takeoutnew".equals(s) || "com.sankuai.meituan".equals(s)) && "android.webkit.WebView".equals(s2));
    }

    public static boolean m(final List<String> list) {
        final int size = list.size();
        final boolean b = true;
        if (size >= 3) {
            boolean b2 = b;
            if (Analyze.checkNode(list, "交易详情", true)) {
                return b2;
            }
            b2 = b;
            if (Analyze.checkNode(list, "交易类型", true)) {
                return b2;
            }
            if (Analyze.checkNode(list, "支付成功", true)) {
                b2 = b;
                return b2;
            }
        }
        return false;
    }

    public f n(final List<String> list, int i) {
        /*final int size = list.size();
        final f f = null;
        if (size == 0) {
            return null;
        }
        if (list.contains("支付成功")) {
            final StringBuilder a = b.c.a("[auto] MeituanDetailParser parsePay ");
            a.append(list.toString());
            k.c(a.toString());
            final f f2 = new f();
            final DetailEntity a2 = this.createBillInfo();
            String s;
            boolean b;
            String o;
            for (i = 0; i < list.size(); ++i) {
                s = list.get(i);
                b = (i < list.size() - 1);
                if ("支付成功".equals(s) && b) {
                    o = e3.b.o((String)list.get(i + 1));
                    if (this.isMoney(o)) {
                        this.setMoney(a2, o);
                        i += 2;
                        if (i < list.size()) {
                            f2.a = list.get(i);
                            break;
                        }
                        break;
                    }
                }
            }
            f f3 = f;
            if (super.isSetMoney) {
                if (this.e(a2.amount)) {
                    f2.g = true;
                }
                a2.remark = "美团订单";
                f2.c = "美团订单";
                f2.d = a2;
                f3 = f2;
            }
            return f3;
        }
        final StringBuilder a3 = u0.a("[auto] MeituanDetailParser parse type ", i, " ");
        a3.append(list.toString());
        k.c(a3.toString());
        final f f4 = new f();
        final DetailEntity a4 = this.createBillInfo();
        String s2;
        boolean b2;
        boolean b3;
        String s3;
        String replace;
        StringBuilder a5 = null;
        Date u;
        String remark = null;
        String remark2;
        String s4;
        String replace2;
        String replace3;
        for (i = 0; i < list.size(); ++i) {
            s2 = list.get(i);
            b2 = (i < list.size() - 1);
            b3 = (i > 0);
            Label_0490: {
                if (("交易成功".equals(s2) || "退款".equals(s2)) && b3) {
                    s3 = list.get(i - 1);
                    replace = s3.replace("+", "").replace("-", "").replace(",", "");
                    if (!this.isMoney(replace)) {
                        continue;
                    }
                    this.setMoney(a4, replace);
                    if (i >= 2) {
                        a4.remark = list.get(i - 2).replace("订单详情", "");
                    }
                    if (!s3.contains("+")) {
                        continue;
                    }
                    a4.detailType = 1;
                    a5 = t.f.a(s2, "-");
                }
                else if (s2.contains("交易时间")) {
                    u = f.e.u(s2.replace("交易时间", ""));
                    a4.time = u;
                    if (u == null && b2) {
                        a4.time = f.e.u((String)list.get(i + 1));
                    }
                    continue;
                }
                else {
                    if ("支付方式".equals(s2) && b2) {
                        f4.a = list.get(i + 1);
                        continue;
                    }
                    if ("还款详情".equals(s2) && b2) {
                        a4.detailType = 2;
                        f4.b = a4.remark;
                        a5 = new StringBuilder();
                        a5.append(a4.remark);
                        remark = list.get(i + 1);
                        break Label_0490;
                    }
                    if ("备注".equals(s2) && b2) {
                        remark2 = list.get(i + 1);
                        a4.remark = remark2;
                        if ("充值".equals(remark2)) {
                            a4.detailType = 2;
                            f4.b = "美团零钱";
                            a5 = new StringBuilder();
                            s4 = f4.b;
                        }
                        else {
                            if (!"提现".equals(a4.remark)) {
                                continue;
                            }
                            a4.detailType = 2;
                            f4.a = "美团零钱";
                            a5 = new StringBuilder();
                            s4 = f4.a;
                        }
                        a5.append(s4);
                    }
                    else if ("入账金额".equals(s2) && b2) {
                        replace2 = list.get(i + 1).replace("+", "");
                        if (this.isMoney(replace2)) {
                            this.setMoney(a4, replace2);
                            a4.detailType = 1;
                        }
                        continue;
                    }
                    else {
                        if (!"出账金额".equals(s2) || !b2) {
                            continue;
                        }
                        replace3 = list.get(i + 1).replace("-", "");
                        if (this.isMoney(replace3)) {
                            this.setMoney(a4, replace3);
                        }
                        continue;
                    }
                }
                remark = a4.remark;
            }
            a5.append(remark);
            a4.remark = a5.toString();
        }
        if (super.isSetMoney) {
            f4.c = a4.remark;
            f4.d = a4;
            return f4;
        }*/
        return null;
    }
}

