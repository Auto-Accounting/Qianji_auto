/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.setting;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.util.Objects;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import de.robv.android.xposed.XposedBridge;


public class AppInfo {
    public static Drawable getIcon(Context context, String pkg) {
        try {
            //包管理操作管理类
            PackageManager pm = context.getApplicationContext().getPackageManager();
            //获取到应用信息
            ApplicationInfo applicationInfo = pm.getApplicationInfo(pkg, 0);
            return pm.getApplicationIcon(applicationInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Context context, String pkg) {
        try {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(pkg, 0);
            return packageInfo;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getName(Context context, String pkg) {
        try {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(pkg, 0);
            return packageInfo.applicationInfo.loadLabel(pm).toString();
        } catch (Throwable e) {
            e.printStackTrace();
            return pkg;
        }
    }

    public static String getAppVersionName(Context context, String packageName) {

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        //System.out.println("没有安装");
        //System.out.println("已经安装");
        if (packageInfo != null) {
            return packageInfo.versionName;
        }

        return "";
    }




    /*
     * check the app is installed
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;

            Log.d("install", packagename + " Error " + e.toString());
        }
        //System.out.println("没有安装");
        //System.out.println("已经安装");
        return packageInfo != null;
    }

    public static String getFrameWork() {

        return "Unknown";
    }
    /**
     * 获取当前app version code
     */
    public static long getAppVerCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 获取当前app version name
     */
    public static String getAppVerName() {

        return BuildConfig.VERSION_NAME;
    }

    public static String getAppPackage() {
        return BuildConfig.APPLICATION_ID;
    }
}
