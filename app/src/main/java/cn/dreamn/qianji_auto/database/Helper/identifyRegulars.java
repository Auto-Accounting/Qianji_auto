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


import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.IdentifyRegular;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class identifyRegulars {


    public static void getFunction(String regex, String body, String remark, String account, String type, String money,  String shopName, String account2, String client,String source,String fee,getString getStr) {
        String js = "function getData(body){ \n" +
                "        var remark,account,type,money,shopName,account2,client,source,fee ;\n" +
                "        %s\n" +
                "return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee \n" +
                "};\n" +
                "getData('%s');";
        String data = "pattern= /%s/;\n" +
                "        if(pattern.test(body)){\n" +
                "                var array = pattern.exec(body);\n" +
                "                console.log(array);\n" +
                "                var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',shopNameNum='%s',accountNum2='%s',clientNum='%s',sourceNum='%s',feeNum='%s';\n" +
                "                \n" +
                "                for(var i=1;i<array.length;i++){\n" +
                "                        \n" +
                "                        var rep=\"$\"+i.toString();\n" +
                "                        var repStr=array[i];\n" +
                "                        \n" +
                "                        remarkNum=remarkNum.replace(rep,repStr);\n" +
                "                        accountNum=accountNum.replace(rep,repStr);\n" +
                "                        typeNum=typeNum.replace(rep,repStr);\n" +
                "                        moneyNum=moneyNum.replace(rep,repStr);\n" +
                "                        shopNameNum=shopNameNum.replace(rep,repStr);\n" +
                "                        accountNum2=accountNum2.replace(rep,repStr);\n" +
                "                        clientNum=clientNum.replace(rep,repStr);\n" +
                "                        sourceNum=sourceNum.replace(rep,repStr);\n" +
                "                        feeNum=feeNum.replace(rep,repStr);\n" +
                "                \n" +
                "                }   \n" +
                "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+numNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum ;\n" +
                "       }        ";

        String data2 = String.format(data, regex, remark, account, type, money, shopName, account2, client,source,fee);
        getStr.onGet(String.format(js, data2, body));

    }


    //获取所有的js
    public static void getAllRegularJs(String Body,String identify,String fromApp,getString getStr) {

        getAll(identify, fromApp, identifyRegulars -> {
            StringBuilder smsList = new StringBuilder();
            for (IdentifyRegular value : identifyRegulars) {

                String data = "pattern= /%s/;\n" +
                        "        if(pattern.test(body)){\n" +
                        "                var array = pattern.exec(body);\n" +
                        "                console.log(array);\n" +
                        "                var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',shopNameNum='%s',accountNum2='%s',clientNum='%s',sourceNum='%s',feeNum='%s';\n" +
                        "                \n" +
                        "                for(var i=1;i<array.length;i++){\n" +
                        "                        \n" +
                        "                        var rep=\"$\"+i.toString();\n" +
                        "                        var repStr=array[i];\n" +
                        "                        \n" +
                        "                        remarkNum=remarkNum.replace(rep,repStr);\n" +
                        "                        accountNum=accountNum.replace(rep,repStr);\n" +
                        "                        typeNum=typeNum.replace(rep,repStr);\n" +
                        "                        moneyNum=moneyNum.replace(rep,repStr);\n" +
                        "                        shopNameNum=shopNameNum.replace(rep,repStr);\n" +
                        "                        accountNum2=accountNum2.replace(rep,repStr);\n" +
                        "                        clientNum=clientNum.replace(rep,repStr);\n" +
                        "                        sourceNum=sourceNum.replace(rep,repStr);\n" +
                        "                        feeNum=feeNum.replace(rep,repStr);\n" +
                        "                \n" +
                        "                }   \n" +
                        "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+numNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum ;\n" +
                        "       }        ";

                data = String.format(data, value.regular, value.shopRemark, value.account1, value.type, value.money, value.shopName, value.account2, value.silent, value.source,value.fee);
                smsList.append(data);


            }
            String js = "function getData(body){ \n" +
                    "        var remark,account,type,money,shopName,account2,client,source,fee ;\n" +
                    "        %s\n" +
                    "return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee \n" +
                    "};\n" +
                    "getData('%s');";
            getStr.onGet(String.format(js, smsList.toString(), Body));

        });


    }


    interface getAll{
        void onGet(IdentifyRegular[] identifyRegulars);
    }

    interface getString{
        void onGet(String str);
    }

    public static void getAll(String identify,String fromApp,getAll getA) {
        Task.onThread(()-> {
            getA.onGet(DbManger.db.IdentifyRegularDao().loadAll(identify,fromApp));

        });
    }

    public static void deny(int id) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().deny(id));
    }

    public static void enable(int id) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().enable(id));
    }


    public static void add(String regex, String name, String text,String account1,String account2,String type,String silent,String money,String fee,String shopName,String shopRemark,String source,String identify,String fromApp) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().add(regex,name,text,account1,account2,type,silent,money,fee,shopName,shopRemark,source,identify,fromApp));
    }

    public static void change(int id, String regex, String name, String text,String account1,String account2,String type,String silent,String money,String fee,String shopName,String shopRemark,String source,String identify,String fromApp) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().update(id,regex,name,text,account1,account2,type,silent,money,fee,shopName,shopRemark,source,identify,fromApp));
    }

    public static void del(int id) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().delete(id));
    }

    public static void setSort(int id, int sort) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().setSort(id, sort));
    }

    public static void clear(String identify,String fromApp) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().clean(identify,fromApp));
    }
}
