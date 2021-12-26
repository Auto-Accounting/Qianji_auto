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

package cn.dreamn.qianji_auto.core.hook.hooks.alipay.hooks;

import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookSafe {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.alipay.apmobilesecuritysdk.scanattack.common.ScanAttack", mAppClassLoader), "getScanAttackInfo", Context.class, int.class, int.class, boolean.class, int.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.getResult() != null)
                    utils.log("支付宝原本监测到的安全信息：" + param.getResult().toString(), true);
                param.setResult(null);
                utils.log("hook 支付宝安全监测 succeed！", true);
            }

        });
    }


}
