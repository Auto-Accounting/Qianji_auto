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
import cn.dreamn.qianji_auto.utils.runUtils.DataUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class identifyRegulars {



    public static String getRegData(String regex,  String remark, String account, String type, String money,  String shopName, String account2, String client,String source,String fee){
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
                "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum ;\n" +
                "       }        ";

        return String.format(data, regex, remark, account, type, money, shopName, account2, client,source,fee);
    }

    public static String getFunction(String body,String regex) {
        String js = "function getData(body){ \n" +
                "        var remark,account,type,money,shopName,account2,client,source,fee ;\n" +
                "        %s\n" +
                "return remark+'|'+account+'|'+type+'|'+money+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee \n" +
                "};\n" +
                "getData('%s');";

        return String.format(js, regex, body);

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
                        "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum ;\n" +
                        "       }        ";


                DataUtils dataUtils = new DataUtils();
                dataUtils.parse(value.tableList);
                data = String.format(data, value.regular, dataUtils.get("shopRemark"), dataUtils.get("account1"), dataUtils.get("type"), dataUtils.get("money"), dataUtils.get("shopName"), dataUtils.get("account2"), dataUtils.get("silent"), dataUtils.get("source"), dataUtils.get("fee"));
                smsList.append(data);


            }
            String js = "function getData(body){ \n" +
                    "        var remark,account,type,money,shopName,account2,client,source,fee ;\n" +
                    "        %s\n" +
                    "return remark+'|'+account+'|'+type+'|'+money+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee \n" +
                    "};\n" +
                    "getData('%s');";
            getStr.onGet(String.format(js, smsList.toString(), Body));

        });


    }


    public interface getAll{
        void onGet(IdentifyRegular[] identifyRegulars);
    }

    public interface getString{
        void onGet(String str);
    }

    public static void getAll(String identify,String fromApp,getAll getA) {
        Task.onThread(()-> {
            if(fromApp==null){
                getA.onGet(DbManger.db.IdentifyRegularDao().loadAll(identify));
            }else{
                getA.onGet(DbManger.db.IdentifyRegularDao().loadAll(identify,fromApp));
            }


        });
    }

    public static void deny(int id) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().deny(id));
    }

    public static void enable(int id) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().enable(id));
    }


    public static void add(String regex, String name, String text,String account1,String account2,String type,String silent,String money,String fee,String shopName,String shopRemark,String source,String identify,String fromApp) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.put("account1", account1);
        dataUtils.put("account2", account2);
        dataUtils.put("type", type);
        dataUtils.put("silent", silent);
        dataUtils.put("money", money);
        dataUtils.put("fee", fee);
        dataUtils.put("shopName", shopName);
        dataUtils.put("shopRemark", shopRemark);
        dataUtils.put("source", source);
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().add(regex, name, text, dataUtils.toString(), identify, fromApp));
    }

    public static void change(int id, String regex, String name, String text,String account1,String account2,String type,String silent,String money,String fee,String shopName,String shopRemark,String source,String identify,String fromApp) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.put("account1", account1);
        dataUtils.put("account2", account2);
        dataUtils.put("type", type);
        dataUtils.put("silent", silent);
        dataUtils.put("money", money);
        dataUtils.put("fee", fee);
        dataUtils.put("shopName", shopName);
        dataUtils.put("shopRemark", shopRemark);
        dataUtils.put("source", source);
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().update(id, regex, name, text, dataUtils.toString(), identify, fromApp));
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
