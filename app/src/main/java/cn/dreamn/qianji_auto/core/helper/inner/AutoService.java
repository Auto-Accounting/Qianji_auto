package cn.dreamn.qianji_auto.core.helper.inner;

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
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.core.helper.AutoBillService;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


public class AutoService extends AccessibilityService {

    public static List<String> nodeList = new ArrayList<>();
    public static boolean l;
    public static boolean m;
    public final ExecutorService h;
    public volatile boolean useful;
    public volatile int flag;



    public AutoService() {
        this.useful = false;
        this.flag = 0;
        this.h = Executors.newSingleThreadExecutor();
    }

    public static List<String> ergodicList(AutoService autoService, AccessibilityNodeInfo nodeInfo) {
        Objects.requireNonNull(autoService);
        if (nodeInfo == null) return nodeList;
        if (nodeList.size() > 100) {
            nodeList.remove(0);
        }
        autoService.ergodic(nodeInfo);
        return nodeList;
    }

    public static void clear(AutoService autoService) {
        autoService.useful = false;
        autoService.flag = 0;
    }

    public final void ergodic(AccessibilityNodeInfo nodeInfo) {
        int i;
        for (i = 0; i < nodeInfo.getChildCount() && nodeList != null && nodeList.size() <= 120; ++i) {
            AccessibilityNodeInfo nodeChild = nodeInfo.getChild(i);
            if (nodeChild != null && nodeChild.getChildCount() > 0) {
                ergodic(nodeChild);
            } else if (nodeChild != null && !TextUtils.isEmpty(nodeChild.getText()) && nodeList != null) {
                nodeList.add(nodeChild.getText().toString());
            }
        }
    }


    @Override  // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {


        String packageName = accessibilityEvent.getPackageName() == null ? "" : accessibilityEvent.getPackageName().toString();
        //获取监控范围数据
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getApplicationContext(), "apps", Context.MODE_PRIVATE);
        String[] apps = sharedPreferences.getString("apps", "").split(",");
        if (!isIn(apps, packageName)) return;
        //不再监控范围不管

        AccessibilityNodeInfo nodeInfo = null;
        try {
            nodeInfo = accessibilityEvent.getSource();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        String className = "";
        if (accessibilityEvent.getClassName() != null) {
            className = accessibilityEvent.getClassName().toString();
        }


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
                            SendDataToApp.call(getApplicationContext(), billInfo);
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
        } else {
            new Analyze(this, packageName, className, nodeInfo).run();
        }


    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override  // android.app.Service
    public void onDestroy() {
        this.stopForeground(true);
        super.onDestroy();
    }

    @Override  // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override  // android.accessibilityservice.AccessibilityService
    public void onServiceConnected() {
        getApplicationContext().startService(new Intent(getApplicationContext(), AutoBillService.class));
    }

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
}

