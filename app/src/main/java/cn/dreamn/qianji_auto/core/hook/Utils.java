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

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class Utils {
    public static final String SEND_ACTION = "cn.dreamn.qianji_auto.XPOSED";
    public static final String SEND_LOG_ACTION = "cn.dreamn.qianji_auto.XPOSED_LOG";
    public static final String SEND_ACTION_APP = "cn.dreamn.qianji_auto.APP";
    private Context mContext;
    private final ClassLoader mAppClassLoader;
    private final String appName;
    private final String packageName;

    public Utils(Context context, ClassLoader classLoader, String name, String packageName) {
        mContext = context;
        mAppClassLoader = classLoader;
        appName = name;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public ClassLoader getClassLoader() {
        return mAppClassLoader;
    }

    public Context getContext() {
        if (mContext == null)
            mContext = AndroidAppHelper.currentApplication();
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
        return sharedPreferences.getString(key, "");
    }


    //JSON数据转URL
    public static String convert(JSONObject object) {
        String buff = object.toJSONString();
        buff = buff.replace("{", "")
                .replace("}", "")
                .replace(":\"", ":")
                .replace("\"", "")
                .replace("\\", "");
        return buff.replaceAll("\\r\\n|\\r|\\n", "");
    }


    public void send(JSONObject jsonObject) {
        if (jsonObject == null) return;
        XposedBridge.log("原始数据：" + jsonObject.toJSONString());
        Bundle bundle = new Bundle();
        bundle.putString("data", convert(jsonObject));
        send(bundle, "app");
    }

    public void sendString(String str) {
        sendString(str, "app", getPackageName());
    }

    public void sendString(String str, String identify, String packageName) {
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        bundle.putString("app_identify", identify);
        log("广播给自动记账：" + str, true);
        if (packageName == null)
            sendBroadcast(SEND_ACTION, bundle);
        else sendBroadcast(SEND_ACTION, bundle, packageName);
    }

    public void send2auto(String str) {
        log("APP数据广播给自动记账：" + str, true);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        sendBroadcast(SEND_ACTION_APP, bundle);
    }

    private void sendBroadcast(String Action, Bundle bundle, String packageName) {
        //消息去重
        if(readData("last").equals(bundle.getString("data", null))){
            return;
        }
        writeData("last",bundle.getString("data", ""));

        bundle.putString("app_package", packageName);
        Intent intent = new Intent(Action);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        try {
            if (mContext != null) {
                getContext().sendBroadcast(intent, null);
            } else {
                XposedBridge.log("content为空，无法传递数据:" + bundle.toString());
            }
        } catch (Throwable e) {
            XposedBridge.log("异常：" + e.toString());
            XposedBridge.log("出现异常：" + bundle.toString());
        }

    }

    private void sendBroadcast(String Action, Bundle bundle) {
        sendBroadcast(Action, bundle, getPackageName());
    }

    /**
     * 获取版本名
     */
    public String getVerName() {
        String verName = "";
        try {
            verName = getContext().getPackageManager().
                    getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (Exception ignored) {

        }
        return verName;
    }

    /**
     * 获取app的版本代码
     *
     * @return
     */
    public int getVerCode() {
        int verName = 0;
        try {
            verName = getContext().getPackageManager().
                    getPackageInfo(getContext().getPackageName(), 0).versionCode;
        } catch (Exception ignored) {

        }
        return verName;
    }

    /**
     * 判断是否是支持版本
     */

    public boolean isDebug() {
        return readDataByBoolean("apps", "debug");
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     */
    public void log(String... msg) {
        StringBuilder m = new StringBuilder();
        for (String mm : msg) {
            m.append(mm).append(",");
        }
        if (!m.toString().equals("")) {
            m = new StringBuilder(m.substring(0, m.length() - 1));
        }
        log(m.toString(), true);
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     * @param xp  是否输出到xposed
     */
    public void log(String msg, boolean xp) {
        XposedBridge.log("Ankio-" + appName + " -> " + msg);
        //发送到自动记账日志
        //  Log.i("Ankio-" + appName, msg);
        // 采用分段打印 四千字符分一段
        if (msg.length() > 4000) {
            for (int i = 0; i < msg.length(); i += 4000) {
                if (i + 4000 < msg.length()) {
                    Log.i("Ankio-" + appName + " 第" + i + "数据", msg.substring(i, i + 4000));
                } else {
                    Log.i("Ankio-" + appName + " 第" + i + "数据", msg.substring(i));
                }
            }
        } else {
            Log.i("Ankio-" + appName + " 全部数据", "************************  response = " + msg);
        }
        Bundle bundle = new Bundle();
        bundle.putString("tag", "Ankio-" + appName);
        bundle.putString("msg", msg);
        sendBroadcast(SEND_LOG_ACTION, bundle);
    }

    public Boolean readDataByBoolean(String app, String name) {
        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");
        SharedPreferences data = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);
        return data.getBoolean(name, false);
    }

    public void writeDataByData(String app, String name, String data) {
        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(name, data).apply();
    }

    public String readDataByApp(String app, String name) {
        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");
        SharedPreferences data = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);

        return data.getString(name, "");
    }

    public void dumpFields(Object classObj, Class<?> dumpClass) {
        log("开始获取对象中的属性值：" + dumpClass.getSimpleName());
        try {
            Field[] fields = dumpClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                log(field.getName(), String.valueOf(field.get(classObj)));
            }
            log("dump完成：");
        } catch (Exception e) {
            log("dump失败：", e.toString());
        }
    }

    public void dumpFields(Object classObj) {
        dumpFields(classObj, classObj.getClass());
    }

    // 获取指定名称的类声明的类成员变量、类方法、内部类的信息
    public void dumpClass(Class<?> actions) {
        XposedBridge.log("[Ankio]" + "Dump class " + actions.getName());
        XposedBridge.log("[Ankio]" + "Methods");
        // 获取到指定名称类声明的所有方法的信息
        Method[] m = actions.getDeclaredMethods();
        // 打印获取到的所有的类方法的信息
        for (Method method : m) {
            XposedBridge.log(method.toString());
        }
        XposedBridge.log("[Ankio]" + "Fields");
        // 获取到指定名称类声明的所有变量的信息
        Field[] f = actions.getDeclaredFields();
        // 打印获取到的所有变量的信息
        for (Field field : f) {
            XposedBridge.log("[Ankio]" + field.toString());
        }
        XposedBridge.log("[Ankio]Classes");
        // 获取到指定名称类中声明的所有内部类的信息
        Class<?>[] c = actions.getDeclaredClasses();
        // 打印获取到的所有内部类的信息
        for (Class<?> aClass : c) {
            XposedBridge.log("[Ankio]" + aClass.toString());
        }

    }


    public void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void send(Bundle bundle, String identify) {
        bundle.putString("app_identify", identify);
        log("广播给自动记账：" + bundle.toString(), true);
        sendBroadcast(SEND_ACTION, bundle);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void restart() {
        final Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

