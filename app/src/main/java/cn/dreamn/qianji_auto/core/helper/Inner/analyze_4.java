//
// Decompiled by FernFlower - 1459ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class analyze_4 extends baseAnalyze {
    // $FF: synthetic field
    public final int b;

    public analyze_4(int var1) {
        this.b = var1;
    }

    public BillInfo h(List var1, int var2) {
        boolean var4;
        StringBuilder var6;
        BillInfo var17;
        String var20;
        switch (this.b) {
            case 0:
                if (var1.size() != 0) {
                    BillInfo billInfo;
                    String var9;
                    if (var2 == 1) {
                        var6 = new StringBuilder();
                        var6.append("[auto] JdDetailParser parsePay ");
                        var6.append(var1.toString());
                        Log.d(var6.toString());
                        billInfo = new BillInfo();

                        for (var2 = 0; var2 < var1.size(); ++var2) {
                            var20 = (String) var1.get(var2);
                            var4 = var2 < var1.size() - 1;

                            if ("支付成功".equals(var20) && var4) {
                                label242:
                                {
                                    var20 = (String) var1.get(var2 + 1);
                                    if (var20 != null) {
                                        label240:
                                        {
                                            Exception var10000;
                                            label390:
                                            {
                                                int var5;
                                                boolean var10001;
                                                char[] var25;
                                                try {
                                                    if (var20.length() == 0) {
                                                        break label240;
                                                    }

                                                    var9 = var20.replace(",", "");
                                                    var6 = new StringBuilder();
                                                    var25 = var9.toCharArray();
                                                    var5 = var25.length;
                                                } catch (Exception var16) {
                                                    var10000 = var16;
                                                    var10001 = false;
                                                    break label390;
                                                }

                                                for (int var18 = 0; var18 < var5; ++var18) {
                                                    char var3 = var25[var18];
                                                    if (var3 < '0' || var3 > '9') {
                                                        if (var3 != '.') {
                                                            try {
                                                                if (var6.length() != 0) {
                                                                    break;
                                                                }
                                                                continue;
                                                            } catch (Exception var14) {
                                                                var10000 = var14;
                                                                var10001 = false;
                                                                break label390;
                                                            }
                                                        }

                                                        try {
                                                            if (var6.length() == 0) {
                                                                continue;
                                                            }
                                                        } catch (Exception var15) {
                                                            var10000 = var15;
                                                            var10001 = false;
                                                            break label390;
                                                        }

                                                        try {
                                                            if (var6.indexOf(".") != -1) {
                                                                break;
                                                            }
                                                        } catch (Exception var13) {
                                                            var10000 = var13;
                                                            var10001 = false;
                                                            break label390;
                                                        }
                                                    }

                                                    try {
                                                        var6.append(var3);
                                                    } catch (Exception var12) {
                                                        var10000 = var12;
                                                        var10001 = false;
                                                        break label390;
                                                    }
                                                }

                                                try {
                                                    var20 = var6.toString();
                                                    break label242;
                                                } catch (Exception var11) {
                                                    var10000 = var11;
                                                    var10001 = false;
                                                }
                                            }

                                            Exception var21 = var10000;
                                            var21.printStackTrace();
                                        }
                                    }

                                    var20 = null;
                                }

                                if (var20 != null && isMoney(var20)) {
                                    setMoney(billInfo, var20);
                                }
                            } else if (var20.contains("白条支付")) {
                                billInfo.setAccountName("京东白条");
                            } else if (var20.contains("充值面值")) {
                                billInfo.setShopRemark(var20);
                            }
                        }

                        if (super.a) {
                            if (billInfo.getShopRemark() == null) {
                                billInfo.setShopRemark("京东订单");
                            }
                            var17 = billInfo;
                            return var17;
                        }
                    } else {
                        var6 = new StringBuilder();
                        var6.append("[auto] JdDetailParser parse type ");
                        var6.append(var2);
                        var6.append(" ");
                        var6.append(var1.toString());
                        Log.d(var6.toString());
                        billInfo = new BillInfo();


                        for (var2 = 0; var2 < var1.size(); ++var2) {
                            var20 = (String) var1.get(var2);
                            var4 = var2 < var1.size() - 1;

                            boolean var19;
                            var19 = var2 > 0;

                            if (("交易成功".equals(var20) || "退款成功".equals(var20) || "转出到账".equals(var20)) && var19) {
                                String var10 = (String) var1.get(var2 - 1);
                                var9 = var10.replace("+", "").replace("-", "");
                                if (!isMoney(var9)) {
                                    continue;
                                }

                                setMoney(billInfo, var9);
                                if (var2 >= 2) {
                                    billInfo.setShopRemark(((String) var1.get(var2 - 2)).replace("订单详情", ""));
                                }

                                if (!var10.contains("+")) {
                                    continue;
                                }

                                billInfo.setType(BillInfo.TYPE_INCOME);
                                StringBuilder var26 = new StringBuilder();
                                var26.append(var20);
                                var26.append("-");
                                var26.append(billInfo.getShopRemark());
                                var20 = var26.toString();
                            } else {
                                label413:
                                {
                                    if ("创建时间：".equals(var20) && var4) {
                                        billInfo.setTimeStamp(Tool.dateToStamp((String) var1.get(var2 + 1), "yyyy-MM=dd HH:mm:ss"));
                                        continue;
                                    }

                                    if (("支付方式：".equals(var20) || "退款至：".equals(var20)) && var4) {
                                        billInfo.setAccountName(((String) var1.get(var2 + 1)).replace("微信-", ""));
                                        continue;
                                    }

                                    if (var20.endsWith("说明：") && var4) {
                                        var20 = (String) var1.get(var2 + 1);
                                        billInfo.setShopRemark(var20);
                                        if ("京东钱包余额充值".equals(var20)) {
                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                            billInfo.setAccountName2("京东钱包");
                                            continue;
                                        }

                                        if ("京东钱包余额提现".equals(billInfo.getShopRemark())) {
                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                            billInfo.setAccountName("京东钱包");
                                            continue;
                                        }

                                        if (!"京东小金库-转入".equals(billInfo.getShopRemark())) {
                                            continue;
                                        }

                                        billInfo.setType(BillInfo.TYPE_INCOME);
                                        var20 = "京东小金库";
                                    } else {
                                        if ("还款详情：".equals(var20)) {
                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                            if (!TextUtils.isEmpty(billInfo.getShopRemark()) && billInfo.getShopRemark().contains("白条")) {
                                                billInfo.setAccountName2("京东白条");
                                            }
                                            continue;
                                        }

                                        if ("商品：".equals(var20) && var4) {
                                            if (billInfo.getShopRemark() == null) {
                                                var20 = (String) var1.get(var2 + 1);
                                            } else {
                                                var6 = new StringBuilder();
                                                var6.append(billInfo.getShopRemark());
                                                var6.append("-");
                                                var6.append((String) var1.get(var2 + 1));
                                                var20 = var6.toString();
                                            }
                                            break label413;
                                        }

                                        if (!"提现至：".equals(var20) && !"转出至：".equals(var20) || !var4) {
                                            continue;
                                        }

                                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                        var20 = (String) var1.get(var2 + 1);
                                    }

                                    billInfo.setAccountName2(var20);
                                    continue;
                                }
                            }

                            billInfo.setShopRemark(var20);
                        }

                        if (super.a) {
                            var17 = billInfo;
                            return var17;
                        }
                    }
                }

                var17 = null;
                return var17;
            default:
                if (var1.size() != 0) {
                    var6 = new StringBuilder();
                    var6.append("[auto] PddDetailParser parse type ");
                    var6.append(var2);
                    var6.append(" ");
                    var6.append(var1.toString());
                    Log.d(var6.toString());
                    BillInfo billInfo = new BillInfo();
                    for (var2 = 0; var2 < var1.size(); ++var2) {
                        var20 = (String) var1.get(var2);
                        var4 = var2 < var1.size() - 1;

                        label376:
                        {
                            label375:
                            {
                                StringBuilder var23;
                                label374:
                                {
                                    label373:
                                    {
                                        label412:
                                        {
                                            String var22;
                                            if (var2 == 4) {
                                                var22 = var20.replace("+", "").replace("-", "");
                                                if (isMoney(var22)) {
                                                    setMoney(billInfo, var22);
                                                    if (var20.contains("+")) {
                                                        billInfo.setType(BillInfo.TYPE_INCOME);
                                                        billInfo.setAccountName("多多钱包");
                                                    }

                                                    var20 = (String) var1.get(3);
                                                    billInfo.setShopRemark(var20);
                                                    if (!"余额提现".equals(var20)) {
                                                        if (!"余额充值".equals(billInfo.getShopRemark())) {
                                                            continue;
                                                        }
                                                        break label373;
                                                    }
                                                    break label412;
                                                }
                                            }

                                            if (("商品详情".equals(var20) || "关联商品".equals(var20)) && var4) {
                                                if (billInfo.getShopRemark() == null) {
                                                    var20 = (String) var1.get(var2 + 1);
                                                    break label375;
                                                }

                                                var23 = new StringBuilder();
                                                var23.append(billInfo.getShopRemark());
                                                var23.append("-");
                                                var20 = (String) var1.get(var2 + 1);
                                                break label374;
                                            }

                                            if (("支付时间".equals(var20) || "发起时间".equals(var20) || "充值时间".equals(var20)) && var4) {
                                                billInfo.setTimeStamp(Tool.dateToStamp((String) var1.get(var2 + 1), "yyyy-MM-dd HH:mm:ss"));
                                                continue;
                                            }

                                            if ("支付方式".equals(var20) && var4) {
                                                var22 = (String) var1.get(var2 + 1);
                                                var20 = var22;
                                                if (var22.contains(":")) {
                                                    var20 = var22.split(":")[0];
                                                }
                                                break label376;
                                            }

                                            if ("提现至".equals(var20) && var4) {
                                                billInfo.setAccountName2((String) var1.get(var2 + 1));
                                                continue;
                                            }

                                            if ("充值金额".equals(var20) && var4) {
                                                var20 = ((String) var1.get(var2 + 1)).replace("¥", "");
                                                if (!isMoney(var20)) {
                                                    continue;
                                                }

                                                setMoney(billInfo, var20);
                                                break label373;
                                            }

                                            if (!"提现金额".equals(var20) || !var4) {
                                                if ("充值方式".equals(var20) && var4) {
                                                    var20 = (String) var1.get(var2 + 1);
                                                    break label376;
                                                }

                                                if (!"退款至".equals(var20) || !var4) {
                                                    continue;
                                                }

                                                billInfo.setAccountName((String) var1.get(var2 + 1));
                                                if (TextUtils.isEmpty(billInfo.getShopRemark())) {
                                                    var20 = "退款";
                                                    break label375;
                                                }

                                                var23 = new StringBuilder();
                                                var23.append("退款-");
                                                var20 = billInfo.getShopRemark();
                                                break label374;
                                            }

                                            var20 = ((String) var1.get(var2 + 1)).replace("¥", "");
                                            if (!isMoney(var20)) {
                                                continue;
                                            }

                                            setMoney(billInfo, var20);
                                        }

                                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                        billInfo.setAccountName("多多钱包");
                                        var20 = "拼多多余额提现";
                                        break label375;
                                    }

                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    billInfo.setAccountName2("多多钱包");
                                    var20 = "拼多多余额充值";
                                    break label375;
                                }

                                var23.append(var20);
                                var20 = var23.toString();
                            }

                            billInfo.setShopRemark(var20);
                            continue;
                        }

                        billInfo.setAccountName(var20);
                    }

                    if (super.a) {
                        if ("多多钱包余额".equals(billInfo.getShopAccount())) {
                            billInfo.setShopAccount("多多钱包");
                        }
                        var17 = billInfo;
                        return var17;
                    }
                }

                return null;
        }
    }
}

