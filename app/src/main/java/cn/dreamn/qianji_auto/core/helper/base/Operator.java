/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.helper.base;


import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by popfisher on 2017/7/11.
 */

public class Operator {

    private static final Operator mInstance = new Operator();
    private Context mContext;
    private AccessibilityEvent mAccessibilityEvent;
    private AccessibilityService mAccessibilityService;

    private Operator() {
    }

    public static Operator getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void updateEvent(AccessibilityService service, AccessibilityEvent event) {
        if (service != null && mAccessibilityService == null) {
            mAccessibilityService = service;
        }
        if (event != null) {
            mAccessibilityEvent = event;
        }
    }

    public boolean isServiceRunning() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(mContext.getPackageName() + ".AutoReadAcessibilityService")) {
                return true;
            }
        }
        return false;
    }

    public AccessibilityNodeInfo getRootNodeInfo() {
        AccessibilityEvent curEvent = mAccessibilityEvent;
        AccessibilityNodeInfo nodeInfo = null;
        // 建议使用getRootInActiveWindow，这样不依赖当前的事件类型
        if (mAccessibilityService != null) {
            nodeInfo = mAccessibilityService.getRootInActiveWindow();
            //   Logs.d("nodeInfo: " + nodeInfo);
        }
        // 下面这个必须依赖当前的AccessibilityEvent
        //   nodeInfo = curEvent.getSource();
        // Logs.d("Node-Info","nodeInfo: " + nodeInfo);
        return nodeInfo;
    }

    /**
     * 根据Text搜索所有符合条件的节点, 模糊搜索方式
     */
    public List<AccessibilityNodeInfo> findNodesByText(String text) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);
            if (nodes.size() == 0) return null;
            return nodes;
        }
        return null;
    }


    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     *
     * @param viewId
     */
    public List<AccessibilityNodeInfo> findNodesById(String viewId) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
            if (nodes.size() == 0) return null;
            return nodes;
        }
        return null;
    }

    public String findNodesByDesc(String desc) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
            CharSequence contentDescription = nodeInfo.getContentDescription();
            if (contentDescription == null) return null;
            if (contentDescription.toString().contains(desc)) {
                return contentDescription.toString();
            }
        }
        return null;
    }

    public String findNodesByDescRegex(String desc) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
            CharSequence contentDescription = nodeInfo.getContentDescription();
            if (contentDescription == null) return null;
            if (findHook(desc, contentDescription.toString())) {
                return contentDescription.toString();
            }
        }
        return null;
    }

    public boolean findHook(String content, String Str) {


        //    Logs.d("Qianji_Match", "匹配文本：" + Str);
        //  Logs.d("Qianji_Match", "匹配规则：" + content);

        return Regex.isMatch(Str, content);

        //Logs.d("Qianji_Match", "匹配成功！");
    }


    public String isNeedPage(String[] text, String[] id, String desc, boolean reg) {
        if (text != null) {
            for (String s : text) {
                if (findNodesByText(s) == null) return null;
            }
        }
        if (id != null) {
            for (String s : id) {
                if (findNodesById(s) == null) return null;
            }
        }
        if (desc != null) {
            if (reg) return findNodesByDescRegex(desc);
            else return findNodesByDesc(desc);
        }
        return null;
    }


    public boolean clickByText(String text) {
        return performClick(findNodesByText(text));
    }

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     *
     * @param viewId
     * @return 是否点击成功
     */
    public boolean clickById(String viewId) {
        return performClick(findNodesById(viewId));
    }

    private boolean performClick(List<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < nodeInfos.size(); i++) {
                node = nodeInfos.get(i);
                // 获得点击View的类型
                //Logs.d("View类型：" + node.getClassName());
                // 进行模拟点击
                if (node.isEnabled()) {
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    public boolean clickBackKey() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private boolean performGlobalAction(int action) {
        return mAccessibilityService.performGlobalAction(action);
    }
}