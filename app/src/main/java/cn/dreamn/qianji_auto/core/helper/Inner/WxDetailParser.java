//
// Decompiled by FernFlower - 1349ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class WxDetailParser extends baseAnalyze {
    public String b;
    public boolean c;

    public BillInfo run(List<Object> nodeList, int var2) {
        if (nodeList.size() == 0) return null;
        Log.d("[auto] WxDetailParser parse type" + var2 + " " + nodeList.toString());
        String var31 = "零钱通";
        String var13 = "零钱";
        String var14 = "¥";
        byte var3 = 0;
        boolean var5;
        String var7;
        String var10;
        BillInfo var27;
        int var30;
        if (var2 == 1) {
            BillInfo billinfo = new BillInfo();
            var5 = ((String) nodeList.get(0)).endsWith("发起的群收款");
            var2 = var3;

            while (var2 < nodeList.size()) {
                String var29;
                label433:
                {
                    var10 = (String) nodeList.get(var2);
                    var7 = var10.replace("￥", "").replace("¥", "").replace(",", "");
                    if (this.isMoney(var7)) {
                        this.setMoney(billinfo, var7);
                        if (this.c) {
                            billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                            billinfo.setAccountName2(var31);
                            var29 = "转入零钱通";
                            break label433;
                        }
                    } else {
                        label455:
                        {
                            label444:
                            {
                                if ("收款方".equals(var10) && var2 < nodeList.size() - 1) {
                                    var30 = var2 + 1;
                                } else {
                                    if (!"支付成功".equals(var10) || var2 >= nodeList.size() - 2) {
                                        if (var5 && var10.contains("已收齐") && var2 < nodeList.size() - 1) {
                                            var7 = ((String) nodeList.get(var2 + 1)).replace("收到¥", "");
                                            billinfo.setType(BillInfo.TYPE_INCOME);
                                            if (isMoney(var7)) {
                                                setMoney(billinfo, var7);
                                                var29 = "我发起的群收款-已收齐";
                                                break label433;
                                            }
                                            break label455;
                                        }

                                        if (var5 && var10.contains("已支付")) {
                                            var7 = var7.replace("已支付", "");
                                            if (isMoney(var7)) {
                                                setMoney(billinfo, var7);
                                                var29 = (String) nodeList.get(0);
                                                break label433;
                                            }
                                            break label455;
                                        }

                                        if (!"充值成功".equals(var10)) {
                                            break label455;
                                        }

                                        billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                        billinfo.setAccountName2("微信钱包");
                                        var7 = "微信零钱充值";
                                        break label444;
                                    }

                                    var30 = var2 + 1;
                                    if (((String) nodeList.get(var30)).contains("¥")) {
                                        break label455;
                                    }
                                }

                                var7 = (String) nodeList.get(var30);
                            }

                            billinfo.setShopRemark(var7);
                        }
                    }

                    ++var2;
                    continue;
                }
                billinfo.setShopRemark(var29);
                break;
            }

            if (!TextUtils.isEmpty(this.b)) {
                if ("零钱".equals(this.b)) {
                    this.b = "微信钱包";
                }

                billinfo.setAccountName(this.b);
            }

            if (isSetMoney) {
                var27 = billinfo;
            } else {
                var27 = null;
            }

            return var27;
        } else {
            String var12 = "零钱通";
            var10 = "收款时间";
            String var8;
            BillInfo billInfo;
            if (var2 == 3) {
                billInfo = new BillInfo();


                for (var2 = 0; var2 < nodeList.size(); ++var2) {
                    var8 = (String) nodeList.get(var2);
                    if (("已收款".equals(var8) || "你已收款".equals(var8)) && var2 < nodeList.size() - 2) {
                        var8 = ((String) nodeList.get(var2 + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                        if (isMoney(var8)) {
                            setMoney(billInfo, var8);
                        }
                    } else if ("收款时间".equals(var8) && var2 < nodeList.size() - 1) {
                        billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(var2 + 1), "yyyy年MM月dd日 hh:mm:ss"));
                    }
                }

                if (isSetMoney) {
                    billInfo.setShopRemark("微信收款");
                    billInfo.setAccountName("微信钱包");
                    billInfo.setType(BillInfo.TYPE_INCOME);
                    var27 = billInfo;
                } else {
                    var27 = null;
                }

                return var27;
            } else {
                var8 = "转账说明";
                String var11 = "转账时间";
                String var9;
                if (var2 == 2) {
                    billInfo = new BillInfo();

                    for (var2 = 0; var2 < nodeList.size(); ++var2) {
                        var9 = (String) nodeList.get(var2);
                        var8 = var9.replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                        if (isMoney(var8)) {
                            setMoney(billInfo, var8);
                        } else if ("转账时间".equals(var9) && var2 < nodeList.size() - 1) {
                            billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(var2 + 1), "yyyy年MM月dd日 hh:mm:ss"));
                        } else if ("转账说明".equals(var9) && var2 < nodeList.size() - 1) {
                            billInfo.setShopRemark((String) nodeList.get(var2 + 1));
                        } else if (var9.startsWith("待") && var9.endsWith("收款")) {

                            billInfo.setShopAccount(var9.substring(1, var9.lastIndexOf("收款")));
                        }
                    }

                    if (isSetMoney) {

                        if (billInfo.getShopRemark().equals("")) {
                            billInfo.setShopRemark("微信转账");
                        }
                        if (billInfo.getAccountName().equals("")) {
                            billInfo.setAccountName("微信");
                        }
                        return billInfo;
                    }

                    return null;
                } else {
                    BillInfo billinfo = new BillInfo();


                    for (var2 = 0; var2 < nodeList.size(); var11 = var31) {
                        String var19 = (String) nodeList.get(var2);
                        boolean var28;
                        var28 = var2 < nodeList.size() - 1;

                        label456:
                        {
                            if (!isSetMoney) {
                                var5 = var19.contains("转入");
                                var31 = var11;
                                if (var5 || var19.contains("转出") || var19.contains("还款") || var19.contains("零钱充值") || var19.contains("零钱提现") || "提现金额".equals(var19)) {
                                    var30 = var2 + 1;
                                    if (var30 < nodeList.size()) {
                                        var7 = ((String) nodeList.get(var30)).replace(var14, "").replace("￥", "");
                                        if (isMoney(var7)) {
                                            setMoney(billinfo, var7);
                                            billinfo.setShopRemark(var19);
                                            billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                            if ("提现金额".equals(var19)) {
                                                billinfo.setAccountName("微信钱包");
                                                billinfo.setShopRemark("微信零钱提现");
                                            } else {
                                                var30 = var19.indexOf("-");
                                                if (var30 != -1) {
                                                    label337:
                                                    {
                                                        Exception var10000;
                                                        label424:
                                                        {
                                                            boolean var10001;
                                                            try {
                                                                if (var19.startsWith("转入")) {
                                                                    billinfo.setAccountName2(var19.substring(2, var30));
                                                                    billinfo.setAccountName(var19.substring(var30 + 3));
                                                                    break label337;
                                                                }
                                                            } catch (Exception var23) {
                                                                var10000 = var23;
                                                                var10001 = false;
                                                                break label424;
                                                            }

                                                            label419:
                                                            {
                                                                try {
                                                                    if (var19.contains("转出")) {
                                                                        billinfo.setAccountName(var19.substring(0, var19.indexOf("转出")));
                                                                        var7 = var19.substring(var30 + 2);
                                                                        break label419;
                                                                    }
                                                                } catch (Exception var26) {
                                                                    var10000 = var26;
                                                                    var10001 = false;
                                                                    break label424;
                                                                }

                                                                try {
                                                                    if (var19.contains("还款")) {
                                                                        String[] var36 = var19.split("还款");
                                                                        if (var36.length < 2) {
                                                                            break label337;
                                                                        }

                                                                        var7 = var36[1].substring(1);
                                                                        break label419;
                                                                    }
                                                                } catch (Exception var25) {
                                                                    var10000 = var25;
                                                                    var10001 = false;
                                                                    break label424;
                                                                }

                                                                try {
                                                                    if (var19.contains("零钱充值")) {
                                                                        billinfo.setAccountName2("微信钱包");
                                                                        break label337;
                                                                    }
                                                                } catch (Exception var24) {
                                                                    var10000 = var24;
                                                                    break label424;
                                                                }

                                                                try {
                                                                    if (var19.contains("零钱提现")) {
                                                                        billinfo.setAccountName("微信钱包");
                                                                    }
                                                                    break label337;
                                                                } catch (Exception var22) {
                                                                    var10000 = var22;
                                                                    break label424;
                                                                }
                                                            }

                                                            try {
                                                                billinfo.setAccountName2(var7);
                                                                break label337;
                                                            } catch (Exception var21) {
                                                                var10000 = var21;
                                                            }
                                                        }

                                                        Exception var38 = var10000;
                                                        var38.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    var7 = var8;
                                    var9 = var10;
                                    break label456;
                                }
                            }

                            if (!var19.contains("-") && !var19.contains("+")) {
                                if ("支付时间".equals(var19) || var11.equals(var19) || var10.equals(var19)) {
                                    var9 = var10;
                                    if (var28) {
                                        billinfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(var2 + 1), "yyyy-MM-dd HH:mm:ss"));
                                        var31 = var11;
                                        var7 = var8;
                                        break label456;
                                    }
                                }

                                var9 = var10;
                                var31 = var11;
                                if (billinfo.getAccountName() != null || !"支付方式".equals(var19) && !"收款帐号".equals(var19) && !"退款方式".equals(var19) || !var28) {
                                    label363:
                                    {
                                        StringBuilder var32;
                                        label442:
                                        {
                                            if ("商品".equals(var19) && var28) {
                                                if (billinfo.getShopRemark() != null) {
                                                    var32 = new StringBuilder();
                                                    var32.append((String) nodeList.get(var2 + 1));
                                                    var32.append("-");
                                                    var10 = billinfo.getShopRemark();
                                                    break label442;
                                                }
                                            } else {
                                                var7 = var8;
                                                if (!var8.equals(var19) || !var28) {
                                                    if (("提现银行".equals(var19) || "到账银行卡".equals(var19)) && var28) {
                                                        billinfo.setAccountName2((String) nodeList.get(var2 + 1));
                                                        if (billinfo.getAccountName() == null && this.c) {
                                                            billinfo.setAccountName(var12);
                                                        }
                                                    } else if ("服务费".equals(var19) && var28) {
                                                        var8 = ((String) nodeList.get(var2 + 1)).replace("￥", "");
                                                        if (isMoney(var8)) {
                                                            billinfo.setFee(var8);
                                                        }
                                                    }
                                                    break label456;
                                                }

                                                if (billinfo.getShopRemark() != null) {
                                                    var32 = new StringBuilder();
                                                    var32.append(billinfo.getShopRemark());
                                                    var32.append("-");
                                                    var10 = (String) nodeList.get(var2 + 1);
                                                    break label442;
                                                }
                                            }

                                            var10 = (String) nodeList.get(var2 + 1);
                                            break label363;
                                        }

                                        var32.append(var10);
                                        var10 = var32.toString();
                                    }

                                    var7 = var8;
                                    billinfo.setShopRemark(var10);
                                    break label456;
                                }

                                billinfo.setAccountName((String) nodeList.get(var2 + 1));
                                var5 = var19.contains("收");
                                var7 = var8;
                                var31 = var11;
                                if (!var5) {
                                    break label456;
                                }

                                var31 = var11;
                            } else {
                                String var20 = var19.replace("+", "").replace("-", "");
                                var9 = var10;
                                var7 = var8;
                                var31 = var11;
                                if (!isMoney(var20)) {
                                    break label456;
                                }

                                setMoney(billinfo, var20);
                                if (var2 > 0) {
                                    billinfo.setShopRemark((String) nodeList.get(var2 - 1));
                                }

                                var9 = var10;
                                var7 = var8;
                                var31 = var11;
                                if (!var19.contains("+")) {
                                    break label456;
                                }

                                var31 = var11;
                            }

                            var7 = var8;
                            var9 = var10;
                            billinfo.setType(BillInfo.TYPE_INCOME);
                        }

                        ++var2;
                        var10 = var9;
                        var8 = var7;
                    }

                    if (isSetMoney) {
                        if (var13.equals(billinfo.getAccountName())) {
                            billinfo.setAccountName("微信钱包");
                        }

                        if (var13.equals(billinfo.getAccountName2())) {
                            billinfo.setAccountName2("微信钱包");
                        }

                        if (billinfo.getTypeInt() == 1 && billinfo.getAccountName() == null) {
                            billinfo.setAccountName("微信钱包");
                        }
                        var27 = billinfo;
                    } else {
                        var27 = null;
                    }

                    return var27;
                }
            }

        }
    }
}

