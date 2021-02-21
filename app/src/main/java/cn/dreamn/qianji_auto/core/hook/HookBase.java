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

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class HookBase implements IHooker {

    protected ClassLoader mAppClassLoader;
    protected Context mContext;
    protected String TAG = "Qianji-Auto";
    private static Integer mHookCount = 0;
    private static Integer mHookCountIndex = 1;//HOOK第几个进程
    protected Utils utils;

    /**
     * @param packPagename   String 需要hook的包名
     * @param processName    String 序号hook的进程名
     * @param hookCountIndex 第几个进程才HOOK，一般为1，vxp可能要写2，如果不知道，那就写0，都去hook
     */
    public void hook(String packPagename, String processName, Integer hookCountIndex) {
        if (!getPackPageName().contentEquals(packPagename)) {
            return;
        }
        if (!getPackPageName().equals(processName)) {
            return;
        }
        mHookCountIndex = hookCountIndex;
        hookMainInOtherAppContext();
    }

    private void hookMainInOtherAppContext() {

        try {
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.args[0];
                    mAppClassLoader = mContext.getClassLoader();
                    init();
                }
            });
        } catch (Throwable e) {
            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = (Context) param.args[0];
                        mAppClassLoader = mContext.getClassLoader();
                        init();
                    }
                });

            } catch (Throwable e2) {
                XposedBridge.log("XposedErr: " + e.toString());
            }
        }


    }


    public void init() {
        utils = new Utils(mContext, mAppClassLoader, getAppName());
        mHookCount = mHookCount + 1;
        utils.log("hook id " + mHookCount.toString());
        if (mHookCountIndex != 0 && !mHookCount.equals(mHookCountIndex)) {
            return;
        }

        Task.onMain(100, () -> {
            utils.compare(getAppVer());//判断版本
            Toast.makeText(mContext, "加载自动记账成功！", Toast.LENGTH_LONG).show();
        });
        Task.onMain(() -> {
            try {

                hookFirst();

            } catch (Error | Exception e) {
                utils.log("hook 出现严重错误！" + e.toString(), false);
            }

        });


    }
  


   


}
