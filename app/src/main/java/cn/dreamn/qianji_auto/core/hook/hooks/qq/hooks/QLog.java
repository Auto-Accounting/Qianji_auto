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

package cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks;

import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


public class QLog {
    public static void init(Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Context mContext = utils.getContext();
        Class<?> QLOG = mAppClassLoader.loadClass("com.tencent.qphone.base.util.QLog");
        //private static void addLogItem(String str, int i, int i2, String str2, byte[] bArr, Throwable th) {
        XposedHelpers.findAndHookMethod(QLOG, "addLogItem", String.class, int.class, int.class, String.class, byte[].class, Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[3];
                utils.log("QQ日志-" + tag + ":" + msg);
            }
        });

    }


}
