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

package cn.dreamn.qianji_auto.core.utils;

import android.annotation.SuppressLint;

import com.eclipsesource.v8.V8;
import com.xuexiang.xutil.data.DateUtils;

import java.text.SimpleDateFormat;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Regular;
import cn.dreamn.qianji_auto.utils.tools.Logs;
public class Category {

    public static String getCategory(String shopAccount, String shopRemark, String type){
        try {
            V8 runtime = V8.createV8Runtime();
            String result = runtime.executeStringScript(getCategoryRegularJs(shopAccount, shopRemark, type));
            Logs.d("Qianji_Cate", "自动分类结果：" + result);
            return result;
        }catch (Exception e){
            Logs.i("自动分类执行出错！"+e.toString());
            return "其他";
        }

    }

    //获取所有的js
   public static String getCategoryRegularJs(String shopAccount, String shopRemark, String type){
       if(shopAccount==null)shopAccount="";
       if(shopRemark==null)shopRemark="";
       StringBuilder regList= new StringBuilder();
       Regular[] regular = DbManger.db.RegularDao().load();
       for (Regular value : regular) {
           regList.append(value.regular);
       }

       type=BillInfo.getTypeName(type);

       String js="function getCategory(shopName,shopRemark,type,time){%s return '其他';} getCategory('%s','%s','%s','%s');";

       String time= Tools.getTime("HH");
       return String.format(js,regList.toString(),shopAccount,shopRemark,type,time);
   }

    //获取所有的js
    public static String getOneRegularJs(String jsData,String shopAccount, String shopRemark, String type,String time){
        if(shopAccount==null)shopAccount="";
        if(shopRemark==null)shopRemark="";

        type=BillInfo.getTypeName(type);

        String js="function getCategory(shopName,shopRemark,type,time){%s return '其他';} getCategory('%s','%s','%s','%s');";

        return String.format(js,jsData,shopAccount,shopRemark,type,time);
    }

    /**
     * js demo
     *  if(type==0)return "啊啊啊"
     *     if(shopName.startsWith("王记"))return "早餐"
     *     if(shopRemark.endsWith("迎选购"))return "主主主主"
     *     if(shopRemark.indexOf("迎选购")!=-1)return "主12主主主"
     *     if(shopRemark=="新老顾客欢")return "滚滚"
     *     if((/新老/g).test(shopName))return "ddd"
     *     return "其他"
     */
    /**
     *js demo
     * if(title.contents("123"))//标题 contents 、not contents、indexof、endof、regular（匹配到）
     *      * if(sub.contents("123"))//副标题
     *      * if(time>200 && time <100)//时间 < 、>、=
     *      * return "okk"
     */

    public static Regular[] getAll(){
        return DbManger.db.RegularDao().loadAll();
    }

    public static void deny(int id) {
        DbManger.db.RegularDao().deny(id);
    }
    public static void enable(int id) {
        DbManger.db.RegularDao().enable(id);
    }

    public static Regular[] getOne(int id){
        return DbManger.db.RegularDao().getOne(id);
    }

   public static void addCategory(String js,String name,String cate,String tableList){
        DbManger.db.RegularDao().add(js,name,cate,tableList);
   }

   public static void changeCategory(int id,String js,String name,String cate,String tableList){
       DbManger.db.RegularDao().update(id,js,name,cate,tableList);
   }

   public static void del(int id){
        DbManger.db.RegularDao().delete(id);
   }



}
