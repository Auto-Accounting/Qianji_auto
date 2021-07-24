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
import android.os.Bundle;
import android.text.TextUtils;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


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
                   // String contentStr = contentValues.getAsString("content");


                    Bundle bundle = new Bundle();
                    bundle.putInt("isSend", contentValues.getAsInteger("isSend"));
                    bundle.putInt("status", contentValues.getAsInteger("status"));
                    bundle.putString("talker", contentValues.getAsString("talker"));
                    bundle.putString("content", contentValues.getAsString("content"));
                    bundle.putString("cache_money", utils.readData("cache_wechat_payMoney"));
                    bundle.putString("cache_user", utils.readData("cache_userName"));
                    bundle.putString("cache_paytools", utils.readData("cache_wechat_paytool"));
                    //转账消息
                    if (type == 419430449) {
                        utils.send(bundle);
                    } else if (type == 436207665) {
                        utils.send(bundle);
                    } else if (type == 318767153) {
                        utils.send(bundle);
                    } else {
                        utils.log("微信数据：" + type + "\n \n" + contentValues.toString());
                    }

                } catch (Exception e) {
                    utils.log("获取账单信息出错：" + e.getMessage(), true);
                }

            }
        });
    }


}
