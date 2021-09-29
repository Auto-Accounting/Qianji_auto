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
    public static int TYPE_WECHAT_GROUP = 1;
    public static int TYPE_WECHAT_TRANSFER = 2;
    public static int TYPE_WECHAT_REC = 3;
    public static int TYPE_WECHAT_OTHER = 0;
    public String payTools;
    public boolean wechatTransferLqt;

    public BillInfo run(List<Object> nodeList, int type) {
        if (nodeList.size() == 0) return null;
        Log.d("[auto] WxDetailParser parse type" + type + " " + nodeList.toString());

        String replaceStr;
        String nodeStr;
        if (type == TYPE_WECHAT_GROUP) {
            BillInfo billinfo = new BillInfo();
            int index = 0;
            while (index < nodeList.size()) {
                nodeStr = (String) nodeList.get(index);
                replaceStr = nodeStr.replace("￥", "").replace("¥", "").replace(",", "");
                if (isMoney(replaceStr)) {
                    setMoney(billinfo, replaceStr);
                    if (wechatTransferLqt) {
                        billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                        billinfo.setAccountName2("零钱通");
                        billinfo.setShopRemark("转入零钱通");
                        break;
                    }
                } else {
                    if ("收款方".equals(nodeStr) && index < nodeList.size() - 1) {
                        Log.i("收款方:" + nodeStr);
                        billinfo.setShopAccount((String) nodeList.get(index + 1));
                    } else {
                        if (!"支付成功".equals(nodeStr) || index >= nodeList.size() - 2) {
                            if (((String) nodeList.get(0)).endsWith("发起的群收款") && nodeStr.contains("已收齐") && index < nodeList.size() - 1) {
                                replaceStr = ((String) nodeList.get(index + 1)).replace("收到¥", "");
                                billinfo.setType(BillInfo.TYPE_INCOME);
                                if (isMoney(replaceStr)) {
                                    setMoney(billinfo, replaceStr);
                                    billinfo.setShopRemark("我发起的群收款-已收齐");
                                    break;
                                }
                            } else if (((String) nodeList.get(0)).endsWith("发起的群收款") && nodeStr.contains("已支付")) {
                                replaceStr = replaceStr.replace("已支付", "");
                                if (isMoney(replaceStr)) {
                                    setMoney(billinfo, replaceStr);
                                    billinfo.setShopRemark((String) nodeList.get(0));
                                    break;
                                }
                            } else if ("充值成功".equals(nodeStr)) {
                                billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                billinfo.setAccountName2("微信");
                                billinfo.setShopRemark("微信零钱充值");
                                break;
                            }
                        } else if (!((String) nodeList.get(index + 1)).contains("¥")) {
                            billinfo.setShopRemark((String) nodeList.get(index + 1));
                        }
                    }
                }
                ++index;
            }

            if (!TextUtils.isEmpty(payTools)) {
                billinfo.setAccountName(payTools);
            }

            if (isSetMoney) {
                return billinfo;
            } else {
                return null;
            }
        } else if (type == TYPE_WECHAT_REC) {
            BillInfo billInfo = new BillInfo();
            for (int index = 0; index < nodeList.size(); ++index) {
                String node = (String) nodeList.get(index);
                if (("已收款".equals(node) || "你已收款".equals(node)) && index < nodeList.size() - 2) {
                    node = ((String) nodeList.get(index + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                    if (isMoney(node)) {
                        setMoney(billInfo, node);
                    }
                } else if ("收款时间".equals(node) && index < nodeList.size() - 1) {
                    billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(index + 1), "yyyy年MM月dd日 hh:mm:ss"));
                }
            }

            if (isSetMoney) {
                billInfo.setShopRemark("微信收款");
                billInfo.setAccountName("零钱");
                billInfo.setType(BillInfo.TYPE_INCOME);
                return billInfo;
            } else {
                return null;
            }

        } else if (type == TYPE_WECHAT_TRANSFER) {
            BillInfo billInfo = new BillInfo();

            for (int index = 0; index < nodeList.size(); ++index) {
                String node = (String) nodeList.get(index);
                node = node.replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                if (isMoney(node)) {
                    setMoney(billInfo, node);
                } else if ("转账时间".equals(node) && index < nodeList.size() - 1) {
                    billInfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(index + 1), "yyyy年MM月dd日 hh:mm:ss"));
                } else if ("转账说明".equals(node) && index < nodeList.size() - 1) {
                    billInfo.setShopRemark((String) nodeList.get(index + 1));
                } else if (node.startsWith("待") && node.endsWith("收款")) {
                    billInfo.setShopAccount(node.substring(1, node.lastIndexOf("收款")));
                }
            }

            if (isSetMoney) {

                if (billInfo.getShopRemark().equals("")) {
                    billInfo.setShopRemark("微信转账");
                }
                if (billInfo.getAccountName().equals("")) {
                    billInfo.setAccountName("零钱");
                }
                return billInfo;
            }

            return null;
        } else {
            BillInfo billinfo = new BillInfo();
            for (int index = 0; index < nodeList.size(); index++) {
                String node = (String) nodeList.get(index);
                boolean indexLast = index < nodeList.size() - 1;

                if (!isSetMoney) {
                    if (node.contains("转入") || node.contains("转出") || node.contains("还款") || node.contains("零钱充值") || node.contains("零钱提现") || "提现金额".equals(node)) {
                        int j = index + 1;
                        if (j < nodeList.size()) {
                            replaceStr = ((String) nodeList.get(j)).replace("¥", "").replace("￥", "");
                            if (isMoney(replaceStr)) {
                                setMoney(billinfo, replaceStr);
                                billinfo.setShopRemark(node);
                                billinfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
                                int i = node.indexOf("-");
                                if ("提现金额".equals(node)) {
                                    billinfo.setAccountName("零钱");
                                    billinfo.setShopAccount("微信");
                                    billinfo.setShopRemark("零钱提现");
                                } else if (i != -1) {
                                    if (node.startsWith("转入")) {
                                        billinfo.setAccountName2(node.substring(2, i));
                                        billinfo.setAccountName(node.substring(i + 3));
                                        break;
                                    } else if (node.contains("转出")) {
                                        billinfo.setAccountName(node.substring(0, node.indexOf("转出")));
                                        billinfo.setShopRemark(node.substring(i + 2));
                                        break;
                                    } else if (node.contains("还款")) {
                                        String[] strs = node.split("还款");
                                        if (strs.length >= 2) {
                                            billinfo.setShopRemark(strs[1].substring(1));
                                            break;
                                        }
                                    } else if (node.contains("零钱充值")) {
                                        billinfo.setAccountName2("零钱");
                                        break;
                                    } else if (node.contains("零钱提现")) {
                                        billinfo.setAccountName("零钱");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (!node.contains("-") && !node.contains("+")) {
                    if ("支付时间".equals(node) || "转账时间".equals(node) || "收款时间".equals(node)) {
                        if (index < nodeList.size() - 1) {
                            billinfo.setTimeStamp(Tool.dateToStamp((String) nodeList.get(index + 1), "yyyy-MM-dd HH:mm:ss"));
                        }
                    } else {
                        if (billinfo.getAccountName() != null || !"支付方式".equals(node) && !"收款帐号".equals(node) && !"退款方式".equals(node) || !indexLast) {
                            if ("商品".equals(node) && indexLast) {
                                billinfo.setShopRemark((String) nodeList.get(index + 1));
                            } else {
                                if (!"转账说明".equals(node) || !indexLast) {
                                    if (("提现银行".equals(node) || "到账银行卡".equals(node)) && indexLast) {
                                        billinfo.setAccountName2((String) nodeList.get(index + 1));
                                        if (billinfo.getAccountName() == null && wechatTransferLqt) {
                                            billinfo.setAccountName("零钱通");
                                        }
                                    } else if ("服务费".equals(node) && indexLast) {
                                        String m = ((String) nodeList.get(index + 1)).replace("￥", "");
                                        if (isMoney(m)) {
                                            billinfo.setFee(m);
                                        }
                                    }
                                } else if (billinfo.getShopRemark() != null) {
                                    billinfo.setShopRemark(billinfo.getShopRemark() + "-" + (String) nodeList.get(index + 1));
                                }
                            }
                        }

                        billinfo.setAccountName((String) nodeList.get(index + 1));
                        if (node.contains("收")) {
                            billinfo.setType(BillInfo.TYPE_INCOME);
                        }
                    }
                } else {
                    String m = node.replace("+", "").replace("-", "");
                    if (isMoney(m)) {
                        setMoney(billinfo, m);
                    }
                    if (index > 0) {
                        billinfo.setShopRemark((String) nodeList.get(index - 1));
                    }

                    if (node.contains("+")) {
                        billinfo.setType(BillInfo.TYPE_INCOME);
                    }

                }
            }

            if (isSetMoney) {
                if (billinfo.getTypeInt() == 1 && billinfo.getAccountName() == null) {
                    billinfo.setAccountName("零钱");
                }
                return billinfo;
            } else {
                return null;
            }
        }


    }
}

