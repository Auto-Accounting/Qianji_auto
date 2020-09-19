/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.hook;

import android.content.Context;
import android.content.IntentFilter;

import cn.dreamn.qianji_auto.utils.active.Api;
import cn.dreamn.qianji_auto.utils.active.Bill;
import cn.dreamn.qianji_auto.utils.active.Fun;
import cn.dreamn.qianji_auto.utils.file.Log;
import cn.dreamn.qianji_auto.utils.file.Storage;


import static cn.dreamn.qianji_auto.utils.active.Api.getQianji;

public class tools {


    //是否自动记录支出
    public static Boolean  isAutoPay(){
        return Storage.type(Storage.Set).getBoolean("autoPay",false);
    }
    //是否自动记录收入
    public static Boolean  isAutoIncome(){
        return Storage.type(Storage.Set).getBoolean("autoIncome",false);
    }

    public static void sendPay2QianJi(Context context, String cost, String product, String payTool, String sort,String string){
        if(Float.parseFloat(cost)<0)return;
        Log.i(string,"" +
                " 信息："+product+
                "\n 金额：￥"+cost+
                "\n 支付渠道："+payTool
        );
        if(isAutoPay()|| !sort.equals("其它")){
            Bill.put(payTool,cost,product,string,Api.TYPE_PAY,sort);
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool,sort);
        }else{
            Bill.put(payTool,cost,product,string,Api.TYPE_PAY);
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool);
        }
    }
    public static void sendGain2QianJi(Context context, String cost, String product, String payTool, String sort,String string){
        if(Float.parseFloat(cost)<0)return;
        Log.i(string,"" +
                " 信息："+product+
                "\n 金额：￥"+cost+
                "\n 收款渠道："+payTool
        );

        if(isAutoIncome() || !sort.equals("其它")){
            Bill.put(payTool,cost,product,string,Api.TYPE_GAIN,sort);
            Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool,sort);
        }else {
            Bill.put(payTool,cost,product,string,Api.TYPE_GAIN);
            Log.i(string, "通知栏记账提醒");
            Fun.sendNotify(context, "记账提醒", "￥" + cost + " " + product, getQianji(Api.TYPE_GAIN, cost, product, payTool, null));
        }
    }
    public static  String getProduct(String user,String detail){
        String product=Storage.type(Storage.Set).get("remark","[交易对象] - [说明]");
        if(detail.equals(""))detail=user;
        if(product.contains("[交易对象]"))
            product=product.replace("[交易对象]",user);
        if(product.contains("[说明]"))
            product=product.replace("[说明]",detail);
        return product;
    }



}
