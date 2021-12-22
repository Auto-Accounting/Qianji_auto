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

package cn.dreamn.qianji_auto.core.hook.app.qianji.hooks;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.broadcast.AppBroadcast;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.app.qianji.DBHelper;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DataBase {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils) {
        utils.log("自动记账同步：钱迹初始化", false);
        ClassLoader mAppClassLoader = utils.getClassLoader();
        XposedHelpers.findAndHookConstructor("com.mutangtech.qianji.data.model.DaoMaster", mAppClassLoader, SQLiteDatabase.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SQLiteDatabase database = (SQLiteDatabase) param.args[0];
                // 获取数据库操作对象
                final DBHelper[] dbHelper = new DBHelper[1];
                utils.log("自动记账同步：获取钱迹数据库对象", false);
                if (database != null && database.isOpen()) {
                    db = database;
                    utils.log("使用钱迹对象获取信息", false);
                    dbHelper[0] = new DBHelper(db, utils);
                } else {
                    utils.log("钱迹数据库对象无法获取到数据，尝试文件模式", false);
                    dbHelper[0] = new DBHelper(utils);
                }

                Class<?> loginClass = null;
                try {
                    loginClass = mAppClassLoader.loadClass("com.mutangtech.qianji.app.f.b");
                } catch (Throwable e) {
                    try {
                        loginClass = mAppClassLoader.loadClass("com.mutangtech.qianji.app.c.b");
                    } catch (Throwable e2) {
                        utils.log("钱迹加载类失败！");
                    }
                }
                if (loginClass == null) return;
                //获取loginClass
                Method getInstance = loginClass.getDeclaredMethod("getInstance");
                //反射调用单例模式
                Object object = getInstance.invoke(null);
                //获取对象
                Method IsVip = loginClass.getMethod("isVip");
                boolean isVip = (boolean) IsVip.invoke(object);
                //获取最终的UID
                utils.log("钱迹用户:" + (isVip ? "会员" : "非会员"));

                Method getLoginUserID = loginClass.getMethod("getLoginUserID");
                String userId = (String) getLoginUserID.invoke(object);

                final boolean[] hooked = {false};
                XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (hooked[0]) return;
                        hooked[0] = true;
                        Activity activity = (Activity) param.thisObject;
                        final String activityClzName = activity.getClass().getName();
                        if (activityClzName.contains("com.mutangtech.qianji.ui.main.MainActivity")) {
                            Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
                            if (intent != null) {
                                int AutoSignal = intent.getIntExtra("AutoSignal", AppBroadcast.BROADCAST_NOTHING);
                                if (AutoSignal == AppBroadcast.BROADCAST_ASYNC) {
                                    utils.log("钱迹收到同步信号:开始从本地数据库提取数据");
                                    utils.log("用户ID:", userId);
                                    JSONObject jsonObject = new JSONObject();
                                    JSONArray userBooks = dbHelper[0].getUserBook(isVip, userId);
                                    List<String> uids = new ArrayList<>();
                                    jsonObject.put("userBook", userBooks);
                                    JSONArray categorys = new JSONArray();
                                    JSONArray asset = new JSONArray();
                                    for (int i = 0; i < userBooks.size(); i++) {
                                        JSONObject userBook = userBooks.getJSONObject(i);
                                        String userId = userBook.getString("userId");
                                        if (!uids.contains(userId)) {
                                            uids.add(userId);
                                            asset.addAll(dbHelper[0].getAsset(userId));
                                            categorys.addAll(dbHelper[0].getCategory(userId));
                                        }
                                    }
                                    jsonObject.put("asset", asset);
                                    jsonObject.put("category", categorys);
                                    jsonObject.put("AutoSignal", AutoSignal);
                                    // jsonObject.put("billInfo", dbHelper[0].getBills());
                                    utils.send2auto(jsonObject.toJSONString());

                                    Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在返回自动记账。", Toast.LENGTH_LONG).show();
                                    XposedHelpers.callMethod(activity, "finishAndRemoveTask");

                                } else if (AutoSignal == AppBroadcast.BROADCAST_GET_REI) {
                                    utils.log("钱迹收到信号:开始从本地数据库提取待报销账单");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("bill", dbHelper[0].getBills("5", userId));
                                    jsonObject.put("AutoSignal", AutoSignal);
                                    utils.send2auto(jsonObject.toJSONString());
                                    Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在返回自动记账。", Toast.LENGTH_LONG).show();
                                    XposedHelpers.callMethod(activity, "finishAndRemoveTask");
                                }

                            }
                        }
                    }
                });
            }
        });

    }

}
