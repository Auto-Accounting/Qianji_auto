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


    public static void hookQianjiApp(Utils utils) {
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
                                            if (allTable == null) {
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
                        }
                    });
                }
            }
        });
    }

    public static void hookQianjiTimeout(Utils utils) {
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.a", utils.getClassLoader(), "timeoutApp", String.class, long.class, XC_MethodReplacement.returnConstant(true));
    }

    //hook钱迹timeout

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
