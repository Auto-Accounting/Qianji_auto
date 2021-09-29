//
// Decompiled by FernFlower - 970ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class RedDetailParser extends baseAnalyze {
    public boolean alipay;
    public boolean alipay2;

    public BillInfo run(List<Object> nodeList) {
        if (nodeList.size() == 0) {
            return null;
        } else {
            BillInfo billinfo;
            String nodeStr;
            {
                Log.i("[auto]RedDetailParser parseIncome:" + nodeList.toString());
                billinfo = new BillInfo();
                int index = 0;
                int mIndex;
                Log.i("[auto]alipay：" + (alipay ? "true" : "false"));
                if (alipay) {
                    while (true) {
                        if (index >= nodeList.size()) {
                            break;
                        }
                        Log.i("[auto]当前遍历数据：" + (String) nodeList.get(index));
                        if ("元".equals((String) nodeList.get(index)) && index > 2) {

                            if (isMoney(((String) nodeList.get(index - 1)).replace(",", ""))) {
                                setMoney(billinfo, (String) nodeList.get(index - 1));
                                nodeStr = (String) nodeList.get(index - 3) + "的红包";
                                billinfo.setShopAccount(nodeStr.replace("的红包", ""));
                                billinfo.setShopRemark(nodeStr);
                                Log.i("[auto]当前账单数据" + billinfo.toString());
                                break;
                            }
                        }
                        ++index;
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
                        if (index >= nodeList.size()) {
                            break;
                        }

                        String m = ((String) nodeList.get(index)).replace("元", "").replace(",", "");
                        Log.i("[auto]金额" + m);
                        if (isMoney(m)) {
                            Log.i("[auto]金额" + m);
                            setMoney(billinfo, m);
                            if (index >= nodeList.size() - 1 || !((String) nodeList.get(index)).contains("元") && !((String) nodeList.get(index + 1)).contains("元")) {
                                if (alipay2 && index >= 2) {
                                    mIndex = index - 2;
                                    if (((String) nodeList.get(mIndex)).contains("的红包")) {
                                        index = mIndex;
                                        break;
                                    }
                                }
                            } else {
                                if (index > 1) {
                                    mIndex = index - 2;
                                    if (((String) nodeList.get(mIndex)).contains("的红包")) {
                                        index = mIndex;
                                        break;
                                    }
                                }

                                if (index > 0) {
                                    mIndex = index - 1;
                                    if (((String) nodeList.get(mIndex)).contains("的红包")) {
                                        index = mIndex;
                                        break;
                                    }
                                }

                            }
                        }
                        ++index;
                    }
                    if (index < nodeList.size()) {
                        nodeStr = (String) nodeList.get(index);
                        Log.i("[auto]当前备注数据" + nodeStr);
                        billinfo.setShopAccount(nodeStr.replace("的红包", ""));
                        billinfo.setShopRemark(nodeStr);
                        Log.i("[auto]当前账单数据" + billinfo.toString());
                    }


                }

            }

            if (!isSetMoney) {
                return null;
            } else {
                if (!alipay && !alipay2) {
                    nodeStr = "零钱";
                } else {
                    nodeStr = "余额";
                }
                billinfo.setAccountName(nodeStr);
                billinfo.setType(BillInfo.TYPE_INCOME);
                return billinfo;
            }
        }
    }
}

