package cn.dreamn.qianji_auto.core.helper.yimu;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class AutoBillDetail {
    public static long time;

    public static void goApp(Context context, BillInfo billInfo) {
        if (billInfo == null) {
            Log.i("Billinfo数据为空");
            return;
        }
        //
        // Log.i("Billinfo数据："+billInfo.toString());

        //防止出现多次识别
        if (System.currentTimeMillis() - time > 1000L) {
            time = System.currentTimeMillis();
            SendDataToApp.call(context, billInfo);
            //进行记账
        }
    }

    public static BillInfo alipay(List<Object> list) {
        int isRemarkFlag = 0;
        String account = "";
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        String name = "支付宝";
        billInfo.setAccountName("支付宝");
        int i = 0;
        int isRemark = 0;
        while (i < list.size()) {
            String remark = (String) list.get(i);
            if (remark.equals("转账备注")) {
                isRemark = 1;
            }

            if ((remark.equals("收款方")) && i < list.size() - 1) {
                billInfo.setRemark(((String) list.get(i + 1)));
                billInfo.setShopAccount(billInfo.getRemark());
                account = name;
                isRemarkFlag = isRemark;
                ++i;
                isRemark = isRemarkFlag;
                name = account;
            } else {
                if ((remark.equals("支付成功")) && i < list.size() - 2) {
                    String money = ((String) list.get(i + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                    if (BillTools.isMoney(money)) {
                        billInfo.setMoney(money);
                    }

                    String money2 = ((String) list.get(i + 2)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                    if (!BillTools.isMoney(money) && (BillTools.isMoney(money2))) {
                        billInfo.setMoney(money2);
                    } else if (!money2.equals("付款方式")) {
                        billInfo.setRemark(money2);
                        billInfo.setShopAccount(money2);
                    }

                }

                isRemarkFlag = isRemark;
                account = name;
                if ((
                        (remark.equals("有退款")) ||
                                (remark.equals("自动扣款成功")) ||
                                (remark.equals("已全额退款")) ||
                                (remark.equals("退款成功")) ||
                                (remark.equals("交易成功")) ||
                                (remark.equals("还款成功")) ||
                                (remark.equals("亲情卡付款成功")) ||
                                (remark.equals("等待对方发货")) ||
                                (remark.equals("等待确认收货")) ||
                                (remark.contains("付款成功"))
                ) &&
                        i > 0
                ) {
                    int size = i - 1;
                    String money3 = ((String) list.get(size)).
                            replace("￥", "").
                            replace("¥", "").
                            replace(",", "").
                            replace("+", "").
                            replace("元", "").
                            replace("-", "");
                    if (BillTools.isMoney(money3)) {
                        billInfo.setMoney(money3);
                    }

                    if (((String) list.get(size)).contains("+")) {
                        billInfo.setRemark("支付宝收款");
                        billInfo.setType(BillInfo.TYPE_INCOME);
                    } else if (i > 1) {
                        billInfo.setRemark(((String) list.get(i - 2)));
                    } else {
                        billInfo.setRemark("支付宝付款");
                    }

                    if (remark.equals("退款成功")) {
                        billInfo.setRemark("退款-来自" + billInfo.getRemark());
                        billInfo.setType(BillInfo.TYPE_INCOME);
                    }

                    billInfo.setShopAccount(billInfo.getRemark());
                } else if (
                        (TextUtils.isEmpty(billInfo.getMoney())) &&
                                ((remark.contains("￥")) ||
                                        (remark.contains("¥")) ||
                                        (remark.contains("-")) ||
                                        (remark.contains("+")))) {
                    String money = remark.replace("￥", "").
                            replace("¥", "").
                            replace(",", "").
                            replace("+", "");

                    if (!BillTools.isMoney(money) && i < list.size() - 1) {
                        money = ((String) list.get(i + 1)).
                                replace("￥", "").
                                replace("¥", "").
                                replace(",", "").
                                replace("+", "");
                    }
                    if (BillTools.isMoney(money)) {
                        billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                        if (remark.contains("+")) {
                            billInfo.setRemark("支付宝收款");
                            billInfo.setShopAccount(billInfo.getRemark());
                            billInfo.setType(BillInfo.TYPE_INCOME);
                        }
                    }

                    if ((TextUtils.isEmpty(billInfo.getRemark())) && i > 0) {
                        billInfo.setRemark(((String) list.get(i - 1)));
                        billInfo.setShopAccount(billInfo.getRemark());
                    }
                } else if (((remark.equals("付款方式")) ||
                        (remark.equals("付款信息"))) &&
                        i < list.size() - 1
                ) {
                    billInfo.setAccountName(((String) list.get(i + 1)));
                } else if ((remark.equals("创建时间")) &&
                        i < list.size() - 1
                ) {
                    billInfo.setTimeStamp(Tool.dateToStamp(((String) list.get(i + 1)), "yyyy-MM-dd HH:mm"));
                } else if ((remark.equals("商品说明")) && i < list.size() - 1) {
                    billInfo.setRemark(((String) list.get(i + 1)));
                }
            }

        }

        String accountName = account;
        if (!TextUtils.isEmpty(billInfo.getMoney()) && !TextUtils.isEmpty(billInfo.getAccountName()) && (TextUtils.isEmpty(billInfo.getRemark()))) {
            billInfo.setRemark("支付宝付款");
            billInfo.setShopAccount(billInfo.getRemark());
        }

        if (("账户余额".equals(billInfo.getAccountName())) || ("余额".equals(billInfo.getAccountName()))) {
            billInfo.setAccountName(accountName);
        }

        if (!TextUtils.isEmpty(billInfo.getRemark()) && !TextUtils.isEmpty(billInfo.getMoney())) {
            if (isRemark != 0 && !billInfo.getRemark().equals("支付宝收款")) {
                billInfo.setRemark("转账给" + billInfo.getRemark());
                billInfo.setShopAccount(billInfo.getRemark());
            }

            return billInfo;
        }

        return null;
    }

    public static BillInfo alipayTransfer(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("支付宝");
        int i;
        for (i = 0; i < list.size(); ++i) {
            String node = (String) list.get(i);
            if ((node.equals("收款方")) && i < list.size() - 1) {
                billInfo.setRemark("转账给" + list.get(i + 1));
                billInfo.setShopAccount(billInfo.getRemark());
            } else if ((node.equals("转账成功")) && i < list.size() - 2) {
                String money = ((String) list.get(i + 1)).
                        replace("￥", "").
                        replace("¥", "").
                        replace("元", "").
                        replace(",", "");
                if (!BillTools.isMoney(money)) {
                    money = ((String) list.get(i + 2)).
                            replace("￥", "").
                            replace("¥", "").
                            replace("元", "").
                            replace(",", "");
                }

                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(money);
                }
            } else if (node.equals("付款方式")) {
                billInfo.setAccountName(((String) list.get(i + 1)));
            }
        }

        if (!TextUtils.isEmpty(billInfo.getRemark()) &&
                !TextUtils.isEmpty(billInfo.getAccountName()) &&
                (TextUtils.isEmpty(billInfo.getRemark()))
        ) {
            billInfo.setRemark("支付宝转账");
            billInfo.setShopAccount(billInfo.getRemark());
        }

        if (("账户余额".equals(billInfo.getAccountName())) ||
                ("余额".equals(billInfo.getAccountName()))
        ) {
            billInfo.setAccountName("支付宝");
        }

        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo alipayRedPackage(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("支付宝");
        billInfo.setType(BillInfo.TYPE_INCOME);
        int i;
        for (i = 0; i < list.size(); ++i) {
            if ((list.get(i).equals("元")) && i > 2) {
                int j = i - 1;
                if (BillTools.isMoney(((String) list.get(j)).replace(",", ""))) {
                    billInfo.setMoney(((String) list.get(j)));
                    billInfo.setRemark(list.get(i - 3) + "的红包");
                    billInfo.setShopAccount(billInfo.getRemark());
                    return billInfo;
                }
            }
        }

        return null;
    }

    public static BillInfo wechatRedPackage(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("微信");
        billInfo.setType(BillInfo.TYPE_INCOME);
        int i;
        for (i = 0; i < list.size(); ++i) {
            String money = ((String) list.get(i)).replace("元", "").replace(",", "");
            if (BillTools.isMoney(money)) {
                billInfo.setMoney(money);
                if (i < list.size() - 1 && ((((String) list.get(i)).contains("元")) || (((String) list.get(i + 1)).contains("元")))) {
                    if (i > 1) {
                        int j = i - 2;
                        if (((String) list.get(j)).contains("的红包")) {
                            billInfo.setRemark(((String) list.get(j)));
                            billInfo.setShopAccount(billInfo.getRemark());
                            return billInfo;
                        }
                    }

                    if (i > 0) {
                        int j = i - 1;
                        if (((String) list.get(j)).contains("的红包")) {
                            billInfo.setRemark(((String) list.get(j)));
                            billInfo.setShopAccount(billInfo.getRemark());
                            return billInfo;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static BillInfo jdPay(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("京东金融");
        int i = 0;
        int j = 0;
        while (i < list.size()) {
            String node = (String) list.get(i);
            if (((node.equals("支出金额")) || (node.equals("订单金额"))) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if ((TextUtils.isEmpty(billInfo.getMoney())) && (BillTools.isMoney(money))) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }
            } else if ((node.equals("实付金额（元）")) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }
            } else if ((node.equals("收入金额")) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }

                billInfo.setType(BillInfo.TYPE_INCOME);
                j = 1;
            } else if ((node.equals("商户名称")) && i < list.size() - 1) {
                billInfo.setShopAccount(((String) list.get(i + 1)));
            } else if ((node.equals("卡号")) && i < list.size() - 1) {
                billInfo.setAccountName(((String) list.get(i + 1)).replace("尾号", ""));
            } else if ((node.equals("付款卡号")) && i < list.size() - 1) {
                String accountName = (String) list.get(i + 1);
                billInfo.setAccountName(accountName.substring(accountName.length() - 4));
                // i0.l(new Object[]{billInfo.getAccountName()});
            } else if (((node.equals("时间")) || (node.equals("订单时间"))) && i < list.size() - 1) {
                billInfo.setTimeStamp(Tool.dateToStamp(((String) list.get(i + 1)), "yyyy-MM-dd HH:mm"));
            } else if ((node.equals("分类")) && i < list.size() - 1) {
                billInfo.setRemark(((String) list.get(i + 1)));
            }

            ++i;
        }

        if (TextUtils.isEmpty(billInfo.getRemark())) {
            billInfo.setRemark(billInfo.getShopAccount());
        }

        if (j != 0) {
            billInfo.setShopAccount("收入-" + billInfo.getShopAccount());
        }

        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo redPackageSend(List<Object> list, boolean alipay) {
        BillInfo billInfo = new BillInfo();
        if (alipay) {
            billInfo.setFromApp("支付宝");
        } else {
            billInfo.setFromApp("微信");
        }

        int j;
        for (j = 0; j < list.size(); ++j) {
            String node = (String) list.get(j);
            if (node.contains("红包金额")) {
                int index = node.indexOf("元");
                if (index > 4) {
                    String money = node.substring(0, index).replace("红包金额", "").replace(",", "");
                    if (BillTools.isMoney(money)) {
                        billInfo.setMoney(money);
                        if (alipay) {
                            billInfo.setRemark("发送支付宝红包");
                        } else {
                            billInfo.setRemark("发送微信红包");
                        }

                        billInfo.setShopAccount(billInfo.getRemark());
                        return billInfo;
                    }
                }
            } else if (node.contains("个红包共")) {
                int index = node.indexOf("元");
                if (index > 4) {
                    String money = node.substring(node.indexOf("个红包共") + 4, index).replace(",", "");
                    if (BillTools.isMoney(money)) {
                        billInfo.setMoney(money);
                        billInfo.setRemark("发送微信红包");
                        billInfo.setShopAccount(billInfo.getRemark());
                        return billInfo;
                    }
                }
            } else if (node.contains("人已领取")) {
                int index = node.indexOf("元");
                if (index > 4) {
                    String money = node.substring(node.indexOf("人已领取") + 6, index).replace(",", "");
                    if (BillTools.isMoney(money)) {
                        billInfo.setMoney(money);
                        billInfo.setRemark("发送支付宝红包");
                        billInfo.setShopAccount(billInfo.getRemark());
                        return billInfo;
                    }
                }
            }
        }

        return null;
    }

    public static BillInfo unionPay(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("云闪付");
        int i = 0;
        int minSize = 0;
        while (i < list.size()) {
            String node = (String) list.get(i);
            if (((node.equals("支出金额")) || (node.equals("订单金额"))) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if ((TextUtils.isEmpty(billInfo.getMoney())) && (BillTools.isMoney(money))) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }
            } else if ((node.equals("实付金额（元）")) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }
            } else if ((node.equals("收入金额")) && i < list.size() - 1) {
                String money = ((String) list.get(i + 1)).replace("元", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }

                billInfo.setType(BillInfo.TYPE_INCOME);
                minSize = 1;
            } else if (((node.equals("商户名称")) || (node.equals("乘车线路"))) && i < list.size() - 1) {
                billInfo.setShopAccount(((String) list.get(i + 1)));
            } else if ((node.equals("交易类型")) && (TextUtils.isEmpty(billInfo.getShopAccount())) && i < list.size() - 1) {
                billInfo.setShopAccount(((String) list.get(i + 1)));
            } else if ((node.equals("卡号")) && i < list.size() - 1) {
                billInfo.setAccountName(((String) list.get(i + 1)).replace("尾号", ""));
            } else if ((node.equals("付款卡")) && i < list.size() - 1) {
                billInfo.setAccountName(((String) list.get(i + 1)));
            } else if ((node.equals("付款卡号")) && i < list.size() - 1) {
                String accountName = (String) list.get(i + 1);
                billInfo.setAccountName(accountName.substring(accountName.length() - 4));
            } else if (((node.equals("时间")) || (node.equals("订单时间")) || (node.equals("扣款时间"))) && i < list.size() - 1) {
                billInfo.setTimeStamp(Tool.dateToStamp(((String) list.get(i + 1)), "yyyy-MM-dd HH:mm"));
            } else if ((node.equals("分类")) && i < list.size() - 1) {
                billInfo.setRemark(((String) list.get(i + 1)));
            }

            ++i;
        }

        if (TextUtils.isEmpty(billInfo.getRemark())) {
            if (TextUtils.isEmpty(billInfo.getShopAccount())) {
                billInfo.setRemark(billInfo.getFromApp());
            } else {
                billInfo.setRemark(billInfo.getShopAccount());
            }
        }

        if (minSize != 0) {
            billInfo.setShopAccount("收入-" + billInfo.getShopAccount());
        }

        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo unionPayDetail(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("云闪付");
        int j;
        for (j = 0; j < list.size(); ++j) {
            String node = (String) list.get(j);
            if ((TextUtils.isEmpty(billInfo.getMoney())) && (node.contains("¥"))) {
                String money = node.replace("¥", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(money)) + "");
                }
            } else if ((node.equals("订单信息")) && j < list.size() - 1) {
                int i = j + 1;
                billInfo.setRemark(((String) list.get(i)));
                billInfo.setShopAccount(((String) list.get(i)));
            } else if ((node.equals("付款方式")) && j < list.size() - 1) {
                int index1 = j + 1;
                billInfo.setAccountName(((String) list.get(index1)));
                if (j < list.size() - 3) {
                    int index2 = j + 3;
                    if ((((String) list.get(index2)).contains("[")) && (((String) list.get(index2)).contains("]"))) {
                        billInfo.setAccountName(list.get(index1) + ((String) list.get(j + 2)) + list.get(index2));
                    } else {
                        int index3 = j + 2;
                        if ((((String) list.get(index3)).contains("[")) && (((String) list.get(index3)).contains("]"))) {
                            billInfo.setAccountName(list.get(index3) + ((String) list.get(index3)));
                        }
                    }
                } else if (j < list.size() - 2) {
                    int index4 = j + 2;
                    if ((((String) list.get(index4)).contains("[")) && (((String) list.get(index4)).contains("]"))) {
                        billInfo.setAccountName(list.get(index1) + ((String) list.get(index4)));
                    }
                }
            }
        }

        billInfo.setTimeStamp();
        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo wechatDetail(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("微信");
        int i;
        for (i = 0; i < list.size(); ++i) {
            String node = (String) list.get(i);
            if ((node.contains("-")) || (node.contains("+"))) {
                String index = node.replace("+", "").replace("-", "");
                if (BillTools.isMoney(index)) {
                    billInfo.setMoney(Math.abs(Double.parseDouble(index)) + "");
                    if (i > 0) {
                        billInfo.setRemark(((String) list.get(i - 1)));
                        billInfo.setShopAccount(billInfo.getRemark());
                    }

                    if (node.contains("+")) {
                        billInfo.setType(BillInfo.TYPE_INCOME);
                    }
                }
            } else if (((node.equals("支付时间")) || (node.equals("转账时间")) || (node.equals("收款时间"))) && i < list.size() - 1) {
                billInfo.setTimeStamp(Tool.dateToStamp(((String) list.get(i + 1)), "yyyy-MM-dd HH:mm:ss"));
            } else if ((node.equals("支付方式")) && i < list.size() - 1) {
                billInfo.setAccountName(((String) list.get(i + 1)));
            } else if ((node.equals("商品")) && i < list.size() - 1) {
                billInfo.setRemark(((String) list.get(i + 1)));
            }
        }

        if ("零钱".equals(billInfo.getAccountName())) {
            billInfo.setAccountName("微信");
        }

        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo wechatDetailTransfer(List<Object> list, String payTools) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("微信");
        int i;
        for (i = 0; i < list.size(); ++i) {
            String node = (String) list.get(i);
            String money = node.replace("￥", "").replace("¥", "").replace(",", "");
            if ((TextUtils.isEmpty(billInfo.getMoney())) && (BillTools.isMoney(money))) {
                billInfo.setMoney(money);
            } else if ((node.equals("收款方")) && i < list.size() - 1) {
                billInfo.setRemark(((String) list.get(i + 1)));
                billInfo.setShopAccount(billInfo.getRemark());
            } else if ((node.equals("支付成功")) && i < list.size() - 2) {
                int index = i + 1;
                if (!((String) list.get(index)).contains("¥")) {
                    billInfo.setRemark(((String) list.get(index)));
                    billInfo.setShopAccount(billInfo.getRemark());
                    if ((billInfo.getRemark().startsWith("待")) && (billInfo.getRemark().endsWith("确认收款"))) {
                        billInfo.setRemark("转账给" + billInfo.getRemark().substring(1, billInfo.getRemark().length() - 4));
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(payTools)) {
            billInfo.setAccountName(payTools);
        }

        if ("零钱".equals(billInfo.getAccountName())) {
            billInfo.setAccountName("微信");
        }

        return (TextUtils.isEmpty(billInfo.getRemark())) || (TextUtils.isEmpty(billInfo.getMoney())) ? null : billInfo;
    }

    public static BillInfo wechatDetailReceiveTransfer(List<Object> list) {
        if (list.size() == 0) {
            return null;
        }

        BillInfo billInfo = new BillInfo();
        billInfo.setFromApp("微信");
        int j;
        for (j = 0; j < list.size(); ++j) {
            String node = (String) list.get(j);
            if (((node.equals("已收款")) || (node.equals("你已收款"))) && j < list.size() - 2) {
                String money = ((String) list.get(j + 1)).replace("￥", "").replace("¥", "").replace("元", "").replace(",", "");
                if (BillTools.isMoney(money)) {
                    billInfo.setMoney(money);
                }
            }
        }

        if (!TextUtils.isEmpty(billInfo.getMoney())) {
            billInfo.setRemark("微信收款");
            billInfo.setShopAccount(billInfo.getRemark());
            billInfo.setType(BillInfo.TYPE_INCOME);
            return billInfo;
        }

        return null;
    }
}

