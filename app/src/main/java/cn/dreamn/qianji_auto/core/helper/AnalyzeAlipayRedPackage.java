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
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeAlipayRedPackage {
    public final static String TAG = "alipay_redpackage";
    //获取备注
    public static boolean remark(List<String> list){
        String remark,money,shopName;

        remark=list.get(0);
        shopName=list.get(2);
        money=BillTools.getMoney(list.get(4));
        // Caches.Clean();
        Cache cache=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        BillInfo billInfo ;

        if(cache!=null){
            billInfo=BillInfo.parse(cache.cacheData);
        }else{
            billInfo=new BillInfo();
            Caches.add(TAG,billInfo.toString(),BillInfo.TYPE_PAY);
        }



        billInfo.setRemark(remark);
        billInfo.setShopRemark(remark);
        billInfo.setMoney(money);
        billInfo.setShopAccount(shopName);
        Caches.update(TAG,billInfo.toString());
        Logs.d("Qianji_Analyze",billInfo.getQianJi());
        Logs.d("Qianji_Analyze","红包说明："+remark);
        return true;
    }





    public static void succeed(String money, Context applicationContext) {
        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if(data==null){
            return;
        }else {
            billInfo=BillInfo.parse(data.cacheData);
        }

        billInfo.setMoney(money);

        billInfo.setBookName(BookNames.getDefault());
        billInfo.setTime();

        billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));

        billInfo.setType(BillInfo.TYPE_PAY);

        if(billInfo.getAccountName()==null)
            billInfo.setAccountName("支付宝");

        billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));
        billInfo.setSource("支付宝红包捕获");
        billInfo.setCateName(Category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark(),BillInfo.TYPE_PAY));
        billInfo.dump();
        CallAutoActivity.call(applicationContext, billInfo);

        Caches.del(TAG);

        Logs.d("Qianji_Analyze","捕获的金额:"+money);
    }
}
