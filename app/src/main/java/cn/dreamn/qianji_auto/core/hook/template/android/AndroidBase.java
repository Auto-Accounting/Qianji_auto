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

package cn.dreamn.qianji_auto.core.hook.template.android;

import android.app.AndroidAppHelper;
import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public abstract class AndroidBase implements AndroidHooker {

    protected ClassLoader mAppClassLoader;
    protected Context mContext;
    protected String TAG = "Auto-AndroidHook";
    protected Utils utils;

    public void hook(ClassLoader classLoader) {
        mContext = AndroidAppHelper.currentApplication();
        mAppClassLoader = classLoader;
        hookMain();
    }


    private void hookMain() {
        Class<?> ContextClass = XposedHelpers.findClass("android.content.ContextWrapper", mAppClassLoader);
        XposedHelpers.findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (mContext != null)
                    return;
                mContext = (Context) param.getResult();
                //XposedBridge.log("CSDN_LQR-->得到上下文");
                init();
            }
        });
    }

    public void init() {
        hookBefore();
        utils = new Utils(mContext, mAppClassLoader, getAppName(), "");
        utils.log("Hook系统功能成功！" + getAppName());
        try {
            hookFirst();
        } catch (Error | Exception e) {
            utils.log("hook 出现严重错误！" + e.toString(), true);
        }


    }

}
