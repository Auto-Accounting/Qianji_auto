package cn.dreamn.qianji_auto.core.helper.inner;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.core.helper.AutoBillService;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class AutoAccessibilityService extends AccessibilityService {
    private static final int WECHAT_DETAIL_TRANSFER = 1;
    private static final int ALIPAY = 2;
    private static final int ALIPAY_DETAIL = 6;
    private static final int WECHAT_DETAIL = 3;
    private static final int UNION_PAY_DETAIL = 4;
    private static final int UNION_PAY = 5;
    private static final int JD_JR = 7;
    String payTools;
    private boolean canAdd;
    private int flag;

    public AutoAccessibilityService() {
        canAdd = false;
        payTools = null;
    }

    private boolean isServiceRunning(String ServicePackageName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ServicePackageName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //验证节点
    private boolean checkNode(List<Object> nodeList, String checkStr, boolean equals) {
        for (Object node : nodeList) {
            String nodeName = (String) node;
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

    //获取当前节点信息
    private List<Object> getNodes(AccessibilityNodeInfo accessibilityNodeInfoMain) {
        List<Object> arrayList = new ArrayList<>();
        int i;
        for (i = 0; i < accessibilityNodeInfoMain.getChildCount(); ++i) {
            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoMain.getChild(i);
            if (accessibilityNodeInfo != null && accessibilityNodeInfo.getChildCount() > 0) {
                if (arrayList.size() > 999) {
                    return arrayList;
                }
                arrayList.addAll(getNodes(accessibilityNodeInfo));
            } else if (accessibilityNodeInfo != null && !TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
                Log.i("nodeInfo:" + accessibilityNodeInfo.getText());
                arrayList.add(accessibilityNodeInfo.getText().toString());
            }
        }

        return arrayList;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        BillInfo billinfo;
        boolean redPayMoeny;
        AutoAccessibilityService autoAccessibilityService = this;
        //判断服务运行
        if ((isServiceRunning(AutoBillService.class.getName())) && (AutoBillService.isStart)) {
            String className = "";
            String packageName = accessibilityEvent.getPackageName() == null ? "" : accessibilityEvent.getPackageName().toString();
            if (accessibilityEvent.getClassName() != null) {
                className = accessibilityEvent.getClassName().toString();
            }

            int eventType = accessibilityEvent.getEventType();
            Log.i("packageName:" + packageName);
            Log.i("className:" + className);
            Log.i("eventType:" + eventType);
            if ((className.startsWith("com.tencent")) || (className.startsWith("com.alipay"))) {
                Log.d("className:" + accessibilityEvent.getClassName());
            }

            AccessibilityNodeInfo nodeSource = accessibilityEvent.getSource();
            //微信支付
            if (
                    (className.equals("com.tencent.mm.plugin.remittance.ui.RemittanceBusiUI")) ||
                            (className.equals("com.tencent.mm.plugin.remittance.ui.RemittanceUI")) ||
                            (className.equals("com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI")) ||
                            (className.equals("com.tencent.mm.plugin.wallet_index.ui.WalletBrandUI")) ||
                            (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI")) ||
                            (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) ||
                            (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI")) ||
                            (className.equals("com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI")) ||
                            (className.equals("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI")) ||
                            (packageName.equals("com.tencent.mm")) &&
                                    nodeSource != null &&
                                    nodeSource.findAccessibilityNodeInfosByText("支付成功").size() > 0 &&
                                    nodeSource.findAccessibilityNodeInfosByText("查看账单详情").size() == 0 &&
                                    nodeSource.findAccessibilityNodeInfosByText("订单支付成功通知").size() == 0) {
                flag = WECHAT_DETAIL_TRANSFER;
                canAdd = true;
                Log.d("set can add true one");
            } else if (
                    (packageName.equals("com.tencent.mm")) &&
                            nodeSource != null &&
                            nodeSource.findAccessibilityNodeInfosByText("账单详情").size() > 0 &&
                            nodeSource.findAccessibilityNodeInfosByText("查看账单详情").size() == 0
            ) {
                flag = WECHAT_DETAIL;
                canAdd = true;
                Log.d("set can add true two");
            } else if (
                    (className.equals("com.alipay.android.msp.ui.views.MspContainerActivity")) ||
                            (className.equals("com.alipay.android.msp.ui.views.MspUniRenderActivity")) ||
                            (className.equals("com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity")) ||
                            (packageName.equals("com.eg.android.AlipayGphone")) &&
                                    nodeSource != null &&
                                    nodeSource.findAccessibilityNodeInfosByText("向商家付钱").size() > 0
            ) {
                flag = ALIPAY;
                canAdd = true;
                Log.d("set can add true three");
            } else if (
                    (packageName.equals("com.eg.android.AlipayGphone")) &&
                            nodeSource != null &&
                            nodeSource.findAccessibilityNodeInfosByText("账单详情").size() > 0
            ) {
                flag = ALIPAY_DETAIL;
                canAdd = true;
                Log.d("set can add true three");
            } else if (
                    (className.equals("com.unionpay.activity.payment.UPActivityScan")) ||
                            (className.equals("com.unionpay.activity.payment.UPActivityPaymentQrCodeOut"))
            ) {
                flag = UNION_PAY_DETAIL;
                canAdd = true;
                Log.d("set can add true three");
            } else if (
                    (packageName.equals("com.unionpay")) &&
                            nodeSource != null &&
                            (nodeSource.findAccessibilityNodeInfosByText("查看账单").size() > 0 ||
                                    (className.equals("com.unionpay.cordova.UPActivityWeb")) ||
                                    (className.equals("android.view.ViewGroup")) &&
                                            nodeSource.findAccessibilityNodeInfosByText("交易记录").size() > 0 &&
                                            nodeSource.findAccessibilityNodeInfosByText("筛选").size() > 0 ||
                                    nodeSource.findAccessibilityNodeInfosByText("动账通知").size() > 0 &&
                                            nodeSource.findAccessibilityNodeInfosByText("支付助手").size() > 0)
            ) {
                flag = UNION_PAY;
                canAdd = true;
                Log.d("set can add true three");
            } else if (
                    (packageName.equals("com.jd.jrapp")) &&
                            nodeSource != null &&
                            nodeSource.findAccessibilityNodeInfosByText("账单详情").size() > 0
            ) {
                flag = JD_JR;
                canAdd = true;
                Log.d("set can add true three");
            } else if (
                    (className.equals("com.unionpay.activity.UPActivityMain")) ||
                            (className.equals("com.alipay.mobile.bill.list.ui.BillMainListActivity")) ||
                            (className.equals("com.tencent.mm.ui.LauncherUI")) ||
                            (className.equals("com.eg.android.AlipayGphone.AlipayLogin"))
            ) {
                canAdd = false;
                payTools = null;
            }

            if (
                    nodeSource != null &&
                            (canAdd)
            ) {
                Log.d("start find node");
                List<Object> nodes = autoAccessibilityService.getNodes(nodeSource);
                if (nodes.size() > 0) {
                    boolean nodeResult = checkNode(nodes, "支付成功", true);
                    if (flag == WECHAT_DETAIL_TRANSFER) {
                        if (checkNode(nodes, "支付方式", true)) {
                            int nodeIndex = nodes.indexOf("支付方式");
                            if (nodeIndex < nodes.size() - 1) {
                                payTools = (String) nodes.get(nodeIndex + 1);
                            }
                        }

                        if (checkNode(nodes, "优先使用此支付方式付款", true)) {
                            int nodeIndex = nodes.indexOf("优先使用此支付方式付款") - 1;
                            if (nodeIndex >= 0) {
                                payTools = (String) nodes.get(nodeIndex);
                            }
                        }
                    }

                    if (!nodeResult && flag == WECHAT_DETAIL) {
                        nodeResult = true;
                    }

                    if (!nodeResult && flag == ALIPAY_DETAIL) {
                        nodeResult = true;
                    }

                    if (!nodeResult && flag == ALIPAY) {
                        nodeResult = (checkNode(nodes, "交易成功", true)) ||
                                (checkNode(nodes, "还款成功", true)) ||
                                (checkNode(nodes, "退款成功", true)) ||
                                (checkNode(nodes, "自动扣款成功", true)) ||
                                (checkNode(nodes, "有退款", true)) ||
                                (checkNode(nodes, "已全额退款", true)) ||
                                (checkNode(nodes, "亲情卡付款成功", true)) ||
                                (checkNode(nodes, "付款成功", false));
                    }

                    if (!nodeResult && flag == UNION_PAY) {
                        nodeResult = (checkNode(nodes, "交易详情", true))
                                || (checkNode(nodes, "订单详情", true));
                    }

                    boolean pay = checkNode(nodes, "转账成功", true);
                    int receive = (checkNode(nodes, "已收款", true)) || (checkNode(nodes, "资金待入账", true)) || (checkNode(nodes, "你已收款", true)) ? 1 : 0;
                    boolean redPackage = checkNode(nodes, "支付宝红包", true);
                    if (redPackage) {
                        redPayMoeny = checkNode(nodes, "红包金额", false);
                        if (!redPayMoeny) {
                            redPayMoeny = checkNode(nodes, "人已领取", false);
                        }
                        if (!redPayMoeny) {
                            redPayMoeny = checkNode(nodes, "领取成功", false);
                        }
                    } else {
                        redPayMoeny = checkNode(nodes, "的红包", false);
                    }

                    if (nodeResult) {
                        Log.d("start check pay:" + flag);
                        int flag1 = flag;
                        if (flag1 == ALIPAY || flag1 == ALIPAY_DETAIL) {
                            billinfo = AutoBillDetail.alipay(nodes);
                        } else if (flag1 == WECHAT_DETAIL_TRANSFER) {
                            billinfo = AutoBillDetail.wechatDetailTransfer(nodes, payTools);
                        } else if (flag1 == WECHAT_DETAIL) {
                            billinfo = AutoBillDetail.wechatDetail(nodes);
                        } else if (flag1 == UNION_PAY_DETAIL) {
                            billinfo = AutoBillDetail.unionPayDetail(nodes);
                        } else if (flag1 == UNION_PAY) {
                            billinfo = AutoBillDetail.unionPay(nodes);
                        } else if (flag1 == JD_JR) {
                            //TODO 京东金融
                            billinfo = AutoBillDetail.jdPay(nodes);
                        } else {
                            billinfo = null;
                        }

                        if (billinfo != null) {
                            canAdd = false;
                            payTools = null;
                            Log.d("start addBill");
                            AutoBillDetail.goApp(autoAccessibilityService, billinfo);
                        }
                    } else if (pay) {
                        Log.d("start check transfer");
                        BillInfo billinfo2 = AutoBillDetail.alipayTransfer(nodes);
                        if (billinfo2 != null) {
                            canAdd = false;
                            payTools = null;
                            AutoBillDetail.goApp(autoAccessibilityService, billinfo2);
                        }
                    } else if (receive != 0) {
                        Log.d("start check receive");
                        BillInfo billinfo2 = flag == ALIPAY ? AutoBillDetail.alipay(nodes) : AutoBillDetail.wechatDetailReceiveTransfer(nodes);
                        if (billinfo2 != null) {
                            canAdd = false;
                            payTools = null;
                            AutoBillDetail.goApp(autoAccessibilityService, billinfo2);
                        }
                    } else if (redPayMoeny) {
                        Log.d("start check red package");
                        BillInfo billInfo = AutoBillDetail.redPackageSend(nodes, redPackage);
                        if (billInfo == null) {
                            billInfo = redPackage ? AutoBillDetail.alipayRedPackage(nodes) : AutoBillDetail.wechatRedPackage(nodes);
                        }

                        if (billInfo != null) {
                            canAdd = false;
                            payTools = null;
                            AutoBillDetail.goApp(autoAccessibilityService, billInfo);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}

