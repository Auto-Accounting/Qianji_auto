//
// Decompiled by Procyon - 3012ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.core.helper.star.Analyze;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


public class h extends c {
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

    public f r(final List<String> list, int i) {
        final int size = list.size();
        if (size == 0) {
            return null;
        }
        Log.i("[auto] WxDetailParser parse type" + i + " " + list.toString());
        final String s = "零钱通";
        final int n = 0;
        if (i == 1) {
            f f2;
            if ("二维码收款".equals(list.get(0))) {
                f2 = null;
            } else {
                final f f3 = new f();
                final BillInfo billinfo = this.createBillInfo();
                final boolean endsWith = list.get(0).endsWith("发起的群收款");
                i = n;
                while (i < list.size()) {
                    final String s7 = list.get(i);
                    Log.i("循环数据：" + s7);
                    final String replace = this.replace(s7);
                    String remark;

                    if (this.isMoney(replace)) {
                        this.setMoney(billinfo, replace);
                        if (this.c) {
                            billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                            f3.b = s;
                            remark = "转入零钱通";
                            billinfo.setShopRemark(remark);
                            break;
                        }
                    } else {
                        String remark2;

                        int n2;
                        if ("收款方".equals(s7) && i < list.size() - 1) {
                            n2 = i + 1;
                            remark2 = list.get(n2);
                            billinfo.setShopRemark(remark2);
                        } else if ("支付成功".equals(s7) && i < list.size() - 2) {
                            n2 = i + 1;
                            if (list.get(n2).contains("¥")) {
                                ++i;
                                continue;
                            }
                            remark2 = list.get(n2);
                            if (remark2.endsWith("确认收款") && remark2.startsWith("待")) {

                                billinfo.setShopAccount(remark2.substring(1, remark2.length() - 4));
                                billinfo.setShopRemark("转账");
                            } else {
                                billinfo.setShopRemark(remark2);
                            }

                        } else if (endsWith && s7.contains("已收齐") && i < list.size() - 1) {
                            final String replace2 = list.get(i + 1).replace("收到¥", "");
                            billinfo.setType(BillInfo.TYPE_INCOME);
                            if (this.isMoney(replace2)) {
                                this.setMoney(billinfo, replace2);
                                remark = "我发起的群收款-已收齐";
                                billinfo.setShopRemark(remark);
                                break;
                            }
                            ++i;
                            continue;
                        } else if (endsWith && s7.contains("已支付")) {
                            final String replace3 = replace.replace("已支付", "");
                            if (this.isMoney(replace3)) {
                                this.setMoney(billinfo, replace3);
                                remark = list.get(0);
                                billinfo.setShopRemark(remark);
                                break;
                            }
                            ++i;
                            continue;
                        } else {
                            if ("充值成功".equals(s7)) {
                                billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                // billinfo.detailType = 2;
                                f3.b = "微信钱包";
                                remark2 = "微信零钱充值";
                                billinfo.setShopRemark(remark2);
                            }
                            ++i;
                            continue;
                        }
                    }
                    ++i;

                }
                if (!TextUtils.isEmpty(this.b)) {
                    if ("零钱".equals(this.b)) {
                        this.b = "微信钱包";
                    }
                    f3.a = this.b;
                }
                if (super.isSetMoney) {
                    f3.c = billinfo.getShopRemark();
                    f3.d = billinfo;
                    f2 = f3;
                } else {
                    f2 = null;
                }
            }
            return f2;
        } else if (i == 3) {
            final f f4 = new f();
            final BillInfo a4 = this.createBillInfo();
            String s9;
            String replace4;
            for (i = 0; i < list.size(); ++i) {
                s9 = list.get(i);
                if (("已收款".equals(s9) || "你已收款".equals(s9)) && i < list.size() - 2) {
                    replace4 = list.get(i + 1).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                    if (this.isMoney(replace4)) {
                        this.setMoney(a4, replace4);
                    }
                } else if ("收款时间".equals(s9) && i < list.size() - 1) {
                    a4.setTimeStamp(DateUtils.dateToStamp(list.get(i + 1), "yyyy年MM月dd日 HH:mm:ss"));
                }
            }
            f f5;
            if (super.isSetMoney) {
                a4.setShopRemark("微信收款");
                f4.c = "微信收款";
                f4.a = "微信钱包";
                a4.setType(BillInfo.TYPE_INCOME);
                f4.d = a4;
                f5 = f4;
            } else {
                f5 = null;
            }
            return f5;
        } else if (i == 2) {
            final f f6 = new f();
            final BillInfo a5 = this.createBillInfo();
            String s11;
            String replace5;
            StringBuilder a6;
            for (i = 0; i < list.size(); ++i) {
                s11 = list.get(i);
                replace5 = replace(s11);
                if (s11.startsWith("¥") && this.isMoney(replace5)) {
                    this.setMoney(a5, replace5);
                    if (i > 0) {
                        String shopName = list.get(i - 1);
                        if (shopName.endsWith("收款") && shopName.startsWith("待")) {
                            a5.setShopAccount(shopName.substring(1, shopName.length() - 2));
                        }
                    }

                } else if ("转账时间".equals(s11) && i < list.size() - 1) {
                    a5.setTimeStamp(DateUtils.dateToStamp(list.get(i + 1), "yyyy年MM月dd日 HH:mm:ss"));
                } else if ("转账说明".equals(s11) && i < list.size() - 1) {
                    a6 = new StringBuilder("转账-");
                    a6.append(list.get(i + 1));
                    a5.setShopRemark(a6.toString());
                }
            }
            f f7;
            if (super.isSetMoney) {
                if (a5.getShopRemark().equals("")) {
                    a5.setShopRemark("微信转账");
                }
                if (a5.getAccountName().equals("")) {
                    a5.setAccountName("微信");
                }
                f6.c = a5.getShopRemark();
                f6.d = a5;
                f7 = f6;
            } else {
                f7 = null;
            }
            return f7;
        }
        /*final f f8 = new f();
        final BillInfo a7 = this.createBillInfo();
        String remark3;
        boolean b2;
        String s12;
        String[] split;
        String s13;
        String o;
        String s14;
        int index;
        String b3;
        String[] split2;
        String s15;
        String replace6;
        String s16;
        String s17;
        Date time;
        StringBuilder sb;
        String string;
        String replace7;
        int n3;
        String s18;
        String s19;
        String replace8;
        int index2;
        String b4;
        String[] split3;
        final Exception ex2;
        for (i = 0; i < list.size(); ++i, list2 = list) {
            remark3 = list2.get(i);
            b2 = (i < list.size() - 1);
            Label_2156:
            {
                Label_1850:
                {

                    {
                        if (remark3.contains(s3) && (remark3.contains("支出") || remark3.contains("收入") || remark3.contains("转入") || remark3.contains("转出") || remark3.contains("还款") || remark3.contains("零钱充值") || remark3.contains("零钱提现") || "提现金额".equals(remark3))) {
                            s12 = list2.get(i);
                            split = list2.get(i).split(s3);
                            s13 = split[split.length - 1];
                            o = replace(s13);

                            {
                                if (o != null && this.isMoney(o)) {
                                    this.setMoney(a7, o);
                                    a7.setShopRemark(split[0]);
                                    if (!s12.contains("转入") && !s12.contains("转出") && !s12.contains("还款") && !s12.contains("零钱充值") && !s12.contains("零钱提现") && !"提现金额".equals(s12)) {
                                        if (s13.contains("收入")) {
                                            a7.setType(BillInfo.TYPE_INCOME);
                                        }
                                    } else {
                                        a7.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                        if ("提现金额".equals(s12)) {
                                            f8.a = "微信钱包";
                                            f8.f = true;
                                            a7.setShopRemark("微信零钱提现");
                                        }
                                        s14 = split[0];
                                        index = s14.indexOf("-");
                                        if (index != -1) {
                                            try {
                                                if (s14.startsWith("转入")) {
                                                    f8.b = s14.substring(2, index);
                                                    f8.a = s14.substring(index + 3);
                                                } else {
                                                    if (s14.contains("转出")) {
                                                        f8.a = s14.substring(0, s14.indexOf("转出"));
                                                        b3 = s14.substring(index + 2);
                                                    } else if (s14.contains("还款")) {
                                                        split2 = s14.split("还款");
                                                        if (split2.length < 2) {
                                                            break Label_1850;
                                                        }
                                                        b3 = split2[1].substring(1);
                                                    } else {
                                                        if (s14.contains("零钱充值")) {
                                                            f8.b = "微信钱包";
                                                            break Label_1850;
                                                        }
                                                        if (s14.contains("零钱提现")) {
                                                            f8.a = "微信钱包";
                                                        }
                                                        break Label_1850;
                                                    }
                                                    f8.b = b3;
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                            break Label_1850;
                        }
                        if (super.isSetMoney || (!remark3.contains("转入") && !remark3.contains("转出") && !remark3.contains("还款") && !remark3.contains("零钱充值") && !remark3.contains("零钱提现") && !"提现金额".equals(remark3))) {
                            s15 = s5;
                            Label_2333:
                            {
                                if (!super.isSetMoney && (remark3.startsWith("-") || remark3.startsWith("+"))) {
                                    replace6 = remark3.replace("+", s15).replace("-", s15);
                                    if (!this.isMoney(replace6)) {
                                        break Label_1850;
                                    }
                                    this.setMoney(a7, replace6);
                                    if (i > 0) {
                                        a7.remark = list.get(i - 1);
                                    }
                                    s16 = s5;
                                    if (!remark3.contains("+")) {
                                        break Label_2156;
                                    }
                                } else {
                                    if (a7.time == null && remark3.length() == 4 && remark3.endsWith("时间") && b2) {
                                        s17 = list.get(i + 1);
                                        if (s17.contains("年")) {
                                            time = e.t(s17, s8);
                                        } else {
                                            time = e.u(s17);
                                        }
                                        a7.time = time;
                                        break Label_2333;
                                    }
                                    if (f8.a != null || (!"支付方式".equals(remark3) && !"收款帐号".equals(remark3) && !"退款方式".equals(remark3)) || !b2) {
                                        if (("商品".equals(remark3) || s10.equals(remark3) || "付款备注".equals(remark3)) && b2) {
                                            if (a7.remark != null) {
                                                sb = new StringBuilder();
                                                sb.append(list.get(i + 1));
                                                sb.append("-");
                                                sb.append(a7.remark);
                                                string = sb.toString();
                                            } else {
                                                string = list.get(i + 1);
                                            }
                                            a7.remark = string;
                                        } else if (("提现银行".equals(remark3) || "到账银行卡".equals(remark3)) && b2) {
                                            f8.b = list.get(i + 1);
                                            if (f8.a == null && this.c) {
                                                f8.a = a3;
                                                s5 = s15;
                                                continue;
                                            }
                                        } else {
                                            s5 = s15;
                                            if (!"服务费".equals(remark3)) {
                                                continue;
                                            }
                                            s5 = s15;
                                            if (!b2) {
                                                continue;
                                            }
                                            replace7 = list.get(i + 1).replace(s6, s15);
                                            s5 = s15;
                                            if (this.isMoney(replace7)) {
                                                f8.e = e3.b.H(replace7);
                                                s5 = s15;
                                            }
                                            continue;
                                        }
                                        s5 = s15;
                                        continue;
                                    }
                                    f8.a = list.get(i + 1);
                                    if (!remark3.contains("收")) {
                                        break Label_2333;
                                    }
                                }
                                a7.detailType = 1;
                            }
                            continue;
                        }
                        n3 = i + 1;
                        if (n3 >= list.size()) {
                            break Label_1850;
                        }
                        s18 = list.get(n3);
                        s19 = s5;
                        replace8 = s18.replace(s4, s19).replace(s6, s19);
                        if (!this.isMoney(replace8)) {
                            break Label_1850;
                        }
                        this.setMoney(a7, replace8);
                        a7.remark = remark3;
                        a7.detailType = 2;
                        if ("提现金额".equals(remark3)) {
                            f8.a = "微信钱包";
                            f8.f = true;
                        } else {
                            index2 = remark3.indexOf("-");
                            if (index2 == -1) {
                                break Label_1850;
                            }
                            Label_2033:
                            {
                                try {
                                    if (remark3.startsWith("转入")) {
                                        f8.b = remark3.substring(2, index2);
                                        f8.a = remark3.substring(index2 + 3);
                                        break Label_1850;
                                    }
                                } catch (Exception ex2) {
                                    break Label_2033;
                                }
                                if (remark3.contains("转出")) {
                                    f8.a = remark3.substring(0, remark3.indexOf("转出"));
                                    b4 = remark3.substring(index2 + 2);
                                } else if (remark3.contains("还款")) {
                                    split3 = remark3.split("还款");
                                    if (split3.length < 2) {
                                        break Label_1850;
                                    }
                                    b4 = split3[1].substring(1);
                                } else {
                                    if (remark3.contains("零钱充值")) {
                                        f8.b = "微信钱包";
                                        break Label_1850;
                                    }
                                    if (remark3.contains("零钱提现")) {
                                        f8.a = "微信钱包";
                                    }
                                    break Label_1850;
                                }
                                f8.b = b4;
                                break Label_1850;
                            }
                            ex2.printStackTrace();
                            break Label_1850;
                        }
                    }
                    a7.remark = "微信零钱提现";
                }
                s16 = s5;
            }
            s5 = s16;
        }
        f f9;
        if (super.isSetMoney) {
            if (className.equals(f8.a)) {
                f8.a = "微信钱包";
            }
            if (className.equals(f8.b)) {
                f8.b = "微信钱包";
            }
            if (a7.detailType == 1 && f8.a == null) {
                f8.a = "微信钱包";
            }
            f8.c = a7.remark;
            f8.d = a7;
            f9 = f8;
        } else {
            f9 = null;
        }
        return f9;*/
        return null;
    }
}

