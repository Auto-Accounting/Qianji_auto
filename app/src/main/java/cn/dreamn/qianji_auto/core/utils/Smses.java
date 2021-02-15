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

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Sms;

public class Smses {


    public static String getFunction(String regex, String body, String remark, String account, String type, String money, String num) {
        String js = "function getSms(body){ var remark,account,type,money,num,shopName,pattern ; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num };getSms('%s');";
        String data = "pattern= /%s/;" +
                "      if(pattern.test(body)){" +
                "         var array = pattern.exec(body);" +
                "         var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',numNum='%s';" +
                "         if(remarkNum!=0){if(!isNaN(parseInt(remarkNum)))remark=array[remarkNum];else remark=remarkNum;}" +
                "         if(accountNum!=0){if(!isNaN(parseInt(accountNum)))account=array[accountNum];else account=accountNum;}" +
                "         if(typeNum!=0){if(!isNaN(parseInt(typeNum)))type=array[typeNum];else type=typeNum;}" +
                "         if(moneyNum!=0){if(!isNaN(parseInt(moneyNum)))money=array[moneyNum];else money=moneyNum;}" +
                "         if(numNum!=0){if(!isNaN(parseInt(numNum)))num=array[numNum];else num=numNum;}" +
                "      return remark+'|'+account+'|'+type+'|'+money+'|'+num ;" +
                "}    ";

        String data2 = String.format(data, regex, remark, account, type, money, num);
        return String.format(js, data2, body);

    }

    //获取所有的js
    public static String getSmsRegularJs(String Body) {
        StringBuilder smsList = new StringBuilder();
        Sms[] sms = DbManger.db.SmsDao().load();
        for (Sms value : sms) {
            String[] nums = value.smsNum.split("\\|", -1);
            if (nums.length != 5) return null;

            String data = "pattern= /%s/;" +
                    "      if(pattern.test(body)){" +
                    "         var array = pattern.exec(body);" +
                    "         var remarkNum='%s',accountNum='%s',typeNum='%s',moneyNum='%s',numNum='%s';" +
                    "         if(remarkNum!=0){if(!isNaN(parseInt(remarkNum)))remark=array[remarkNum];else remark=remarkNum;}" +
                    "         if(accountNum!=0){if(!isNaN(parseInt(accountNum)))account=array[accountNum];else account=accountNum;}" +
                    "         if(typeNum!=0){if(!isNaN(parseInt(typeNum)))type=array[typeNum];else type=typeNum;}" +
                    "         if(moneyNum!=0){if(!isNaN(parseInt(moneyNum)))money=array[moneyNum];else money=moneyNum;}" +
                    "         if(numNum!=0){if(!isNaN(parseInt(numNum)))num=array[numNum];else num=numNum;}" +
                    "      return remark+'|'+account+'|'+type+'|'+money+'|'+num ;" +
                    "}    ";

            data = String.format(data, value.regular, nums[0], nums[1], nums[2], nums[3], nums[4]);
            smsList.append(data);


        }
        String js = "function getSms(body){ var remark,account,type,money,num,shopName,pattern ; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num };getSms('%s');";

        return String.format(js, smsList.toString(), Body);
    }

    /**
     * var pattern = /(.*)向您尾号(.*)的储蓄卡银联(.*)存入人民币(.*)元,活期余额.*元。\[建设银行\]/g,
     * str = '徐先生1月2日12时18分向您尾号5064的储蓄卡银联入账存入人民币10000.00元,活期余额10050.96元。[建设银行]';
     * var array = pattern.exec(str);
     * if(array<=0)return;
     * <p>
     * function getSms(body){ var remark,account,type,money,num,shopName; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num };getSms('%s');
     */

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
        DbManger.db.SmsDao().add(regex, name, num);
    }

    public static void change(int id, String regex, String name, String num) {
        DbManger.db.SmsDao().update(id, regex, name, num);
    }

    public static void del(int id) {
        DbManger.db.SmsDao().delete(id);
    }


    public static void clear() {
        DbManger.db.SmsDao().clean();
    }
}
