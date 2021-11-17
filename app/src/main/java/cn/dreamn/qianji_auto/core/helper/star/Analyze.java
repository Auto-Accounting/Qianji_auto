package cn.dreamn.qianji_auto.core.helper.star;


import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.core.helper.star.b6.a;
import cn.dreamn.qianji_auto.core.helper.star.b6.c;
import cn.dreamn.qianji_auto.core.helper.star.b6.d;
import cn.dreamn.qianji_auto.core.helper.star.b6.e;
import cn.dreamn.qianji_auto.core.helper.star.b6.f;
import cn.dreamn.qianji_auto.core.helper.star.b6.g;
import cn.dreamn.qianji_auto.core.helper.star.b6.h;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


public class Analyze implements Runnable {

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
    public static final int FLAG_WECHAT_DETAIL_UI_2 = 11;
    //新版微信账单详情

    public static long time;
    public final String packageName;
    public final String className;
    public final AccessibilityNodeInfo nodeInfo;
    public final AutoService autoService;

    public Analyze(AutoService autoService, String packageName, String className, AccessibilityNodeInfo nodeInfo) {
        this.autoService = autoService;
        this.packageName = packageName;
        this.className = className;
        this.nodeInfo = nodeInfo;
        Log.init("helper");
    }

    public static void goApp(Context context, BillInfo billInfo) {
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

    //验证节点
    public static boolean checkNode(List<String> nodeList, String checkStr, boolean equals) {
        //从最后的数据开始查找
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            String nodeName = nodeList.get(i);
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

    public static boolean isNullOrEmpty(List<String> list) {
        return list == null || list.size() == 0;
    }

    @Override
    public void run() {
        Log.i("开始分析页面");
        boolean v3;
        a v0_6;
        d v0_5;
        d v0_4;
        f v15 = null;
        int v10;
        boolean alipayRedPackage;
        int receivedType;
        boolean paySuccess;
        try {
            if (h.findWechatPayUi(packageName, className, nodeInfo)) {
                Log.i("[auto]微信支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_UI;
            } else if (h.findWechatPayUi2(className)) {
                Log.i("[auto]微信零钱通_WX_PAY");
                autoService.useful = true;
                autoService.g = true;
                autoService.flag = FLAG_WECHAT_PAY_UI;
            } else if (h.findWechatPayBill(className)) {
                Log.i("[auto]微信零钱通_WX_BILL");
                autoService.useful = true;
                autoService.g = true;
                autoService.flag = FLAG_WECHAT_PAY_MONEY_UI;
            } else if (h.findWechatPayDetail(packageName, nodeInfo)) {
                Log.i("[auto]微信账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_MONEY_UI;
            } else if (h.findWechatPayDetail2(packageName, className)) {
                Log.i("[auto]微信新版账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_DETAIL_UI_2;
            } else if (a.o(packageName, className, nodeInfo)) {
                Log.i("[auto]支付宝支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_ALIPAY_PAY_UI;
            } else if (a.l(packageName, nodeInfo)) {
                Log.i("[auto]支付宝账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_ALIPAY_PAY_DETAIL_UI;
            } else if (d.o(packageName, className, nodeInfo)) {
                Log.i("[auto]云闪付支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_UNION_PAY_UI;
            } else if (d.m(packageName, className, nodeInfo)) {
                Log.i("[auto]云闪付账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_UNION_PAY_DETAIL_UI;
            } else if (e.l(packageName, className)) {
                Log.i("[auto]美团账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_MT_PAY_DETAIL_UI;
            } else if (d.l(packageName, nodeInfo)) {
                Log.i("[auto]京东账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_JD_PAY_DETAIL_UI;
            } else if (d.n(className)) {
                Log.i("[auto]京东支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_JD_PAY_UI;
            } else if (a.m(packageName, className)) {
                Log.i("[auto]拼多多账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_PDD_DETAIL_UI;
            } else if (c.isInApp(className)) {
                autoService.useful = false;
                Log.i("[auto]退出");
                AutoService.clear(autoService);
            }

            if (!autoService.useful) {
                Log.i("识别结束,淘汰");
                return;
            }
            List<String> nodeListInfo = AutoService.ergodicList(autoService, nodeInfo);
            AutoService.nodeList = new ArrayList<>();
            if (!isNullOrEmpty(nodeListInfo)) {

                if (AppStatus.isDebug()) {
                    String s = "Pacakge:" + packageName + "\nFlag:" + autoService.flag + "\n数据部分:" + nodeListInfo.toString();
                    BottomArea.msg(autoService.getApplicationContext(), "当前数据抓取", s, "复制数据", "关闭", true, new BottomArea.MsgCallback() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void sure() {
                            Tool.clipboard(autoService.getApplicationContext(), nodeListInfo.toString());
                            ToastUtils.show("数据已复制，请私聊群主数据");
                            //Tool.goUrl(autoService.getApplicationContext(), "http://wpa.qq.com/msgrd?v=3&uin=573658513&site=qq&menu=yes");
                        }
                    });
                    Log.i("无障碍调试数据：" + nodeListInfo.toString());
                    return;
                }

                if (autoService.flag == FLAG_WECHAT_DETAIL_UI_2 && !h.isUseful(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_MT_PAY_DETAIL_UI && !e.m(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_PDD_DETAIL_UI && !a.n(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_ALIPAY_PAY_DETAIL_UI && (checkNode(nodeListInfo, "添加照片", true))) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                paySuccess = checkNode(nodeListInfo, "支付成功", true) || checkNode(nodeListInfo, "充值成功", true);

                if (autoService.flag == FLAG_ALIPAY_PAY_UI && !paySuccess) {
                    paySuccess = checkNode(nodeListInfo, "代付成功", true);
                }


                if (autoService.flag == FLAG_WECHAT_PAY_UI) {
                    if (autoService.g) {
                        paySuccess = true;
                    } else {
                        int nodeIndex = nodeListInfo.indexOf("支付方式");
                        if (nodeIndex != -1) {
                            int nodeIndex2 = nodeIndex + 1;
                            if (nodeIndex2 < nodeListInfo.size()) {
                                autoService.payTool = nodeListInfo.get(nodeIndex2);
                                Log.i("[auto]微信支付 " + autoService.payTool);
                            }
                        }

                        int nodexIndex3 = nodeListInfo.indexOf("优先使用此支付方式付款") - 1;
                        if (nodexIndex3 >= 0) {
                            autoService.payTool = nodeListInfo.get(nodexIndex3);
                            Log.i("[auto]微信支付 " + autoService.payTool);
                        }
                        if (nodeListInfo.get(0).endsWith("发起的群收款")) {
                            if (!checkNode(nodeListInfo, "已收到", false)) {
                            }
                            if (!checkNode(nodeListInfo, "你需支付", false)) {
                            }
                        }
                    }
                }


                if (!paySuccess) {
                    if (autoService.flag != FLAG_WECHAT_DETAIL_UI_2 && autoService.flag != FLAG_WECHAT_PAY_MONEY_UI && autoService.flag != FLAG_ALIPAY_PAY_DETAIL_UI && autoService.flag != FLAG_MT_PAY_DETAIL_UI && autoService.flag != FLAG_JD_PAY_DETAIL_UI) {
                        if (autoService.flag != FLAG_PDD_DETAIL_UI) {
                            if (autoService.flag == FLAG_ALIPAY_PAY_UI) {
                                paySuccess = (checkNode(nodeListInfo, "交易成功", true)) || (checkNode(nodeListInfo, "还款成功", true)) || (checkNode(nodeListInfo, "退款成功", true)) || (checkNode(nodeListInfo, "自动扣款成功", true)) || (checkNode(nodeListInfo, "有退款", true)) || (checkNode(nodeListInfo, "已全额退款", true)) || (checkNode(nodeListInfo, "亲情卡付款成功", true)) || (checkNode(nodeListInfo, "付款成功", false));
                            } else if (autoService.flag == FLAG_UNION_PAY_DETAIL_UI) {
                                autoService.f = true;
                            }
                        }
                    } else {
                        paySuccess = true;
                    }
                }

                boolean transferSuccess = checkNode(nodeListInfo, "转账成功", true);
                if (!checkNode(nodeListInfo, "已收款", true) && !checkNode(nodeListInfo, "资金待入账", true) && !checkNode(nodeListInfo, "你已收款", true)) {
                    receivedType = autoService.flag == FLAG_WECHAT_PAY_UI && ((checkNode(nodeListInfo, "已收款", false)) || (checkNode(nodeListInfo, "提醒对方收款", false))) ? 3 : 0;
                } else {
                    receivedType = 1;
                }

                if (checkNode(nodeListInfo, "支付宝红包", true)) {
                    alipayRedPackage = true;
                } else {
                    alipayRedPackage = autoService.flag == FLAG_ALIPAY_PAY_UI && (checkNode(nodeListInfo, "红包编号", false));
                }

                if (alipayRedPackage) {
                    if (!checkNode(nodeListInfo, "红包金额", false) && !checkNode(nodeListInfo, "人已领取", false) && !checkNode(nodeListInfo, "领取成功", false)) {
                        v10 = 0;
                    } else {
                        v10 = 1;
                    }
                } else {
                    if ((checkNode(nodeListInfo, "的红包", false)) && autoService.flag != FLAG_UNION_PAY_DETAIL_UI) {
                        v10 = 1;
                    } else v10 = 0;
                }

                if (paySuccess) {
                    int v0_2 = autoService.flag;
                    if (v0_2 != FLAG_ALIPAY_PAY_UI && v0_2 != FLAG_ALIPAY_PAY_DETAIL_UI) {
                        if (v0_2 == 1) {
                            h v0_3 = new h();
                            v0_3.b = autoService.payTool;
                            v0_3.c = autoService.g;
                            v15 = v0_3.r(nodeListInfo, 1);

                        } else if (v0_2 != FLAG_WECHAT_PAY_MONEY_UI && v0_2 != FLAG_WECHAT_DETAIL_UI_2) {
                            if (v0_2 == FLAG_UNION_PAY_UI) {
                                v0_4 = new d(1);
                                v15 = v0_4.p(nodeListInfo, 1);

                            } else if (v0_2 == FLAG_UNION_PAY_DETAIL_UI) {
                                v0_5 = new d(1);
                                v15 = v0_5.p(nodeListInfo, 0);

                            } else if (v0_2 == FLAG_MT_PAY_DETAIL_UI) {
                                v15 = new e().n(nodeListInfo, 0);

                            } else if (v0_2 == FLAG_JD_PAY_DETAIL_UI) {
                                v0_5 = new d(0);
                                v15 = v0_5.p(nodeListInfo, 0);

                            } else if (v0_2 == FLAG_JD_PAY_UI) {
                                v0_4 = new d(0);
                                v15 = v0_4.p(nodeListInfo, 1);

                            } else if (v0_2 == FLAG_PDD_DETAIL_UI) {
                                v0_6 = new a(1);
                                v15 = v0_6.p(nodeListInfo, 0);

                            }
                        } else {
                            h v0_7 = new h();
                            v0_7.c = autoService.g;
                            v15 = v0_7.r(nodeListInfo, 0);
                        }
                    } else {
                        v0_6 = new a(0);
                        v15 = v0_6.p(nodeListInfo, 0);
                    }


                } else {
                    if (transferSuccess) {
                        v15 = new a(0).p(nodeListInfo, 2);

                    } else if (receivedType == 0) {
                        if (v10 == 0) {
                            v15 = null;
                        }

                        g v0_8 = new g();
                        if (autoService.flag == FLAG_ALIPAY_PAY_DETAIL_UI) {
                            v3 = true;
                        } else {
                            v3 = autoService.flag == FLAG_ALIPAY_PAY_UI;
                        }

                        v0_8.isAlipay = alipayRedPackage;
                        v0_8.c = v3;
                        if (autoService.flag == FLAG_WECHAT_PAY_UI && (checkNode(nodeListInfo, "已存入零钱", false))) {
                            v15 = v0_8.m(nodeListInfo);
                        } else {
                            v15 = v0_8.l(nodeListInfo, 0);
                            if (v15 == null) {
                                v15 = v0_8.m(nodeListInfo);
                            }

                        }
                    } else if (autoService.flag == FLAG_ALIPAY_PAY_UI) {
                        v0_6 = new a(0);
                        v15 = v0_6.p(nodeListInfo, 0);

                    } else if (receivedType == 1) {
                        v15 = new h().r(nodeListInfo, 3);
                    } else {
                        v15 = new h().r(nodeListInfo, 2);
                    }
                }


                if (v15 != null && v15.d != null) {
                    Log.i("[auto]解析成功");
                    AutoService.clear(autoService);
                    if (v15.g) {
                        Log.i("same pay return");
                        return;
                    }
                    BillInfo billInfo = v15.d;
                    goApp(autoService.getApplicationContext(), billInfo);
                    return;
                }

                Log.i("[auto]解析失败");


            }
        } catch (Throwable v0) {
            Log.i("出现异常");
            v0.printStackTrace();
        }


    }

}

