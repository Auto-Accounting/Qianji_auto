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
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


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


    private static boolean method_pm(String targets) {
        Set<String> packages = new HashSet<>();
        try {
            java.lang.Process p = Runtime.getRuntime().exec("pm list packages");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 8) {
                    String prefix = line.substring(0, 8);
                    if (prefix.equalsIgnoreCase("package:")) {
                        line = line.substring(8).trim();
                        if (!TextUtils.isEmpty(line))
                            packages.add(line);
                    }
                }
            }
            br.close();
            p.destroy();
        } catch (Exception e) {
            Log.d("install", targets + " Error " + e.toString());
        }
        if (packages.isEmpty()) packages = null;
        //    Log.d("install",packages.toString());
        return findPackages(packages, targets);
    }


    private static boolean findPackages(Set<String> packages, String targets) {
        if (packages == null) return false;
        for (String name : packages) {
            //   Log.d("install","name "+name+" packages " +targets);
            if (name.contains(targets))
                return true;
        }

        return false;
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

    //判断是否安装某个框架
    public static String getFrameWork(Context context) {
        String farmework = "";//判断加载的框架
        String[] appName =
                {
                        "taichi",
                        "edxposed",
                        "lsposed",
                        "bug",
                        "Dreamn",
                        "xposed",
                        "lsposed_old",
                        "sandvxposed",
                        "vx"
                };
        String[] appPackage = {
                "me.weishu.exp",
                "org.meowcat.edxposed.manager",
                "org.lsposed.manager",
                "com.bug.xposed",
                "top.canyie.dreamland.manager",
                "de.robv.android.xposed.installer",
                "io.github.lsposed.manager",
                "io.virtualapp.sandvxposed",
                "io.va.exposed64"
        };

        for (int i = 0; i < appName.length; i++) {
            if (method_pm(appPackage[i])) {
                farmework = appName[i];
                break;
            }
        }

        switch (farmework) {

            case "taichi":
                return context.getResources().getString(R.string.frame_taichi);
            case "bug":
                return context.getResources().getString(R.string.frame_bug);
            case "edxposed":
                return context.getResources().getString(R.string.frame_edxposed);
            case "lsposed":
                return context.getResources().getString(R.string.frame_lsposed);
            case "lsposed_old":
                return context.getResources().getString(R.string.frame_lsposed_old);
            case "xposed":
                return context.getResources().getString(R.string.frame_xposed);
            case "Dreamn":
                return context.getResources().getString(R.string.frame_dreamn);
            case "sandvxposed":
                return context.getResources().getString(R.string.frame_sandvxposed);
            case "vx":
                return context.getResources().getString(R.string.frame_vx);
            default:
                return context.getResources().getString(R.string.frame_others);
        }
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
