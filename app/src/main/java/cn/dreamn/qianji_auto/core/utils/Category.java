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

import com.eclipsesource.v8.V8;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Regular;
import cn.dreamn.qianji_auto.utils.tools.Logs;
public class Category {

    public String getCategory(String shopAccount, String shopRemark){
        V8 runtime = V8.createV8Runtime();
        String result=runtime.executeStringScript(getCategoryRegularJs(shopAccount,shopRemark));

        Logs.d("Qianji_Cate","自动分类结果："+result);
        return result;

    }

    //获取所有的js
   public String getCategoryRegularJs(String shopAccount, String shopRemark){
       StringBuilder regList= new StringBuilder();
       Regular[] regular = DbManger.db.RegularDao().load();
       for (Regular value : regular) {
           regList.append(value.regular);
       }

       String js="function getCategory(shopName,shopMark){%s%s};getCategory('%s','%s');";

       return String.format(js,regList.toString()," return '其他';" ,shopAccount,shopRemark);
   }


    /**
     *js demo
     * if(title.contents("123"))//标题 contents 、not contents、indexof、endof、regular（匹配到）
     *      * if(sub.contents("123"))//副标题
     *      * if(time>200 && time <100)//时间 < 、>、=
     *      * return "okk"
     */

   public void addCategory(String js){
        DbManger.db.RegularDao().add(js);
   }

   public void changeCategory(int id,String js){
       DbManger.db.RegularDao().update(id,js);
   }



}
