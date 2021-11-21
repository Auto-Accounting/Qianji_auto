package cn.dreamn.qianji_auto.core.helper.inner;


import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.core.helper.inner.pkg.alipay;
import cn.dreamn.qianji_auto.core.helper.inner.pkg.baseHelper;
import cn.dreamn.qianji_auto.core.helper.inner.pkg.mt;
import cn.dreamn.qianji_auto.core.helper.inner.pkg.unionOrjd;
import cn.dreamn.qianji_auto.core.helper.inner.pkg.wechat;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


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
        try {
            if (wechat.findWechatPayUi(packageName, className, nodeInfo)) {
                Log.i("[auto]微信支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_UI;
            } else if (wechat.findWechatPayUi2(className)) {
                Log.i("[auto]微信零钱通_WX_PAY");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_UI;
            } else if (wechat.findWechatPayBill(className)) {
                Log.i("[auto]微信零钱通_WX_BILL");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_MONEY_UI;
            } else if (wechat.findWechatPayDetail(packageName, nodeInfo)) {
                Log.i("[auto]微信账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_PAY_MONEY_UI;
            } else if (wechat.findWechatPayDetail2(packageName, className)) {
                Log.i("[auto]微信新版账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_WECHAT_DETAIL_UI_2;
            } else if (alipay.o(packageName, className, nodeInfo)) {
                Log.i("[auto]支付宝支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_ALIPAY_PAY_UI;
            } else if (alipay.l(packageName, nodeInfo)) {
                Log.i("[auto]支付宝账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_ALIPAY_PAY_DETAIL_UI;
            } else if (unionOrjd.o(packageName, className, nodeInfo)) {
                Log.i("[auto]云闪付支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_UNION_PAY_UI;
            } else if (unionOrjd.m(packageName, className, nodeInfo)) {
                Log.i("[auto]云闪付账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_UNION_PAY_DETAIL_UI;
            } else if (mt.l(packageName, className)) {
                Log.i("[auto]美团账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_MT_PAY_DETAIL_UI;
            } else if (unionOrjd.l(packageName, nodeInfo)) {
                Log.i("[auto]京东账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_JD_PAY_DETAIL_UI;
            } else if (unionOrjd.n(className)) {
                Log.i("[auto]京东支付界面");
                autoService.useful = true;
                autoService.flag = FLAG_JD_PAY_UI;
            } else if (alipay.m(packageName, className)) {
                Log.i("[auto]拼多多账单详情");
                autoService.useful = true;
                autoService.flag = FLAG_PDD_DETAIL_UI;
            } else if (baseHelper.isInApp(className)) {
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

                if (autoService.flag == FLAG_WECHAT_DETAIL_UI_2 && !wechat.isUseful(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_MT_PAY_DETAIL_UI && !mt.m(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_PDD_DETAIL_UI && !alipay.n(nodeListInfo)) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                if (autoService.flag == FLAG_ALIPAY_PAY_DETAIL_UI && (checkNode(nodeListInfo, "添加照片", true))) {
                    Log.i("[auto]无效账单详情");
                    AutoService.clear(autoService);
                    return;
                }

                String s = "Flag:" + autoService.flag + ",Data:" + nodeListInfo.toString();

                String identify = "helper";

                TaskThread.onThread(() -> {
                    Db.db.AppDataDao().add(s, identify, packageName);
                });
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        BillInfo billInfo = (BillInfo) msg.obj;
                        billInfo.setFromApp(AppInfo.getName(autoService.getApplicationContext(), packageName));

                        //  billInfo.setFromApp(app);
                        goApp(autoService.getApplicationContext(), billInfo);
                    }
                };

                RegularCenter.getInstance(identify).run(packageName, s, null, new TaskThread.TaskResult() {
                    @Override
                    public void onEnd(Object obj) {
                        BillInfo billInfo = (BillInfo) obj;
                        if (billInfo != null) {
                            Log.i("[auto]解析成功");
                            AutoService.clear(autoService);
                            HandlerUtil.send(mHandler, billInfo, HANDLE_OK);
                        } else {
                            Log.i("[auto]解析失败");
                        }
                    }
                });
            }
        } catch (Throwable v0) {
            Log.i("出现异常");
            v0.printStackTrace();
        }


    }

}

