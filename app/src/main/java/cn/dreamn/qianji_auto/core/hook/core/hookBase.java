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

package cn.dreamn.qianji_auto.core.hook.core;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class hookBase implements iHooker {

    protected ClassLoader mAppClassLoader;
    protected WeakReference<Context> mContext = new WeakReference<>(null);
    protected String TAG = "Auto-AppHook";
    protected Utils utils;

    @Override
    protected void finalize() throws Throwable {
        mContext = new WeakReference<>(null);
        mAppClassLoader = null;
        System.setProperty("AUTO_FULL_TAG_" + getClass().getName(), "false");
    }

    private void hookMainInOtherAppContext(Runnable runnable) {
        Runnable findContext1 = new Runnable() {
            @Override
            public void run() {
                XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = new WeakReference<>((Context) param.args[0]);
                        mAppClassLoader = mContext.get().getClassLoader();
                        runnable.run();
                    }
                });
            }
        };
        Runnable findContext2 = new Runnable() {
            @Override
            public void run() {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = new WeakReference<>((Context) param.args[0]);
                        mAppClassLoader = mContext.get().getClassLoader();
                        runnable.run();
                    }
                });
            }
        };
        try {
            findContext1.run();
        } catch (Throwable e) {
            findContext2.run();
        }


    }


    public void initLoadPackage(String pkg) {
       if ("true".equals(System.getProperty("AUTO_FULL_TAG_" + getClass().getName()))) {
            //  XposedBridge.log("不允许二次加载hook代码");
            //I don't know... What happened?
            return;
        }
        System.setProperty("AUTO_FULL_TAG_" + getClass().getName(), "true");
        utils = new Utils(mContext.get(), mAppClassLoader, getAppName(), getPackPageName());
        XposedBridge.log(" 自动记账加载成功！\n应用名称:" + utils.getAppName() + "  当前版本号:" + utils.getVerCode() + "  当前版本名：" + utils.getVerName());
        try {
            hookLoadPackage();
        } catch (Error | Exception e) {
            utils.log("hook 出现严重错误！" + e.toString(), true);
        }

    }

    public abstract void hookLoadPackage() throws ClassNotFoundException;

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        String pkg = lpparam.packageName;
        String processName = lpparam.processName;
        if (!lpparam.isFirstApplication) return;
        if (getPackPageName() != null) {
            if (!pkg.equals(getPackPageName()) || !processName.equals(getPackPageName())) return;
        }
        // if (hookLoadPackage > getHookIndex()) return;

        mAppClassLoader = lpparam.classLoader;
        mContext = new WeakReference<>(AndroidAppHelper.currentApplication());
        if (!needHelpFindApplication()) {
            initLoadPackage(pkg);
            return;
        }
        hookMainInOtherAppContext(() -> initLoadPackage(pkg));
    }


    /**
     * 防止重复执行Hook代码
     *
     * @param flag 判断标识,针对不同Hook代码分别进行判断
     * @return 是否已经注入Hook代码
     */
    private boolean isInject(String flag) {
        try {
            if (TextUtils.isEmpty(flag)) return false;
            Field methodCacheField = XposedHelpers.class.getDeclaredField("methodCache");
            methodCacheField.setAccessible(true);
            HashMap<String, Method> methodCache = (HashMap<String, Method>) methodCacheField.get(null);
            Method method = XposedHelpers.findMethodBestMatch(Application.class, "onCreate");
            String key = String.format("%s#%s", flag, method.getName());
            if (methodCache.containsKey(key)) return true;
            methodCache.put(key, method);
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean canUseCache() {
        try {
            XposedHelpers.class.getDeclaredField("methodCache");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
