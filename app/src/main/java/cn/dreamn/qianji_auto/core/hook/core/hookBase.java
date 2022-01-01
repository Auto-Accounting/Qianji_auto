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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class hookBase implements iHooker {

    protected ClassLoader mAppClassLoader;
    protected Context mContext = null;
    protected String TAG = "Auto-AppHook";
    protected Utils utils;
    protected static int hookLoadPackage = 0;
    protected static int hookZygoteMain = 0;

    private void hookMainInOtherAppContext(Runnable runnable, Boolean isInit) {
        Runnable findContext1 = new Runnable() {
            @Override
            public void run() {
                XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = (Context) param.args[0];
                        mAppClassLoader = mContext.getClassLoader();
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
                        mContext = (Context) param.args[0];
                        mAppClassLoader = mContext.getClassLoader();
                        runnable.run();
                    }
                });
            }
        };
        if (isInit) {
            findContext2.run();
        } else {
            try {
                findContext1.run();
            } catch (Throwable e) {
                findContext2.run();
            }
        }


    }


    public void initLoadPackage(String pkg) {

        hookLoadPackage++;
        if (hookLoadPackage != getHookIndex()) return;


        utils = new Utils(mContext, mAppClassLoader, getAppName(), getPackPageName());
        XposedBridge.log(" 自动记账加载成功！\n应用名称:" + utils.getAppName() + "  当前版本号:" + utils.getVerCode() + "  当前版本名：" + utils.getVerName());
        try {
            hookLoadPackage();
        } catch (Error | Exception e) {
            utils.log("hook 出现严重错误！" + e.toString(), true);
        }

    }

    public abstract void hookLoadPackage();

    public abstract void hookInitZygoteMain();

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        String pkg = lpparam.packageName;
        String processName = lpparam.processName;
        if (getPackPageName() != null) {
            if (!pkg.equals(getPackPageName()) || !processName.equals(getPackPageName())) return;
        }
        if (hookLoadPackage >= getHookIndex()) return;
        mAppClassLoader = lpparam.classLoader;
        mContext = AndroidAppHelper.currentApplication();
        if (!needHelpFindApplication()) {
            initLoadPackage(pkg);
            return;
        }
        hookMainInOtherAppContext(() -> initLoadPackage(pkg), false);
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

        mContext = AndroidAppHelper.currentApplication();
        mAppClassLoader = getClass().getClassLoader();
        if (!needHelpFindApplication()) {
            initZygoteMainHook();
            return;
        }
        hookMainInOtherAppContext(this::initZygoteMainHook, true);
    }

    private void initZygoteMainHook() {
        hookZygoteMain++;
        if (hookZygoteMain != getHookIndex()) return;

        utils = new Utils(mContext, mAppClassLoader, getAppName(), "");
        try {
            hookInitZygoteMain();
        } catch (Throwable e) {
            utils.log("hook 出现严重错误！" + e.toString(), true);
        }
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
