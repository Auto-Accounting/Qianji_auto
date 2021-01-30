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

class AnalyzeWeChatRedPackage {
    private final static String TAG = "wechat_redpackage";
    //获取备注
    public static boolean remark(List<String> list){
        String remark,money,shopName;
        if(list.size()==9||list.size()==8){//个人红包
             remark=list.get(4);
             money=BillTools.getMoney(list.get(6));
             shopName="个人红包";
        }else if(list.size()==13||list.size()==14){//群红包
             remark=list.get(9);
             money=BillTools.getMoney(list.get(2));
             shopName="群红包";
        }else{
            return false;
        }

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


    public static boolean account(List<String> list, Context context){
        String money,account;
        if(list.size()==5) {
            money = BillTools.getMoney(list.get(2));
            account = list.get(4);
        }else{
            return false;
        }

        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        Cache data2=Caches.getOne("shopName","0");
        BillInfo billInfo;
        if(data==null) return false;

        billInfo=BillInfo.parse(data.cacheData);

        if(data2!=null){
            billInfo.setShopAccount(data2.cacheData);
            Caches.del("shopName");
        }

        billInfo.setMoney(money);
        billInfo.setAccountName(account);

        //Caches.update(TAG,billInfo.toString());
        Logs.d("Qianji_Analyze",billInfo.toString());
        Logs.d("Qianji_Analyze","捕获的金额:"+money+",捕获的资产："+account);

        billInfo.setMoney(money);
        billInfo.setBookName(BookNames.getDefault());
        billInfo.setTime();
        Logs.d("Qianji_Analyze",billInfo.toString());

        billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));

        billInfo.setType(BillInfo.TYPE_PAY);

        if(billInfo.getAccountName()==null)
            billInfo.setAccountName("微信");

        billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));
        billInfo.setSource("微信红包捕获");
        billInfo.setCateName(Category.getCategory(null,billInfo.getShopRemark(),BillInfo.TYPE_PAY));
        billInfo.dump();
        CallAutoActivity.call(context, billInfo);

        Caches.del(TAG);

        Logs.d("Qianji_Analyze","捕获的金额:"+money+",红包页面无法捕获商户名");


        return true;
    }


}
