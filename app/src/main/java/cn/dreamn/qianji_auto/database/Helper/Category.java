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

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.Regular;
import cn.dreamn.qianji_auto.utils.runUtils.DataUtils;
import cn.dreamn.qianji_auto.utils.runUtils.JsEngine;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class Category {
    public interface getStrings{
        void onGet(String str);
    }

    public static void getCategory(BillInfo billInfo,getStrings getStr) {
            getCategoryRegularJs(billInfo,string->{
                try {
                String result = JsEngine.run(string);
                    Log.m("Qianji_Cate", "自动分类结果：" + result);
                    getStr.onGet(result);
                } catch (Exception e) {
                    Log.d(" 自动分类执行出错！" + e.toString());
                    getStr.onGet("NotFound");
                }
            });

    }

    public static void setCateJs(BillInfo billInfo, String sort) {
        //这两种类型不需要
        if (billInfo.getType(true).equals(BillInfo.TYPE_CREDIT_CARD_PAYMENT) || billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            return;
        }
        // String time = Tools.getTime("HH");
        String name = billInfo.getSource();
        // String sort = "其它";
        String str = "";

        //    str += String.format("time = %s && ", time);
        str += String.format("shopName.indexOf('%s')!=-1 && ", billInfo.getShopAccount());
        str += String.format("shopRemark.indexOf('%s')!=-1 && ", billInfo.getShopRemark());
        str += String.format("type == '%s' && ", BillInfo.getTypeName(billInfo.getType(true)));
        str += String.format("source == '%s' && ", billInfo.getSource());

        String regular = "if(%s)return '%s';";

        int last = str.lastIndexOf('&');
        if (last != -1 && last != 0)
            str = str.substring(0, last - 1);

        regular = String.format(regular, str, sort);

        DataUtils dataUtils = new DataUtils();

        dataUtils.put("regular_billtype", billInfo.getSource());

        dataUtils.put("regular_name", billInfo.getSource());
        dataUtils.put("regular_time1_link", "");
        dataUtils.put("regular_time1", "");
        dataUtils.put("regular_time2_link", "");
        dataUtils.put("regular_time2", "");
        dataUtils.put("regular_money1_link", "");
        dataUtils.put("regular_money1", "");
        dataUtils.put("regular_money2_link", "");
        dataUtils.put("regular_money2", "");

        dataUtils.put("regular_shopName_link", "包含");
        dataUtils.put("regular_shopName", billInfo.getShopAccount());
        dataUtils.put("regular_shopRemark_link", "包含");
        dataUtils.put("regular_shopRemark", billInfo.getShopRemark());

        dataUtils.put("regular_type", BillInfo.getTypeName(billInfo.getType(true)));
        dataUtils.put("bill_type1", "");
        dataUtils.put("bill_type2", "");
        dataUtils.put("iconImg", sort);
        dataUtils.put("regular_sort", sort);
        Category.addCategory(regular, name, dataUtils.toString(), "[自动生成]", new Finish() {
            @Override
            public void onFinish() {

            }
        });

    }

    //获取所有的js
    public static void getCategoryRegularJs(BillInfo billInfo , getStrings getStr) {

        StringBuilder regList = new StringBuilder();

       Task.onThread(()-> {
           Regular[] regular = DbManger.db.RegularDao().load();
           for (Regular value : regular) {
               regList.append(value.regular);
           }

           String jsInner = "\n" +
                   "const isInTimeInner = function (minTime, maxTime,timeHour,timeMinute) {\n" +
                   "\n" +
                   "    let regT = /([01\\b]\\d|2[0-3]):([0-5]\\d)/;\n" +
                   "    const t1 = minTime.match(regT);\n" +
                   "    const t2 = maxTime.match(regT);\n" +
                   "    if(t1==null||t2==null||t1.length<3||t2.length<3){\n" +
                   "        return false;\n" +
                   "    }\n" +
                   "    const h1 = parseInt(t1[1]), h2 =  parseInt(t2[1]), m1 =  parseInt(t1[2]), m2 =  parseInt(t2[2]);\n" +
                   "    if (h1 > h2)\n" +
                   "        return (timeHour === h1 && timeMinute >= m1) || timeHour > h1 || timeHour < h2 || timeHour === h2 && timeMinute <= m2;\n" +
                   "    else if (h1 < h2)\n" +
                   "        return (timeHour === h1 && timeMinute >= m1) || (timeHour > h1 && timeHour < h2) || timeHour === h2 && timeMinute <= m2;\n" +
                   "    else if (h1 === h2) {\n" +
                   "        if (m1 < m2)\n" +
                   "            return timeHour === h1 && timeMinute >= m1 && timeMinute <= m2;\n" +
                   "        else if (m1 > m2)\n" +
                   "            return (timeHour === h1 && timeMinute >= m1 || timeMinute <= m2) || (timeHour !== h1);\n" +
                   "        else\n" +
                   "            return m1 === m2 && timeMinute === m1 && timeHour === h1;\n" +
                   "    }\n" +
                   "};";
           String js = "function getCategory(shopName,shopRemark,type,hour,minute,source,money){%s  %s return '其它';} getCategory('%s','%s','%s',%s,%s,'%s','%s');";

           String hour = Tool.getTime("HH");
           String minute = Tool.getTime("mm");
           getStr.onGet(String.format(js, jsInner, regList.toString(), billInfo.getShopAccount(), billInfo.getShopRemark(), billInfo.getType(true), hour, minute, billInfo.getSource(), billInfo.getMoney()));
       });


    }

    //获取所有的js
    public static String getOneRegularJs(String jsData, String shopAccount, String shopRemark, String type, String hour, String minute, String source, String money) {
        if (shopAccount == null) shopAccount = "";
        if (shopRemark == null) shopRemark = "";

        //type = BillInfo.getTypeName(type);

        String jsInner = "\n" +
                "const isInTimeInner = function (minTime, maxTime,timeHour,timeMinute) {\n" +
                "\n" +
                "    let regT = /([01\\b]\\d|2[0-3]):([0-5]\\d)/;\n" +
                "    const t1 = minTime.match(regT);\n" +
                "    const t2 = maxTime.match(regT);\n" +
                "    if(t1==null||t2==null||t1.length<3||t2.length<3){\n" +
                "        return false;\n" +
                "    }\n" +
                "    const h1 = parseInt(t1[1]), h2 =  parseInt(t2[1]), m1 =  parseInt(t1[2]), m2 =  parseInt(t2[2]);\n" +
                "    if (h1 > h2)\n" +
                "        return (timeHour === h1 && timeMinute >= m1) || timeHour > h1 || timeHour < h2 || timeHour === h2 && timeMinute <= m2;\n" +
                "    else if (h1 < h2)\n" +
                "        return (timeHour === h1 && timeMinute >= m1) || (timeHour > h1 && timeHour < h2) || timeHour === h2 && timeMinute <= m2;\n" +
                "    else if (h1 === h2) {\n" +
                "        if (m1 < m2)\n" +
                "            return timeHour === h1 && timeMinute >= m1 && timeMinute <= m2;\n" +
                "        else if (m1 > m2)\n" +
                "            return (timeHour === h1 && timeMinute >= m1 || timeMinute <= m2) || (timeHour !== h1);\n" +
                "        else\n" +
                "            return m1 === m2 && timeMinute === m1 && timeHour === h1;\n" +
                "    }\n" +
                "};";
        String js = "function getCategory(shopName,shopRemark,type,hour,minute,source,money){%s  %s return '其它';} getCategory('%s','%s','%s',%s,%s,'%s','%s');";
        return String.format(js, jsInner, jsData, shopAccount, shopRemark, type, hour, minute, source, money);
    }

    /**
     * js demo
     * if(type==0)return "啊啊啊"
     * if(shopName.startsWith("王记"))return "早餐"
     * if(shopRemark.endsWith("迎选购"))return "主主主主"
     * if(shopRemark.indexOf("迎选购")!=-1)return "主12主主主"
     * if(shopRemark=="新老顾客欢")return "滚滚"
     * if((/新老/g).test(shopName))return "ddd"
     * return "其它"
     */
    public static void getAll(Regex getRegular) {
        Task.onThread(() -> {
            Regular[] regular = DbManger.db.RegularDao().loadAll();
            List<Bundle> bundleList = new ArrayList<>();
            for (Regular regular1 : regular) {
                Bundle bundle = new Bundle();
                bundle.putString("name", regular1.name);
                bundle.putInt("id", regular1.id);
                bundle.putInt("sort", regular1.sort);
                bundle.putInt("use", regular1.use);
                bundle.putString("des", regular1.des);
                bundle.putString("regular", regular1.regular);
                bundle.putString("tableList", regular1.tableList);
                bundleList.add(bundle);
            }
            getRegular.onGet(bundleList.toArray(new Bundle[0]));
        });
    }

    /**
     * js demo
     * if(title.contents("123"))//标题 contents 、not contents、indexof、endof、regular（匹配到）
     * * if(sub.contents("123"))//副标题
     * * if(time>200 && time <100)//时间 < 、>、=
     * * return "okk"
     */

    public interface Regex {
        void onGet(Bundle[] bundle);
    }

    public interface Finish {
        void onFinish();
    }

    public static void deny(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().deny(id);
            finish.onFinish();
        });
    }

    public static void enable(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().enable(id);
            finish.onFinish();
        });
    }


    public static void addCategory(String js, String name, String tableList, String des, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().add(js, name, tableList, des);
            finish.onFinish();
        });
    }

    public static void changeCategory(int id, String js, String name, String tableList, String des, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().update(id, js, name, tableList, des);
            finish.onFinish();
        });
    }

    public static void del(int id, Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().delete(id);
            finish.onFinish();
        });
    }

    public static void setSort(int id, int sort) {
        Task.onThread(() -> DbManger.db.RegularDao().setSort(id, sort));
    }


    public static void clear() {
        Task.onThread(() -> DbManger.db.RegularDao().clean());
    }

    public static void clear(Finish finish) {
        Task.onThread(() -> {
            DbManger.db.RegularDao().clean();
            finish.onFinish();
        });
    }
}
