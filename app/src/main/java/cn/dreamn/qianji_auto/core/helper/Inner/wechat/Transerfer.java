package cn.dreamn.qianji_auto.core.helper.Inner.wechat;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class Transerfer {

    public static String payTools = "";//支付方式
    public static String money = "";//支付金额
    public static String remark = "无";//支付备注

    public static BillInfo run(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "￥", true);
        // Log.i("微信红包："+index+"长度"+nodeList.size());
        if (index > 1) {
            money = BillTools.getMoney(nodeList.get(index));
            BillInfo billInfo = new BillInfo();
            billInfo.setShopRemark(remark);
            billInfo.setMoney(money);
            billInfo.setAccountName("零钱");//此处无法识别
            billInfo.setShopAccount(nodeList.get(index - 1).replace("向", "").replace("转账", ""));
            return billInfo;
        }
        return null;
    }


    public static void findRemark(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "转账说明", true);
        if (index + 3 < nodeList.size()) {
            remark = nodeList.get(index + 1);
            if (remark.equals("收付款双方可见，最多60个字。"))
                remark = "微信转账";
            Log.i("转账捕获支付备注：" + remark);
        }

    }

    public static BillInfo runInDetail(List<String> nodeList) {
        NodeListManage.clear();
        int index = NodeListManage.indexOf(nodeList, "1天内对方未收款，将退还给你。提醒对方收款", true);
        //Log.i("index："+index+"长度"+nodeList.size());
        if (index + 3 < nodeList.size() && index > 2) {
            BillInfo billInfo = new BillInfo();
            if (nodeList.get(index + 1).equals("转账说明") && index + 6 < nodeList.size()) {
                billInfo.setTimeStamp(DateUtils.dateToStamp(nodeList.get(index + 6), "yyyy年MM月dd日 HH:mm:ss"));
                billInfo.setShopRemark(nodeList.get(index + 3));
            } else {
                billInfo.setShopRemark("微信转账");
                billInfo.setTimeStamp(DateUtils.dateToStamp(nodeList.get(index + 1), "yyyy年MM月dd日 HH:mm:ss"));

            }
            money = BillTools.getMoney(nodeList.get(index - 1));
            billInfo.setMoney(money);
            billInfo.setAccountName("零钱");//此处无法识别
            billInfo.setShopAccount(nodeList.get(index - 2).replace("待", "").replace("收款", ""));
            return billInfo;
        }
        return null;
    }
}
