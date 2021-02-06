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

package cn.dreamn.qianji_auto.core.helper;

import android.content.Context;

import java.util.List;

import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeAlipayPayPerson {

    public final static String TAG = "alipay_pay_person";



    public static boolean succeed(List<String> list, Context context){

       String money= BillTools.getMoney(list.get(1));
       String shopName = list.get(2);


        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if(data==null){
            return false;
        }else {
            billInfo=BillInfo.parse(data.cacheData);
        }

        billInfo.setMoney(money);
        billInfo.setShopAccount(shopName);


        billInfo.setType(BillInfo.TYPE_PAY);

        if(billInfo.getAccountName()==null)
            billInfo.setAccountName("支付宝");

        billInfo.setAccountName(billInfo.getAccountName());
        billInfo.setSource("支付宝扫码付款捕获");
        billInfo.dump();
        CallAutoActivity.call(context, billInfo);

        Caches.del(TAG);

        Logs.d("Qianji_Analyze","捕获的金额:"+money+",捕获的商户名："+shopName);
        return true;
    }


    public static boolean remark() {
        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if(data==null){
            billInfo=new BillInfo();
            Caches.add(TAG,billInfo.toString(),BillInfo.TYPE_PAY);
        }
        return true;
    }
}
