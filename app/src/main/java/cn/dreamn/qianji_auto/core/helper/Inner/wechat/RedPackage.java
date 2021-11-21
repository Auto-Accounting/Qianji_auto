package cn.dreamn.qianji_auto.core.helper.Inner.wechat;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class RedPackage {

    public static String payTools = "";//支付方式
    public static String money = "";//支付金额
    public static String remark = "";//支付备注

    public static BillInfo run(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "微信红包", true);
        // Log.i("微信红包："+index+"长度"+nodeList.size());
        if (nodeList.size() > index + 1) {
            if (money.equals(BillTools.getMoney(nodeList.get(index + 1)))) {
                //红包有效
                BillInfo billInfo = new BillInfo();
                billInfo.setShopRemark(remark);
                billInfo.setMoney(money);
                billInfo.setAccountName("零钱");//此处无法识别
                billInfo.setShopAccount("微信红包");
                return billInfo;
            }
        }
        return null;
    }

    public static BillInfo runInDetail(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "等待对方领取", true);
        Log.i("微信红包：" + index + "长度" + nodeList.size() + nodeList.toString());
        if (index >= 2) {
            money = BillTools.getMoney(nodeList.get(index));
            if (nodeList.get(index - 2).equals("正在加载...")) {
                index++;
            }
            BillInfo billInfo = new BillInfo();
            billInfo.setShopRemark(nodeList.get(index - 1));
            billInfo.setMoney(money);
            billInfo.setAccountName("零钱");//此处无法识别
            billInfo.setShopAccount(nodeList.get(index - 2).replace("的红包", ""));
            return billInfo;
        }
        return null;
    }

    public static void findMoneyAndRemark(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "塞钱进红包", true);
        if (index > 3) {
            money = BillTools.getMoney(nodeList.get(index - 1));
            remark = nodeList.get(index - 3);
            Log.i("发红包捕获支付金额、支付备注：" + money + "-" + remark);

        }

    }

    public static BillInfo runReceiveInDetail(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "回复表情到聊天", true);
        Log.i("微信红包：" + index + "长度" + nodeList.size() + nodeList.toString());
        if (index >= 4) {
            money = BillTools.getMoney(nodeList.get(index - 2));
            BillInfo billInfo = new BillInfo();
            billInfo.setShopRemark(nodeList.get(index - 3));
            billInfo.setMoney(money);
            billInfo.setAccountName("零钱");//此处无法识别
            billInfo.setType(BillInfo.TYPE_INCOME);
            billInfo.setShopAccount(nodeList.get(index - 4).replace("的红包", ""));
            return billInfo;
        }
        return null;
    }
}
