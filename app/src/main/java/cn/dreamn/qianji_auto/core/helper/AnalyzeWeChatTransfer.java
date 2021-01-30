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

class AnalyzeWeChatTransfer {
    private final static String TAG = "wechat_transfer";
    //获取备注
    public static boolean remark(List<String> list){
        String  remark = "";
        if(list.size()==1&&list.get(0).contains("修改")){
            remark=list.get(0);//.replace(" 修改","");
        }else if(list.size()>=6&&list.get(5).contains("修改")){
            remark=list.get(5);
        }else if(list.size()>=5&&list.get(4).contains("修改")){
            remark=list.get(4);
        }else{
            return false;
        }

        remark=remark.replace("修改","").replace("，","");
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


        Caches.update(TAG,billInfo.toString());
        Logs.d("Qianji_Analyze",billInfo.getQianJi());
        Logs.d("Qianji_Analyze","转账说明："+remark);
        return true;
    }


    public static boolean account(List<String> list){
        String money="",account="";
        if(list.size()==5) {
            money = BillTools.getMoney(list.get(2));
            account = list.get(4);
        }else{
            return false;
        }


        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);

        BillInfo billInfo;

        if(data!=null){
            billInfo=BillInfo.parse(data.cacheData);
        }else{
            billInfo=new BillInfo();
            Caches.add(TAG,billInfo.toString(),BillInfo.TYPE_PAY);
        }


        billInfo.setMoney(money);
        billInfo.setAccountName(account);

        Caches.update(TAG,billInfo.toString());
        Logs.d("Qianji_Analyze",billInfo.toString());
        Logs.d("Qianji_Analyze","捕获的金额:"+money+",捕获的资产："+account);
        return true;
    }

    public static boolean succeed(List<String> list, Context context){
       if(list.size()<4)return false;
       String money= BillTools.getMoney(list.get(2));
       String shopName = list.get(1).substring(1);
        shopName = shopName.substring(0,shopName.length()-4);
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            Logs.d("Qianji_Analyze",i+"  "+str);
        }

        Cache data=Caches.getOne(TAG,BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if(data==null){
            return false;
        }else {
            billInfo=BillInfo.parse(data.cacheData);
        }

        billInfo.setMoney(money);
        billInfo.setShopAccount(shopName);
        billInfo.setBookName(BookNames.getDefault());
        billInfo.setTime();
        Logs.d("Qianji_Analyze",billInfo.toString());
        if(billInfo.getShopRemark()==null){
            billInfo.setRemark("转账给"+shopName);
            billInfo.setShopRemark("转账给"+shopName);
        }

        billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));

        billInfo.setType(BillInfo.TYPE_PAY);

        if(billInfo.getAccountName()==null)
            billInfo.setAccountName("微信");

        billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));
        billInfo.setSource("微信转账捕获");
        billInfo.setCateName(Category.getCategory(shopName,billInfo.getShopRemark(),BillInfo.TYPE_PAY));
        billInfo.dump();
        CallAutoActivity.call(context, billInfo);

        Caches.del(TAG);

        Logs.d("Qianji_Analyze","捕获的金额:"+money+",捕获的商户名："+shopName);
        return true;
    }


}
