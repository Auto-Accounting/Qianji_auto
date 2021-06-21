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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
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
        try {
            hookQianjiLeft(utils);
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

    public static void hookQianjiLeft(Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> MainDrawerLayout = mAppClassLoader.loadClass("com.mutangtech.qianji.ui.maindrawer.MainDrawerLayout");
        XposedHelpers.findAndHookMethod(MainDrawerLayout, "a", new XC_MethodHook() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object object = param.thisObject;
                //获取对象
                Field field = object.getClass().getDeclaredField("b");
                //打开私有访问
                field.setAccessible(true);
                //获取属性值
                LinearLayout linearLayout = (LinearLayout) field.get(object);
                //   Method findViewById = MainDrawerLayout.getMethod("findViewById",int.class);
                //
                //   LinearLayout linearLayout = (LinearLayout) findViewById.invoke(MainDrawerLayout,0x7F0902C4);
                //     TextView tv1 = new TextView(utils.getContext());
                //   tv1.setText("   💰     自动记账（"+BuildConfig.VERSION_NAME+"）");
                //          @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableLeft = utils.getContext().getDrawable(android.R.drawable.ic_dialog_dialer);

                //     tv1.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

                //     tv1.setCompoundDrawablePadding(10);
                //int padding=ScreenUtils.px2dip(utils.getContext(),26);
                //              tv1.setPadding(16,32,0,16);
                //             tv1.setTextSize(17);
                // <LinearLayout android:gravity="0x10" android:id="@id/user_center_multi_book" android:layout_height="-2" android:layout_marginTop="@dimen/keyline_24" android:layout_width="-1" style="@style/MainDrawerItem">
                //      <TextView android:drawableLeft="@drawable/ic_multi_book" android:drawablePadding="@dimen/keyline_16" android:layout_height="-2" android:layout_width="-2" android:text="@string/title_my_book" android:textAppearance="?com.mutangtech.qianji:attr/textAppearanceBody1"/>
                //      <TextView android:gravity="0x5" android:id="@id/user_center_multi_book_name" android:layout_height="-2" android:layout_width="-1" style="@style/OptionItem_Hint"/>
                //    </LinearLayout>
        /*        if(ThemeManager.isDarkMode(utils.getContext())){
                    tv1.setTextColor(0x80ffffff);
                }else{
                    tv1.setTextColor(0x80000000);
                }
               tv1.setOnClickListener(v->{
                    Intent intent = utils.getContext().getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        utils.getContext().startActivity(intent);
                    }
                });*/


                // linearLayout.addView(tv1);

                Context activity = utils.getContext();

                LinearLayout settingsItemRootLLayout = new LinearLayout(activity);
                settingsItemRootLLayout.setOrientation(LinearLayout.VERTICAL);
                settingsItemRootLLayout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                settingsItemRootLLayout.setPadding(16, 0, 0, 16);

                LinearLayout settingsItemLinearLayout = new LinearLayout(activity);
                settingsItemLinearLayout.setOrientation(LinearLayout.VERTICAL);

                settingsItemLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                LinearLayout itemHlinearLayout = new LinearLayout(activity);
                itemHlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemHlinearLayout.setWeightSum(1);
                //itemHlinearLayout.setBackground(ViewUtil.genBackgroundDefaultDrawable(isDarkMode ? 0xFF191919 : Color.WHITE, isDarkMode ? 0xFF1D1D1D : 0xFFE5E5E5));
                itemHlinearLayout.setGravity(Gravity.CENTER_VERTICAL);
                itemHlinearLayout.setClickable(true);
                itemHlinearLayout.setOnClickListener(view -> {
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
                    if (intent != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                });


                int defHPadding = ScreenUtils.dip2px(activity, 15);

                boolean isDarkMode = ThemeManager.isDarkMode(activity);

                TextView itemNameText = new TextView(activity);
                itemNameText.setTextColor(isDarkMode ? 0xFFD3D3D3 : 0xFF353535);
                itemNameText.setText("💰    自动记账");
                itemNameText.setGravity(Gravity.CENTER_VERTICAL);
                itemNameText.setPadding(ScreenUtils.dip2px(activity, 16), 0, 0, 0);
                itemNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                TextView itemSummerText = new TextView(activity);
                itemSummerText.setText(BuildConfig.VERSION_NAME);
                itemSummerText.setGravity(Gravity.CENTER_VERTICAL);
                itemSummerText.setPadding(0, 0, defHPadding, 0);
                itemSummerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                itemSummerText.setTextColor(isDarkMode ? 0xFF656565 : 0xFF999999);


                itemHlinearLayout.addView(itemNameText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                itemHlinearLayout.addView(itemSummerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                View lineView = new View(activity);
                lineView.setBackgroundColor(isDarkMode ? 0xFF2E2E2E : 0xFFD5D5D5);
                // settingsItemLinearLayout.addView(lineView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                settingsItemLinearLayout.addView(itemHlinearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(activity, 55)));

                settingsItemRootLLayout.addView(settingsItemLinearLayout);
                settingsItemRootLLayout.setTag(BuildConfig.APPLICATION_ID);

                linearLayout.addView(settingsItemRootLLayout);

            }
        });
        //获取最终的UID
        //
        //private View createView(String txt) {
        //        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        //                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        //        // View view =LayoutInflater.from(this).inflate(R.layout.view_item, null);//也可以从XML中加载布局
        //        LinearLayout view = new LinearLayout(this);
        //        view.setLayoutParams(lp);//设置布局参数
        //        view.setOrientation(LinearLayout.HORIZONTAL);// 设置子View的Linearlayout// 为垂直方向布局
        //
        //        //定义子View中两个元素的布局
        //        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
        //                ViewGroup.LayoutParams.WRAP_CONTENT,
        //                ViewGroup.LayoutParams.WRAP_CONTENT);
        //        ViewGroup.LayoutParams vlp2 = new ViewGroup.LayoutParams(
        //                ViewGroup.LayoutParams.WRAP_CONTENT,
        //                ViewGroup.LayoutParams.WRAP_CONTENT);
        //
        //        TextView tv1 = new TextView(this);
        //        TextView tv2 = new TextView(this);
        //        tv1.setLayoutParams(vlp);//设置TextView的布局
        //        tv2.setLayoutParams(vlp2);
        //        tv1.setText("姓名: ");
        //        tv2.setText(txt);
        //        tv2.setPadding(calculateDpToPx(50), 0, 0, 0);//设置边距
        //        view.addView(tv1);//将TextView 添加到子View 中
        //        view.addView(tv2);//将TextView 添加到子View 中
        //        return view;
        //    }
    }
}
