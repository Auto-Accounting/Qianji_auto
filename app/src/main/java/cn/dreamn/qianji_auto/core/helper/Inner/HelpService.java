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
import static cn.dreamn.qianji_auto.core.helper.Inner.TransferDetailParser.AlipayDetailParser;
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

    public String payTools = null;
    public boolean unionFlag = false;
    //转入转出零钱通
    public boolean wechatTransferLqt = false;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i("-----------------无障碍事件---------------------");
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


        boolean alipayRed;
        boolean nodeResult = false;


        int weChatType;

        if ("com.tencent.mm.plugin.remittance.ui.RemittanceBusiUI".equals(className) || "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.WalletBrandUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className) || "com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI".equals(className) || "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiReceiveUI".equals(className) || "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyBusiDetailUI".equals(className) || "com.tencent.mm.plugin.aa.ui.PaylistAAUI".equals(className) || "com.tencent.mm.plugin.wallet.balance.ui.WalletBalanceResultUI".equals(className) || "com.tencent.mm".equals(packageName) && source != null && source.findAccessibilityNodeInfosByText("支付成功").size() > 0 && source.findAccessibilityNodeInfosByText("查看账单详情").size() == 0) {
            Log.d("[页面识别]微信支付界面");
            NodeListManage.setFlag(FLAG_WECHAT_PAY_UI);
        } else if ("com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishUI".equals(className)) {
            Log.d("[页面识别]微信零钱通_WX_PAY");
            wechatTransferLqt = true;
            NodeListManage.setFlag(FLAG_WECHAT_PAY_UI);
        } else if ("com.tencent.mm.plugin.wallet.balance.ui.lqt.WalletLqtSaveFetchFinishProgressNewUI".equals(className)) {
            Log.d("[页面识别]微信零钱通_WX_BILL");
            wechatTransferLqt = true;
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

        if (NodeListManage.flag != FLAG_NO_USE && NodeListManage.isNullOrEmpty(NodeListManage.globalNodeList)) {
            //flag有数据，node也有数据
            List<Object> nodeList = NodeListManage.globalNodeList;
            //Log.d("[页面识别]拼多多账单详情");
            boolean isUseful;
            Log.d("[FLAG]FLAG判断");
            if (NodeListManage.flag == FLAG_MT_PAY_DETAIL_UI) {
                Log.i("[FLAG]美团账单详情 FLAG_MT_PAY_DETAIL_UI");
                isUseful = nodeList.size() > 4 && (NodeListManage.checkNode(nodeList, "交易详情", true) || NodeListManage.checkNode(nodeList, "交易类型", true) || "支付成功".equals(nodeList.get(0)));
                if (!isUseful) {
                    Log.i("[页面识别]无效账单详情");
                    return;
                }
            } else if (NodeListManage.flag == FLAG_PDD_DETAIL_UI) {
                Log.i("[FLAG]拼多多账单详情 FLAG_PDD_DETAIL_UI");
                isUseful = nodeList.size() > 10 && NodeListManage.checkNode(nodeList, "账单详情", true) && (NodeListManage.checkNode(nodeList, "交易单号", true) || NodeListManage.checkNode(nodeList, "提现单号", true));
                isUseful = isUseful || (nodeList.size() > 6 && (NodeListManage.checkNode(nodeList, "充值成功", true) || NodeListManage.checkNode(nodeList, "提现发起成功", true)));
                if (!isUseful) {
                    Log.i("[页面识别]无效账单详情");
                    return;
                }

            } else if (NodeListManage.flag == FLAG_ALIPAY_PAY_UI) {
                Log.i("[FLAG]支付宝支付UI FLAG_ALIPAY_PAY_UI");
                nodeResult = !(NodeListManage.checkNode(nodeList, "支付成功", true) || NodeListManage.checkNode(nodeList, "充值成功", true)) && NodeListManage.checkNode(nodeList, "代付成功", true);
            } else if (NodeListManage.flag == FLAG_WECHAT_PAY_UI) {
                int index;
                Log.i("[FLAG]微信支付UI FLAG_WECHAT_PAY_UI");
                nodeResult = true;
                //微信支付的一个标记位为false
                if (!wechatTransferLqt) {
                    Log.i("微信：零钱通，获取支付工具");
                    if (NodeListManage.checkNode(nodeList, "支付方式", true)) {
                        index = nodeList.indexOf("支付方式");
                        if (index < nodeList.size() - 1) {
                            payTools = (String) nodeList.get(index + 1);
                        }
                    }

                    if (NodeListManage.checkNode(nodeList, "优先使用此支付方式付款", true)) {
                        index = nodeList.indexOf("优先使用此支付方式付款") - 1;
                        if (index >= 0) {
                            payTools = (String) nodeList.get(index);
                        }
                    }
                    if (
                            !((String) nodeList.get(0)).endsWith("发起的群收款") ||
                                    NodeListManage.checkNode(nodeList, "已收到", false) ||
                                    NodeListManage.checkNode(nodeList, "你需支付", true)) {
                        nodeResult = false;
                    }
                }
            }
            Log.d("[FLAG]nodeResult 1 判断结束，进入是否记账判断");
            int index;
            if (!nodeResult) {
                Log.d("[FLAG]nodeResult = false 再次进行判断");
                if (NodeListManage.flag != FLAG_WECHAT_PAY_MONEY_UI && NodeListManage.flag != FLAG_ALIPAY_PAY_DETAIL_UI && NodeListManage.flag != FLAG_MT_PAY_DETAIL_UI && NodeListManage.flag != FLAG_JD_PAY_DETAIL_UI && NodeListManage.flag != FLAG_PDD_DETAIL_UI) {
                    nodeResult = true;
                    if (NodeListManage.flag != FLAG_ALIPAY_PAY_UI) {
                        nodeResult = false;
                        if (NodeListManage.flag == FLAG_UNION_PAY_DETAIL_UI) {
                            nodeResult = NodeListManage.checkNode(nodeList, "交易详情", true) || NodeListManage.checkNode(nodeList, "订单详情", true);
                            if (nodeResult) {
                                unionFlag = true;
                                nodeResult = true;
                            }
                        }
                    } else if (!NodeListManage.checkNode(nodeList, "交易成功", true) && !NodeListManage.checkNode(nodeList, "还款成功", true) && !NodeListManage.checkNode(nodeList, "退款成功", true) && !NodeListManage.checkNode(nodeList, "自动扣款成功", true) && !NodeListManage.checkNode(nodeList, "有退款", true) && !NodeListManage.checkNode(nodeList, "已全额退款", true) && !NodeListManage.checkNode(nodeList, "亲情卡付款成功", true) && !NodeListManage.checkNode(nodeList, "付款成功", false)) {
                        nodeResult = false;
                    }
                }
            }
            Log.d("[FLAG]nodeResult 2 判断结束，进入是否记账判断");
            boolean payTransfer = NodeListManage.checkNode(nodeList, "转账成功", true);
            Log.d("判断是否有转账成功payTransfer：" + (payTransfer ? "true" : "false"));
            if (!NodeListManage.checkNode(nodeList, "已收款", true) && !NodeListManage.checkNode(nodeList, "资金待入账", true) && !NodeListManage.checkNode(nodeList, "你已收款", true)) {
                Log.d("确定为微信收款TYPE_WECHAT_OTHER");
                weChatType = WxDetailParser.TYPE_WECHAT_OTHER;
                if (NodeListManage.flag == FLAG_WECHAT_PAY_UI) {
                    if (NodeListManage.checkNode(nodeList, "已收款", false) || NodeListManage.checkNode(nodeList, "提醒对方收款", false)) {
                        Log.d("确定为微信收款TYPE_WECHAT_REC");
                        weChatType = WxDetailParser.TYPE_WECHAT_REC;
                    }
                }
            } else {
                Log.d("确定为微信群收款");
                weChatType = WxDetailParser.TYPE_WECHAT_GROUP;
            }
            alipayRed = true;
            if (nodeResult) {
                Log.d("检查支付宝红包");
                if (!NodeListManage.checkNode(nodeList, "红包金额", false) && !NodeListManage.checkNode(nodeList, "人已领取", false) && !NodeListManage.checkNode(nodeList, "领取成功", false)) {
                    Log.d("不是红包1");
                    alipayRed = false;
                }
            } else if (!NodeListManage.checkNode(nodeList, "的红包", false) || NodeListManage.flag == FLAG_UNION_PAY_DETAIL_UI) {
                Log.d("不是红包2");
                alipayRed = false;
            }
            Log.d("进入账单确认核对页面");
            //此处写入billinfo
            BillInfo billinfo = null;
            TransferDetailParser transferDetailParser;//转账处理
            WxDetailParser wechatDetail;//微信处理
            // weChatType = WxDetailParser.TYPE_WECHAT_GROUP;
            if (!nodeResult && (NodeListManage.flag != FLAG_UNION_PAY_DETAIL_UI || !unionFlag)) {
                //转账成功
                if (payTransfer) {
                    Log.d("支付宝转账成功---------->处理");
                    billinfo = (new TransferDetailParser(AlipayDetailParser)).h(nodeList, 2);
                    Log.d("支付宝转账成功---------->处理结束，BillInfo已确定");

                } else if (weChatType == WxDetailParser.TYPE_WECHAT_OTHER) {

                    if (alipayRed) {
                        Log.d("红包处理");
                        RedDetailParser redDetail = new RedDetailParser();
                        redDetail.alipay = false;
                        redDetail.alipay2 = NodeListManage.flag == FLAG_ALIPAY_PAY_DETAIL_UI || NodeListManage.flag == FLAG_ALIPAY_PAY_UI;
                        if (NodeListManage.flag != FLAG_WECHAT_PAY_UI || !NodeListManage.checkNode(nodeList, "已存入零钱", false)) {
                            Log.d("[页面识别] RedDetailParser parse type 666" + nodeList.toString());
                            BillInfo billInfo = new BillInfo();
                            index = 0;

                            while (true) {
                                if (index >= nodeList.size()) {
                                    Log.d("循环已经越界，pass");
                                    break;
                                }
                                Log.i("当前红包数据(alipayRed):" + (String) nodeList.get(index));
                                String nodeStr = (String) nodeList.get(index);
                                if (nodeStr.contains("红包金额")) {
                                    Log.d("有 红包金额");
                                    int mIndex = nodeStr.indexOf("元");
                                    Log.d("找到中文 元 的位置" + mIndex);
                                    if (mIndex > 4) {
                                        nodeStr = nodeStr.substring(0, mIndex).replace("红包金额", "").replace(",", "");
                                        Log.d("金额 " + nodeStr);
                                        if (redDetail.isMoney(nodeStr)) {
                                            redDetail.setMoney(billInfo, nodeStr);
                                            if (redDetail.alipay) {
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (!nodeStr.contains("个红包共")) {

                                        if (nodeStr.contains("人已领取")) {
                                            Log.d("属于群红包 ");
                                            int mIndex = nodeStr.indexOf("元");
                                            Log.d("找到中文 元 的位置" + mIndex);
                                            if (mIndex > 4) {
                                                nodeStr = nodeStr.substring(nodeStr.indexOf("人已领取") + 6, mIndex).replace(",", "");
                                                if (redDetail.isMoney(nodeStr)) {
                                                    redDetail.setMoney(billInfo, nodeStr);
                                                    if (index < 1) {
                                                        break;
                                                    }
                                                    --index;
                                                    if ("恭喜发财，万事如意！".equals(nodeList.get(index))) {
                                                        break;
                                                    }
                                                    Log.d("可能是支付宝红包");
                                                    billInfo.setShopRemark("支付宝红包");
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    Log.d("再找 元");
                                    int mIndex = nodeStr.indexOf("元");
                                    if (mIndex <= 4) {
                                        break;
                                    }
                                    Log.d("找到" + mIndex);
                                    nodeStr = nodeStr.substring(nodeStr.indexOf("个红包共") + 4, mIndex).replace(",", "");
                                    Log.d("金额" + nodeStr);
                                    if (redDetail.isMoney(nodeStr)) {
                                        redDetail.setMoney(billInfo, nodeStr);
                                        if (index >= 1) {
                                            --index;
                                            if (!((String) nodeList.get(index)).endsWith("的红包")) {
                                                billInfo.setShopRemark("微信红包");
                                                break;
                                            }
                                        }
                                    }


                                }
                                Log.d("数据递增" + index);
                                ++index;
                                // billInfo.setShopRemark((String) nodeList.get(index));
                            }

                            if (redDetail.isSetMoney) {
                                billinfo = billInfo;
                            }

                            if (billinfo == null) {
                                Log.d("红包处理,billinfo为NULL");
                                billinfo = redDetail.run(nodeList);
                            }
                        }
                    }
                }

                if (NodeListManage.flag == FLAG_ALIPAY_PAY_UI) {
                    Log.d("进入阿里的分析");
                    transferDetailParser = new TransferDetailParser(AlipayDetailParser);
                    billinfo = transferDetailParser.h(nodeList, 0);
                    Log.d("分析完毕");
                }

                if (weChatType != WxDetailParser.TYPE_WECHAT_GROUP) {
                    Log.d("进入微信的分析");
                    billinfo = (new WxDetailParser()).run(nodeList, WxDetailParser.TYPE_WECHAT_TRANSFER);
                    Log.d("微信分析完毕");
                }


            } else if (NodeListManage.flag == FLAG_ALIPAY_PAY_UI || NodeListManage.flag == FLAG_ALIPAY_PAY_DETAIL_UI) {
                transferDetailParser = new TransferDetailParser(0);
                billinfo = transferDetailParser.h(nodeList, 0);
            } else if (NodeListManage.flag != FLAG_WECHAT_PAY_UI) {
                if (NodeListManage.flag == FLAG_WECHAT_PAY_MONEY_UI) {
                    wechatDetail = new WxDetailParser();
                    wechatDetail.wechatTransferLqt = wechatTransferLqt;
                    billinfo = wechatDetail.run(nodeList, WxDetailParser.TYPE_WECHAT_OTHER);

                } else if (NodeListManage.flag == FLAG_UNION_PAY_UI) {
                    billinfo = (new TransferDetailParser(2)).h(nodeList, 1);

                } else if (NodeListManage.flag == FLAG_UNION_PAY_DETAIL_UI) {
                    transferDetailParser = new TransferDetailParser(2);
                    billinfo = transferDetailParser.h(nodeList, 0);
                } else {
                    if (NodeListManage.flag != FLAG_MT_PAY_DETAIL_UI) {
                        PddAndJDDetailParser var24;
                        if (NodeListManage.flag == FLAG_JD_PAY_DETAIL_UI) {
                            var24 = new PddAndJDDetailParser(0);
                            billinfo = var24.run(nodeList, PddAndJDDetailParser.BILL_INCOME);

                        } else {
                            if (NodeListManage.flag == FLAG_JD_PAY_UI) {
                                billinfo = (new PddAndJDDetailParser(PddAndJDDetailParser.TYPE_JD)).run(nodeList, PddAndJDDetailParser.BILL_PAY);

                            } else if (NodeListManage.flag == FLAG_PDD_DETAIL_UI) {
                                var24 = new PddAndJDDetailParser(PddAndJDDetailParser.TYPE_PDD);
                                billinfo = var24.run(nodeList, PddAndJDDetailParser.BILL_INCOME);
                            }
                        }

                    } else {
                        transferDetailParser = new TransferDetailParser(1);
                        billinfo = transferDetailParser.h(nodeList, 0);
                    }


                }
            } else {
                wechatDetail = new WxDetailParser();
                wechatDetail.payTools = payTools;
                wechatDetail.wechatTransferLqt = wechatTransferLqt;

                billinfo = wechatDetail.run(nodeList, weChatType);
                //
            }

            if (billinfo != null) {
                Log.i("数据：" + billinfo.toString());
                Log.d("[页面识别]解析成功");
                NodeListManage.goApp(getApplicationContext(), billinfo);
            } else {
                Log.d("[页面识别]解析失败");
            }

        }
        Log.i("-----------------无障碍事件结束---------------------");
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
