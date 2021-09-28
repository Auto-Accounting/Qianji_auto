//
// Decompiled by FernFlower - 1898ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


public class TransferDetailParser extends baseAnalyze {
    public static int AlipayDetailParser = 0;
    public static int MTDetailParser = 1;
    public static int UnionPayDetailParser = 2;
    public int type;

    public TransferDetailParser(int type) {
        this.type = type;
        Log.init("自动记账辅助:转账");
    }

    public BillInfo h(List<Object> nodeList, int var2) {
        int var3;
        String var26 = "";
        StringBuilder log;
        String remark;
        String node;
        BillInfo var28;
        boolean var29;
        String var35;
        if (nodeList.size() != 0) return null;
        switch (type) {
            case 0:
                log = new StringBuilder();
                log.append("[auto] AlipayDetailParser parse type ");
                log.append(var2);
                log.append(" ");
                log.append(nodeList.toString());
                Log.i(log.toString());

                String var36;
                String var18 = "余额";
                String var38 = "付款方式";
                String var17 = "账户余额";
                String var14 = ",";
                var35 = "+";
                remark = "交易成功";

                StringBuilder str;
                if (var2 == 2) {
                    Log.i("[auto] AlipayDetailParser parseTransfer");
                    BillInfo billinfo = new BillInfo();

                    for (int index = 0; index < nodeList.size(); ++index) {
                        String nodeStr = (String) nodeList.get(index);
                        if ("收款方".equals(nodeStr) && index < nodeList.size() - 1) {
                            str = new StringBuilder();
                            str.append("转账给");
                            str.append((String) nodeList.get(index + 1));
                            billinfo.setShopRemark(str.toString());
                        } else if ("转账成功".equals(nodeStr) && index < nodeList.size() - 2) {
                            nodeStr = ((String) nodeList.get(index + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                            if (!isMoney(nodeStr)) {
                                nodeStr = ((String) nodeList.get(index + 2)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                            }
                            if (isMoney(nodeStr)) {
                                setMoney(billinfo, nodeStr);
                            }
                        } else if ("付款方式".equals(nodeStr)) {
                            billinfo.setAccountName((String) nodeList.get(index + 1));
                        }
                    }

                    if (isSetMoney) {
                        if (!TextUtils.isEmpty(billinfo.getAccountName()) && TextUtils.isEmpty(billinfo.getShopRemark())) {
                            billinfo.setShopRemark("支付宝转账");
                        }
                        return billinfo;
                    }
                } else {
                    var36 = "收款方";
                    String var20 = "支付宝";
                    BillInfo billInfo = new BillInfo();
                    String var13 = "->";

                    boolean var31 = false;
                    for (int index = 0; index < nodeList.size(); index++, var31 = var29) {
                        String nodeStr = (String) nodeList.get(index);
                        boolean var5 = index < nodeList.size() - 1;

                        String var15;
                        String var24;
                        label570:
                        {
                            if ("转账备注".equals(nodeStr)) {
                                var29 = var31;
                                if (var5) {
                                    node = (String) nodeList.get(index + 1);
                                    if (!"转账".equals(node)) {
                                        billInfo.setShopRemark("转账-" + node);
                                        var29 = var31;
                                    } else {
                                        var29 = true;
                                    }
                                }

                                var31 = var29;
                            } else {
                                String var22 = var36;
                                if (!"收款方".equals(nodeStr) || !var5) {
                                    String var16;

                                    if (("支付成功".equals(nodeStr) || "代付成功".equals(nodeStr)) && index < nodeList.size() - 2) {
                                        String node1 = ((String) nodeList.get(index + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(var14, "");
                                        if (isMoney(node1)) {
                                            setMoney(billInfo, node1);
                                            if ("代付成功".equals(nodeStr)) {
                                                billInfo.setShopRemark("支付宝代付");
                                            }
                                        }

                                        node = ((String) nodeList.get(index + 2)).replace("￥", "").replace("¥", "").replace("元", "").replace(var14, "");
                                        if (!isMoney(node1) && isMoney(node)) {
                                            setMoney(billInfo, node);
                                        } else if (!"付款方式".equals(node)) {
                                            billInfo.setShopRemark(node);
                                        }

                                        node = var35;
                                        var35 = var38;
                                    } else {
                                        remark = "交易成功";
                                        if (("有退款".equals(nodeStr) ||
                                                "自动扣款成功".equals(nodeStr) ||
                                                "已全额退款".equals(nodeStr) ||
                                                "退款成功".equals(nodeStr) ||
                                                "交易成功".equals(nodeStr) ||
                                                "还款成功".equals(nodeStr) ||
                                                "亲情卡付款成功".equals(nodeStr) ||
                                                "等待对方发货".equals(nodeStr) ||
                                                "等待确认收货".equals(nodeStr) ||
                                                "付款成功".contains(nodeStr)) && index > 0) {
                                            int i = index - 1;
                                            String s = ((String) nodeList.get(i)).replace("￥", "").replace("¥", "").replace(var14, "");
                                            s = s.replace("+", "").replace("元", "").replace("-", "");
                                            if (isMoney(s)) {
                                                setMoney(billInfo, s);
                                                if ("还款成功".equals(nodeStr) && index > 1) {
                                                    billInfo.setAccountName2((String) nodeList.get(index - 2));
                                                    billInfo.setType(BillInfo.TYPE_CREDIT_CARD_PAYMENT);
                                                    remark = var36;

                                                } else {
                                                    if (((String) nodeList.get(i)).contains("+")) {
                                                        if (billInfo.getShopRemark() == null) {
                                                            billInfo.setShopRemark("支付宝收款");
                                                        }
                                                        billInfo.setType(BillInfo.TYPE_INCOME);
                                                    } else {
                                                        remark = "支付宝付款";
                                                        if (index > 1) {
                                                            if (TextUtils.isEmpty(billInfo.getShopRemark())) {
                                                                remark = (String) nodeList.get(index - 2);
                                                            }
                                                        }
                                                        billInfo.setShopRemark(remark);
                                                    }

                                                    if ("退款成功".equals(nodeStr)) {
                                                        billInfo.setType(BillInfo.TYPE_INCOME);
                                                        str = new StringBuilder();
                                                        str.append(billInfo.getShopRemark());
                                                        str.append("-退款");
                                                        var38 = str.toString();
                                                        remark = var36;
                                                    }
                                                }
                                            }


                                        } else if (isSetMoney || !nodeStr.contains("￥") && !nodeStr.contains("¥") && !nodeStr.contains("-") && !nodeStr.contains("+")) {
                                            var36 = var38;
                                            if (("付款方式".equals(nodeStr) || "退款方式".equals(nodeStr) || "付款信息".equals(nodeStr)) && var5) {
                                                billInfo.setAccountName((String) nodeList.get(index + 1));
                                            } else if (!"创建时间".equals(nodeStr)) {
                                                if ("商品说明".equals(nodeStr) || "红包说明".equals(nodeStr) || "红包来自".equals(nodeStr)) {
                                                    node = var35;


                                                    if ("商品说明".equals(nodeStr)) {
                                                        var3 = index + 1;
                                                        if (!((String) nodeList.get(var3)).contains("-转出到")) {
                                                            var35 = billInfo.getShopRemark();
                                                            if (var35 != null && !"支付宝收款".equals(var35)) {
                                                                log = new StringBuilder();
                                                                log.append((String) nodeList.get(index + 1));
                                                                log.append("-");
                                                                log.append(billInfo.getShopRemark());
                                                                var35 = log.toString();
                                                            } else {
                                                                var35 = (String) nodeList.get(index + 1);
                                                            }

                                                            billInfo.setShopRemark(var35);
                                                            var35 = var38;

                                                        } else {
                                                            billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                            String[] var42 = ((String) nodeList.get(var3)).split("-转出到");
                                                            billInfo.setAccountName(var42[0]);
                                                            billInfo.setAccountName2(var42[1]);
                                                            var38 = "转出到";
                                                        }
                                                    }


                                                }


                                            } else {
                                                billInfo.setTimeStamp(DateUtils.dateToStamp((String) nodeList.get(index + 1), "yyyy-MM-dd HH:mm:ss"));

                                            }

                                            var36 = remark;
                                        } else {
                                            var38 = nodeStr.replace("￥", "").replace("¥", "");
                                            var38 = var38.replace(var14, "").replace(var35, "");
                                            var16 = nodeStr;
                                            node = var38;
                                            if (!isMoney(var38)) {
                                                var16 = nodeStr;
                                                node = var38;
                                                if (var5) {
                                                    var16 = (String) nodeList.get(index + 1);
                                                    node = var16.replace("￥", "").replace("¥", "").replace(var14, "").replace(var35, "");
                                                }
                                            }

                                            // var38 = var15;
                                            if (isMoney(node)) {
                                                setMoney(billInfo, node);
                                                if (var16.contains(var35)) {
                                                    billInfo.setShopRemark("支付宝收款");
                                                    billInfo.setType(BillInfo.TYPE_INCOME);

                                                } else if (TextUtils.isEmpty(billInfo.getShopRemark())) {
                                                    if (index > 0) {
                                                        var38 = (String) nodeList.get(index - 1);
                                                    }
                                                }


                                            }
                                            billInfo.setShopRemark(var38);
                                            //var38 = var15;
                                            var36 = remark;


                                        }


                                        //  var38 = var13;
                                        //  var36 = var35;
                                        //  break label556;
                                    }

                                    var16 = var35;
                                    if (!"还款到".equals(nodeStr) && !"提现到".equals(nodeStr) && !"转入账户".equals(nodeStr) && !"到账银行卡".equals(nodeStr) || !var5) {
                                        label409:
                                        {
                                            if (!"提现说明".equals(nodeStr)) {
                                                if ("转出说明".equals(nodeStr) && var5) {
                                                    var35 = (String) nodeList.get(index + 1);
                                                    if (var35.contains("-转出到")) {
                                                        billInfo.setAccountName(var35.split("-转出到")[0]);
                                                    }
                                                    break label409;
                                                }

                                                if (nodeStr.contains("收益发放") || nodeStr.contains("奖励发放") || nodeStr.contains("理财赎回") || nodeStr.contains("卖出至")) {
                                                    billInfo.setType(BillInfo.TYPE_INCOME);
                                                    var15 = var22;
                                                    var29 = var31;
                                                    var24 = var13;
                                                    node = remark;
                                                    break label570;
                                                }

                                                if ("充值成功".equals(nodeStr) && var5) {
                                                    var3 = index + 1;
                                                    if (isMoney((String) nodeList.get(var3))) {
                                                        setMoney(billInfo, (String) nodeList.get(var3));
                                                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                                        billInfo.setAccountName2(var20);
                                                        node = remark;
                                                        var24 = var13;
                                                        var29 = var31;
                                                        var15 = var22;
                                                        break label570;
                                                    }
                                                    break label409;
                                                }

                                                if ("服务费".equals(nodeStr) && var5) {
                                                    nodeStr = ((String) nodeList.get(index + 1)).replace("￥", "").replace("¥", "");
                                                    node = remark;
                                                    var24 = var13;
                                                    var29 = var31;
                                                    var15 = var22;
                                                    if (isMoney(nodeStr)) {
                                                        billInfo.setFee(nodeStr);
                                                        var35 = var16;
                                                        node = remark;
                                                        var24 = var13;
                                                        var38 = var36;
                                                        var29 = var31;
                                                        var15 = var22;
                                                    }
                                                    break label570;
                                                }

                                                var35 = var35;
                                                node = remark;
                                                var24 = var13;
                                                var38 = var38;
                                                var29 = var31;
                                                var15 = var22;
                                                if (!"提现金额".equals(nodeStr)) {
                                                    break label570;
                                                }

                                                var35 = var16;
                                                node = remark;
                                                var24 = var13;
                                                var38 = var36;
                                                var29 = var31;
                                                var15 = var22;
                                                if (!var5) {
                                                    break label570;
                                                }

                                                nodeStr = ((String) nodeList.get(index + 1)).replace("￥", "").replace("¥", "");
                                                var35 = var16;
                                                node = remark;
                                                var24 = var13;
                                                var38 = var36;
                                                var29 = var31;
                                                var15 = var22;
                                                if (!isMoney(nodeStr)) {
                                                    break label570;
                                                }

                                                setMoney(billInfo, nodeStr);
                                                billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                            }

                                            billInfo.setAccountName(var20);
                                            var35 = var35;
                                            node = remark;
                                            var24 = var13;
                                            var38 = var38;
                                            var29 = var31;
                                            var15 = var22;
                                            break label570;
                                        }

                                        var35 = var35;
                                        node = remark;
                                        var24 = var13;
                                        var38 = var38;
                                        var29 = var31;
                                        var15 = var22;
                                        break label570;
                                    }

                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    billInfo.setAccountName2((String) nodeList.get(index + 1));
                                    node = var35;
                                    var38 = nodeStr;
                                } else {
                                    billInfo.setShopRemark((String) nodeList.get(index + 1));
                                }

                            }

                            node = remark;
                            var24 = var13;
                            var29 = var31;
                            var15 = var36;
                        }

                        var36 = var15;
                        remark = node;
                        var13 = var24;
                    }

                    if (isSetMoney) {
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

                            str = new StringBuilder();
                            str.append(var32);
                            str.append(var13);
                            str.append(var35);
                            billInfo.setShopRemark(str.toString());
                        } else if (var2 == 1 && billInfo.getAccountName() == null) {
                            billInfo.setAccountName(var20);
                        }


                        return billInfo;
                    }
                }

                return null;
            case 1:
                if (nodeList.size() != 0) {
                    if ("支付成功".equals(nodeList.get(0))) {
                        var28 = this.j(nodeList);
                        return var28;
                    }

                    log = new StringBuilder();
                    log.append("[auto] MeituanDetailParser parse type ");
                    log.append(var2);
                    log.append(" ");
                    log.append(nodeList.toString());
                    Log.i(log.toString());
                    BillInfo billInfo = new BillInfo();


                    for (var2 = 0; var2 < nodeList.size(); ++var2) {
                        remark = (String) nodeList.get(var2);
                        var29 = var2 < nodeList.size() - 1;

                        boolean var4;
                        var4 = var2 > 0;

                        label582:
                        {
                            if (("交易成功".equals(remark) || "退款".equals(remark)) && var4) {
                                var35 = (String) nodeList.get(var2 - 1);
                                node = var35.replace("+", "").replace("-", "");
                                if (!isMoney(node)) {
                                    continue;
                                }

                                setMoney(billInfo, node);
                                if (var2 >= 2) {
                                    billInfo.setShopRemark(((String) nodeList.get(var2 - 2)).replace("订单详情", ""));
                                }

                                if (!var35.contains("+")) {
                                    continue;
                                }

                                billInfo.setType(BillInfo.TYPE_INCOME);
                                log = new StringBuilder();
                                log.append(remark);
                                log.append("-");
                            } else {
                                if (remark.contains("交易时间")) {
                                    billInfo.setTime(remark.replace("交易时间", ""));
                                    if (billInfo.getTime() == null && var29) {
                                        billInfo.setTime((String) nodeList.get(var2 + 1));
                                    }
                                    continue;
                                }

                                if ("支付方式".equals(remark) && var29) {
                                    billInfo.setAccountName((String) nodeList.get(var2 + 1));
                                    continue;
                                }

                                if ("还款详情".equals(remark) && var29) {
                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    log = new StringBuilder();
                                    log.append(billInfo.getShopRemark());
                                    remark = (String) nodeList.get(var2 + 1);
                                    break label582;
                                }

                                if (!"备注".equals(remark) || !var29) {
                                    if ("入账金额".equals(remark) && var29) {
                                        var35 = ((String) nodeList.get(var2 + 1)).replace("+", "");
                                        if (isMoney(var35)) {
                                            setMoney(billInfo, var35);
                                            billInfo.setType(BillInfo.TYPE_INCOME);
                                        }
                                    } else if ("出账金额".equals(remark) && var29) {
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
                                    log = new StringBuilder();
                                    remark = billInfo.getAccountName2();
                                } else {
                                    if (!"提现".equals(billInfo.getShopRemark())) {
                                        continue;
                                    }

                                    billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                    billInfo.setAccountName("美团零钱");
                                    log = new StringBuilder();
                                    remark = billInfo.getAccountName();
                                }

                                log.append(remark);
                            }

                            remark = billInfo.getShopRemark();
                        }

                        log.append(remark);
                        var35 = log.toString();
                        billInfo.setShopRemark(var35);
                    }

                    if (isSetMoney) {
                        return billInfo;
                    }
                }

                return null;
            default:
                if (nodeList.size() == 0) {
                    var28 = null;
                } else {
                    log = new StringBuilder();
                    log.append("[auto] UnionPayDetailParser parse type ");
                    log.append(var2);
                    log.append(" ");
                    log.append(nodeList.toString());
                    Log.i(log.toString());
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
                    if (isSetMoney) {
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

        if (isSetMoney) {
            return billInfo;
        } else {
            return null;
        }
    }

    public BillInfo j(List<Object> nodeList) {
        StringBuilder var5 = null;
        BillInfo billInfo = null;
        int var2 = 0;
        String log;
        switch (type) {
            case 1:
                var5 = new StringBuilder("[auto] MeituanDetailParser parsePay ");
                var5.append(nodeList.toString());
                Log.i(var5.toString());
                BillInfo var10 = new BillInfo();
                billInfo = new BillInfo();

                for (var2 = 0; var2 < nodeList.size(); ++var2) {
                    log = (String) nodeList.get(var2);
                    boolean var9;
                    var9 = var2 < nodeList.size() - 1;

                    log = log.replace("￥", "").replace("¥", "");
                    if (isMoney(log)) {
                        setMoney(billInfo, log);
                        if (var9) {
                            billInfo.setAccountName((String) nodeList.get(var2 + 1));
                        }
                        break;
                    }
                }

                if (isSetMoney) {
                    billInfo.setShopRemark("美团订单");
                }

                return billInfo;
            default:
                billInfo = new BillInfo();

                for (; var2 < nodeList.size(); ++var2) {
                    log = (String) nodeList.get(var2);
                    if (!isSetMoney && log.contains("¥")) {
                        log = log.replace("¥", "");
                        if (isMoney(log)) {
                            setMoney(billInfo, log);
                        }
                    } else if ("订单信息".equals(log) && var2 < nodeList.size() - 1) {
                        billInfo.setShopRemark((String) nodeList.get(var2 + 1));
                    } else if ("付款方式".equals(log) && var2 < nodeList.size() - 1) {
                        billInfo.setAccountName((String) nodeList.get(var2 + 1));
                    }
                }


                return billInfo;
        }
    }
}

