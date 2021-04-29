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

package cn.dreamn.qianji_auto.core.hook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Utils {
    public static final String SEND_ACTION = "cn.dreamn.qianji_auto.XPOSED";
    public static final String SEND_LOG_ACTION = "cn.dreamn.qianji_auto.XPOSED_LOG";
    public static final String SEND_ACTION_QIANJI = "cn.dreamn.qianji_auto.QIANJI";
    private final Context mContext;
    private final ClassLoader mAppClassLoader;
    private final String appName;

    public Utils(Context context, ClassLoader classLoader, String name) {
        mContext = context;
        mAppClassLoader = classLoader;
        appName = name;
    }

    public String getAppName() {
        return appName;
    }

    public ClassLoader getClassLoader() {
        return mAppClassLoader;
    }

    public Context getContext() {
        return mContext;
    }

    public void writeData(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ankio_xp", Context.MODE_PRIVATE); //私有数据

        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器

        editor.putString(key, value);

        editor.apply();//提交修改
    }

    public String readData(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ankio_xp", Context.MODE_PRIVATE); //私有数据
        return sharedPreferences.getString(key, "false");
    }

    /**
     * 发送广播通知
     *
     * @param bundle 广播数据
     */
    public void send(Bundle bundle) {
        log("广播给自动记账：" + bundle.toString(), true);
        sendBroadcast(SEND_ACTION, bundle);
    }

    public void send2auto(Bundle bundle) {
        log("钱迹数据广播给自动记账：" + bundle.toString(), true);
        sendBroadcast(SEND_ACTION_QIANJI, bundle);
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     */
    public void log(String msg) {
        Log.i("Qianji-" + appName, msg);
        log(msg,false);
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     * @param xp  是否输出到xposed
     */
    public void log(String msg, boolean xp) {
        if (xp) XposedBridge.log("Qianji-" + appName + " -> " + msg);
        //发送到自动记账日志
        Log.i("Qianji-" + appName, msg);
        Bundle bundle = new Bundle();
        bundle.putString("tag", "Qianji-" + appName);
        bundle.putString("msg", msg);
        sendBroadcast(SEND_LOG_ACTION, bundle);
    }

    private void sendBroadcast(String Action, Bundle bundle) {
        Intent intent = new Intent(Action);
        intent.setPackage("cn.dreamn.qianji_auto");
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mContext.sendBroadcast(intent, null);
    }

    /**
     * 获取版本名
     */
    protected String getVerName() {
        String verName = "";
        try {
            verName = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取app的版本代码
     * @return
     */
    protected  int getVerCode() {
        int verName = 0;
        try {
            verName = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 判断是否是支持版本
     */
    public void compare(String[] version) {

        if (version == null) {
            //表示全版本支持
            return;
        }

        String string = getVerName();
        if (readData("version_" + string).equals("true")) {
            return;
        }

        for (String s : version) {
            if (s.equals(string)) return;
        }
        String string2 = String.format("当前应用[%s]版本[%s]可能不受支持！您可以继续使用，但可能部分功能不支持。支持的版本为：%s", appName, getVerName(), Arrays.toString(version));
        Toast.makeText(mContext, string2, Toast.LENGTH_LONG).show();
        log(string, false);
        writeData("version_" + string, "true");
    }

}
