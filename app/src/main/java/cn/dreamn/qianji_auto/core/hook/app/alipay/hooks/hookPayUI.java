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

package cn.dreamn.qianji_auto.core.hook.app.alipay.hooks;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookPayUI {
    public static void init(Utils utils) {
        hookUI(utils);

        hookData(utils);
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> LogUtil = XposedHelpers.findClass("com.alipay.android.msp.utils.LogUtil", mAppClassLoader);
        XposedHelpers.findAndHookMethod(LogUtil, "record", int.class, String.class, String.class, String.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (methodHookParam.args[3] == null || methodHookParam.args[2] == null) return;
                String str = methodHookParam.args[2].toString();
                String data = methodHookParam.args[3].toString();
                try {
                    utils.log("TAG:" + str + " DATA: " + data);
                   /* if (str.equals("MspUIClient:handleUiShow")) {
                        //找到所需
                        utils.log("数据：" + data);
                        //*data = data.substring("data=".length());
                        JSONObject parseObject = JSON.parseObject(data);
                        if (parseObject.containsKey("data")) {
                            JSONObject parseObject2 = JSON.parseObject(parseObject.getString("data"));
                            if (parseObject2.containsKey("costTitle")) {
                                utils.writeData("alipay_cache_shopremark", parseObject2.getString("product"));
                                utils.writeData("alipay_cache_money", parseObject2.getString("cost"));
                                utils.writeData("alipay_cache_payTool", parseObject2.getString("payTool"));
                            }
                        }
                    }*/
                } catch (Exception e) {
                    utils.log("出现错误" + e.toString(), true);
                }
            }
        });
    }

    private static void hookData(Utils utils) {
        Class<?> data = XposedHelpers.findClass("com.alipay.mobileprod.biz.transfer.dto.CreateToAccountReq", utils.getClassLoader());
        XposedHelpers.findAndHookMethod(data, "toString", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String str = (String) param.getResult();
                utils.log("数据：" + str, true);
            }
        });
    }

    private static void hookUI(Utils utils) {
        Class<?> UIAction = XposedHelpers.findClass("com.alipay.android.msp.drivers.actions.UIAction", utils.getClassLoader());
        Class<?> MspUIClient = XposedHelpers.findClass("com.alipay.android.msp.core.clients.MspUIClient", utils.getClassLoader());
        XposedHelpers.findAndHookMethod(MspUIClient, "handleUiShow", UIAction, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                JSONObject str = (JSONObject) param.getResult();
                utils.log("数据：" + str.toJSONString(), true);
            }
        });

    }
}
