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

package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import java.lang.reflect.Method;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class PayTools {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> MMKRichLabelView = XposedHelpers.findClass("com.tencent.kinda.framework.widget.base.MMKRichLabelView", mAppClassLoader);
        Class<?> KTex = XposedHelpers.findClass("com.tencent.kinda.gen.KText", mAppClassLoader);
        Class<?> MMKRichText = XposedHelpers.findClass("com.tencent.kinda.framework.widget.base.MMKRichText", mAppClassLoader);
        XposedHelpers.findAndHookMethod(MMKRichLabelView, "setRichText", KTex, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Method get = MMKRichText.getDeclaredMethod("get");
                if (param.args[0] == null) {
                    utils.log("arg1 is null");
                    return;
                }
                String data = get.invoke(param.args[0]).toString();
                utils.log("设置数据：" + data, true);
                if (data.contains("零钱") || (data.contains("卡(") && data.endsWith(")"))) {
                    utils.writeData("cache_wechat_paytool", data);
                    utils.log("识别的账户名：" + data);
                } else if (data.startsWith("￥")) {
                    //金额
                    utils.writeData("cache_wechat_payMoney", data);
                } else if (data.contains("向") && data.endsWith(")转账")) {
                    //转账人
                    utils.writeData("cache_wechat_payUser", data);
                }

            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }

}
