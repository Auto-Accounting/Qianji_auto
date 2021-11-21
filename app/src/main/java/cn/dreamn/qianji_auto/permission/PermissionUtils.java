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

package cn.dreamn.qianji_auto.permission;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.broadcast.NotificationMonitor;


public class PermissionUtils {


    public static final int Assist = 1;//辅助功能
    public static final int Sms = 2;//短信
    public static final int Float = 3;//悬浮窗
    public static final int Start = 4;//自启动
    public static final int Battery = 5;//省电策略
    public static final int Lock = 6;//锁定
    public static final int BatteryIngore = 7;//电池忽略
    public static final int Hide = 8;//多任务隐藏
    public static final int Storage = 9;//存储权限
    public static final int StorageReadExt = 10;//外部储存读取权限
    public static final int Notice = 11;//外部储存读取权限

    private final Context mContext;

    public PermissionUtils(Context context) {
        mContext = context;
    }


    @SuppressLint("BatteryLife")
    public void grant(int permission) {
        Intent intent;

        switch (permission) {
            case Assist:
                intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ToastUtils.show(R.string.permission_helper_tip);
                break;
            case Sms:
                grantPermission(Permission.RECEIVE_SMS);
                break;
            case Float:
                grantPermission(Permission.SYSTEM_ALERT_WINDOW);
                break;
            case Start:
                AutoStart.startToAutoStartSetting(mContext);
                break;
            case Battery:
// 将用户引导到系统设置页面
                intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
                mContext.startActivity(intent);
                break;
            case Lock:
                ToastUtils.show(R.string.permission_lock_tip);
                break;
            case Hide:
                showTaskBar();
                break;
            case BatteryIngore:
                intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
                break;

            case Storage:
                grantPermission(Permission.Group.STORAGE);
                grantPermission(Permission.MANAGE_EXTERNAL_STORAGE);
                break;
            case StorageReadExt:
                ToastUtils.show(R.string.permission_storage_tip);
                XXPermissions.startPermissionActivity(mContext, Permission.READ_EXTERNAL_STORAGE);
                break;
            case Notice:
                try {
                    Intent intent1;
                    intent1 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    mContext.startActivity(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NotificationMonitor.start(mContext);
                break;
            default:
                break;
        }
    }

    public String isGrant(int permission) {
        switch (permission) {
            case Assist:
                return "0";
            case Sms:
                return XXPermissions.isGranted(mContext, Permission.RECEIVE_SMS) ? "1" : "0";
            case Float:
                return XXPermissions.isGranted(mContext, Permission.SYSTEM_ALERT_WINDOW) ? "1" : "0";
            case Hide:
                MMKV mmkv = MMKV.defaultMMKV();
                return mmkv.getBoolean("tasker_show", true) ? "1" : "0";
            case BatteryIngore:
                PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(mContext.getPackageName());
                return hasIgnored ? "1" : "0";

            case Storage://Permission.Group.STORAGE
                return (XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE) && XXPermissions.isGranted(mContext, Permission.Group.STORAGE)) ? "1" : "0";
            case StorageReadExt:
                return XXPermissions.isGranted(mContext, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE) ? "1" : "0";
            case Notice:
                return isNotificationListenersEnabled() ? "1" : "0";
            default:
                break;

        }
        return "-1";
    }


    public void showTaskBar() {
        MMKV mmkv = MMKV.defaultMMKV();
        boolean isShow = mmkv.getBoolean("tasker_show", true);
        mmkv.encode("tasker_show", !isShow);
        ToastUtils.show(String.format(mContext.getString(R.string.permission_task), (isShow ? mContext.getString(R.string.permission_task_show) : mContext.getString(R.string.permission_task_hide))));
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.AppTask> tasks;
            tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                tasks.get(0).setExcludeFromRecents(!isShow);
            }
        }

    }

    private void grantPermission(String... permission) {
        XXPermissions with = XXPermissions.with(mContext);
        with.permission(permission);
        with.request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    ToastUtils.show(R.string.permission_success);
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never) {
                    ToastUtils.show(R.string.permission_never);
                    XXPermissions.startPermissionActivity(mContext, permissions);
                } else {
                    ToastUtils.show(R.string.permission_fail);
                }
            }
        });
    }

    public boolean isNotificationListenersEnabled() {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
