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

package cn.dreamn.qianji_auto.utils.tools;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.utils.XToastUtils;

import static com.xuexiang.xutil.XUtil.getContentResolver;
import static com.xuexiang.xutil.app.ActivityUtils.startActivity;
import static com.xuexiang.xutil.app.AppUtils.getPackageName;


public class Permission {


    public static final int Assist = 1;
    public static final int Sms = 2;
    public static final int Float = 3;
    public static final int Start = 4;
    public static final int Battery = 5;
    public static final int Lock = 6;
    public static final int BatteryIngore = 7;
    public static final int Security = 8;
    public static final int Storage = 9;
    public static final int All = 10;
    public static final int Notification = 11;
    static Permission permission;

    public static Permission getInstance() {
        if (permission == null) permission = new Permission();
        return permission;
    }


    @SuppressLint("BatteryLife")
    public void grant(Context context, int permission) {
        Intent intent;
        switch (permission) {
            case Assist:
                if (Status.isActive(context)) break;
                intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case Sms:

                XXPermissions.with(context).permission(com.hjq.permissions.Permission.READ_SMS).request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            XToastUtils.info("获取短信阅读权限成功");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            XToastUtils.error("被永久拒绝授权，请手动授予短信阅读权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                        } else {
                            XToastUtils.error("获取短信阅读权限失败");
                        }
                    }
                });
                XXPermissions.with(context).permission(com.hjq.permissions.Permission.RECEIVE_SMS).request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            XToastUtils.info("获取短信接收权限成功");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            XToastUtils.error("被永久拒绝授权，请手动授予短信接收权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                        } else {
                            XToastUtils.error("获取短信接收权限失败");
                        }
                    }
                });
                break;
            case Float:
                XXPermissions.with(context)

                        .permission(com.hjq.permissions.Permission.SYSTEM_ALERT_WINDOW)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all) {
                                    XToastUtils.info("获取悬浮窗权限成功");
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never) {
                                    XToastUtils.error("被永久拒绝授权，请手动授予悬浮窗权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(context, permissions);
                                } else {
                                    XToastUtils.error("获取悬浮窗权限失败");
                                }
                            }
                        });
                // FloatWindowPermission.getInstance().applyFloatWindowPermission(context);
                break;
            case Start:
                mobileInfoUtil.jumpStartInterface(context);
                break;
            case Battery:
// 将用户引导到系统设置页面
                intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(intent);
                break;
            case Lock:
                //TODO 直接跳到多任务界面
                break;
            case Security:
                //TODO 留待后期强化
                break;
            case BatteryIngore:
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
                //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
                if (!hasIgnored) {
                    intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
                break;

            case Storage:
                XXPermissions.with(context)
                        // 不适配 Android 11 可以这样写
                        //.permission(Permission.Group.STORAGE)
                        // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                        .permission(com.hjq.permissions.Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all) {
                                    XToastUtils.info("获取存储权限成功");
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never) {
                                    XToastUtils.error("被永久拒绝授权，请手动授予存储权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(context, permissions);
                                } else {
                                    XToastUtils.error("获取存储权限失败");
                                }
                            }
                        });
                break;
            case All:

                break;
            case Notification:

                if (!isNotificationListenersEnabled())
                    gotoNotificationAccessSetting();
                break;
            default:
                break;
        }
    }



    public boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
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

    protected void gotoNotificationAccessSetting() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                startActivity(intent);
                return;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


}
