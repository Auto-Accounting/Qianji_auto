package cn.dreamn.qianji_auto.core.helper.Inner;

import android.content.Context;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class NodeListManage {
    public static final int FLAG_WECHAT_PAY_UI = 1;
    //微信支付界面
    public static final int FLAG_ALIPAY_PAY_UI = 2;
    //支付宝支付界面
    public static final int FLAG_WECHAT_PAY_MONEY_UI = 3;
    //微信账单详情
    public static final int FLAG_UNION_PAY_UI = 4;
    //云闪付支付界面
    public static final int FLAG_UNION_PAY_DETAIL_UI = 5;
    //云闪付账单详情
    public static final int FLAG_ALIPAY_PAY_DETAIL_UI = 6;
    //支付宝账单详情
    public static final int FLAG_MT_PAY_DETAIL_UI = 7;
    //美团账单详情
    public static final int FLAG_JD_PAY_DETAIL_UI = 8;
    //京东账单详情
    public static final int FLAG_JD_PAY_UI = 9;
    //京东支付界面
    public static final int FLAG_PDD_DETAIL_UI = 10;
    //拼多多账单详情
    public static final int FLAG_NO_USE = -1;
    public static int flag = FLAG_NO_USE;//目前检测不为使用状态
    public static List<String> globalNodeList = new ArrayList<>();//数据列表
    public static long time;
    public static String packageName = "";
    public static int listIndex = 0;
    public static String nodeData = "";
    private static String className = "";

    public static void setFlag(int Flag) {
        clear();//每次设置flag就是有新的目标出现
        flag = Flag;
    }

    public static void clear() {
        globalNodeList = new ArrayList<>();
        flag = FLAG_NO_USE;
    }

    public static void goApp(Context context, BillInfo billInfo) {
        clear();//执行goApp后清空
        if (billInfo == null) {
            Log.i("Billinfo数据为空");
            return;
        }
        //防止出现多次识别
        if (System.currentTimeMillis() - time > 1000L) {
            time = System.currentTimeMillis();
            SendDataToApp.call(context, billInfo);
            //进行记账
        }
    }

    public static void updateEvent(String pkg, String cls) {
        className = cls;
        packageName = pkg;
    }

    //验证节点
    public static boolean checkNode(List<String> nodeList, String checkStr, boolean equals) {
        //从最后的数据开始查找
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            String nodeName = (String) nodeList.get(i);
            if (equals) {
                if (!nodeName.equals(checkStr)) {
                    continue;
                }
                return true;
            }
            if (!nodeName.contains(checkStr)) {
                continue;
            }
            return true;
        }
        return false;
    }

    //验证节点
    public static int indexOf(List<String> nodeList, String checkStr, boolean equals) {
        //从最后的数据开始查找
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            String nodeName = (String) nodeList.get(i);
            if (equals) {
                if (!nodeName.contains(checkStr)) {
                    continue;
                }
                return i;
            }
            if (!nodeName.contains(checkStr)) {
                continue;
            }
            return i;
        }
        return -1;
    }

    public static boolean isNeedPage(String[] text, String cls, String pkg) {
        if (cls != null) {
            if (!cls.contains(className)) return false;
        }
        if (pkg != null) {
            if (!pkg.contains(packageName)) return false;
        }

        if (text != null) {
            for (String s : text) {
                if (!NodeListManage.checkNode(NodeListManage.globalNodeList, s, false))
                    return false;
            }
        }


        return true;
    }


    public static void findNodeInfo(AccessibilityNodeInfo nodeInfo, boolean isFirst) {

        if (isFirst) {
            listIndex = 0;
        }
        if (nodeInfo == null) return;
        int i = listIndex + 1;
        listIndex = i;
        if (i <= 100) {
            if (globalNodeList.size() > 200) {
                globalNodeList.remove(0);
            }
            for (i = 0; i < nodeInfo.getChildCount(); ++i) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (childNodeInfo != null && childNodeInfo.getChildCount() > 0) {
                    findNodeInfo(childNodeInfo, false);
                } else if (childNodeInfo != null && !TextUtils.isEmpty(childNodeInfo.getText())) {
                    if (childNodeInfo.getContentDescription() != null) {
                        globalNodeList.add(childNodeInfo.getContentDescription().toString());
                    }
                    globalNodeList.add(childNodeInfo.getText().toString());
                }
            }
        }
    }

    public static boolean isNullOrEmpty(List<String> list) {
        return list == null || list.size() == 0;
    }

}
