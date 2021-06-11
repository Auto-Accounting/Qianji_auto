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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cn.dreamn.qianji_auto.BuildConfig;


public class AppInfo {


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
    private static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
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
                        "xposed"
                };
        String[] appPackage = {
                "me.weishu.exp",
                "org.meowcat.edxposed.manager",
                "io.github.lsposed.manager",
                "com.bug.xposed",
                "com.bug.dreamn",
                "de.robv.android.xposed.installer"
        };

        for (int i = 0; i < appName.length; i++) {

            if (isAppInstalled(context, appPackage[i])) {
                farmework = appName[i];
            }
        }

        switch (farmework) {

            case "taichi":
                return "太极";
            case "bug":
                return "应用转生";
            case "edxposed":
                return "EdXposed";
            case "lsposed":
                return "LSPosed";
            case "xposed":
                return "Xposed";
            case "Dreamn":
                return "梦境";
            default:
                return "未知Xp";
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
