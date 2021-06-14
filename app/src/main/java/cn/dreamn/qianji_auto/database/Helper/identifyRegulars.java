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

import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.IdentifyRegular;
import cn.dreamn.qianji_auto.utils.runUtils.DataUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class identifyRegulars {
    private static ArrayList<Bundle> list;
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
                "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum+'|'+index\n" +
                "       }        ";

        return String.format(data+"index++;\n", regex, remark, account, type, money, shopName, account2, client,source,fee);
    }

    public static String getFunction(String body,String regex) {
        String js = "function getData(body){ \n" +
                "        var remark,account,type,money,shopName,account2,client,source,fee,index=0 ;\n" +
                "        %s\n" +
                "return remark+'|'+account+'|'+type+'|'+money+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee +'|'+index\n" +
                "};\n" +
                "getData('%s');";

        return String.format(js, regex, body);

    }


    //获取所有的js
    public static void getAllRegularJs(String Body,String identify,String fromApp,getString getStr) {

        getAll(identify, fromApp, identifyRegulars -> {
            StringBuilder smsList = new StringBuilder();
            ArrayList<Bundle> arrayList=new ArrayList();
            for (Bundle value : identifyRegulars) {

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
                        "             return remarkNum+'|'+accountNum+'|'+typeNum+'|'+moneyNum+'|'+accountNum2+'|'+shopNameNum+'|'+clientNum+'|'+sourceNum+'|'+feeNum+'|'+index\n" +
                        "       }        ";


                DataUtils dataUtils = new DataUtils();
                dataUtils.parse(value.getString("tableList"));
                data = String.format(data, value.getString("regular"), dataUtils.get("shopRemark"), dataUtils.get("account1"), dataUtils.get("type"), dataUtils.get("money"), dataUtils.get("shopName"), dataUtils.get("account2"), dataUtils.get("silent"), dataUtils.get("source"), dataUtils.get("fee"));
                smsList.append(data).append("index++;\n");
                Bundle bundle = new Bundle();
                bundle.putString("name", value.getString("name"));
                bundle.putString("text", value.getString("text"));
                bundle.putString("regular", value.getString("regular"));
                bundle.putString("tableList", value.getString("tableList"));
                bundle.putString("identify", value.getString("identify"));
                bundle.putString("fromApp", value.getString("fromApp"));
                arrayList.add(bundle);
            }
            list= new ArrayList<>(arrayList);
            String js = "function getData(body){ \n" +
                    "        var remark,account,type,money,shopName,account2,client,source,fee,index=0 ;\n" +
                    "        %s\n" +
                    "return remark+'|'+account+'|'+type+'|'+money+'|'+shopName+'|'+account2+'|'+client+'|'+source+'|'+fee+'|'+index\n" +
                    "};\n" +
                    "getData('%s');";
            getStr.onGet(String.format(js, smsList.toString(), Body));

        });


    }


    public static Bundle getIndexRegular(int i){
        if(list!=null&&list.size()>i){
            return list.get(i);
        }
        return null;
    }

    public static void getAll(String identify,String fromApp,getAll getA) {
        Task.onThread(()-> {
            IdentifyRegular[] identifyRegular;
            if (fromApp == null) {
                identifyRegular = DbManger.db.IdentifyRegularDao().loadAll(identify);
            } else {
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

    public interface getString {
        void onGet(String str);
    }

    public static void add(String regex, String name, String text, String tableList, String identify, String fromApp, String des, Finish finish) {

        Task.onThread(() -> {
            DbManger.db.IdentifyRegularDao().add(regex, name, text, tableList, identify, fromApp, des);
            finish.onFinish();
        });
    }

    public static void deny(int id) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().deny(id));
    }

    public static void enable(int id) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().enable(id));
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

    public static void del(int id) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().delete(id));
    }

    public static void setSort(int id, int sort) {
        Task.onThread(() -> DbManger.db.IdentifyRegularDao().setSort(id, sort));
    }

    public static void clear(String identify,String fromApp) {
        Task.onThread(()-> DbManger.db.IdentifyRegularDao().clean(identify,fromApp));
    }
}
