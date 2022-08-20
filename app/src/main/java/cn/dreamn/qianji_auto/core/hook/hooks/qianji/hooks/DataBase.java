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

package cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.core.broadcast.AppBroadcast;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.DBHelper;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DataBase {
    private static SQLiteDatabase db = null;

    public static void init(Utils utils,JSONArray jsonArray) {
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
                    loginClass = mAppClassLoader.loadClass(jsonArray.getString(0));
                } catch (Throwable ignored) {
                }
                if (loginClass == null) {
                    utils.log("钱迹未适配！");
                    return;
                }
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
                Class<?> finalLoginClass = loginClass;
                XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
                    @SuppressLint("Range")
                    protected void beforeHookedMethod(MethodHookParam param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
                                } else if (AutoSignal == AppBroadcast.BROADCAST_GET_YEAR) {
                                    utils.log("钱迹收到信号:开始从本地数据库提取待年度账单");
                                    JSONObject jsonObject = new JSONObject();
                                    Class<?> User = mAppClassLoader.loadClass("com.mutangtech.qianji.data.model.User");
                                    Method getLoginUser = finalLoginClass.getMethod("getLoginUser");
                                    Object UserData = getLoginUser.invoke(object);
                                    Method getName = User.getMethod("getName");
                                    String userName = (String) getName.invoke(UserData);
                                    jsonObject.put("name", userName);
                                    //获取用户昵称
                                    //"bill": {
                                    //    "page1": {
                                    //      "name": "年度总消费",
                                    //      "yearAll": 3190000.20,
                                    //      "yearTotal": 3190000
                                    //    },
                                    //

                                    JSONObject jsonObjectBill = new JSONObject();
                                    SQLiteDatabase db = dbHelper[0].getDb();
                                    Cursor cursor = db.rawQuery("select sum(MONEY) as yearAll  from user_bill where USERID='" + userId + "' and type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021' limit 1", null);
                                    JSONObject jsonObject1 = new JSONObject();
                                    double outTotal = 0;
                                    double inTotal = 0;

                                    while (cursor.moveToNext()) {
                                        BigDecimal b = new BigDecimal(cursor.getDouble(0));
                                        outTotal = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        jsonObject1.put("yearAll", outTotal);
                                    }
                                    cursor.close();

                                    cursor = db.rawQuery("select count(_id) as yearAll  from user_bill where USERID='" + userId + "' and type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021' limit 1", null);
                                    while (cursor.moveToNext()) {
                                        jsonObject1.put("yearTotal", cursor.getDouble(0));
                                    }
                                    cursor.close();

                                    jsonObjectBill.put("page1", jsonObject1);
//"page7":{
//      "name": "年底信息",
//      "yearMoney": 9000,
//      "inMoney": 9000,
//      "outMoney": 9000
//    }
                                    JSONObject jsonObject12 = new JSONObject();
                                    cursor = db.rawQuery("select sum(MONEY) as yearAll  from user_bill where USERID='" + userId + "' and type = 1 and strftime('%Y',createtime,'unixepoch','localtime')='2021' limit 1", null);
                                    while (cursor.moveToNext()) {
                                        inTotal = cursor.getDouble(0);
                                        BigDecimal b = new BigDecimal(inTotal);
                                        inTotal = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                        jsonObject12.put("inMoney", inTotal);
                                        jsonObject12.put("outMoney", outTotal);
                                        jsonObject12.put("yearMoney", inTotal - outTotal);
                                    }
                                    cursor.close();
                                    jsonObjectBill.put("page7", jsonObject12);
                                    //"page2": {
                                    //      "name": "最早的和最晚的消费，取22点到3点，3点到7点",
                                    //      "earlyDate": "2021-06-04",
                                    //      "earlyTime": "05:31:00",
                                    //      "earlyMoney": "66.0",
                                    //      "earlyRemark": "万源饺子城 - 三餐",
                                    //      "lateDate": "2021-06-04",
                                    //      "lateTime": "05:31:00",
                                    //      "lateMoney": "66.0",
                                    //      "lateRemark": "万源饺子城 - 三餐"
                                    //    },
                                    //select *,min(time(createtime, 'unixepoch', 'localtime','-4 hour')) ,datetime(createtime, 'unixepoch', 'localtime') from user_bill where USERID='200104405e109647c18e9' and type = 0 ;
                                    JSONObject jsonObject2 = new JSONObject();
                                    cursor = db.rawQuery("select *,min(time(createtime, 'unixepoch', 'localtime','-4 hour')) ,datetime(createtime, 'unixepoch', 'localtime') as billtime  from user_bill where USERID='" + userId + "' and type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021'", null);
                                    while (cursor.moveToNext()) {
                                        String time = cursor.getString(cursor.getColumnIndex("billtime"));
                                        String[] t = time.split(" ");
                                        if (t.length != 2) continue;
                                        jsonObject2.put("earlyDate", t[0]);
                                        jsonObject2.put("earlyTime", t[1]);
                                        jsonObject2.put("earlyMoney", cursor.getString(cursor.getColumnIndex("MONEY")));
                                        jsonObject2.put("earlyRemark", cursor.getString(cursor.getColumnIndex("REMARK")));
                                    }
                                    cursor.close();
                                    cursor = db.rawQuery("select *,max(time(createtime, 'unixepoch', 'localtime','-4 hour')) ,datetime(createtime, 'unixepoch', 'localtime') as billtime  from user_bill where USERID='" + userId + "' and type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021'", null);
                                    while (cursor.moveToNext()) {
                                        String time = cursor.getString(cursor.getColumnIndex("billtime"));
                                        String[] t = time.split(" ");
                                        if (t.length != 2) continue;
                                        jsonObject2.put("lateDate", t[0]);
                                        jsonObject2.put("lateTime", t[1]);
                                        jsonObject2.put("lateMoney", cursor.getString(cursor.getColumnIndex("MONEY")));
                                        jsonObject2.put("lateRemark", cursor.getString(cursor.getColumnIndex("REMARK")));
                                    }
                                    cursor.close();
                                    jsonObjectBill.put("page2", jsonObject2);
                                    //"page3":{
                                    //      "name": "消费偏好",
                                    //      "maxPay": "花呗",
                                    //      "maxPayMoney": "12.00",
                                    //      "maxNumber": 999,
                                    //      "data": {
                                    //        "建设银行": 99,
                                    //        "交通银行": 99,
                                    //        "现金":999
                                    //      }
                                    //    },
                                    JSONObject jsonObject3 = new JSONObject();
                                    cursor = db.rawQuery("select count(FROMACT) as num,FROMACT,sum(MONEY) as money   from user_bill where USERID='" + userId + "' and type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021' GROUP BY FROMACT ORDER BY count(FROMACT) DESC;", null);
                                    int maxTime = 0;
                                    String maxPayTool = "";
                                    double maxMoney = 0.0d;
                                    //JSONObject jsonObject4 = new JSONObject();
                                    JSONArray jsonArray4 = new JSONArray();
                                    while (cursor.moveToNext()) {
                                        int time = cursor.getInt(cursor.getColumnIndex("num"));
                                        double money = cursor.getDouble(cursor.getColumnIndex("money"));
                                        String payTool = cursor.getString(cursor.getColumnIndex("FROMACT"));
                                        if (time == 0)
                                            continue;
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        if (time > maxTime) {
                                            maxTime = time;
                                            maxPayTool = payTool;
                                            maxMoney = money;
                                        }
                                        JSONObject jsonObject5 = new JSONObject();
                                        jsonObject5.put("num", time);
                                        jsonObject5.put("money", money);
                                        jsonObject5.put("payTool", payTool);
                                        jsonArray4.add(jsonObject5);
                                        // jsonObject4.put(payTool, time);
                                    }
                                    jsonObject3.put("data", jsonArray4);
                                    jsonObject3.put("maxPay", maxPayTool);
                                    jsonObject3.put("maxPayMoney", maxMoney);
                                    jsonObject3.put("maxNumber", maxTime);
                                    cursor.close();
                                    jsonObjectBill.put("page3", jsonObject3);

                                    // "page4":{
                                    //      "name": "消费和收入最高的分类",
                                    //      "maxType": "单车",
                                    //      "maxMoney": 999,
                                    //      "maxInType": "医疗",
                                    //      "maxInMoney": 999,
                                    //      "in":[13,  6, 7,1, 23, 10, 11, 23, 11, 23, 34, 5],
                                    //      "out":[11, 23, 9, 10, 11, 23, 9, 10, 11, 53, 44]
                                    //    },

                                    JSONObject jsonObject5 = new JSONObject();
                                    cursor = db.rawQuery("select strftime('%m',createtime,'unixepoch','localtime') as Month,SUM(MONEY) as Money from user_bill where USERID='" + userId + "' and user_bill.type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021' GROUP BY strftime('%m',createtime,'unixepoch','localtime');", null);

                                    // JSONObject jsonObject6 = new JSONObject();
                                    JSONArray jsonArray = new JSONArray();
                                    for (int i = 0; i < 12; i++) {
                                        jsonArray.add(0);
                                    }

                                    while (cursor.moveToNext()) {
                                        String month = cursor.getString(cursor.getColumnIndex("Month"));
                                        double money = cursor.getDouble(cursor.getColumnIndex("Money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        //  utils.log("month:"+month,"money:",String.valueOf(money));
                                        jsonArray.set(Integer.parseInt(month) - 1, money);
                                    }
                                    jsonObject5.put("out", jsonArray);
                                    cursor.close();


                                    cursor = db.rawQuery("select strftime('%m',createtime,'unixepoch','localtime') as Month,SUM(MONEY) as Money from user_bill where USERID='" + userId + "' and user_bill.type = 1 and strftime('%Y',createtime,'unixepoch','localtime')='2021' GROUP BY strftime('%m',createtime,'unixepoch','localtime');", null);
                                    // JSONObject jsonObject6 = new JSONObject();
                                    JSONArray jsonArray2 = new JSONArray();
                                    for (int i = 0; i < 12; i++) {
                                        jsonArray2.add(0);
                                    }
                                    while (cursor.moveToNext()) {
                                        String month = cursor.getString(cursor.getColumnIndex("Month"));
                                        double money = cursor.getDouble(cursor.getColumnIndex("Money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        jsonArray2.set(Integer.parseInt(month) - 1, money);
                                    }
                                    jsonObject5.put("in", jsonArray2);
                                    cursor.close();

                                    //"page6":{
                                    //      "name": "收支分类",
                                    //      "maxPay": {
                                    //        "炒股":5,
                                    //        "炒股2":5,
                                    //        "炒股3":5
                                    //      },
                                    //      "maxIn":  {
                                    //        "炒股":5,
                                    //        "炒股2":5,
                                    //        "炒股3":5
                                    //      }
                                    JSONObject jsonObject6 = new JSONObject();
                                    cursor = db.rawQuery("select SUM(MONEY) as money,category.NAME as cname  from user_bill,category where USERID='" + userId + "' and user_bill.type = 0 and strftime('%Y',createtime,'unixepoch','localtime')='2021' and user_bill.CATEGORY_ID=category._id GROUP BY CATEGORY_ID ORDER BY SUM(MONEY) DESC;", null);


                                    int i = 0;
                                    JSONArray jsonArray1 = new JSONArray();
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex("cname"));
                                        double money = cursor.getDouble(cursor.getColumnIndex("money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        if (i == 0) {
                                            jsonObject5.put("maxType", name);
                                            jsonObject5.put("maxMoney", money);
                                        }
                                        JSONObject json = new JSONObject();
                                        json.put("name", name);
                                        json.put("money", money);
                                        jsonArray1.add(json);

                                        i++;
                                    }
                                    jsonObject6.put("maxPay", jsonArray1);
                                    cursor.close();
                                    cursor = db.rawQuery("select SUM(MONEY) as money,category.NAME as cname  from user_bill,category where USERID='" + userId + "' and user_bill.type = 1 and strftime('%Y',createtime,'unixepoch','localtime')='2021' and user_bill.CATEGORY_ID=category._id GROUP BY CATEGORY_ID ORDER BY SUM(MONEY) DESC;", null);
                                    i = 0;
                                    JSONArray jsonArray3 = new JSONArray();
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex("cname"));
                                        double money = cursor.getDouble(cursor.getColumnIndex("money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        if (i == 0) {
                                            jsonObject5.put("maxInType", name);
                                            jsonObject5.put("maxInMoney", money);
                                        }
                                        JSONObject json = new JSONObject();
                                        json.put("name", name);
                                        json.put("money", money);
                                        jsonArray3.add(json);

                                        i++;
                                    }
                                    jsonObject6.put("maxIn", jsonArray3);
                                    jsonObjectBill.put("page6", jsonObject6);
                                    jsonObjectBill.put("page4", jsonObject5);
                                    cursor.close();


                                    //"page5":{
                                    //      "name": "借钱信息",
                                    //      "outTotal": 99.41,
                                    //      "outPeople": "王二",
                                    //      "outPeopleMoney": 66,
                                    //      "inTotal": 99.41,
                                    //      "inPeople": "王二",
                                    //      "inPeopleMoney": 66
                                    //    },

                                    cursor = db.rawQuery("select COUNT(MONEY)as totals,SUM(MONEY)as money,DESCINFO  from user_bill where USERID='" + userId + "' and user_bill.type = 7  and strftime('%Y',createtime,'unixepoch','localtime')='2021' GROUP BY DESCINFO ORDER BY SUM(MONEY) DESC;", null);
                                    JSONObject jsonObject9 = new JSONObject();
                                    i = 0;
                                    double total = 0;
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex("DESCINFO"));

                                        double money = cursor.getDouble(cursor.getColumnIndex("money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        if (i == 0) {
                                            jsonObject9.put("outPeople", name);
                                            jsonObject9.put("outPeopleMoney", money);
                                        }
                                        total += money;
                                        i++;
                                    }
                                    jsonObject9.put("outTotal", total);
                                    cursor.close();

                                    cursor = db.rawQuery("select COUNT(MONEY)as totals,SUM(MONEY)as money,DESCINFO  from user_bill where USERID='" + userId + "' and user_bill.type = 6  and strftime('%Y',createtime,'unixepoch','localtime')='2021' GROUP BY DESCINFO ORDER BY SUM(MONEY) DESC;", null);
                                    i = 0;
                                    total = 0;
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex("DESCINFO"));

                                        double money = cursor.getDouble(cursor.getColumnIndex("money"));
                                        BigDecimal b = new BigDecimal(money);
                                        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        if (i == 0) {
                                            jsonObject9.put("inPeople", name);
                                            jsonObject9.put("inPeopleMoney", money);
                                        }
                                        total += money;

                                    }
                                    jsonObject9.put("inTotal", total);
                                    cursor.close();

                                    jsonObjectBill.put("page5", jsonObject9);
                                    jsonObject.put("bill", jsonObjectBill);
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
