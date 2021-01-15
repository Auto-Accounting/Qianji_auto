package cn.dreamn.qianji_auto.core.helper;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.Regex;
import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class AutoAccessibilityService extends AccessibilityService {

    private static final int Wechat = 1;

    private static final int  Alipay = 2;

    private static final int WechatDetail = 3;

    private boolean setTrue = false;
    private int uiType;

    private static boolean isStart=false;

    private String packageName="";
    private String className="";
    private  int eventType=0;

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStart=false;
        Logs.d("辅助功能已停用");
    }



    public static boolean isStart(){return isStart;}

    public void onInterrupt() {
        //isStart=false;
        Logs.d("辅助功能被中断...");
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        boolean redPackage;
        final BillInfo[] billInfo = new BillInfo[1];
        if(!isStart)return;//服务未启动

        this.packageName = accessibilityEvent.getPackageName().toString();
        this.className = accessibilityEvent.getClassName().toString();
        this.eventType = accessibilityEvent.getEventType();

        Logs.d("Qianji_Screen","------------Start-----------" );
        Logs.d("Qianji_Screen","packageName:" + this.packageName);
        Logs.d("Qianji_Screen","className:" + this.packageName);
        Logs.d("Qianji_Screen","eventType:" + this.eventType);

        if(eventType!=2048&&eventType!=32)return;

        if(!packageName.equals("com.tencent.mm")&&!packageName.equals("com.alipay.mm"))return;

        AccessibilityNodeInfo source = accessibilityEvent.getSource();

        if(source==null)return;

       /* final Thread threadA = new Thread(() -> {

        });
        threadA.start();
*/
        List<String> nodeList = NodeTransfer(source);

        if(nodeList.size()<=0)return;


        String nodeListStr=nodeList.toString();


        if(findHook(".*修改.*",nodeListStr)){
            Logs.d("Qianji_Analyze","=======抓取备注信息======="+eventType);
            AnalyzeWeChatTransfer.remark(nodeList);
            Logs.d("Qianji_Analyze","===============");
        }else if(findHook("向.*转账, ￥.*, 支付方式, .*",nodeListStr)){
            Logs.d("Qianji_Analyze","=======抓取支付账户信息======="+eventType);
            AnalyzeWeChatTransfer.account(nodeList);
            Logs.d("Qianji_Analyze","===============");
        }else if(findHook("支付成功, 待.*确认收款, ￥.*, 完成",nodeListStr)){
            Logs.d("Qianji_Analyze","=======抓取转账支付成功信息======="+eventType);
            AnalyzeWeChatTransfer.succeed(nodeList,getApplicationContext());
            Logs.d("Qianji_Analyze","===============");
        }



      /*  String[] payData={"支付成功","￥","完成"};
        findHook("com.tencent.mm",null,0,transfer3,source,()->{
            Logs.d("Qianji_Screen","=======抓取转账支付成功信息 Start=======");
            wechatTransferSucceed(getApplicationContext());
            Logs.d("Qianji_Screen","===============");
        });*/
        /*if (charSequence2.equals("com.tencent.mm.plugin.remittance.ui.RemittanceBusiUI")
                || charSequence2.equals("com.tencent.mm.plugin.remittance.ui.RemittanceUI")
                || charSequence2.equals("com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI")
                || charSequence2.equals("com.tencent.mm.plugin.wallet_index.ui.WalletBrandUI")
                || charSequence2.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI")
                || charSequence2.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")
                || charSequence2.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI")
                || charSequence2.equals("com.tencent.mm.plugin.wallet_index.ui.OrderHandlerUI")
                || charSequence2.equals("com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI"))
        {
            this.uiType = Wechat;
            this.setTrue = true;
        } else if ((source != null
                && source.findAccessibilityNodeInfosByText("微信支付").size() > 0
                && source.findAccessibilityNodeInfosByText("付款金额").size() > 0)
                ||
                (charSequence.equals("com.tencent.mm")
                && source != null
                && source.findAccessibilityNodeInfosByText("账单").size() > 0
                && source.findAccessibilityNodeInfosByText("常见问题").size() > 0)
        ) {
            this.uiType = WechatDetail;
            this.setTrue = true;
        } else if (
                charSequence2.equals("com.alipay.android.msp.ui.views.MspContainerActivity")
                        || charSequence2.equals("com.alipay.android.msp.ui.views.MspUniRenderActivity")
                        || charSequence2.equals("com.alipay.mobile.nebulax.integration.mpaas.activity.NebulaActivity$Main")
                        || charSequence2.equals("com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity")
                        || (source != null && source.findAccessibilityNodeInfosByText("向商家付钱").size() > 0)) {
            this.uiType = Alipay;
            this.setTrue = true;

        }*/
       /* if (source != null && this.setTrue) {
            Logs.d("开始查找节点");
            List<String> nodeList = NodeTransfer(source);
            if (nodeList.size() > 0) {
                boolean paySucceed = findNodeStr(nodeList, "支付成功", true);
                if (!paySucceed && this.uiType == Alipay) {//支付宝查找账单详情
                    paySucceed = findNodeStr(nodeList, "账单详情", true);
                }
                boolean transfer = findNodeStr(nodeList, "转账成功", true);
                boolean received = findNodeStr(nodeList, "已收款", true);
                if (!received) {
                    received = findNodeStr(nodeList, "资金待入账", true);
                }
                boolean Red = findNodeStr(nodeList, "支付宝红包", true);
                if (Red) {
                    redPackage = findNodeStr(nodeList, "红包金额", false);
                    if (!redPackage) {
                        redPackage = findNodeStr(nodeList, "人已领取", false);
                    }
                    if (!redPackage) {
                        redPackage = findNodeStr(nodeList, "领取成功", false);
                    }
                } else {
                    redPackage = findNodeStr(nodeList, "的红包", false);
                }

                if (paySucceed) {
                    Logs.d("检查支付情况");
                    BillInfo billInfo2 = null;
                    int i = this.uiType;
                    if (i == Alipay) {
                       // billInfo2 = AnalyzeBill.d(nodeList);
                        Logs.d("alipay");
                    } else if (i == Wechat) {
                        Logs.d("wechat");
                        billInfo2 = AnalyzeBill.a(nodeList);
                    } else if (i == WechatDetail) {
                        Logs.d("wechat_detail");
                        //billInfo2 = AnalyzeBill.b(nodeList);
                    }
                    if (billInfo2 != null) {
                        this.setTrue = false;
                       // AnalyzeBill.a(this, billInfo2);
                    }
                }

                *//*if (paySucceed) {
                    Logs.d("检查支付情况");
                    BillInfo billInfo2 = null;
                    int i = this.uiType;
                    if (i == Alipay) {
                        billInfo2 = AnalyzeBill.d(nodeList);
                    } else if (i == Wechat) {
                        billInfo2 = AnalyzeBill.a(nodeList);
                    } else if (i == WechatDetail) {
                        billInfo2 = AnalyzeBill.b(nodeList);
                    }
                    if (billInfo2 != null) {
                        this.setTrue = false;
                        AnalyzeBill.a(this, billInfo2);
                    }
                } else if (transfer) {
                    //com.wangc.bill.utils.p.NodeTransfer("start check transfer");
                    BillInfo e2 = AnalyzeBill.e(nodeList);
                    if (e2 != null) {
                        this.setTrue = false;
                        AnalyzeBill.a(this, e2);
                    }
                } else if (received) {
                    //com.wangc.bill.utils.p.NodeTransfer("start check receive");
                    if (this.uiType == Alipay) {
                        billInfo = AnalyzeBill.d(nodeList);
                    } else {
                        billInfo = AnalyzeBill.c(nodeList);
                    }
                    if (billInfo != null) {
                        this.setTrue = false;
                        AnalyzeBill.a(this, billInfo);
                    }
                } else if (redPackage) {
                  //  com.wangc.bill.utils.p.NodeTransfer("start check red package");
                    BillInfo redPackageBillinfo = AnalyzeBill.a(nodeList, Red);
                    if (redPackageBillinfo == null) {
                        if (Red) {
                            redPackageBillinfo = AnalyzeBill.g(nodeList);
                        } else {
                            redPackageBillinfo = AnalyzeBill.f(nodeList);
                        }
                    }
                    if (redPackageBillinfo != null) {
                        this.setTrue = false;
                        AnalyzeBill.a(this, redPackageBillinfo);
                    }
                }*//*
            }
        }*/
        Logs.d("Qianji_Screen","------------End-----------" );
    }

    private List<String> NodeTransfer(AccessibilityNodeInfo accessibilityNodeInfo) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
            if (child != null && child.getChildCount() > 0) {
                arrayList.addAll(NodeTransfer(child));
            } else if (child != null && !TextUtils.isEmpty(child.getText())) {
               Logs.d("Qianji_Node", "nodeInfo:" + child.getText());
                arrayList.add(child.getText().toString());
            }
        }

        return arrayList;
    }



    private boolean findHook(String content, String nodeListStr){


        Logs.d("Qianji_Match","匹配文本："+nodeListStr);
        Logs.d("Qianji_Match","匹配规则："+content);

        if(!Regex.isMatch(nodeListStr,content)) return false;

        Logs.d("Qianji_Match","匹配成功！");
        return true;
    }

    public void onServiceConnected() {

        super.onServiceConnected();
        isStart=true;
        Logs.i("辅助功能已启用");
        ServerManger.startAutoNotify(getApplicationContext());
        ServerManger.startSms(getApplicationContext());
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }
}