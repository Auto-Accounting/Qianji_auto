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
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class hookDb {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils) {

        try {
            hookQianjiApp(utils);
        } catch (Throwable e) {
            utils.log("钱迹 HookError " + e.toString());
        }
        try {
            hookQianjiTimeout(utils);
        } catch (Throwable e) {
            utils.log("钱迹 HookError " + e.toString());
        }
        try {
            hookQianjiError(utils);
        } catch (Throwable e) {
            utils.log("钱迹 HookError " + e.toString());
        }


    }

    // hook钱迹数据库
    public static void hookQianjiApp(Utils utils) {
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

                String allTable = dbHelper[0].getAllTables();
                // 测试一下前面的对象有效
                if (allTable == null) {
                    utils.log("钱迹获取数据失败");
                    dbHelper[0].finalize();
                    return;
                }

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
                                ArrayList<Data> asset = dbHelper[0].getAsset(finalUserId);
                                ArrayList<Data> category = dbHelper[0].getCategory(finalUserId);
                                ArrayList<Data> userBook = dbHelper[0].getUserBook(finalUserId);
                                ArrayList<Data> billInfo = dbHelper[0].getBills(finalUserId);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("asset", asset);
                                bundle.putParcelableArrayList("category", category);
                                bundle.putParcelableArrayList("userBook", userBook);
                                bundle.putParcelableArrayList("billInfo", billInfo);
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

    //hook钱迹timeout
    public static void hookQianjiTimeout(Utils utils) {
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.a", utils.getClassLoader(), "timeoutApp", String.class, long.class, XC_MethodReplacement.returnConstant(true));
    }

    //hook钱迹error信息

    public static void hookQianjiError(Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> AutoTaskLog = mAppClassLoader.loadClass("com.mutangtech.qianji.data.model.AutoTaskLog");
        Class<?> WebViewActivity = mAppClassLoader.loadClass("com.mutangtech.qianji.ui.webview.WebViewActivity");
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.bill.auto.AddBillIntentAct", mAppClassLoader, "a", String.class, AutoTaskLog, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                String string = (String) param.args[0];

                //钱迹弹出错误信息，转发给自动记账处理~
                if (string != null) {
                    String url = "https://pan.ankio.net";
                    if (string.contains("bookname")) {
                        // TODO 根据不同错误信息给出解决方案地址
                    } else if (string.contains("accountname")) {

                    }
                    //WebViewActivity.start(this.getActivity(), com.mutangtech.qianji.f.e.a.getPrivacyPolicyUrl(), e.b(0x7F1004A8));
                    // Method method = WebViewActivity.getDeclaredMethod("start", Context.class,String.class,String.class);
//Context arg1, String arg2, String arg3
                    // method.invoke(null, utils.getContext(),url,"自动记账错误解决方案"); // obj 传 null

                    Intent v0 = new Intent(utils.getContext(), WebViewActivity);
                    v0.putExtra("param_web_url", url);
                    v0.putExtra("param_web_title", "自动记账错误解决方案");
                    v0.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    utils.getContext().startActivity(v0);
                    //com.mutangtech.qianji.ui.webview
                    //使用钱迹的WebView
                    //加载解决方案
                    Looper.prepare();
                    Toast.makeText(utils.getContext(), string + "\n发生了错误，正在为您加载解决方案！", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    utils.log("钱迹错误捕获： " + string, true);
                }


            }
        });
    }

    // hook钱迹登录信息
    public void hookQianjiLogin(Utils utils) {
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.app.f.b", utils.getClassLoader(), "isLogin", String.class, long.class, XC_MethodReplacement.returnConstant(true));
    }
}
