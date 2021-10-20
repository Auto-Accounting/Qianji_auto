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

package cn.dreamn.qianji_auto.core.hook.template.app;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class AppBase implements AppHooker {

    private static Integer mHookCount = 0;
    protected ClassLoader mAppClassLoader;
    protected Context mContext = null;
    protected String TAG = "Auto-AppHook";
    protected Utils utils;

    /**
     * @param packPagename String 需要hook的包名
     * @param processName  String 序号hook的进程名
     */
    public void hook(String packPagename, String processName) {
        String pkg = getPackPageName();
        if (!packPagename.equals(pkg)) return;
        if (!processName.equals(pkg)) return;
        // 获取hook顺序
        //到了第hook的进程最大值
        if (mHookCount > getHookIndex()) return;
        hookMainInOtherAppContext();

    }

    private void hookMainInOtherAppContext() {

        try {
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    // if(mContext!=null)return;
                    mContext = (Context) param.args[0];
                    mAppClassLoader = mContext.getClassLoader();
                    //    XposedBridge.log("context 成功！" + getAppName());
                    init();
                }
            });


        } catch (Throwable e) {
            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //  if(mContext!=null)return;
                        mContext = (Context) param.args[0];
                        mAppClassLoader = mContext.getClassLoader();
                        //  XposedBridge.log("context 成功2！" + getAppName());
                        init();
                    }
                });
            } catch (Throwable e2) {
                XposedBridge.log("XposedErr: " + e.toString());
            }
        }


    }


    public void init() {
        mHookCount = mHookCount + 1;
        if (!mHookCount.equals(getHookIndex())) {
            return;
        }

        hookBefore();
        utils = new Utils(mContext, mAppClassLoader, getAppName(), getPackPageName());
        TaskThread.onMain(100, () -> {
            XposedBridge.log(" 自动记账加载成功！\n应用名称:" + utils.getAppName() + "  当前版本号:" + utils.getVerCode() + "  当前版本名：" + utils.getVerName());
        });
        try {
            hookFirst();
        } catch (Error | Exception e) {
            utils.log("hook 出现严重错误！" + e.toString(), true);
        }


    }

}
