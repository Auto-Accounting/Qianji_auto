//
// Decompiled by FernFlower - 1459ms
//
package cn.dreamn.qianji_auto.core.helper.Inner;

import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class PddAndJDDetailParser extends baseAnalyze {
    public static int TYPE_JD = 0;
    public static int TYPE_PDD = 1;
    public static int BILL_INCOME = 0;
    public static int BILL_PAY = 1;
    public final int type;

    public PddAndJDDetailParser(int type) {
        this.type = type;
    }

    public BillInfo run(List<Object> nodeList, int payType) {
        boolean isIndex;
        StringBuilder sb;
        String nodeStr;
        if (this.type == TYPE_JD && nodeList.size() != 0) {
            BillInfo billInfo;
            Log.d("[auto] JdDetailParser parsePay " + payType + " " + nodeList.toString());
            if (payType == 1) {

                billInfo = new BillInfo();
                for (int index = 0; index < nodeList.size(); ++index) {
                    nodeStr = (String) nodeList.get(index);

                    if ("支付成功".equals(nodeStr) && index < nodeList.size() - 1) {
                        nodeStr = (String) nodeList.get(index + 1);
                        if (nodeStr != null) {
                            int length;
                            char[] strArray;
                            if (nodeStr.length() != 0) {
                                sb = new StringBuilder();
                                strArray = nodeStr.replace(",", "").toCharArray();
                                length = strArray.length;

                                for (int i = 0; i < length; ++i) {
                                    char c = strArray[i];
                                    if (c < '0' || c > '9') {
                                        if (c != '.') {
                                            if (sb.length() != 0) {
                                                break;
                                            }
                                            continue;
                                        }

                                        if (sb.length() == 0) {
                                            continue;
                                        }

                                        if (sb.indexOf(".") != -1) {
                                            break;
                                        }
                                    }
                                    sb.append(c);
                                }
                                nodeStr = sb.toString();
                            }
                        }
                        if (nodeStr != null && isMoney(nodeStr)) {
                            setMoney(billInfo, nodeStr);
                        }
                    } else if (nodeStr.contains("白条支付") || nodeStr.contains("充值面值")) {
                        billInfo.setAccountName(nodeStr);
                    }
                }
                if (isSetMoney) {
                    if (billInfo.getShopRemark().equals("")) {
                        billInfo.setShopRemark("京东订单");
                    }
                    return billInfo;
                }
            } else {
                billInfo = new BillInfo();
                for (int index = 0; index < nodeList.size(); ++index) {
                    nodeStr = (String) nodeList.get(index);
                    isIndex = index < nodeList.size() - 1;
                    if (("交易成功".equals(nodeStr) || "退款成功".equals(nodeStr) || "转出到账".equals(nodeStr)) && index > 0) {
                        String nodeStr2 = (String) nodeList.get(index - 1);
                        nodeStr2 = nodeStr2.replace("+", "").replace("-", "");
                        if (isMoney(nodeStr2)) {
                            setMoney(billInfo, nodeStr2);
                        }

                        if (index >= 2) {
                            billInfo.setShopRemark(((String) nodeList.get(index - 2)).replace("订单详情", ""));
                        }

                        if (nodeStr2.contains("+")) {
                            billInfo.setType(BillInfo.TYPE_INCOME);
                            billInfo.setShopAccount(nodeStr);
                        }
                    } else if ("创建时间：".equals(nodeStr) && isIndex) {
                        billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(index + 1), "yyyy-MM=dd HH:mm:ss"));
                    } else if (("支付方式：".equals(nodeStr) || "退款至：".equals(nodeStr)) && isIndex) {
                        billInfo.setAccountName(((String) nodeList.get(index + 1)).replace("微信-", ""));
                    } else if (nodeStr.endsWith("说明：") && isIndex) {
                        nodeStr = (String) nodeList.get(index + 1);
                        billInfo.setShopRemark(nodeStr);
                        if ("京东钱包余额充值".equals(nodeStr)) {
                            billInfo.setType(BillInfo.TYPE_INCOME);
                            billInfo.setAccountName2("京东钱包");
                        } else if ("京东钱包余额提现".equals(billInfo.getShopRemark())) {
                            billInfo.setType(BillInfo.TYPE_INCOME);
                            billInfo.setAccountName("京东钱包");
                        } else if ("京东小金库-转入".equals(billInfo.getShopRemark())) {
                            billInfo.setType(BillInfo.TYPE_INCOME);
                            nodeStr = "京东小金库";
                        }

                        billInfo.setAccountName2(nodeStr);
                    } else if ("还款详情：".equals(nodeStr)) {
                        billInfo.setType(BillInfo.TYPE_INCOME);
                        if (!TextUtils.isEmpty(billInfo.getShopRemark()) && billInfo.getShopRemark().contains("白条")) {
                            billInfo.setAccountName2("京东白条");
                        }
                    } else if ("商品：".equals(nodeStr) && isIndex) {
                        if (billInfo.getShopRemark().equals("")) {
                            nodeStr = (String) nodeList.get(payType + 1);
                        } else {
                            sb = new StringBuilder();
                            sb.append(billInfo.getShopRemark());
                            sb.append("-");
                            sb.append((String) nodeList.get(payType + 1));
                            nodeStr = sb.toString();
                        }
                        billInfo.setShopRemark(nodeStr);
                    } else if ("提现至：".equals(nodeStr) || "转出至：".equals(nodeStr) && isIndex) {
                        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                        nodeStr = (String) nodeList.get(payType + 1);
                        billInfo.setAccountName2(nodeStr);

                    }
                }

                if (isSetMoney) {
                    return billInfo;
                }
            }
            return null;
        } else if (nodeList.size() != 0) {
            Log.d("[auto] PddDetailParser parse type " + payType + " " + nodeList.toString());
            BillInfo billInfo = new BillInfo();
            for (int index = 0; index < nodeList.size(); ++index) {
                nodeStr = (String) nodeList.get(index);
                isIndex = index < nodeList.size() - 1;
                Log.d("[auto] 当前数据：  " + nodeStr);
                StringBuilder payType3;
                String s;
                if (payType == 4) {
                    s = nodeStr.replace("+", "").replace("-", "");
                    if (isMoney(s)) {
                        setMoney(billInfo, s);
                        if (nodeStr.contains("+")) {
                            billInfo.setType(BillInfo.TYPE_INCOME);
                            billInfo.setAccountName("多多钱包");
                        }
                        nodeStr = (String) nodeList.get(3);
                        billInfo.setShopRemark(nodeStr);
                                           /* if (!"余额提现".equals(nodeStr)) {
                                                if (!"余额充值".equals(billInfo.getShopRemark())) {
                                                    continue;
                                                }
                                            }*/
                    }
                } else if (("商品详情".equals(nodeStr) || "关联商品".equals(nodeStr)) && isIndex) {
                    if (!billInfo.getShopRemark().equals("")) {
                        payType3 = new StringBuilder();
                        payType3.append(billInfo.getShopRemark());
                        payType3.append("-");
                    }
                    nodeStr = (String) nodeList.get(index + 1);
                    billInfo.setShopRemark(nodeStr);
                } else if (("支付时间".equals(nodeStr) || "发起时间".equals(nodeStr) || "充值时间".equals(nodeStr)) && isIndex) {
                    billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(index + 1), "yyyy-MM-dd HH:mm:ss"));
                } else if ("支付方式".equals(nodeStr) && isIndex) {
                    s = (String) nodeList.get(index + 1);
                    nodeStr = s;
                    if (s.contains(":")) {
                        nodeStr = s.split(":")[0];
                    }
                    billInfo.setShopRemark(nodeStr);
                } else if ("提现至".equals(nodeStr) && isIndex) {
                    billInfo.setAccountName2((String) nodeList.get(index + 1));
                } else if ("充值金额".equals(nodeStr) && isIndex) {
                    nodeStr = ((String) nodeList.get(index + 1)).replace("¥", "");
                    if (isMoney(nodeStr)) {
                        setMoney(billInfo, nodeStr);
                    }

                } else if (!"提现金额".equals(nodeStr) || !isIndex) {
                    if ("充值方式".equals(nodeStr) && isIndex) {
                        nodeStr = (String) nodeList.get(index + 1);

                    }

                    if ("退款至".equals(nodeStr) && isIndex) {
                        billInfo.setAccountName((String) nodeList.get(index + 1));
                        if (TextUtils.isEmpty(billInfo.getShopRemark())) {
                            nodeStr = "退款";
                        } else {
                            payType3 = new StringBuilder();
                            payType3.append("退款-");
                            nodeStr = billInfo.getShopRemark();
                        }
                        billInfo.setShopRemark(nodeStr);
                    }
                }

                nodeStr = ((String) nodeList.get(index + 1)).replace("¥", "");
                if (isMoney(nodeStr)) {
                    setMoney(billInfo, nodeStr);
                }

                billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                billInfo.setAccountName("多多钱包");
                // nodeStr = "拼多多余额提现";
                //  break label375;
                           /* billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                            billInfo.setAccountName2("多多钱包");
                            nodeStr = "拼多多余额充值";

                            break label375;*/
                       /* payType3.append(nodeStr);
                        nodeStr = payType3.toString();*/

                //   billInfo.setShopRemark(nodeStr);


                //     billInfo.setAccountName(nodeStr);
            }

            if (isSetMoney) {
                return billInfo;
            }
        }

        return null;
    }
}

