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

package cn.dreamn.qianji_auto.core.hook.hooks.wangc.hooks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.broadcast.AppBroadcast;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.hooks.wangc.DBHelper;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DataBase {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        utils.log("自动记账同步：一木初始化", false);
        ClassLoader mAppClassLoader = utils.getClassLoader();

        XposedHelpers.findAndHookMethod("com.wangc.bill.database.c", mAppClassLoader, "a", Application.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> litePalClass = XposedHelpers.findClass("org.litepal.LitePal", mAppClassLoader);
                // 获取数据库实例
                Object litePalDatabaseInstance = XposedHelpers.callStaticMethod(litePalClass, "getDatabase");

                if (litePalDatabaseInstance instanceof SQLiteDatabase) {
                    SQLiteDatabase database = (SQLiteDatabase) litePalDatabaseInstance;
                    // 获取数据库操作对象
                    final DBHelper[] dbHelper = new DBHelper[1];
                    utils.log("自动记账同步：获取一木记账数据库对象", false);
                    if (database != null && database.isOpen()) {
                        db = database;
                        utils.log("使用一木记账对象获取信息", false);
                        dbHelper[0] = new DBHelper(db, utils);
                    } else {
                        utils.log("一木记账数据库对象无法获取到数据，尝试文件模式", false);
                        dbHelper[0] = new DBHelper(utils);
                    }

                    final boolean[] hooked = {false};

                    XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {

                        @SuppressLint("Range")
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (hooked[0]) return;
                            hooked[0] = true;
                            Activity activity = (Activity) param.thisObject;
                            final String activityClzName = activity.getClass().getName();
                            if (activityClzName.contains("com.wangc.bill.activity.MainActivity")) {
                                Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
                                if (intent != null) {
                                    int AutoSignal = intent.getIntExtra("AutoSignal", AppBroadcast.BROADCAST_NOTHING);
                                    if (AutoSignal == AppBroadcast.BROADCAST_ASYNC) {
                                        utils.log("自动记账同步：一木记账开始同步", false);
                                        JSONObject jsonObject = new JSONObject();
                                        String userId = dbHelper[0].getUserId();
                                        JSONArray userBooks = dbHelper[0].getUserBook(userId);
                                        List<String> uids = new ArrayList<>();
                                        jsonObject.put("userBook", userBooks);
                                        JSONArray categorys = new JSONArray();
                                        JSONArray asset = new JSONArray();
                                        for (int i = 0; i < userBooks.size(); i++) {
                                            JSONObject userBook = userBooks.getJSONObject(i);
                                            String bookUserId = userBook.getString("userid");
                                            String bookId = userBook.getString("id");
                                            if (!uids.contains(bookUserId)) {
                                                uids.add(bookUserId);
                                                asset.addAll(dbHelper[0].getAsset(userId));
                                                categorys.addAll(dbHelper[0].getCategory(userId, bookId));
                                            }
                                        }
                                        jsonObject.put("asset", asset);
                                        jsonObject.put("category", categorys);
                                        jsonObject.put("AutoSignal", AutoSignal);
                                        utils.send2auto(jsonObject.toJSONString());
                                        Toast.makeText(utils.getContext(), "一木记账数据信息获取完毕，现在返回自动记账。", Toast.LENGTH_LONG).show();
                                        XposedHelpers.callMethod(activity, "finishAndRemoveTask");
                                    }
                                }
                            }
                        }
                    });

                    XposedHelpers.findAndHookMethod("com.wangc.bill.activity.SchemeAddActivity", mAppClassLoader, "onCreate", Bundle.class, new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Activity activity = (Activity) param.thisObject;
                            Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
                            if (intent != null) {
                                String path = intent.getData().getPath();
                                //String parentCategory = intent.getData().getQueryParameter("parentCategory");
                                switch (path){
                                    case "/transfer":
                                        Toast.makeText(utils.getContext(), "暂未支持转账", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        }
                    });

                }
            }
        });
    }

}
