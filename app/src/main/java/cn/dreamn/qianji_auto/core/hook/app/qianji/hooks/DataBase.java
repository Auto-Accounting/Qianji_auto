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

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;

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
                boolean isVip;
                isVip = (boolean) IsVip.invoke(object);
                //获取最终的UID
                utils.log("钱迹用户:" + (isVip ? "会员" : "非会员"));


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
                                String needAsync = intent.getStringExtra("needAsync");
                                if (needAsync == null) {
                                    return;
                                }
                                utils.log("钱迹收到同步信号:" + intent.getStringExtra("needAsync"));

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("asset", dbHelper[0].getAsset());
                                jsonObject.put("category", dbHelper[0].getCategory());
                                jsonObject.put("userBook", dbHelper[0].getUserBook(isVip));
                                jsonObject.put("billInfo", dbHelper[0].getBills());

                                utils.send2auto(jsonObject.toJSONString());

                                Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在返回自动记账。", Toast.LENGTH_LONG).show();
                                XposedHelpers.callMethod(activity, "finishAndRemoveTask");


                            } else {
                                utils.log("intent获取失败");
                            }
                        }
                    }
                });
            }
        });

    }

}
