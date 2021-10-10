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
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.Regular;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.JsEngine;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class Category {
    public interface getStrings{
        void onGet(String str);
    }

    public static void getCategory(BillInfo billInfo,getStrings getStr) {
            getCategoryRegularJs(billInfo,string->{
                try {
                    String result = JsEngine.run(string);
                    Log.m("Qianji_Cate", "自动分类结果：" + result);
                    if (result.contains("Undefined")) {
                        getStr.onGet("NotFound");
                    } else getStr.onGet(result);
                } catch (Exception e) {
                    Log.d(" 自动分类执行出错！" + e.toString());
                    e.printStackTrace();
                    getStr.onGet("NotFound");
                }
            });

    }

    //准备自动规则
    public static void setCateJs(BillInfo billInfo, String sort) {
        //这两种类型不需要
        if (billInfo.getType(true).equals(BillInfo.TYPE_CREDIT_CARD_PAYMENT) || billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            return;
        }

        String str = "";
        // String pattern = "([(（])\\d+月\\d+日([)）])";
        String shopRemark = billInfo.getShopRemark();
        //    str += String.format("time = %s && ", time);
        str += String.format("shopName.indexOf('%s')!=-1 && ", billInfo.getShopAccount());
        str += String.format("shopRemark.indexOf('%s')!=-1 && ", shopRemark);
        str += String.format("type == '%s' && ", BillInfo.getTypeName(billInfo.getType(true)));

        String regular = "if(%s)return '%s';";

        int last = str.lastIndexOf('&');
        if (last != -1 && last != 0)
            str = str.substring(0, last - 1);

        regular = String.format(regular, str, sort);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("regular_name", billInfo.getShopAccount());
        jsonObject.put("regular_remark", billInfo.getShopRemark());
        jsonObject.put("regular_time1_link", "");
        jsonObject.put("regular_time1", "");
        jsonObject.put("regular_time2_link", "");
        jsonObject.put("regular_time2", "");
        jsonObject.put("regular_money1_link", "");
        jsonObject.put("regular_money1", "");
        jsonObject.put("regular_money2_link", "");
        jsonObject.put("regular_money2", "");
        jsonObject.put("regular_shopName_link", "包含");
        jsonObject.put("regular_shopName", billInfo.getShopAccount());
        jsonObject.put("regular_shopRemark_link", "包含");
        jsonObject.put("regular_shopRemark", shopRemark);
        jsonObject.put("regular_type", BillInfo.getTypeName(billInfo.getType()));
        jsonObject.put("iconImg", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        jsonObject.put("regular_sort", sort);
        Category.addCategory(regular, billInfo.getShopAccount(), jsonObject.toJSONString(), "[自动生成]", new Finish() {
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

           String jsInner = "const isInTimeInner=function(a,b,c,d){regT=/([01\\b]\\d|2[0-3]):([0-5]\\d)/;const e=a.match(regT),f=b.match(regT);if(null==e||null==f||e.length<3||f.length<3)return!1;const g=parseInt(e[1],10),h=parseInt(f[1],10),i=parseInt(e[2],10),j=parseInt(f[2],10);return g>h?c===g&&d>=i||c>g||h>c||c===h&&j>=d:h>g?c===g&&d>=i||c>g&&h>c||c===h&&j>=d:g===h?j>i?c===g&&d>=i&&j>=d:i>j?c===g&&d>=i||j>=d||c!==g:i===j&&d===i&&c===g:void 0};";
           String js = "function getCategory(shopName,shopRemark,type,hour,minute,money){%s  %s return 'NotFound';} getCategory('%s','%s','%s',%s,%s,'%s');";

           String hour, minute;

           long stamp = billInfo.getTimeStamp();
           hour = DateUtils.getTime("HH", stamp);
           minute = DateUtils.getTime("mm", stamp);


           getStr.onGet(String.format(js, jsInner, regList.toString(), billInfo.getShopAccount(), billInfo.getShopRemark(), BillInfo.getTypeName(billInfo.getType()), hour, minute, billInfo.getMoney()));
       });


    }


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
