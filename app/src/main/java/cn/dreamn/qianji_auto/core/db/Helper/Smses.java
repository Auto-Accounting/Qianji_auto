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

package cn.dreamn.qianji_auto.core.db.Helper;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Table.Sms;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Smses {


    public static String getFunction(String regex, String body, String remark, String account, String type, String money, String num, String shopName, String account2, String client) {
        String js = "function getSms(body){ var remark,account,type,money,num,shopName,account2,client ; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2+'|'+client };getSms('%s');";
        String data = "pattern= /%s/;" +
                "      if(pattern.test(body)){" +
                "         var array = pattern.exec(body);" +
                "         var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',numNum='%s',shopNameNum='%s',accountNum2='%s',clientNum='%s';" +
                "         if(remarkNum!=0){if(!isNaN(parseInt(remarkNum)))remark=array[remarkNum];else remark=remarkNum;}" +
                "         if(accountNum!=0){if(!isNaN(parseInt(accountNum)))account=array[accountNum];else account=accountNum;}" +
                "         if(typeNum!=0){if(!isNaN(parseInt(typeNum)))type=array[typeNum];else type=typeNum;}" +
                "         if(moneyNum!=0){if(!isNaN(parseInt(moneyNum)))money=array[moneyNum];else money=moneyNum;}" +
                "         if(numNum!=0){if(!isNaN(parseInt(numNum)))num=array[numNum];else num=numNum;}" +
                "         if(shopNameNum!=0){if(!isNaN(parseInt(shopNameNum)))shopName=array[shopNameNum];else shopName=shopNameNum;}" +
                "         if(accountNum2!=0){if(!isNaN(parseInt(accountNum2)))account2=array[accountNum2];else account2=accountNum2;}" +
                "         if(clientNum!=0){if(!isNaN(parseInt(clientNum)))client=array[clientNum];else client=clientNum;}" +
                "      return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2+'|'+client ;" +
                "}    ";

        String data2 = String.format(data, regex, remark, account, type, money, num, shopName, account2, client);
        return String.format(js, data2, body);

    }

    public static String[] getSmsNum(String num) {
        String[] nums = num.split("\\|", -1);
        String[] s = new String[]{"", "", "", "", "", "", "", ""};
        for (int i = 0; i < nums.length; i++) {
            if (i >= 8) break;
            s[i] = (nums[i].equals("undefined") ? "" : nums[i]);
        }
        return s;
    }

    //获取所有的js
    public static String getSmsRegularJs(String Body) {
        StringBuilder smsList = new StringBuilder();
        Sms[] sms = DbManger.db.SmsDao().load();
        for (Sms value : sms) {

            String[] s = getSmsNum(value.smsNum);


            String data = "pattern= /%s/;" +
                    "      if(pattern.test(body)){" +
                    "         var array = pattern.exec(body);" +
                    "         var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',numNum='%s',shopNameNum='%s',accountNum2='%s',clientNum='%s';" +
                    "         if(remarkNum!=0){if(!isNaN(parseInt(remarkNum)))remark=array[remarkNum];else remark=remarkNum;}" +
                    "         if(accountNum!=0){if(!isNaN(parseInt(accountNum)))account=array[accountNum];else account=accountNum;}" +
                    "         if(typeNum!=0){if(!isNaN(parseInt(typeNum)))type=array[typeNum];else type=typeNum;}" +
                    "         if(moneyNum!=0){if(!isNaN(parseInt(moneyNum)))money=array[moneyNum];else money=moneyNum;}" +
                    "         if(numNum!=0){if(!isNaN(parseInt(numNum)))num=array[numNum];else num=numNum;}" +
                    "         if(shopNameNum!=0){if(!isNaN(parseInt(shopNameNum)))shopName=array[shopNameNum];else shopName=shopNameNum;}" +
                    "         if(accountNum2!=0){if(!isNaN(parseInt(accountNum2)))account2=array[accountNum2];else account2=accountNum2;}" +
                    "         if(clientNum!=0){if(!isNaN(parseInt(clientNum)))client=array[clientNum];else client=clientNum;}" +
                    "      return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2+'|'+client ;" +
                    "}    ";

            data = String.format(data, value.regular, s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]);
            smsList.append(data);


        }
        String js = "function getSms(body){ var remark,account,type,money,num,shopName,account2 ; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num+'|'+shopName+'|'+account2; };getSms('%s');";

        return String.format(js, smsList.toString(), Body);
    }


    public static Sms[] getAll() {
        return DbManger.db.SmsDao().loadAll();
    }

    public static void deny(int id) {
        DbManger.db.SmsDao().deny(id);
    }

    public static void enable(int id) {
        DbManger.db.SmsDao().enable(id);
    }


    public static void add(String regex, String name, String num) {
        Logs.d("add ");
        DbManger.db.SmsDao().add(regex, name, num);
    }

    public static void change(int id, String regex, String name, String num) {
        DbManger.db.SmsDao().update(id, regex, name, num);
    }

    public static void del(int id) {
        DbManger.db.SmsDao().delete(id);
    }

    public static void setSort(int id, int sort) {
        DbManger.db.SmsDao().setSort(id, sort);
    }

    public static void clear() {
        DbManger.db.SmsDao().clean();
    }
}
