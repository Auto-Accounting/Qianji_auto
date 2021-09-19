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

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class Msg {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Context mContext = utils.getContext();
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];
                    String arg = (String) param.args[1];

                    if (!tableName.equals("message") || TextUtils.isEmpty(tableName)) {
                        return;
                    }
                    Integer type = contentValues.getAsInteger("type");
                    if (null == type) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("isSend", contentValues.getAsInteger("isSend"));
                    jsonObject.put("status", contentValues.getAsInteger("status"));
                    jsonObject.put("talker", contentValues.getAsInteger("talker"));

                    XmlToJson xmlToJson = new XmlToJson.Builder(contentValues.getAsString("content")).build();
                    String xml = xmlToJson.toString();
                    jsonObject.put("content", JSONObject.parseObject(xml));
                    jsonObject.put("cache_money", utils.readData("cache_wechat_payMoney", true));
                    jsonObject.put("cache_user", utils.readData("cache_wechat_payUser"));
                    jsonObject.put("cache_paytools", utils.readData("cache_wechat_paytool"));
                    //转账消息
                    if (type == 419430449) {
                        jsonObject.put("title", "转账消息");
                        utils.send(jsonObject);
                    } else if (type == 436207665) {
                        jsonObject.put("title", "红包消息");
                        utils.send(jsonObject);
                    } else if (type == 318767153) {
                        jsonObject.put("title", "卡片消息");
                        utils.send(jsonObject);
                    } else {
                    //    utils.log("微信数据【不确定是否要发送】：" + type + "\n \n" + contentValues.toString());
                    }
                } catch (Exception e) {
                    utils.log("获取账单信息出错：" + e.getMessage(), true);
                }

            }
        });
    }


}
