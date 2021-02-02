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

import android.os.Process;
import android.util.Log;

import cn.dreamn.qianji_auto.BuildConfig;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XDebugable implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final int DEBUG_ENABLE_DEBUGGER = 0x1;

    private XC_MethodHook debugAppsHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {

            XposedBridge.log("-- beforeHookedMethod :" + param.args[1]);

            int id = 5;
            int flags = (Integer) param.args[id];
            // 修改类android.os.Process的start函数的第6个传入参数
            if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
                // 增加开启Android调试选项的标志
                flags |= DEBUG_ENABLE_DEBUGGER;
            }
            param.args[id] = flags;

            if (BuildConfig.DEBUG) {
                XposedBridge.log("-- app debugable flags to 1 :" + param.args[1]);
            }
        }
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

    }

    // 实现的接口IXposedHookZygoteInit的函数
    @Override
    public void initZygote(final IXposedHookZygoteInit.StartupParam startupParam) {

        // /frameworks/base/core/java/android/os/Process.java
        // Hook类android.os.Process的start函数
        Log.e("hook ", "initZygote");
        XposedBridge.hookAllMethods(Process.class, "start", debugAppsHook);
    }
}

