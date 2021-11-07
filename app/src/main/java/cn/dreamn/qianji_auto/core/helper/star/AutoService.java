package cn.dreamn.qianji_auto.core.helper.star;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.dreamn.qianji_auto.core.helper.AutoBillService;


public class AutoService extends AccessibilityService {

    public static List<String> nodeList = new ArrayList<>();
    public static boolean l;
    public static boolean m;
    public final ExecutorService h;
    public volatile boolean useful;
    public volatile int flag;
    public volatile String payTool;
    public volatile boolean f;
    public volatile boolean g;


    public AutoService() {
        this.useful = false;
        this.flag = 0;
        this.payTool = null;
        this.f = false;
        this.g = false;
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
        autoService.f = false;
        autoService.useful = false;
        autoService.payTool = null;
        autoService.g = false;
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
    public void onAccessibilityEvent(AccessibilityEvent nodeEvent) {

        AccessibilityNodeInfo nodeInfo = null;
        try {
            nodeInfo = nodeEvent.getSource();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        String className = "";
        String packageName = nodeEvent.getPackageName() == null ? "" : nodeEvent.getPackageName().toString();
        if (nodeEvent.getClassName() != null) {
            className = nodeEvent.getClassName().toString();
        }

        new Analyze(this, packageName, className, nodeInfo).run();
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
}

