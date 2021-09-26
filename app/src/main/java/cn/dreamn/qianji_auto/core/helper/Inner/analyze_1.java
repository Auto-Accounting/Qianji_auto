//
// Decompiled by FernFlower - 1898ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


public class analyze_1 extends baseAnalyze {
    // $FF: synthetic field
    public final int b;

    public analyze_1(int var1) {
        this.b = var1;
    }

    public BillInfo h(List<Object> nodeList, int var2) {
        int var3 = this.b;
        String var19 = "支付成功";
        String var26 = "";
        StringBuilder var7;
        String var8;
        String var11;
        BillInfo var28;
        boolean var29;
        String var35;
        switch (var3) {
            case 0:
                if (nodeList.size() != 0) {

                    var7 = new StringBuilder();
                    var7.append("[auto] AlipayDetailParser parse type ");
                    var7.append(var2);
                    var7.append(" ");
                    var7.append(nodeList.toString());
                    Log.i(var7.toString());

                    String var36 = "收款方";
                    String var18 = "余额";
                    String var38 = "付款方式";
                    String var17 = "账户余额";
                    String var21 = "元";
                    String var14 = ",";
                    String var12 = "-";
                    var35 = "+";
                    var8 = "交易成功";

                    StringBuilder var41;
                    if (var2 == 2) {
                        Log.i("[auto] AlipayDetailParser parseTransfer");
                        BillInfo billinfo = new BillInfo();
                        var2 = 0;

                        for (var35 = var36; var2 < nodeList.size(); ++var2) {
                            var8 = (String) nodeList.get(var2);
                            if (var35.equals(var8) && var2 < nodeList.size() - 1) {
                                var41 = new StringBuilder();
                                var41.append("转账给");
                                var41.append((String) nodeList.get(var2 + 1));
                                billinfo.setShopRemark(var41.toString());
                            } else if ("转账成功".equals(var8) && var2 < nodeList.size() - 2) {
                                var36 = ((String) nodeList.get(var2 + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                                var8 = var36;
                                if (!isMoney(var36)) {
                                    var8 = ((String) nodeList.get(var2 + 2)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                                }

                                if (isMoney(var8)) {
                                    setMoney(billinfo, var8);
                                }
                            } else if ("付款方式".equals(var8)) {
                                billinfo.setAccountName((String) nodeList.get(var2 + 1));
                            }
                        }

                        if (super.a) {
                            if (!TextUtils.isEmpty(billinfo.getAccountName()) && TextUtils.isEmpty(billinfo.getShopRemark())) {
                                billinfo.setShopRemark("支付宝转账");
                            }

                            if ("账户余额".equals(billinfo.getAccountName()) || "余额".equals(billinfo.getAccountName())) {
                                billinfo.setAccountName("支付宝");
                            }

                            return billinfo;
                        }
                    } else {
                        var36 = "收款方";
                        String var20 = "支付宝";
                        BillInfo billInfo = new BillInfo();
                        String var13 = "->";
                        int var33 = 0;

                        boolean var31;
                        for (var31 = false; var33 < nodeList.size(); var31 = var29) {
                            String var23 = (String) nodeList.get(var33);
                            boolean var5;
                            var5 = var33 < nodeList.size() - 1;

                            String var15;
                            String var24;
                            label570:
                            {
                                if ("转账备注".equals(var23)) {
                                    var29 = var31;
                                    if (var5) {
                                        var11 = (String) nodeList.get(var33 + 1);
                                        if (!"转账".equals(var11)) {
                                            String var43 = "转账-" +
                                                    var11;
                                            billInfo.setShopRemark(var43);
                                            var29 = var31;
                                        } else {
                                            var29 = true;
                                        }
                                    }

                                    var31 = var29;
                                } else {
                                    String var22 = var36;
                                    if (!var36.equals(var23) || !var5) {
                                        label556:
                                        {
                                            String var16;
                                            label584:
                                            {
                                                boolean var6 = var19.equals(var23);
                                                if ((var6 || "代付成功".equals(var23)) && var33 < nodeList.size() - 2) {
                                                    var36 = ((String) nodeList.get(var33 + 1)).replace("￥", "").replace("¥", "").replace(var21, "").replace(var14, "");
                                                    if (isMoney(var36)) {
                                                        setMoney(billInfo, var36);
                                                        if ("代付成功".equals(var23)) {
                                                            billInfo.setShopRemark("支付宝代付");
                                                        }
                                                    }

                                                    var11 = ((String) nodeList.get(var33 + 2)).replace("￥", "").replace("¥", "").replace(var21, "").replace(var14, "");
                                                    if (!isMoney(var36) && isMoney(var11)) {
                                                        setMoney(billInfo, var11);
                                                    } else if (!var38.equals(var11)) {
                                                        billInfo.setShopRemark(var11);
                                                    }

                                                    var11 = var35;
                                                    var35 = var38;
                                                } else {
                                                    label596:
                                                    {
                                                        label512:
                                                        {
                                                            byte var34;
                                                            label511:
                                                            {
                                                                label560:
                                                                {
                                                                    label509:
                                                                    {
                                                                        var6 = "有退款".equals(var23);
                                                                        var15 = var38;
                                                                        var36 = var8;
                                                                        if (!var6) {
                                                                            var36 = var8;
                                                                            if (!"自动扣款成功".equals(var23)) {
                                                                                var36 = var8;
                                                                                if (!"已全额退款".equals(var23)) {
                                                                                    var36 = var8;
                                                                                    if (!"退款成功".equals(var23)) {
                                                                                        if (!var8.equals(var23) && !"还款成功".equals(var23)) {
                                                                                            var11 = var8;
                                                                                            var36 = var8;
                                                                                            if (!"亲情卡付款成功".equals(var23)) {
                                                                                                var36 = var8;
                                                                                                if (!"等待对方发货".equals(var23)) {
                                                                                                    var36 = var8;
                                                                                                    if (!"等待确认收货".equals(var23)) {
                                                                                                        var8 = var8;
                                                                                                        if (!var23.contains("付款成功")) {
                                                                                                            break label509;
                                                                                                        }

                                                                                                        var36 = var11;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        } else {
                                                                                            var36 = var8;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                        var8 = var36;
                                                                        if (var33 > 0) {
                                                                            var3 = var33 - 1;
                                                                            var38 = ((String) nodeList.get(var3)).replace("￥", "").replace("¥", "").replace(var14, "");
                                                                            var38 = var38.replace(var35, "").replace(var21, "").replace(var12, "");
                                                                            if (isMoney(var38)) {
                                                                                setMoney(billInfo, var38);
                                                                                if ("还款成功".equals(var23) && var33 > 1) {
                                                                                    billInfo.setAccountName2((String) nodeList.get(var33 - 2));
                                                                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                                                    var8 = var36;
                                                                                    break label511;
                                                                                }
                                                                            }

                                                                            if (((String) nodeList.get(var3)).contains(var35)) {
                                                                                if (billInfo.getShopRemark() != null) {
                                                                                    billInfo.setShopRemark("支付宝收款");
                                                                                }
                                                                                billInfo.setType(BillInfo.TYPE_INCOME);
                                                                            } else {
                                                                                label569:
                                                                                {
                                                                                    if (var33 > 1) {
                                                                                        if (!TextUtils.isEmpty(billInfo.getShopRemark())) {
                                                                                            break label569;
                                                                                        }

                                                                                        var8 = (String) nodeList.get(var33 - 2);
                                                                                    } else {
                                                                                        var8 = "支付宝付款";
                                                                                    }

                                                                                    billInfo.setShopRemark(var8);
                                                                                }
                                                                            }

                                                                            if (!"退款成功".equals(var23)) {
                                                                                var38 = var15;
                                                                                break label512;
                                                                            }

                                                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                                                            var41 = new StringBuilder();
                                                                            var41.append(billInfo.getShopRemark());
                                                                            var41.append("-退款");
                                                                            var38 = var41.toString();
                                                                            var8 = var36;
                                                                            break label560;
                                                                        }
                                                                    }

                                                                    if (super.a || !var23.contains("￥") && !var23.contains("¥") && !var23.contains(var12) && !var23.contains(var35)) {
                                                                        var36 = var38;
                                                                        if ((var38.equals(var23) || "退款方式".equals(var23) || "付款信息".equals(var23)) && var5) {
                                                                            // var25.a = (String) nodeList.get(var33 + 1);
                                                                            billInfo.setAccountName((String) nodeList.get(var33 + 1));
                                                                        } else {
                                                                            if (!"创建时间".equals(var23) || !var5) {
                                                                                var6 = "商品说明".equals(var23);
                                                                                if (!var6 && !"红包说明".equals(var23) && !"红包来自".equals(var23)) {
                                                                                    break label584;
                                                                                }

                                                                                var11 = var35;
                                                                                if (!var5) {
                                                                                    break label584;
                                                                                }

                                                                                if ("商品说明".equals(var23)) {
                                                                                    var3 = var33 + 1;
                                                                                    if (((String) nodeList.get(var3)).contains("-转出到")) {
                                                                                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                                                        String[] var42 = ((String) nodeList.get(var3)).split("-转出到");
                                                                                        billInfo.setAccountName(var42[0]);
                                                                                        billInfo.setAccountName2(var42[1]);
                                                                                        var38 = "转出到";
                                                                                        break label556;
                                                                                    }
                                                                                }

                                                                                var35 = billInfo.getShopRemark();
                                                                                if (var35 != null && !"支付宝收款".equals(var35)) {
                                                                                    var7 = new StringBuilder();
                                                                                    var7.append((String) nodeList.get(var33 + 1));
                                                                                    var7.append(var12);
                                                                                    var7.append(billInfo.getShopRemark());
                                                                                    var35 = var7.toString();
                                                                                } else {
                                                                                    var35 = (String) nodeList.get(var33 + 1);
                                                                                }

                                                                                billInfo.setShopRemark(var35);
                                                                                var35 = var38;
                                                                                break label596;
                                                                            }

                                                                            billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(var33 + 1), "yyyy-MM-dd HH:mm:ss"));
                                                                        }

                                                                        var38 = var38;
                                                                        var36 = var8;
                                                                        break label512;
                                                                    }

                                                                    var38 = var23.replace("￥", "").replace("¥", "");
                                                                    var38 = var38.replace(var14, "").replace(var35, "");
                                                                    var16 = var23;
                                                                    var11 = var38;
                                                                    if (!isMoney(var38)) {
                                                                        var16 = var23;
                                                                        var11 = var38;
                                                                        if (var5) {
                                                                            var16 = (String) nodeList.get(var33 + 1);
                                                                            var11 = var16.replace("￥", "").replace("¥", "").replace(var14, "").replace(var35, "");
                                                                        }
                                                                    }

                                                                    var36 = var8;
                                                                    var38 = var15;
                                                                    if (!isMoney(var11)) {
                                                                        break label512;
                                                                    }

                                                                    setMoney(billInfo, var11);
                                                                    if (var16.contains(var35)) {
                                                                        billInfo.setShopRemark("支付宝收款");
                                                                        billInfo.setType(BillInfo.TYPE_INCOME);
                                                                        break label511;
                                                                    }

                                                                    var36 = var8;
                                                                    var38 = var15;
                                                                    if (!TextUtils.isEmpty(billInfo.getShopRemark())) {
                                                                        break label512;
                                                                    }

                                                                    var36 = var8;
                                                                    var38 = var15;
                                                                    if (var33 <= 0) {
                                                                        break label512;
                                                                    }

                                                                    var38 = (String) nodeList.get(var33 - 1);
                                                                }

                                                                billInfo.setShopRemark(var38);
                                                                var38 = var15;
                                                                var36 = var8;
                                                                break label512;
                                                            }

                                                            // var27.detailType = var34;
                                                            var36 = var8;
                                                            var38 = var15;
                                                        }

                                                        var11 = var35;
                                                        var35 = var38;
                                                        var8 = var36;
                                                    }
                                                }

                                                var38 = var13;
                                                var36 = var35;
                                                break label556;
                                            }

                                            var16 = var35;
                                            if (!"还款到".equals(var23) && !"提现到".equals(var23) && !"转入账户".equals(var23) && !"到账银行卡".equals(var23) || !var5) {
                                                label409:
                                                {
                                                    if (!"提现说明".equals(var23)) {
                                                        if ("转出说明".equals(var23) && var5) {
                                                            var35 = (String) nodeList.get(var33 + 1);
                                                            if (var35.contains("-转出到")) {
                                                                billInfo.setAccountName(var35.split("-转出到")[0]);
                                                            }
                                                            break label409;
                                                        }

                                                        if (var23.contains("收益发放") || var23.contains("奖励发放") || var23.contains("理财赎回") || var23.contains("卖出至")) {
                                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                                            var15 = var22;
                                                            var29 = var31;
                                                            var24 = var13;
                                                            var11 = var8;
                                                            break label570;
                                                        }

                                                        if ("充值成功".equals(var23) && var5) {
                                                            var3 = var33 + 1;
                                                            if (isMoney((String) nodeList.get(var3))) {
                                                                setMoney(billInfo, (String) nodeList.get(var3));
                                                                billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                                billInfo.setAccountName2(var20);
                                                                var11 = var8;
                                                                var24 = var13;
                                                                var29 = var31;
                                                                var15 = var22;
                                                                break label570;
                                                            }
                                                            break label409;
                                                        }

                                                        if ("服务费".equals(var23) && var5) {
                                                            var23 = ((String) nodeList.get(var33 + 1)).replace("￥", "").replace("¥", "");
                                                            var11 = var8;
                                                            var24 = var13;
                                                            var29 = var31;
                                                            var15 = var22;
                                                            if (isMoney(var23)) {
                                                                billInfo.setFee(var23);
                                                                var35 = var16;
                                                                var11 = var8;
                                                                var24 = var13;
                                                                var38 = var36;
                                                                var29 = var31;
                                                                var15 = var22;
                                                            }
                                                            break label570;
                                                        }

                                                        var35 = var35;
                                                        var11 = var8;
                                                        var24 = var13;
                                                        var38 = var38;
                                                        var29 = var31;
                                                        var15 = var22;
                                                        if (!"提现金额".equals(var23)) {
                                                            break label570;
                                                        }

                                                        var35 = var16;
                                                        var11 = var8;
                                                        var24 = var13;
                                                        var38 = var36;
                                                        var29 = var31;
                                                        var15 = var22;
                                                        if (!var5) {
                                                            break label570;
                                                        }

                                                        var23 = ((String) nodeList.get(var33 + 1)).replace("￥", "").replace("¥", "");
                                                        var35 = var16;
                                                        var11 = var8;
                                                        var24 = var13;
                                                        var38 = var36;
                                                        var29 = var31;
                                                        var15 = var22;
                                                        if (!isMoney(var23)) {
                                                            break label570;
                                                        }

                                                        setMoney(billInfo, var23);
                                                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                    }

                                                    billInfo.setAccountName(var20);
                                                    var35 = var35;
                                                    var11 = var8;
                                                    var24 = var13;
                                                    var38 = var38;
                                                    var29 = var31;
                                                    var15 = var22;
                                                    break label570;
                                                }

                                                var35 = var35;
                                                var11 = var8;
                                                var24 = var13;
                                                var38 = var38;
                                                var29 = var31;
                                                var15 = var22;
                                                break label570;
                                            }

                                            billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                            billInfo.setAccountName2((String) nodeList.get(var33 + 1));
                                            var11 = var35;
                                            var38 = var23;
                                        }

                                        var35 = var11;
                                        var11 = var8;
                                        var24 = var38;
                                        var38 = var36;
                                        var29 = var31;
                                        var15 = var22;
                                        break label570;
                                    }

                                    billInfo.setShopRemark((String) nodeList.get(var33 + 1));
                                }

                                var11 = var8;
                                var24 = var13;
                                var29 = var31;
                                var15 = var36;
                            }

                            ++var33;
                            var36 = var15;
                            var8 = var11;
                            var13 = var24;
                        }

                        if (super.a) {
                            if (var17.equals(billInfo.getAccountName()) || var18.equals(billInfo.getAccountName())) {
                                billInfo.setAccountName(var20);
                            }

                            if (var17.equals(billInfo.getAccountName2()) || var18.equals(billInfo.getAccountName2())) {
                                billInfo.setAccountName2(var20);
                            }

                            if (var31 && !"支付宝收款".equals(billInfo.getShopRemark())) {
                                String var30 = "转账给" + billInfo.getShopRemark();
                                billInfo.setShopRemark(var30);
                            }

                            var2 = billInfo.getTypeInt();
                            if (var2 == 2) {
                                var35 = billInfo.getAccountName();
                                String var32 = var35;
                                if (var35 == null) {
                                    var32 = "";
                                }

                                var35 = billInfo.getAccountName2();
                                if (var35 == null) {
                                    var35 = var26;
                                }

                                var41 = new StringBuilder();
                                var41.append(var32);
                                var41.append(var13);
                                var41.append(var35);
                                billInfo.setShopRemark(var41.toString());
                            } else if (var2 == 1 && billInfo.getAccountName() == null) {
                                billInfo.setAccountName(var20);
                            }


                            return billInfo;
                        }
                    }
                }
                return null;
            case 1:
                if (nodeList.size() != 0) {
                    if ("支付成功".equals(nodeList.get(0))) {
                        var28 = this.j(nodeList);
                        return var28;
                    }

                    var7 = new StringBuilder();
                    var7.append("[auto] MeituanDetailParser parse type ");
                    var7.append(var2);
                    var7.append(" ");
                    var7.append(nodeList.toString());
                    Log.i(var7.toString());
                    BillInfo billInfo = new BillInfo();


                    for (var2 = 0; var2 < nodeList.size(); ++var2) {
                        var8 = (String) nodeList.get(var2);
                        var29 = var2 < nodeList.size() - 1;

                        boolean var4;
                        var4 = var2 > 0;

                        label582:
                        {
                            if (("交易成功".equals(var8) || "退款".equals(var8)) && var4) {
                                var35 = (String) nodeList.get(var2 - 1);
                                var11 = var35.replace("+", "").replace("-", "");
                                if (!isMoney(var11)) {
                                    continue;
                                }

                                setMoney(billInfo, var11);
                                if (var2 >= 2) {
                                    billInfo.setShopRemark(((String) nodeList.get(var2 - 2)).replace("订单详情", ""));
                                }

                                if (!var35.contains("+")) {
                                    continue;
                                }

                                billInfo.setType(BillInfo.TYPE_INCOME);
                                var7 = new StringBuilder();
                                var7.append(var8);
                                var7.append("-");
                            } else {
                                if (var8.contains("交易时间")) {
                                    billInfo.setTime(var8.replace("交易时间", ""));
                                    if (billInfo.getTime() == null && var29) {
                                        billInfo.setTime((String) nodeList.get(var2 + 1));
                                    }
                                    continue;
                                }

                                if ("支付方式".equals(var8) && var29) {
                                    billInfo.setAccountName((String) nodeList.get(var2 + 1));
                                    continue;
                                }

                                if ("还款详情".equals(var8) && var29) {
                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    var7 = new StringBuilder();
                                    var7.append(billInfo.getShopRemark());
                                    var8 = (String) nodeList.get(var2 + 1);
                                    break label582;
                                }

                                if (!"备注".equals(var8) || !var29) {
                                    if ("入账金额".equals(var8) && var29) {
                                        var35 = ((String) nodeList.get(var2 + 1)).replace("+", "");
                                        if (isMoney(var35)) {
                                            setMoney(billInfo, var35);
                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                        }
                                    } else if ("出账金额".equals(var8) && var29) {
                                        var35 = ((String) nodeList.get(var2 + 1)).replace("-", "");
                                        if (isMoney(var35)) {
                                            setMoney(billInfo, var35);
                                        }
                                    }
                                    continue;
                                }

                                var35 = (String) nodeList.get(var2 + 1);
                                billInfo.setShopRemark(var35);
                                if ("充值".equals(var35)) {
                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    billInfo.setAccountName("美团零钱");
                                    var7 = new StringBuilder();
                                    var8 = billInfo.getAccountName2();
                                } else {
                                    if (!"提现".equals(billInfo.getShopRemark())) {
                                        continue;
                                    }

                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    billInfo.setAccountName("美团零钱");
                                    var7 = new StringBuilder();
                                    var8 = billInfo.getAccountName();
                                }

                                var7.append(var8);
                            }

                            var8 = billInfo.getShopRemark();
                        }

                        var7.append(var8);
                        var35 = var7.toString();
                        billInfo.setShopRemark(var35);
                    }

                    if (super.a) {
                        return billInfo;
                    }
                }

                var28 = null;
                return var28;
            default:
                if (nodeList.size() == 0) {
                    var28 = null;
                } else {
                    var7 = new StringBuilder();
                    var7.append("[auto] UnionPayDetailParser parse type ");
                    var7.append(var2);
                    var7.append(" ");
                    var7.append(nodeList.toString());
                    Log.i(var7.toString());
                    if (var2 == 1) {
                        var28 = this.j(nodeList);
                    } else {
                        var28 = this.i(nodeList);
                    }
                }

                return var28;
        }
    }

    public BillInfo i(List<Object> nodeList) {
        BillInfo billInfo = new BillInfo();
        int var4 = 0;

        boolean var2;
        boolean var3;
        for (var3 = false; var4 < nodeList.size(); var3 = var2) {
            label118:
            {
                String var5 = (String) nodeList.get(var4);
                if (("支出金额".equals(var5) || "订单金额".equals(var5)) && var4 < nodeList.size() - 1) {
                    var5 = ((String) nodeList.get(var4 + 1)).replace("元", "");
                    var2 = var3;
                    if (super.a) {
                        break label118;
                    }

                    var2 = var3;
                    if (!isMoney(var5)) {
                        break label118;
                    }
                } else {
                    if (!"实付金额（元）".equals(var5) || var4 >= nodeList.size() - 1) {
                        if ("收入金额".equals(var5) && var4 < nodeList.size() - 1) {
                            var5 = ((String) nodeList.get(var4 + 1)).replace("元", "");
                            if (isMoney(var5)) {
                                setMoney(billInfo, var5);
                            }

                            billInfo.setType(BillInfo.TYPE_INCOME);
                            var2 = true;
                            break label118;
                        }

                        if (("商户名称".equals(var5) || "乘车线路".equals(var5)) && var4 < nodeList.size() - 1) {
                            billInfo.setShopAccount((String) nodeList.get(var4 + 1));
                            var2 = var3;
                            break label118;
                        }

                        if ("卡号".equals(var5) && var4 < nodeList.size() - 1) {
                            var5 = ((String) nodeList.get(var4 + 1)).replace("尾号", "");
                        } else {
                            if (!"付款卡号".equals(var5) || var4 >= nodeList.size() - 1) {
                                if (("时间".equals(var5) || "订单时间".equals(var5) || "扣款时间".equals(var5)) && var4 < nodeList.size() - 1) {

                                    billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(var4 + 1), "yyyy-MM-dd HH:mm"));
                                    var2 = var3;
                                    break label118;
                                }

                                var2 = var3;
                                if ("分类".equals(var5)) {
                                    var2 = var3;
                                    if (var4 < nodeList.size() - 1) {
                                        billInfo.setShopRemark((String) nodeList.get(var4 + 1));
                                        var2 = var3;
                                    }
                                }
                                break label118;
                            }

                            var5 = (String) nodeList.get(var4 + 1);
                            var5 = var5.substring(var5.length() - 4);
                        }

                        billInfo.setAccountName(var5);
                        var2 = var3;
                        break label118;
                    }

                    var5 = ((String) nodeList.get(var4 + 1)).replace("元", "");
                    var2 = var3;
                    if (!isMoney(var5)) {
                        break label118;
                    }
                }

                setMoney(billInfo, var5);
                var2 = var3;
            }

            ++var4;
        }


        if (var3) {
            billInfo.setShopRemark("收入-" + billInfo.getShopRemark());
        }

        if (super.a) {
            return billInfo;
        } else {
            return null;
        }
    }

    public BillInfo j(List<Object> nodeList) {
        StringBuilder var5 = null;
        BillInfo billInfo = null;
        int var2 = 0;
        String var7;
        switch (this.b) {
            case 1:
                var5 = new StringBuilder("[auto] MeituanDetailParser parsePay ");
                var5.append(nodeList.toString());
                Log.i(var5.toString());
                BillInfo var10 = new BillInfo();
                billInfo = new BillInfo();

                for (var2 = 0; var2 < nodeList.size(); ++var2) {
                    var7 = (String) nodeList.get(var2);
                    boolean var9;
                    var9 = var2 < nodeList.size() - 1;

                    var7 = var7.replace("￥", "").replace("¥", "");
                    if (isMoney(var7)) {
                        setMoney(billInfo, var7);
                        if (var9) {
                            billInfo.setAccountName((String) nodeList.get(var2 + 1));
                        }
                        break;
                    }
                }

                if (super.a) {
                    billInfo.setShopRemark("美团订单");
                }

                return billInfo;
            default:
                billInfo = new BillInfo();

                for (; var2 < nodeList.size(); ++var2) {
                    var7 = (String) nodeList.get(var2);
                    if (!super.a && var7.contains("¥")) {
                        var7 = var7.replace("¥", "");
                        if (isMoney(var7)) {
                            setMoney(billInfo, var7);
                        }
                    } else if ("订单信息".equals(var7) && var2 < nodeList.size() - 1) {
                        billInfo.setShopRemark((String) nodeList.get(var2 + 1));
                    } else if ("付款方式".equals(var7) && var2 < nodeList.size() - 1) {
                        billInfo.setAccountName((String) nodeList.get(var2 + 1));
                    }
                }


                return billInfo;
        }
    }
}

