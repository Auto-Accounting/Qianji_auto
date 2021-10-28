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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookReceive {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> MsgboxInfoServiceImpl = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.sync.d", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);

        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl, "onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = param.args[0].toString();
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length(), data.indexOf("], pushData=,"));

                try {
                    data = "[" + data + "]";
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        analyze(utils, jsonArray.get(i).toString());
                    }
                } catch (Exception e) {
                    try {
                        analyze(utils, data);
                    } catch (Exception e2) {
                        utils.log("AlipayErr" + e2.toString(), false);
                    }
                }

            }
        });
    }

    private static void analyze(Utils utils, String data) {
        utils.log("支付宝原始数据：" + data, true);
        JSONObject jsonObject = JSON.parseObject(data);
        if (!jsonObject.containsKey("pl")) return;
        String str = jsonObject.getString("pl");
        JSONObject jsonObject1 = JSON.parseObject(str);

        if (!jsonObject1.containsKey("templateType")) return;
        if (!jsonObject1.containsKey("templateName")) return;
        if (!jsonObject1.containsKey("title")) return;
        if (!jsonObject1.containsKey("content")) return;

        utils.log("-------消息开始解析-------");
        String title = jsonObject1.getString("title");
        String templateName = jsonObject1.getString("templateName");
        if (title.equals("其他")) title = templateName;

        if (jsonObject1.getString("templateType").equals("BN")) {
            JSONObject content = jsonObject1.getJSONObject("content");
           /* content.put("alipay_cache_shopremark", utils.readData("alipay_cache_shopremark", true));
            content.put("alipay_cache_money", utils.readData("alipay_cache_money", true));
            content.put("alipay_cache_payTool", utils.readData("alipay_cache_payTool", true));
           */
            content.put("title", title);
            utils.send(content);
        } else if (jsonObject1.getString("templateType").equals("S")) {
            JSONObject content = jsonObject1.getJSONObject("extraInfo");
            content.put("extra", jsonObject1.getString("content"));
          /*  content.put("alipay_cache_shopremark", utils.readData("alipay_cache_shopremark"));
            content.put("alipay_cache_money", utils.readData("alipay_cache_money"));
            content.put("alipay_cache_payTool", utils.readData("alipay_cache_payTool"));
            */
            content.put("title", title);
            utils.send(content);
        }

    }
}
