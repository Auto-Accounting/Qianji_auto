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

package cn.dreamn.qianji_auto.core.hook.app.alipay;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
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
                if (!jsonObject2.getString("subtitle").contains("来自")) return;
                utils.log("hook 支付宝收到红包 succeed！", true);
                utils.log("红包数据：" + data);

                Bundle bundle = new Bundle();
                bundle.putString("from", Alipay.RED_RECEIVED);
                bundle.putString("type", Receive.ALIPAY);
                bundle.putString("data", jsonObject2.toJSONString());
                bundle.putString("title", "红包");
                utils.send(bundle);


            }
        });
    }

}
