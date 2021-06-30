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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Method;
import java.util.List;

import es.dmoral.toasty.Toasty;


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


    private final Context mContext;

    public PermissionUtils(Context context) {
        mContext = context;
    }


    @SuppressLint("BatteryLife")
    public void grant(int permission) {
        Intent intent;
        if (isGrant(permission).equals("1")) return;
        switch (permission) {
            case Assist:
                intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case Sms:

                XXPermissions with = XXPermissions.with(mContext);
                with.permission(Permission.RECEIVE_SMS);
                with.request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toasty.success(mContext, "获取短信权限成功!", Toast.LENGTH_LONG, true).show();

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toasty.error(mContext, "被永久拒绝授权，请手动授予短信权限", Toast.LENGTH_LONG, true).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(mContext, permissions);
                        } else {
                            Toasty.error(mContext, "获取短信权限失败", Toast.LENGTH_LONG, true).show();
                        }
                    }
                });// .permission(Permission.READ_SMS)

                break;
            case Float:
                XXPermissions.with(mContext)
                        .permission(Permission.SYSTEM_ALERT_WINDOW)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                if (all) {
                                    Toasty.success(mContext, "获取悬浮窗权限成功", Toast.LENGTH_LONG, true).show();
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                if (never) {
                                    Toasty.error(mContext, "被永久拒绝授权，请手动授予悬浮窗权限", Toast.LENGTH_LONG, true).show();
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(mContext, permissions);
                                } else {
                                    Toasty.error(mContext, "获取悬浮窗权限失败", Toast.LENGTH_LONG, true).show();
                                }
                            }
                        });
                break;
            case Start:
                intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
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
                try {
                    XXPermissions.with(mContext)
                            // 不适配 Android 11 可以这样写
//                            .permission(Permission.Group.STORAGE)
                            // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE

                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        Toasty.success(mContext, "获取存储权限成功", Toast.LENGTH_LONG, true).show();
                                    }
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    if (never) {
                                        Toasty.error(mContext, "被永久拒绝授权，请手动授予存储权限", Toast.LENGTH_LONG, true).show();
                                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                        XXPermissions.startPermissionActivity(mContext, permissions);
                                    } else {
                                        Toasty.error(mContext, "获取存储权限失败", Toast.LENGTH_LONG, true).show();
                                    }
                                }
                            });
                } catch (Exception exception) {
                    exception.getStackTrace();
                }
                break;
            case StorageReadExt:
                Toasty.error(mContext, "请手动授予存储权限", Toast.LENGTH_LONG, true).show();
                XXPermissions.startPermissionActivity(mContext, Permission.READ_EXTERNAL_STORAGE);
                break;
            default:
                break;
        }
    }

    public String isGrant(int permission) {
        switch (permission) {
            case Assist:
                return Accessibility.isAccessibilityEnabled(mContext) ? "1" : "0";
            case Sms:
                return XXPermissions.isGranted(mContext, Permission.RECEIVE_SMS) ? "1" : "0";

            case Float:
                return XXPermissions.isGranted(mContext, Permission.SYSTEM_ALERT_WINDOW) ? "1" : "0";
            case Start:
                return isAllowAutoStart(mContext, mContext.getPackageName());
            case Battery:

                break;
            case Lock:

                break;
            case Hide:
                MMKV mmkv = MMKV.defaultMMKV();
                return mmkv.getBoolean("tasker_show", true) ? "0" : "1";
            case BatteryIngore:
                PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(mContext.getPackageName());
                return hasIgnored ? "1" : "0";

            case Storage:
                return XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE) ? "1" : "0";
            case StorageReadExt:
                return XXPermissions.isGranted(mContext, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE) ? "1" : "0";
            default:
                break;

        }
        return "-1";
    }

    //判断自启
    public static String isAllowAutoStart(Context context, String packageName) {
        try {
            @SuppressLint("PrivateApi")
            Method method = Class.forName("android.miui.AppOpsUtils")
                    .getMethod("getApplicationAutoStart", Context.class, String.class);
            return (int) method.invoke(null, context, packageName) == 0 ? "0" : "1"; //0已允许, 1已拒绝
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //如果系统更新改了api可能导致没法判断
        return "-1";
    }

    public void showTaskBar() {
        MMKV mmkv = MMKV.defaultMMKV();
        boolean isShow = mmkv.getBoolean("tasker_show", true);
        mmkv.encode("tasker_show", !isShow);
        Toasty.success(mContext, "多任务状态：" + (isShow ? "显示" : "隐藏"), Toast.LENGTH_LONG, true).show();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.AppTask> tasks;
            tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                tasks.get(0).setExcludeFromRecents(!isShow);
            }
        }

    }


}
