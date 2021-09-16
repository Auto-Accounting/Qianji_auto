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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Utils {
    public static final String SEND_ACTION = "cn.dreamn.qianji_auto.XPOSED";
    public static final String SEND_LOG_ACTION = "cn.dreamn.qianji_auto.XPOSED_LOG";
    public static final String SEND_ACTION_APP = "cn.dreamn.qianji_auto.APP";
    private final Context mContext;
    private final ClassLoader mAppClassLoader;
    private final String appName;
    private final String packageName;
    public static final int TRACE_THROWABLE = 1;

    public Utils(Context context, ClassLoader classLoader, String name, String packageName) {
        mContext = context;
        mAppClassLoader = classLoader;
        appName = name;
        this.packageName = packageName;
    }

    public static final int TRACE_EXCEPTION = 2;
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
        return mContext;
    }

    public void writeData(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ankio_xp", Context.MODE_PRIVATE); //私有数据

        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器

        editor.putString(key, value);

        editor.apply();//提交修改
    }

    public String readData(String key) {
        return readData(key, false);
    }

    public String readData(String key, boolean replace) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ankio_xp", Context.MODE_PRIVATE); //私有数据
        String data = sharedPreferences.getString(key, "");
        if (replace) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putString(key, null);
            editor.apply();
        }
        return data;
    }


    public static String convertUrl(Object object, String key) {
        StringBuilder paramStr = new StringBuilder();

        if (object instanceof String || object instanceof Number || object instanceof Boolean) {
            String value = String.valueOf(object);
            value = value.replace("\\n", "").replace("\\t", "");

            value = value.replace("\n", "\\n").replace("\t", "");

            if (key != null && !value.startsWith("#") && !value.equals("") && !value.equals("\\n") && !value.startsWith("http") && !value.startsWith("{"))
                paramStr.append("&").append(key).append("=").append((object));
        } else {


            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                for (Map.Entry entry : jsonObject.entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    paramStr.append(convertUrl(v, k.toString()));
                }
            } else if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                for (Object o : jsonArray) {
                    paramStr.append(convertUrl(o, null));
                }
            }
        }
        return paramStr.toString().replace("\n", "\\n").replace("\t", "");
    }

    public void send(JSONObject jsonObject) {
        if (jsonObject == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("data", convertUrl(jsonObject, null));
        send(bundle, "app");
    }

    public void sendString(String str) {
        sendString(str, "app");
    }

    public void sendString(String str, String identify) {
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        bundle.putString("app_identify", identify);
        log("广播给自动记账：" + str, true);
        sendBroadcast(SEND_ACTION, bundle);
    }

    public void send2auto(Bundle bundle) {
        log("APP数据广播给自动记账：" + bundle.toString(), true);
        sendBroadcast(SEND_ACTION_APP, bundle);
    }

    public void send2auto(String str) {
        log("APP数据广播给自动记账：" + str, true);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        sendBroadcast(SEND_ACTION_APP, bundle);
    }

    public static final int TRACE_DUMPSTACK = 3;
    public static final int TRACE_RUNTIME = 4;

    private void sendBroadcast(String Action, Bundle bundle) {
        bundle.putString("app_package", getPackageName());
        Intent intent = new Intent(Action);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mContext.sendBroadcast(intent, null);
    }

    /**
     * 获取版本名
     */
    public String getVerName() {
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
     *
     * @return
     */
    public int getVerCode() {
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

    public static final int TRACE_ALL = 5;
    private final boolean isDebug = false;

    public boolean isDebug() {
        return isDebug;
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     */
    public void log(String msg) {
        // Log.i("Qianji-" + appName, msg);
        log(msg, false);
    }

    /**
     * 打印日志
     *
     * @param msg 日志数据
     * @param xp  是否输出到xposed
     */
    public void log(String msg, boolean xp) {
        if (xp) XposedBridge.log("Ankio-" + appName + " -> " + msg);
        //发送到自动记账日志
        Log.i("Ankio-" + appName, msg);
        Bundle bundle = new Bundle();
        bundle.putString("tag", "Ankio-" + appName);
        bundle.putString("msg", msg);
        sendBroadcast(SEND_LOG_ACTION, bundle);
    }

    public String readDataByApp(String app, String name) {
        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");
        SharedPreferences data = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);
        return data.getString(name, "");
    }

    public void printTrace(int func) {


        switch (func) {
            case TRACE_THROWABLE: {
                // 方法一:
                log("Dump Stack: ---------------start----------------");
                Throwable ex = new Throwable();
                StackTraceElement[] stackElements = ex.getStackTrace();
                for (int i = 0; i < stackElements.length; i++) {

                    log("Dump Stack" + i + ": " + stackElements[i].getClassName()
                            + "----" + stackElements[i].getFileName()
                            + "----" + stackElements[i].getLineNumber()
                            + "----" + stackElements[i].getMethodName());
                }
                log("Dump Stack:---------------over----------------");
                break;
            }
            case TRACE_EXCEPTION: {
                // 方法二:
                new Exception().printStackTrace(); // 直接干脆
                break;
            }
            case TRACE_DUMPSTACK: {
                // 方法三:
                Thread.dumpStack(); // 直接暴力
                break;
            }
            case TRACE_RUNTIME: {
                // 方法四:
                // 打印调用堆栈: http://blog.csdn.net/jk38687587/article/details/51752436
                RuntimeException e = new RuntimeException("<Start dump Stack !>");
                e.fillInStackTrace();
                log("<Dump Stack>: ++++++++++++" + e);
                break;
            }
            case TRACE_ALL: {
                // 方法五:
                // Thread类的getAllStackTraces（）方法获取虚拟机中所有线程的StackTraceElement对象，可以查看堆栈
                for (Map.Entry<Thread, StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()) {
                    Thread thread = stackTrace.getKey();
                    StackTraceElement[] stack = stackTrace.getValue();

                    // 进行过滤
                    if (thread.equals(Thread.currentThread())) {
                        continue;
                    }

                    log("[Dump Stack] **********Thread name：" + thread.getName() + "**********");
                    int index = 0;
                    for (StackTraceElement stackTraceElement : stack) {

                        log("[Dump Stack]" + index + ": " + stackTraceElement.getClassName()
                                + "----" + stackTraceElement.getFileName()
                                + "----" + stackTraceElement.getLineNumber()
                                + "----" + stackTraceElement.getMethodName());
                    }
                    // 增加序列号
                    index++;
                }
                log("[Dump Stack]********************* over **********************");
                break;
            }

        }
    }

    public void printClassAndFunctions() {
        printClassAndFunctions(null);
    }

    public void printClassAndFunctions(String className) {
        Log.i("Ankio", "try hook " + className);
        // Hook类方法ClassLoader#loadClass(String)
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            // 在类方法loadClass执行之后执行的代码
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                // 参数的检查
                if (param.hasThrowable()) {
                    return;
                }
                // 获取指定名称的类加载之后的Class<?>
                Class<?> clazz = (Class<?>) param.getResult();
                // 获取加载的指定类的名称
                String strClazz = (String) param.args[0];
                if (strClazz == null) return;
                //XposedBridge.log("Needclass:"+className+"\nfindClass:"+strClazz);
                String[] regulars = {"android", ".system", "java.", "google", "xposed", "LspHooker_"};
                if (className == null) {
                    boolean find = false;
                    for (String s : regulars) {
                        if (strClazz.contains(s)) {
                            find = true;
                            break;
                        }
                    }
                    if (find) return;

                } else {
                    if (!className.equals(strClazz)) {
                        XposedBridge.log("Needclass:" + className + "\nfindClass:" + strClazz);
                        return;
                    }
                }

                // 或者只Hook加密算法类、网络数据传输类、按钮事件类等协议分析的重要类
                XposedBridge.log("[Ankio]LoadClass : " + strClazz);

                XposedBridge.log("HookedClass : " + strClazz);
                // 获取到指定名称类声明的所有方法的信息
                Method[] m = clazz.getDeclaredMethods();
                // 打印获取到的所有的类方法的信息
                for (Method method : m) {
                    XposedBridge.log("HOOKED CLASS-METHOD: " + strClazz + "-" + method.toString());
                    if (!Modifier.isAbstract(method.getModifiers())           // 过滤掉指定名称类中声明的抽象方法
                            && !Modifier.isNative(method.getModifiers())     // 过滤掉指定名称类中声明的Native方法
                            && !Modifier.isInterface(method.getModifiers())  // 过滤掉指定名称类中声明的接口方法
                    ) {

                        try {
                            // 对指定名称类中声明的非抽象方法进行java Hook处理
                            XposedBridge.hookMethod(method, new XC_MethodHook() {
                                // 被java Hook的类方法执行完毕之后，打印log日志
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) {
                                    // 打印被java Hook的类方法的名称和参数类型等信息
                                    XposedBridge.log("[Ankio]HOOKED METHOD: " + strClazz + "-" + param.method.toString());
                                    printTrace(TRACE_DUMPSTACK);
                                }
                            });
                        } catch (Throwable e) {
                            XposedBridge.log("[Ankio]Error:" + e.toString());
                        }
                    }
                }


            }
        });
    }

    public void printLogLocation() {
        Log.i("Ankio", "printLog");

        XposedHelpers.findAndHookMethod(Log.class, "println", int.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                printTrace(Utils.TRACE_DUMPSTACK);
            }
        });
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
}

