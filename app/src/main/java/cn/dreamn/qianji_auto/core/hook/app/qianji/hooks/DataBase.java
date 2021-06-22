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
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.app.qianji.DBHelper;
import cn.dreamn.qianji_auto.core.hook.app.qianji.Data;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DataBase {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils) {

        ClassLoader mAppClassLoader = utils.getClassLoader();
        XposedHelpers.findAndHookConstructor("com.mutangtech.qianji.data.model.DaoMaster", mAppClassLoader, SQLiteDatabase.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SQLiteDatabase database = (SQLiteDatabase) param.args[0];
                // 获取数据库操作对象
                final DBHelper[] dbHelper = new DBHelper[1];

                if (database != null && database.isOpen()) {
                    db = database;
                    utils.log("使用钱迹对象获取信息", false);
                    dbHelper[0] = new DBHelper(db);
                } else {
                    utils.log("钱迹数据库对象无法获取到数据，尝试文件模式", false);
                    dbHelper[0] = new DBHelper(utils);
                }
                //获取用户ID,根据用户ID获取对应的资产等信息
                String userId = "u10001";

                Class<?> loginClass = mAppClassLoader.loadClass("com.mutangtech.qianji.app.f.b");
                //获取loginClass
                Method getInstance = loginClass.getDeclaredMethod("getInstance");
                //反射调用单例模式
                Object object = getInstance.invoke(null);
                //获取对象
                Method getLoginUserID = loginClass.getMethod("getLoginUserID");
                //获取UserID方法
                String uid = (String) getLoginUserID.invoke(object);
                //获取最终的UID
                if (uid != null && !uid.equals("")) {
                    userId = uid;
                }
                utils.log("获取到用户ID:" + userId);


                String finalUserId = userId;
                XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
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

                                String allTable = dbHelper[0].getAllTables();
                                // 同步的时候测试对象有效。
                                if (allTable == null) {
                                    utils.log("钱迹获取数据失败");
                                    XposedHelpers.callMethod(activity, "finish");
                                    dbHelper[0].finalize();
                                    return;
                                }

                                ArrayList<Data> asset = dbHelper[0].getAsset(finalUserId);
                                ArrayList<Data> category = dbHelper[0].getCategory(finalUserId);
                                ArrayList<Data> userBook = dbHelper[0].getUserBook(finalUserId);
                                //   ArrayList<Data> billInfo = dbHelper[0].getBills(finalUserId);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("asset", asset);
                                bundle.putParcelableArrayList("category", category);
                                bundle.putParcelableArrayList("userBook", userBook);
                                //   bundle.putParcelableArrayList("billInfo", billInfo);
                                //  utils.log(bundle.toString());
                                utils.send2auto(bundle);

                                Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在返回自动记账。", Toast.LENGTH_LONG).show();
                                XposedHelpers.callMethod(activity, "finish");

                                dbHelper[0].finalize();


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
