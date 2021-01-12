package cn.dreamn.qianji_auto.core.helper;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class AnalyzeBill {


    public static long f12954a;

    public static BillInfo a(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setAccountName("微信");
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            String replace = str.replace("￥", "").replace("¥", "").replace(",", "");
            Logs.d(replace);
            if (replace!=null) {
                billInfo.setMoney(replace);
            } else if (str.equals("收款方") && i < list.size() - 1) {
                billInfo.setRemark(list.get(i + 1));
                billInfo.setShopAccount(billInfo.getRemark());
            } else if (str.equals("支付成功") && i < list.size() - 2) {
                int i2 = i + 1;
                if (!list.get(i2).contains("¥")) {
                    billInfo.setRemark(list.get(i2));
                    billInfo.setShopAccount(billInfo.getRemark());
                }
            }
        }
        if (TextUtils.isEmpty(billInfo.getRemark()) || TextUtils.isEmpty(billInfo.getMoney())) {
            return null;
        }
        return billInfo;
    }

/*    public static BillInfo b(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setAccountName("微信");
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.contains(x.B) && v.a(str)) {
                billInfo.setMoney(Math.abs(Double.parseDouble(str)) + "");
                if (i > 0) {
                    billInfo.setRemark(list.get(i - 1));
                    billInfo.setShopAccount(billInfo.getRemark());
                }
            } else if ((str.equals("支付时间") || str.equals("转账时间")) && i < list.size() - 1) {
                billInfo.setTime(bj.a(list.get(i + 1), h.g));
            } else if (str.equals("支付方式") && i < list.size() - 1) {
                billInfo.setAsset(list.get(i + 1));
            }
        }
        if (TextUtils.isEmpty(billInfo.getRemark()) || TextUtils.isEmpty(billInfo.getNumber())) {
            return null;
        }
        return billInfo;
    }

    public static BillInfo c(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setOrigin("微信");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("已收款") && i < list.size() - 2) {
                String replace = list.get(i + 1).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                if (v.a(replace)) {
                    billInfo.setNumber(replace);
                }
            }
        }
        if (TextUtils.isEmpty(billInfo.getNumber())) {
            return null;
        }
        billInfo.setRemark("微信收款");
        billInfo.setShopName(billInfo.getRemark());
        return billInfo;
    }

    public static BillInfo d(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setOrigin("支付宝");
        boolean z = false;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.equals("转账备注")) {
                z = true;
            }
            if (str.equals("收款方") && i < list.size() - 1) {
                billInfo.setRemark(list.get(i + 1));
                billInfo.setShopName(billInfo.getRemark());
            } else if (str.equals("支付成功") && i < list.size() - 2) {
                String replace = list.get(i + 1).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                if (!v.a(replace)) {
                    replace = list.get(i + 2).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                }
                if (v.a(replace)) {
                    billInfo.setNumber(replace);
                }
            } else if (TextUtils.isEmpty(billInfo.getNumber()) && (str.contains("￥") || str.contains("¥") || str.contains(x.B) || str.contains("+"))) {
                String replace2 = str.replace("￥", "").replace("¥", "").replace(",", "").replace("+", "");
                if (!v.a(replace2) && i < list.size() - 1) {
                    replace2 = list.get(i + 1).replace("￥", "").replace("¥", "").replace(",", "").replace("+", "");
                }
                if (v.a(replace2)) {
                    billInfo.setNumber(Math.abs(Double.parseDouble(replace2)) + "");
                    if (str.contains("+")) {
                        billInfo.setRemark("支付宝收款");
                        billInfo.setShopName(billInfo.getRemark());
                    }
                }
                if (TextUtils.isEmpty(billInfo.getRemark()) && i > 0) {
                    billInfo.setRemark(list.get(i - 1));
                    billInfo.setShopName(billInfo.getRemark());
                }
            } else if ((str.equals("付款方式") || str.equals("付款信息")) && i < list.size() - 1) {
                billInfo.setAsset(list.get(i + 1));
            } else if (str.equals("创建时间") && i < list.size() - 1) {
                billInfo.setTime(bj.a(list.get(i + 1), h.e));
            } else if (str.equals("商品说明") && i < list.size() - 1) {
                billInfo.setRemark(list.get(i + 1));
            }
        }
        if (!TextUtils.isEmpty(billInfo.getNumber()) && !TextUtils.isEmpty(billInfo.getAsset()) && TextUtils.isEmpty(billInfo.getRemark())) {
            billInfo.setRemark("支付宝付款");
            billInfo.setShopName(billInfo.getRemark());
        }
        if (TextUtils.isEmpty(billInfo.getRemark()) || TextUtils.isEmpty(billInfo.getNumber())) {
            return null;
        }
        if (z && !billInfo.getRemark().equals("支付宝收款")) {
            billInfo.setRemark("转账给" + billInfo.getRemark());
            billInfo.setShopName(billInfo.getRemark());
        }
        return billInfo;
    }

    public static BillInfo e(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setOrigin("支付宝");
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.equals("收款方") && i < list.size() - 1) {
                billInfo.setRemark("转账给" + list.get(i + 1));
                billInfo.setShopName(billInfo.getRemark());
            } else if (str.equals("转账成功") && i < list.size() - 2) {
                String replace = list.get(i + 1).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                if (!v.a(replace)) {
                    replace = list.get(i + 2).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                }
                if (v.a(replace)) {
                    billInfo.setNumber(replace);
                }
            } else if (str.equals("付款方式")) {
                billInfo.setAsset(list.get(i + 1));
            }
        }
        if (!TextUtils.isEmpty(billInfo.getRemark()) && !TextUtils.isEmpty(billInfo.getAsset()) && TextUtils.isEmpty(billInfo.getRemark())) {
            billInfo.setRemark("支付宝转账");
            billInfo.setShopName(billInfo.getRemark());
        }
        if (TextUtils.isEmpty(billInfo.getRemark()) || TextUtils.isEmpty(billInfo.getNumber())) {
            return null;
        }
        return billInfo;
    }

    public static BillInfo a(List<String> list, boolean z) {
        int indexOf;
        BillInfo billInfo = new BillInfo();
        if (z) {
            billInfo.setOrigin("支付宝");
        } else {
            billInfo.setOrigin("微信");
        }
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.contains("红包金额")) {
                int indexOf2 = str.indexOf("元");
                if (indexOf2 > 4) {
                    String replace = str.substring(0, indexOf2).replace("红包金额", "").replace(",", "");
                    if (v.a(replace)) {
                        billInfo.setNumber(replace);
                        if (z) {
                            billInfo.setRemark("发送支付宝红包");
                        } else {
                            billInfo.setRemark("发送微信红包");
                        }
                        billInfo.setShopName(billInfo.getRemark());
                        return billInfo;
                    }
                } else {
                    continue;
                }
            } else if (str.contains("个红包共")) {
                int indexOf3 = str.indexOf("元");
                if (indexOf3 > 4) {
                    String replace2 = str.substring(str.indexOf("个红包共") + 4, indexOf3).replace(",", "");
                    if (v.a(replace2)) {
                        billInfo.setNumber(replace2);
                        billInfo.setRemark("发送微信红包");
                        billInfo.setShopName(billInfo.getRemark());
                        return billInfo;
                    }
                } else {
                    continue;
                }
            } else if (str.contains("人已领取") && (indexOf = str.indexOf("元")) > 4) {
                String replace3 = str.substring(str.indexOf("人已领取") + 6, indexOf).replace(",", "");
                if (v.a(replace3)) {
                    billInfo.setNumber(replace3);
                    billInfo.setRemark("发送支付宝红包");
                    billInfo.setShopName(billInfo.getRemark());
                    return billInfo;
                }
            }
        }
        return null;
    }

    public static BillInfo f(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setOrigin("微信");
        for (int i = 0; i < list.size(); i++) {
            String replace = list.get(i).replace("元", "").replace(",", "");
            if (v.a(replace)) {
                billInfo.setNumber(replace);
                if (i < list.size() - 1 && (list.get(i).contains("元") || list.get(i + 1).contains("元"))) {
                    if (i > 1) {
                        int i2 = i - 2;
                        if (list.get(i2).contains("的红包")) {
                            billInfo.setRemark(list.get(i2));
                            billInfo.setShopName(billInfo.getRemark());
                            return billInfo;
                        }
                    }
                    if (i > 0) {
                        int i3 = i - 1;
                        if (list.get(i3).contains("的红包")) {
                            billInfo.setRemark(list.get(i3));
                            billInfo.setShopName(billInfo.getRemark());
                            return billInfo;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public static BillInfo g(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        BillInfo billInfo = new BillInfo();
        billInfo.setOrigin("支付宝");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("元") && i > 2) {
                int i2 = i - 1;
                if (v.a(list.get(i2).replace(",", ""))) {
                    billInfo.setNumber(list.get(i2));
                    billInfo.setRemark(list.get(i - 3) + "的红包");
                    billInfo.setShopName(billInfo.getRemark());
                    return billInfo;
                }
            }
        }
        return null;
    }

    public static void a(Context context, BillInfo billInfo) {
        if (System.currentTimeMillis() - f12954a > 1000) {
            f12954a = System.currentTimeMillis();
            b.a(MyApplication.a()).a(new FirstAddLayout(context, billInfo));
        }
    }*/
}
