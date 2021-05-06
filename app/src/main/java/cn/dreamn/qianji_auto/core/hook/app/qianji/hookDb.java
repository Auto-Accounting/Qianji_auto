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

package cn.dreamn.qianji_auto.core.hook.app.qianji;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.xuexiang.xutil.common.StringUtils;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookDb {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        XposedHelpers.findAndHookConstructor("com.mutangtech.qianji.data.model.DaoMaster", mAppClassLoader, SQLiteDatabase.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                utils.log("HOOK 钱迹数据库对象", false);
                super.beforeHookedMethod(param);
                SQLiteDatabase database = (SQLiteDatabase) param.args[0];
                // 验证一下是否有效
                if (database.isOpen()) {
                    db = database;
                    XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Activity activity = (Activity) param.thisObject;
                            final String activityClzName = activity.getClass().getName();
                            utils.log(activityClzName);
                            if (activityClzName.contains("com.mutangtech.qianji.ui.main.MainActivity")) {
                                doDbHook(activity, utils);
                            }
                        }
                    });
                }
            }
        });




    }

    private static void doDbHook(Activity activity, Utils utils) {
        Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
        if (intent != null) {
            utils.log("钱迹收到数据:" + intent.getStringExtra("needAsync"));
            if (intent.getStringExtra("needAsync") != null && intent.getStringExtra("needAsync").equals("true")) {
                DBHelper dbHelper = null;
                try {
                    String allTable = "";
                    if (db != null) {
                        utils.log("db构建资产同步查询", false);
                        dbHelper = new DBHelper(db);
                        allTable = dbHelper.getAllTables();
                    }
                    // 测试一下前面的对象有效
                    if (StringUtils.isEmptyTrim(allTable)) {
                        utils.log("钱迹数据库对象无法获取到数据，尝试文件模式");
                        dbHelper = null;
                    }

                    if (dbHelper == null) {
                        dbHelper = new DBHelper(utils);
                    }

                    utils.log("钱迹表格数据：" + allTable);
                    ArrayList<Data> asset = dbHelper.getAsset();
                    ArrayList<Data> category = dbHelper.getCategory();
                    ArrayList<Data> userBook = dbHelper.getUserBook();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("asset", asset);
                    bundle.putParcelableArrayList("category", category);
                    bundle.putParcelableArrayList("userBook", userBook);
                    //  utils.log(bundle.toString());
                    utils.send2auto(bundle);

                    Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在请返回自动记账。", Toast.LENGTH_LONG).show();
                    XposedHelpers.callMethod(activity, "finish");
                } catch (Exception e) {
                    utils.log("钱迹数据获取失败" + e.toString(), false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    utils.log("钱迹数据获取失败2" + throwable.toString(), false);
                } finally {
                    if (dbHelper != null) {
                        dbHelper.finalize();
                    }
                }
            }
        } else {
            utils.log("intent获取失败");
        }
    }


    public void hookQianjiApp() {

    }


}
