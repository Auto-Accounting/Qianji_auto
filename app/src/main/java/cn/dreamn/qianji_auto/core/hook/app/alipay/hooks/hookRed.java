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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookRed {

    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> proguard = XposedHelpers.findClass("com.alipay.mobile.redenvelope.proguard.c.b", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);


        XposedHelpers.findAndHookMethod(proguard, "onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = param.args[0].toString();
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length(), data.indexOf("], pushData=,"));

                JSONObject jsonObject = JSON.parseObject(data);
                if (!jsonObject.containsKey("pl")) return;
                String str = jsonObject.getString("pl");
                JSONObject jsonObject1 = JSON.parseObject(str);
                if (!jsonObject1.containsKey("templateJson")) return;
                String templateJson = jsonObject1.getString("templateJson");
                JSONObject jsonObject2 = JSON.parseObject(templateJson);
                if (!jsonObject2.containsKey("statusLine1Text")) return;
                if (!jsonObject2.containsKey("title")) return;
                if (!jsonObject2.containsKey("subtitle")) return;
                utils.log("hook 支付宝收到红包 succeed！", true);
                utils.log("红包数据：" + data);
                jsonObject2.put("auto", "支付宝收红包");
                utils.send(jsonObject2);


            }
        });
    }

}
