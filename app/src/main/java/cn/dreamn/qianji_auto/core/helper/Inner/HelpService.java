package cn.dreamn.qianji_auto.core.helper.Inner;

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import cn.dreamn.qianji_auto.core.helper.Inner.wechat.QrScan;
import cn.dreamn.qianji_auto.core.helper.Inner.wechat.RedPackage;
import cn.dreamn.qianji_auto.core.helper.Inner.wechat.Transerfer;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class HelpService extends AccessibilityService {


    public static String shopAccount = "";

    private boolean isIn(String[] packages, String pack) {
        boolean flag = false;
        for (String package1 : packages) {
            if (pack.equals(package1)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        String packageName = accessibilityEvent.getPackageName() == null ? "" : accessibilityEvent.getPackageName().toString();
        //获取监控范围数据
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getApplicationContext(), "apps", Context.MODE_PRIVATE);
        String[] apps = sharedPreferences.getString("apps", "").split(",");
        if (!isIn(apps, packageName)) return;
        //不再监控范围不管

        AccessibilityNodeInfo source = this.getRootInActiveWindow();
        if (source == null) {
            source = accessibilityEvent.getSource();
        }
        String className = accessibilityEvent.getClassName() == null ? "" : accessibilityEvent.getClassName().toString();


        int type = accessibilityEvent.getEventType();
        if (type == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
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

                    TaskThread.onThread(() -> {
                        Db.db.AppDataDao().add(str, "notice", packageName);
                    });

                    Handler mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            BillInfo billInfo = (BillInfo) msg.obj;
                            billInfo.setFromApp(packageName);
                            SendDataToApp.call(billInfo);
                        }
                    };
                    RegularCenter.getInstance("notice").run(packageName, str, null, new TaskThread.TaskResult() {
                        @Override
                        public void onEnd(Object obj) {
                            BillInfo billInfo = (BillInfo) obj;
                            if (billInfo != null) {
                                HandlerUtil.send(mHandler, billInfo, HANDLE_OK);
                            }
                        }
                    });


                }
            }
        } else if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            NodeListManage.updateEvent(packageName, className);

            NodeListManage.findNodeInfo(source, true);

            Log.i("-----------------无障碍窗口内容变化事件---------------------");

            Log.i("当前页面数据：class=" + className + ",globalNodeList=" + (NodeListManage.globalNodeList.toString()));


            BillInfo billInfo = null;

            List<String> nodeList = NodeListManage.globalNodeList;
            if (NodeListManage.isNullOrEmpty(nodeList)) return;

            if (packageName.equals("com.tencent.mm")) {
                if (NodeListManage.isNeedPage(new String[]{"发红包", "塞钱进红包", "红包封面"}, null)) {
                    Log.i("[页面识别]微信红包备注页面");
                    RedPackage.findMoneyAndRemark(nodeList);
                } else if (NodeListManage.isNeedPage(new String[]{"微信红包", "￥", "微信支付", "请"}, "android.widget.FrameLayout")) {
                    Log.i("[页面识别]微信红包确认页面");
                    billInfo = RedPackage.run(nodeList);
                } else if (NodeListManage.isNeedPage(new String[]{"的红包", "红包金额", "元，等待对方领取", "未领取的红包", "将于24小时后发起退款"}, null)) {
                    Log.i("[页面识别]微信发红包详情");
                    billInfo = RedPackage.runInDetail(nodeList);
                } else if (NodeListManage.isNeedPage(new String[]{"的红包", "元", "回复表情到聊天"}, null)) {
                    Log.i("[页面识别]微信收红包详情");
                    billInfo = RedPackage.runReceiveInDetail(nodeList);
                }

                //微信转账

                else if (NodeListManage.isNeedPage(new String[]{"转账说明", "取消", "确定"}, null)) {
                    Log.i("[页面识别]微信转账说明");
                    Transerfer.findRemark(nodeList);
                } else if (NodeListManage.isNeedPage(new String[]{"^向", "转账$", "￥"}, null)) {
                    Log.i("[页面识别]微信转账识别");
                    billInfo = Transerfer.run(nodeList);
                } else if (NodeListManage.isNeedPage(new String[]{"待", "收款", "1天内对方未收款，将退还给你。提醒对方收款", "转账时间"}, null)) {
                    Log.i("[页面识别]微信转账详情识别");
                    billInfo = Transerfer.runInDetail(nodeList);
                }
                //微信扫码支付
                else if (NodeListManage.isNeedPage(new String[]{"完成", "支付成功", "¥"}, null)) {
                    Log.i("[页面识别]微信扫码识别");
                    billInfo = QrScan.run(nodeList);
                }
            }


            if (billInfo != null) {
                NodeListManage.goApp(getApplicationContext(), billInfo);
            }


            Log.i("-----------------无障碍事件结束---------------------");

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
