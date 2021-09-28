package cn.dreamn.qianji_auto.core.helper.Inner;

import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_ALIPAY_PAY_DETAIL_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_ALIPAY_PAY_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_JD_PAY_DETAIL_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_JD_PAY_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_MT_PAY_DETAIL_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_NO_USE;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_PDD_DETAIL_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_UNION_PAY_DETAIL_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_UNION_PAY_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_WECHAT_PAY_MONEY_UI;
import static cn.dreamn.qianji_auto.core.helper.Inner.NodeListManage.FLAG_WECHAT_PAY_UI;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.core.helper.AutoBillService;
import cn.dreamn.qianji_auto.database.Helper.AppDatas;
import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class HelpService extends AccessibilityService {

    public static boolean j;
    public static boolean k;


    static {
        j = true;
        k = false;
    }


    public int c = 0;
    public String d = null;
    public boolean e = false;
    public boolean f = false;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        String packageName = accessibilityEvent.getPackageName() == null ? "" : accessibilityEvent.getPackageName().toString();
        String className = accessibilityEvent.getClassName() == null ? "" : accessibilityEvent.getClassName().toString();
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

            //获取Parcelable对象
            Parcelable data = accessibilityEvent.getParcelableData();

            //判断是否是Notification对象
            if (data instanceof Notification) {
                // Log.i("通知栏发生变化 > " + accessibilityEvent.getText().toString());
                Notification notification = (Notification) data;
                Bundle extras = notification.extras;
                if (extras != null) {
                    String title = extras.getString(NotificationCompat.EXTRA_TITLE, "");
                    String content = extras.getString(NotificationCompat.EXTRA_TEXT, "");

                    String str = "title=" + title + ",content=" + content;
                    AppDatas.add("notice", packageName, str);
                    Handler mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            BillInfo billInfo = (BillInfo) msg.obj;
                            billInfo.setFromApp(packageName);
                            SendDataToApp.call(getApplicationContext(), billInfo);
                        }
                    };
                    identifyRegulars.run("notice", packageName, str, billInfo -> {
                        if (billInfo != null) {
                            HandlerUtil.send(mHandler, billInfo, HANDLE_OK);
                        }
                    });

                }
            }
            return;
        }


        boolean var3;
        boolean var7 = true;
        boolean var5;
        String var20;


        //
        byte var23;
        byte var29;

        var23 = 3;

        if ("com.tencent.mm.plugin.remittance.ui.RemittanceBusiUI".equals(className) || "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.WalletBrandUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI".equals(className) || "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiReceiveUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiDetailUI".equals(className) || "com.tencent.mm.plugin.aa.ui.PaylistAAUI".equals(className) || "com.tencent.mm.plugin.wallet.balance.ui.WalletBalanceResultUI".equals(className) || "com.tencent.mm".equals(packageName) && source != null && source.findAccessibilityNodeInfosByText("支付成功").size() > 0 && source.findAccessibilityNodeInfosByText("查看账单详情").size() == 0) {
            Log.d("[页面识别]微信支付界面1");
            NodeListManage.setFlag(FLAG_WECHAT_PAY_UI);
        } else if ("com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishUI".equals(className)) {
            Log.d("[页面识别]微信零钱通_WX_PAY");
            this.f = true;
            NodeListManage.setFlag(FLAG_WECHAT_PAY_UI);

        } else if ("com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishProgressNewUI".equals(className)) {
            Log.d("[页面识别]微信零钱通_WX_BILL");
            this.f = true;
            NodeListManage.setFlag(FLAG_WECHAT_PAY_MONEY_UI);
        } else if ("com.tencent.mm".equals(packageName) && source != null && ((source.findAccessibilityNodeInfosByText("账单详情").size() > 0 && source.findAccessibilityNodeInfosByText("查看账单详情").size() == 0) || (source.findAccessibilityNodeInfosByText("零钱提现").size() > 0 && source.findAccessibilityNodeInfosByText("到账成功").size() > 0))) {
            Log.d("[页面识别]微信账单详情");
            NodeListManage.setFlag(FLAG_WECHAT_PAY_MONEY_UI);
        } else if ("com.alipay.android.msp.ui.views.MspContainerActivity".equals(className) || "com.alipay.android.msp.ui.views.MspUniRenderActivity".equals(className) || "com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity".equals(className) || "com.eg.android.AlipayGphone".equals(packageName) && source != null && source.findAccessibilityNodeInfosByText("向商家付钱").size() > 0) {
            Log.d("[页面识别]支付宝支付界面");
            NodeListManage.setFlag(FLAG_ALIPAY_PAY_UI);
        } else if ("com.eg.android.AlipayGphone".equals(packageName) && source != null && (source.findAccessibilityNodeInfosByText("账单详情").size() > 0 || source.findAccessibilityNodeInfosByText("结果详情").size() > 0)) {
            Log.d("[页面识别]支付宝账单详情");
            NodeListManage.setFlag(FLAG_ALIPAY_PAY_DETAIL_UI);
        } else if ("com.unionpay.activity.payment.UPActivityScan".equals(className) || "com.unionpay.activity.payment.UPActivityPaymentQrCodeOut".equals(className)) {
            Log.d("[页面识别]云闪付支付界面");
            NodeListManage.setFlag(FLAG_UNION_PAY_UI);
        } else if ("com.unionpay".equals(packageName) && ((source != null && source.findAccessibilityNodeInfosByText("查看账单").size() > 0) || "com.unionpay.cordova.UPActivityWeb".equals(className) || ("android.view.ViewGroup".equals(className) && source != null && source.findAccessibilityNodeInfosByText("交易记录").size() > 0 && source.findAccessibilityNodeInfosByText("筛选").size() > 0) || (source != null && source.findAccessibilityNodeInfosByText("动账通知").size() > 0 && source.findAccessibilityNodeInfosByText("支付助手").size() > 0))) {
            Log.d("[页面识别]云闪付账单详情");
            NodeListManage.setFlag(FLAG_UNION_PAY_DETAIL_UI);
        } else if ("com.sankuai.waimai.business.knb.KNBWebViewActivity".equals(className) || "com.sankuai.eh.framework.EHContainerActivity".equals(className) || "com.meituan.android.pay.activity.PayActivity".equals(className) || ("com.sankuai.meituan.takeoutnew".equals(packageName) || "com.sankuai.meituan".equals(packageName)) && ("android.webkit.WebView".equals(className) || "android.view.View".equals(className))) {
            Log.d("[页面识别]美团账单详情");
            NodeListManage.setFlag(FLAG_MT_PAY_DETAIL_UI);
        } else if (("com.jingdong.app.mall".equals(packageName) || "com.jd.jrapp".equals(packageName)) && source != null && source.findAccessibilityNodeInfosByText("账单详情").size() > 0) {
            Log.d("[页面识别]京东账单详情");
            NodeListManage.setFlag(FLAG_JD_PAY_DETAIL_UI);
        } else if ("com.jd.lib.cashier.complete.view.CashierCompleteActivity".equals(className)) {
            Log.d("[页面识别]京东支付界面");
            NodeListManage.setFlag(FLAG_JD_PAY_UI);
        } else if ("com.xunmeng.pinduoduo.activity.NewPageActivity".equals(className) || "com.xunmeng.pinduoduo".equals(packageName) && ("android.webkit.WebView".equals(className) || "android.view.View".equals(className) || "android.widget.FrameLayout".equals(className))) {
            Log.d("[页面识别]拼多多账单详情");
            NodeListManage.setFlag(FLAG_PDD_DETAIL_UI);
        } else if ("com.unionpay.activity.UPActivityMain".equals(className) || "com.alipay.mobile.bill.list.ui.BillMainListActivity".equals(className) || "com.tencent.mm.ui.LauncherUI".equals(className) || "com.eg.android.AlipayGphone.AlipayLogin".equals(className) || "com.jd.jrapp.bm.mainbox.main.MainActivity".equals(className) || "com.jingdong.app.mall.MainFrameActivity".equals(className) || "com.sankuai.waimai.business.page.homepage.MainActivity".equals(className) || "com.meituan.android.pt.homepage.activity.MainActivity".equals(className) || "com.xunmeng.pinduoduo.ui.activity.HomeActivity".equals(className)) {
            Log.d("[页面识别]退出_页面识别失败");
        }
        NodeListManage.findNodeInfo(source, packageName, true);
        //啥都不管，先塞数据~
        Log.i("当前页面数据：flag=" + NodeListManage.flag + ",class=" + className + ",globalNodeList=" + (NodeListManage.globalNodeList.toString()));

        if (NodeListManage.flag != FLAG_NO_USE && NodeListManage.globalNodeList.size() != 0) {
            List<Object> nodeList = NodeListManage.globalNodeList;
            if (NodeListManage.isNullOrEmpty(nodeList)) return;
            boolean isUseful;
            if (NodeListManage.flag == FLAG_MT_PAY_DETAIL_UI) {
                Log.i("[FLAG]美团账单详情 FLAG_MT_PAY_DETAIL_UI");
                isUseful = nodeList.size() > 4 && (NodeListManage.checkNode(nodeList, "交易详情", true) || NodeListManage.checkNode(nodeList, "交易类型", true) || "支付成功".equals(nodeList.get(0)));
                if (!isUseful) {
                    Log.i("[页面识别]无效账单详情");
                    return;
                }
            }

            if (NodeListManage.flag == FLAG_PDD_DETAIL_UI) {
                Log.i("[FLAG]拼多多账单详情 FLAG_PDD_DETAIL_UI");
                isUseful = nodeList.size() > 10 && NodeListManage.checkNode(nodeList, "账单详情", true) && (NodeListManage.checkNode(nodeList, "交易单号", true) || NodeListManage.checkNode(nodeList, "提现单号", true));
                isUseful = isUseful || (nodeList.size() > 6 && (NodeListManage.checkNode(nodeList, "充值成功", true) || NodeListManage.checkNode(nodeList, "提现发起成功", true)));
                if (!isUseful) {
                    Log.i("[页面识别]无效账单详情");
                    return;
                }

            }

            boolean var6;
            var6 = NodeListManage.checkNode(nodeList, "支付成功", true) || NodeListManage.checkNode(nodeList, "充值成功", true);

            var5 = var6;
            if (NodeListManage.flag == FLAG_ALIPAY_PAY_UI) {
                Log.i("[FLAG]支付宝支付UI FLAG_ALIPAY_PAY_UI");
                if (!var6) {
                    var5 = NodeListManage.checkNode(nodeList, "代付成功", true);
                }
            }

            var6 = var5;
            int var35;
            if (NodeListManage.flag == FLAG_WECHAT_PAY_UI) {
                label848:
                {
                    if (!this.f) {
                        if (NodeListManage.checkNode(nodeList, "支付方式", true)) {
                            var35 = nodeList.indexOf("支付方式");
                                if (var35 < nodeList.size() - 1) {
                                    this.d = (String) nodeList.get(var35 + 1);
                                }
                            }

                            if (NodeListManage.checkNode(nodeList, "优先使用此支付方式付款", true)) {
                                var35 = nodeList.indexOf("优先使用此支付方式付款") - 1;
                                if (var35 >= 0) {
                                    this.d = (String) nodeList.get(var35);
                                }
                            }

                            var6 = var5;
                            if (!((String) nodeList.get(0)).endsWith("发起的群收款")) {
                                break label848;
                            }

                            var6 = var5;
                            if (NodeListManage.checkNode(nodeList, "已收到", false)) {
                                break label848;
                            }

                            var6 = var5;
                            if (NodeListManage.checkNode(nodeList, "你需支付", true)) {
                                break label848;
                            }
                        }

                        var6 = true;
                    }
                }

                var5 = var6;
                if (!var6) {
                    label849:
                    {
                        var35 = NodeListManage.flag;
                        if (var35 != FLAG_WECHAT_PAY_MONEY_UI && var35 != FLAG_ALIPAY_PAY_DETAIL_UI && var35 != FLAG_MT_PAY_DETAIL_UI && var35 != FLAG_JD_PAY_DETAIL_UI && var35 != FLAG_PDD_DETAIL_UI) {
                            if (var35 != FLAG_ALIPAY_PAY_UI) {
                                var5 = false;
                                if (var35 != FLAG_UNION_PAY_DETAIL_UI) {
                                    break label849;
                                }

                                var6 = NodeListManage.checkNode(nodeList, "交易详情", true) || NodeListManage.checkNode(nodeList, "订单详情", true);

                                var5 = var6;
                                if (var6) {
                                    this.e = true;
                                    var5 = true;
                                }
                                break label849;
                            }

                            if (!NodeListManage.checkNode(nodeList, "交易成功", true) && !NodeListManage.checkNode(nodeList, "还款成功", true) && !NodeListManage.checkNode(nodeList, "退款成功", true) && !NodeListManage.checkNode(nodeList, "自动扣款成功", true) && !NodeListManage.checkNode(nodeList, "有退款", true) && !NodeListManage.checkNode(nodeList, "已全额退款", true) && !NodeListManage.checkNode(nodeList, "亲情卡付款成功", true) && !NodeListManage.checkNode(nodeList, "付款成功", false)) {
                                var5 = false;
                                break label849;
                            }
                        }

                        var5 = true;
                    }
                }

                boolean var8 = NodeListManage.checkNode(nodeList, "转账成功", true);
                if (!NodeListManage.checkNode(nodeList, "已收款", true) && !NodeListManage.checkNode(nodeList, "资金待入账", true) && !NodeListManage.checkNode(nodeList, "你已收款", true)) {
                    label547:
                    {
                        if (NodeListManage.flag == FLAG_WECHAT_PAY_UI) {
                            var29 = var23;
                            if (NodeListManage.checkNode(nodeList, "已收款", false)) {
                                break label547;
                            }

                            if (NodeListManage.checkNode(nodeList, "提醒对方收款", false)) {
                                var29 = var23;
                                break label547;
                            }
                        }

                        var29 = 0;
                    }
                } else {
                    var29 = 1;
                }

            var20 = "支付宝红包";
            var6 = true;
            if (!NodeListManage.checkNode(nodeList, "支付宝红包", true)) {
                var6 = NodeListManage.flag == FLAG_ALIPAY_PAY_UI && NodeListManage.checkNode(nodeList, "红包编号", false);
            }

            label854:
            {
                var3 = false;
                if (var6) {
                    if (!NodeListManage.checkNode(nodeList, "红包金额", false) && !NodeListManage.checkNode(nodeList, "人已领取", false) && !NodeListManage.checkNode(nodeList, "领取成功", false)) {
                        break label854;
                    }
                } else if (!NodeListManage.checkNode(nodeList, "的红包", false) || NodeListManage.flag == FLAG_UNION_PAY_DETAIL_UI) {
                    var3 = false;
                    break label854;
                }

                var3 = true;
            }
            //此处写入billinfo
            BillInfo var26;
                label521:
                {
                    label520:
                    {
                        analyze_1 var22;
                        label885:
                        {
                            WxDetailParser var21;
                            label863:
                            {
                                int var25;
                                if (!var5) {
                                    int var4 = NodeListManage.flag;
                                    if (var4 != FLAG_UNION_PAY_DETAIL_UI || !this.e) {
                                        if (var8) {
                                            var26 = (new analyze_1(0)).h(nodeList, 2);
                                            break label521;
                                        }

                                        if (var29 == 0) {
                                            if (var3) {
                                                analyze_2 var12 = new analyze_2();
                                                var5 = var4 == FLAG_ALIPAY_PAY_DETAIL_UI || var4 == FLAG_ALIPAY_PAY_UI;

                                                var12.b = var6;
                                                var12.c = var5;
                                                if (var4 != FLAG_WECHAT_PAY_UI || !NodeListManage.checkNode(nodeList, "已存入零钱", false)) {
                                                    label473:
                                                    {
                                                        if (nodeList.size() != 0) {
                                                            StringBuilder var28 = new StringBuilder("[页面识别] RedDetailParser parse type " + 0 + " ");
                                                            var28.append(nodeList.toString());
                                                            Log.d(var28.toString());
                                                            BillInfo billInfo = new BillInfo();
                                                            var35 = 0;

                                                            label469:
                                                            {
                                                                while (true) {
                                                                    if (var35 >= nodeList.size()) {
                                                                        break label469;
                                                                    }

                                                                    label465:
                                                                    {
                                                                        label464:
                                                                        {
                                                                            String var30 = (String) nodeList.get(var35);
                                                                            if (var30.contains("红包金额")) {
                                                                                var25 = var30.indexOf("元");
                                                                                if (var25 <= 4) {
                                                                                    break label464;
                                                                                }

                                                                                var30 = var30.substring(0, var25).replace("红包金额", "").replace(",", "");
                                                                                if (!var12.isMoney(var30)) {
                                                                                    break label464;
                                                                                }

                                                                                var12.setMoney(billInfo, var30);
                                                                                if (var12.b) {
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                if (!var30.contains("个红包共")) {
                                                                                    if (var30.contains("人已领取")) {
                                                                                        var25 = var30.indexOf("元");
                                                                                        if (var25 > 4) {
                                                                                            var30 = var30.substring(var30.indexOf("人已领取") + 6, var25).replace(",", "");
                                                                                            if (var12.isMoney(var30)) {
                                                                                                var12.setMoney(billInfo, var30);
                                                                                                if (var35 < 1) {
                                                                                                    break;
                                                                                                }

                                                                                                --var35;
                                                                                                if ("恭喜发财，万事如意！".equals(nodeList.get(var35))) {
                                                                                                    break;
                                                                                                }

                                                                                                var28 = new StringBuilder();
                                                                                                var20 = "支付宝红包-";
                                                                                                break label465;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    break label464;
                                                                                }

                                                                                var25 = var30.indexOf("元");
                                                                                if (var25 <= 4) {
                                                                                    break label464;
                                                                                }

                                                                                var30 = var30.substring(var30.indexOf("个红包共") + 4, var25).replace(",", "");
                                                                                if (!var12.isMoney(var30)) {
                                                                                    break label464;
                                                                                }

                                                                                var12.setMoney(billInfo, var30);
                                                                                if (var35 >= 1) {
                                                                                    --var35;
                                                                                    if (!((String) nodeList.get(var35)).endsWith("的红包")) {
                                                                                        var28 = new StringBuilder();
                                                                                        var20 = "微信红包-";
                                                                                        break label465;
                                                                                    }
                                                                                }
                                                                            }

                                                                            var20 = "微信红包";
                                                                            break;
                                                                        }

                                                                        ++var35;
                                                                        continue;
                                                                    }

                                                                    var28.append(var20);
                                                                    var28.append((String) nodeList.get(var35));
                                                                    var20 = var28.toString();
                                                                    break;
                                                                }

                                                                billInfo.setShopRemark(var20);
                                                            }

                                                            if (var12.a) {
                                                                var26 = billInfo;
                                                                break label473;
                                                            }
                                                        }

                                                        var26 = null;
                                                    }

                                                    if (var26 != null) {
                                                        break label521;
                                                    }
                                                }

                                                var26 = var12.h(nodeList);
                                                break label521;
                                            }
                                            break label520;
                                        }

                                        if (var4 == 2) {
                                            var22 = new analyze_1(0);
                                            break label885;
                                        }

                                        if (var29 != 1) {
                                            var26 = (new WxDetailParser()).h(nodeList, 2);
                                            break label521;
                                        }

                                        var21 = new WxDetailParser();
                                        var29 = 3;
                                        break label863;
                                    }
                                }

                                var25 = NodeListManage.flag;
                                if (var25 == 2 || var25 == 6) {
                                    var22 = new analyze_1(0);
                                    break label885;
                                }

                                var29 = 1;
                                if (var25 != 1) {
                                    if (var25 == 3) {
                                        var21 = new WxDetailParser();
                                        var21.c = this.f;
                                        var26 = var21.h(nodeList, 0);
                                        break label521;
                                    }

                                    if (var25 == 4) {
                                        var26 = (new analyze_1(2)).h(nodeList, 1);
                                        break label521;
                                    }

                                    if (var25 == 5) {
                                        var22 = new analyze_1(2);
                                    } else {
                                        if (var25 != 7) {
                                            analyze_4 var24;
                                            if (var25 == 8) {
                                                var24 = new analyze_4(0);
                                            } else {
                                                if (var25 == 9) {
                                                    var26 = (new analyze_4(0)).h(nodeList, 1);
                                                    break label521;
                                                }

                                                if (var25 != 10) {
                                                    break label520;
                                                }

                                                var24 = new analyze_4(1);
                                            }

                                            var26 = var24.h(nodeList, 0);
                                            break label521;
                                        }

                                        var22 = new analyze_1(1);
                                    }
                                    break label885;
                                }

                                var21 = new WxDetailParser();
                                var21.b = this.d;
                                var21.c = this.f;
                            }

                            var26 = var21.h(nodeList, var29);
                            break label521;
                        }

                        var26 = var22.h(nodeList, 0);
                        break label521;
                    }

                    var26 = null;
                }

                if (var26 != null) {
                    Log.d("[页面识别]解析成功");
                    NodeListManage.goApp(getApplicationContext(), var26);
                } else {
                    Log.d("[页面识别]解析失败");
                }

        }
    }

    @Override
    public void onInterrupt() {
        Log.i("自动记账辅助服务已暂停。");
        getApplicationContext().stopService(new Intent(getApplicationContext(), AutoBillService.class));
    }

    @Override
    protected void onServiceConnected() {
        Log.init("自动记账辅助服务");
        Log.i("自动记账辅助服务已启动。");
        getApplicationContext().startService(new Intent(getApplicationContext(), AutoBillService.class));
    }
}
