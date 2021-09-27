//
// Decompiled by FernFlower - 970ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class analyze_2 extends baseAnalyze {
    public boolean b;
    public boolean c;

    public BillInfo h(List<Object> nodeList) {
        if (nodeList.size() == 0) {
            return null;
        } else {
            BillInfo billinfo;
            String var8;
            label102:
            {
                Log.i("[auto]RedDetailParser parseIncome:" + nodeList.toString());
                billinfo = new BillInfo();
                boolean var4 = this.b;
                int var2 = 0;
                int var9;
                Log.i("[auto]var4：" + (this.b ? "true" : "false"));
                if (var4) {
                    while (true) {
                        if (var2 >= nodeList.size()) {
                            break label102;
                        }

                        Log.i("[auto]当前遍历数据：" + (String) nodeList.get(var2));
                        if ("元".equals((String) nodeList.get(var2)) && var2 > 2) {
                            var9 = var2 - 1;
                            if (isMoney(((String) nodeList.get(var9)).replace(",", ""))) {
                                setMoney(billinfo, (String) nodeList.get(var9));
                                var8 = (String) nodeList.get(var2 - 3) + "的红包";
                                break;
                            }
                        }

                        ++var2;
                    }
                } else {
                    if (NodeListManage.checkNode(nodeList, "个红包", false)) {
                        Log.i("[auto]红包数量");
                        if (NodeListManage.checkNode(nodeList, "被抢光", false)) {
                            if (!NodeListManage.checkNode(nodeList, "已存入零钱", false)) {
                                Log.d("[auto]群红包没有抢到");
                                return null;
                            }
                        }
                    }

                    while (true) {
                        if (var2 >= nodeList.size()) {
                            break label102;
                        }

                        String var7 = ((String) nodeList.get(var2)).replace("元", "").replace(",", "");
                        Log.i("[auto]金额" + var7);
                        if (isMoney(var7)) {
                            Log.i("[auto]金额" + var7);
                            setMoney(billinfo, var7);
                            if (var2 >= nodeList.size() - 1 || !((String) nodeList.get(var2)).contains("元") && !((String) nodeList.get(var2 + 1)).contains("元")) {
                                if (this.c && var2 >= 2) {
                                    var9 = var2 - 2;
                                    if (((String) nodeList.get(var9)).contains("的红包")) {
                                        var2 = var9;
                                        break;
                                    }
                                }
                            } else {
                                if (var2 > 1) {
                                    var9 = var2 - 2;
                                    if (((String) nodeList.get(var9)).contains("的红包")) {
                                        var2 = var9;
                                        break;
                                    }
                                }

                                if (var2 > 0) {
                                    var9 = var2 - 1;
                                    if (((String) nodeList.get(var9)).contains("的红包")) {
                                        var2 = var9;
                                        break;
                                    }
                                }
                            }
                        }

                        ++var2;
                    }

                    var8 = (String) nodeList.get(var2);

                    Log.i("[auto]当前备注数据" + var8);
                }
                billinfo.setShopAccount(var8.replace("的红包", ""));
                billinfo.setShopRemark(var8);
                Log.i("[auto]当前账单数据" + billinfo.toString());
            }

            if (!super.a) {
                return null;
            } else {
                if (!this.b && !this.c) {
                    var8 = "微信钱包";
                } else {
                    var8 = "支付宝";
                }

                billinfo.setAccountName(var8);
                billinfo.setType(BillInfo.TYPE_INCOME);
                return billinfo;
            }
        }
    }
}

