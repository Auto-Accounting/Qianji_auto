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

package cn.dreamn.qianji_auto.database.Helper;


import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.IdentifyRegular;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.JsEngine;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class identifyRegulars {

    public static void run(String identify, String app, String data, BundleGet get) {
        getAllRegularJs(data, identify, app, str -> {
            //获得所有Js
            String result = "";
            try {
                result = JsEngine.run(str);
            } catch (Throwable ex) {
                Log.i("错误：" + ex.toString());
            }
            if (!result.startsWith("undefined")) {

                String[] strs = result.split("\\|", -1);
                Log.i("解析结果", Arrays.toString(strs));
                if (strs.length < 8) {
                    get.onGet(null);
                    return;
                }
                BillInfo billInfo = new BillInfo();
                Log.i(strs[0]);
                billInfo.setShopRemark(strs[0]);
                billInfo.setrawAccount(strs[1]);
                billInfo.setType(strs[2]);
                billInfo.setMoney(strs[3]);
                billInfo.setShopAccount(strs[5]);
                billInfo.setrawAccount2(strs[4]);
                billInfo.setFee(strs[6]);
                billInfo.setTimeStamp(DateUtils.getAnyTime(strs[7]));
                get.onGet(billInfo);
            } else {
                Log.i("js执行结果为NULL");
                get.onGet(null);
            }
        });
    }


    public interface BundleGet {
        void onGet(BillInfo billInfo);
    }




    //获取所有的js
    public static void getAllRegularJs(String Body,String identify,String fromApp,getString getStr) {

        getAll(identify, fromApp, true, identifyRegulars -> {
            StringBuilder smsList = new StringBuilder();
            for (Bundle value : identifyRegulars) {
                String data = ";try{pattern=/%s/;if(pattern.test(a)){var array=pattern.exec(a);var remark='%s',account='%s',type='%s',money='%s',shopName='%s',account2='%s',fee='%s',time='%s';for(var i=array.length-1;i>=1;i--){var rep=\"$\"+i.toString();var repStr=array[i];remark=remark.replace(rep,repStr);account=account.replace(rep,repStr);type=type.replace(rep,repStr);money=money.replace(rep,repStr);shopName=shopName.replace(rep,repStr);account2=account2.replace(rep,repStr);fee=fee.replace(rep,repStr);time=time.replace(rep,repStr)}return remark+'|'+account+'|'+type+'|'+money+'|'+account2+'|'+shopName+'|'+fee+\"|\"+time}}catch(e){console.log(e)};";
                JSONObject jsonObject = JSONObject.parseObject(value.getString("tableList"));
                data = String.format(data, value.getString("regular"), jsonObject.get("shopRemark"), jsonObject.get("account1"), jsonObject.get("type"), jsonObject.get("money"), jsonObject.get("shopName"), jsonObject.get("account2"), jsonObject.get("fee"), jsonObject.get("time"));
                smsList.append(data);
            }
            String js = ";function getData(a){a=(a.replace(/\\n/g,\"\\\\n\"));var b,account,type,money,shopName,account2,fee,time;%s return b+'|'+account+'|'+type+'|'+money+'|'+account2+'|'+shopName+'|'+fee+\"|\"+time};;getData('%s')";
            getStr.onGet(String.format(js, smsList.toString(), Body));

        });


    }

    public static void getAll(String identify, String fromApp, boolean onlyUse, getAll getA) {
        Task.onThread(() -> {
            IdentifyRegular[] identifyRegular;
            if (onlyUse) {//是否只加载使用的规则
                if (fromApp == null) {
                    identifyRegular = DbManger.db.IdentifyRegularDao().load(identify);
                } else {
                    identifyRegular = DbManger.db.IdentifyRegularDao().load(identify, fromApp);
                }
            } else {
                if (fromApp == null) {
                    identifyRegular = DbManger.db.IdentifyRegularDao().loadAll(identify);
                } else if (identify.equals("sms")) {
                    identifyRegular = DbManger.db.IdentifyRegularDao().loadAllSMS(fromApp);
                } else
                    identifyRegular = DbManger.db.IdentifyRegularDao().loadAll(identify, fromApp);

            }


            List<Bundle> bundleList = new ArrayList<>();
            for (IdentifyRegular regular1 : identifyRegular) {
                Bundle bundle = new Bundle();
                bundle.putString("name", regular1.name);
                bundle.putInt("id", regular1.id);
                bundle.putInt("sort", regular1.sort);
                bundle.putString("fromApp", regular1.fromApp);
                bundle.putString("identify", regular1.identify);
                bundle.putString("text", regular1.text);
                bundle.putInt("use", regular1.use);
                bundle.putString("des", regular1.des);
                bundle.putString("regular", regular1.regular);
                bundle.putString("tableList", regular1.tableList);
                bundleList.add(bundle);
            }
            getA.onGet(bundleList.toArray(new Bundle[0]));


        });
    }


    public static void getAll(String identify,String fromApp,getAll getA) {
        getAll(identify, fromApp, false, getA);
    }

    public interface getString {
        void onGet(String str);
    }

    public static void add(String regex, String name, String text, String tableList, String identify, String fromApp, String des, Finish finish) {

        Task.onThread(() -> {
            IdentifyRegular[] identifyRegular = DbManger.db.IdentifyRegularDao().getByName(identify, fromApp, name, regex);
            if (identifyRegular != null && identifyRegular.length == 1) {
                finish.onFinish();
            } else {
                identifyRegular = DbManger.db.IdentifyRegularDao().getByName(identify, fromApp, name);
                if (identifyRegular != null && identifyRegular.length == 1) {
                    DbManger.db.IdentifyRegularDao().update(identifyRegular[0].id, regex, name, text, tableList, identify, fromApp, des);
                } else {
                    DbManger.db.IdentifyRegularDao().add(regex, name, text, tableList, identify, fromApp, des);
                    finish.onFinish();
                }
            }


        });
    }

    public static void deny(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().deny(id);
            finish.onFinish();
        });
    }

    public static void enable(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().enable(id);
            finish.onFinish();
        });
    }

    public interface getAll {
        void onGet(Bundle[] identifyRegulars);
    }

    public interface Finish {
        void onFinish();
    }

    public static void change(int id, String regex, String name, String text, String tableList, String identify, String fromApp, String des, Finish finish) {

        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().update(id, regex, name, text, tableList, identify, fromApp, des);
            finish.onFinish();
        });
    }

    public static void del(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().delete(id);
            finish.onFinish();
        });
    }

    public static void setSort(int id, int sort) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().setSort(id, sort));
    }

    public static void clear(String identify) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().clean(identify));
    }

    public static void clear(String identify, getString getString1) {
        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().clean(identify);
            getString1.onGet(null);
        });
    }

    public static void clear(String identify, String fromApp) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().clean(identify, fromApp));
    }
}
