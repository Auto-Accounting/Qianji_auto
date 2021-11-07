//
// Decompiled by Procyon - 2100ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;


import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class g extends c {
    public boolean isAlipay;
    public boolean c;

    public f l(final List<String> list, int i) {
        if (list.size() == 0) {
            return null;
        }
        Log.i("[auto] RedDetailParser parse type " + i + " " + list.toString());
        final f f = new f();
        final BillInfo a2 = this.createBillInfo();
        i = 0;
        while (i < list.size()) {
            final String s = list.get(i);
            Log.i("s:" + s);
            if (s.endsWith("的红包")) {
                a2.setShopAccount(s.substring(0, s.length() - 3));
                if (i + 1 < list.size()) {
                    a2.setShopRemark(list.get(i + 1));
                }

                if (i + 2 < list.size()) {
                    String ss = list.get(i + 2);
                    String m = replace(ss);
                    if (this.isMoney(m)) {
                        setMoney(a2, m);
                        a2.setType(BillInfo.TYPE_INCOME);
                    } else if (ss.startsWith("已领取0") && ss.contains("个，共")) {
                        m = ss.substring(ss.lastIndexOf("/") + 1, ss.length() - 1);
                        if (this.isMoney(m)) {
                            setMoney(a2, m);
                        }
                    }

                }
            } else if (!s.contains("红包金额") && !s.contains("元")) {
                StringBuilder sb = new StringBuilder();
                String s2;
                if (s.contains("个红包共")) {
                    final int index = s.indexOf("元");
                    s2 = "微信红包";
                    if (index > 4) {
                        final String replace = s.substring(s.indexOf("个红包共") + 4, index).replace(",", "");
                        if (this.isMoney(replace)) {
                            this.setMoney(a2, replace);
                            if (i >= 1) {
                                --i;
                                Log.i("当前数据：" + list.get(i));
                                if (!list.get(i).endsWith("的红包") && !list.get(i).startsWith("恭喜发财")) {
                                    s2 = "微信红包-";
                                }
                            }

                            sb.append(s2);
                            sb.append(list.get(i));
                            a2.setShopRemark(sb.toString());
                            break;
                        }
                    }

                } else {
                    s2 = "支付宝红包";
                    if (s.contains("人已领取")) {
                        final int index2 = s.indexOf("元");
                        if (index2 > 4) {
                            final String replace2 = s.substring(s.indexOf("人已领取") + 6, index2).replace(",", "");
                            if (this.isMoney(replace2)) {
                                this.setMoney(a2, replace2);
                                if (i >= 1) {
                                    --i;
                                    Log.i("当前数据：" + list.get(i));
                                    if (!list.get(i).startsWith("恭喜发财")) {
                                        sb = new StringBuilder();
                                        s2 = "支付宝红包-";
                                    }

                                }
                                sb.append(s2);
                                sb.append(list.get(i));
                                a2.setShopRemark(sb.toString());
                                break;

                            }

                        }

                    }

                }
            } else {
                final int index3 = s.indexOf("元");
                Log.i("ss:" + index3);
                if (index3 > 4) {

                    final String replace3 = s.substring(0, index3).replace("红包金额", "").replace(",", "");

                    if (this.isMoney(replace3)) {
                        this.setMoney(a2, replace3);
                        if (isAlipay && a2.getShopRemark().equals("")) {
                            a2.setShopRemark("支付宝红包");
                        } else if (a2.getShopRemark().equals("")) {
                            a2.setShopRemark("微信红包");
                        }
                        break;
                    }
                }
            }

            ++i;
        }


        if (super.isSetMoney) {
            f.c = a2.getShopRemark();
            f.d = a2;
            return f;
        }
        return null;
    }

    public f m(final List<String> list) {
        if (list.size() == 0) {
            return null;
        }
     /*   final StringBuilder a = b.c.a("[auto]RedDetailParser parseIncome:");
        a.append(list.toString());
        k.a("CXINCX-Log", a.toString());
        final f f = new f();
        final DetailEntity a2 = this.createBillInfo();
        final boolean b = this.b;
        final int n = 0;
        int i = 0;
        Label_0450: {
            String a3 = null;
            Label_0435: {
                if (b) {
                    while (i < list.size()) {
                        if ("元".equals(list.get(i)) && i > 2) {
                            final int n2 = i - 1;
                            if (this.isMoney(list.get(n2).replace(",", ""))) {
                                this.setMoney(a2, (String)list.get(n2));
                                a3 = b.b.a(new StringBuilder(), (String)list.get(i - 3), "的红包");
                                break Label_0435;
                            }
                        }
                        ++i;
                    }
                    break Label_0450;
                }
                int j = n;
                if (f.c.B((List)list, "个红包", false)) {
                    j = n;
                    if (f.c.B((List)list, "被抢光", false)) {
                        j = n;
                        if (!f.c.B((List)list, "已存入零钱", false)) {
                            k.d("[auto]群红包没有抢到");
                            return null;
                        }
                    }
                }
                while (j < list.size()) {
                    final String replace = list.get(j).replace("元", "").replace(",", "");
                    Label_0444: {
                        if (this.isMoney(replace)) {
                            this.setMoney(a2, replace);
                            int n4 = 0;
                            Label_0424: {
                                if (j < list.size() - 1 && (list.get(j).contains("元") || list.get(j + 1).contains("元"))) {
                                    if (j > 1) {
                                        final int n3 = j - 2;
                                        if (list.get(n3).contains("的红包")) {
                                            n4 = n3;
                                            break Label_0424;
                                        }
                                    }
                                    if (j <= 0) {
                                        break Label_0444;
                                    }
                                    final int n5 = j - 1;
                                    if (!list.get(n5).contains("的红包")) {
                                        break Label_0444;
                                    }
                                    n4 = n5;
                                }
                                else {
                                    if (!this.c || j < 2) {
                                        break Label_0444;
                                    }
                                    final int n6 = j - 2;
                                    if (!list.get(n6).contains("的红包")) {
                                        break Label_0444;
                                    }
                                    n4 = n6;
                                }
                            }
                            a3 = list.get(n4);
                            break Label_0435;
                        }
                    }
                    ++j;
                }
                break Label_0450;
            }
            a2.remark = a3;
        }
        if (super.isSetMoney) {
            String a4;
            if (!this.b && !this.c) {
                a4 = "微信钱包";
            }
            else {
                a4 = "支付宝";
            }
            f.a = a4;
            a2.detailType = 1;
            f.c = a2.remark;
            f.d = a2;
            return f;
        }*/
        return null;
    }
}

