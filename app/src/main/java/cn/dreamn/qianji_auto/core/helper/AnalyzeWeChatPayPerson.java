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

import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.db.Table.Cache;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeWeChatPayPerson {
    private final static String TAG = "wechat_pay_person";

    //获取备注
    public static boolean remark(List<String> list) {

        String shopName = list.get(2);
        String money = BillTools.getMoney(list.get(5));
        String remark = "";
        if (list.size() >= 8 && list.get(7).contains("修改")) {
            remark = list.get(6);
        }
        remark = remark.replace(" 修改", "").replace("，", "");

        Cache cache = Caches.getOne(TAG, BillInfo.TYPE_PAY);
        BillInfo billInfo;

        if (cache != null) {
            billInfo = BillInfo.parse(cache.cacheData);
        } else {
            billInfo = new BillInfo();
            Caches.add(TAG, billInfo.toString(), BillInfo.TYPE_PAY);
        }


        billInfo.setRemark(remark);
        billInfo.setShopRemark(remark);
        billInfo.setShopAccount(shopName);
        billInfo.setMoney(money);

        Caches.update(TAG, billInfo.toString());
        Logs.d("Qianji_Analyze", billInfo.getQianJi());
        Logs.d("Qianji_Analyze", "付款说明：" + remark);
        return true;
    }


    public static boolean account(List<String> list) {

        String money = "", account = "";
        if (list.size() == 5) {
            money = BillTools.getMoney(list.get(2));
            account = list.get(4);
        } else {
            return false;
        }


        Cache data = Caches.getOne(TAG, BillInfo.TYPE_PAY);

        BillInfo billInfo;

        if (data != null) {
            billInfo = BillInfo.parse(data.cacheData);
        } else {
            billInfo = new BillInfo();
            Caches.add(TAG, billInfo.toString(), BillInfo.TYPE_PAY);
        }


        billInfo.setMoney(money);
        billInfo.setAccountName(account);

        Caches.update(TAG, billInfo.toString());
        Logs.d("Qianji_Analyze", billInfo.toString());
        Logs.d("Qianji_Analyze", "捕获的金额:" + money + ",捕获的资产：" + account);
        return true;
    }

    public static boolean succeed(List<String> list, Context context) {
        if (list.size() < 6) return false;
        String money = BillTools.getMoney(list.get(2));
        String shopName = list.get(4);


        Cache data = Caches.getOne(TAG, BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if (data == null) {
            return false;
        } else {
            billInfo = BillInfo.parse(data.cacheData);
        }

        billInfo.setMoney(money);
        billInfo.setShopAccount(shopName);

        if (list.size() == 8) {
            billInfo.setShopRemark(list.get(6));
        }

        Logs.d("Qianji_Analyze", billInfo.toString());
        if (billInfo.getShopRemark() == null) {
            billInfo.setRemark("付款给" + shopName);
            billInfo.setShopRemark("付款给" + shopName);
        }


        billInfo.setType(BillInfo.TYPE_PAY);

        if (billInfo.getAccountName() == null)
            billInfo.setAccountName("微信");


        Caches.del(TAG);

        Logs.d("Qianji_Analyze", "捕获的金额:" + money + ",捕获的商户名：" + shopName);

        billInfo.setSource(Wechat.PAYMENT);
        CallAutoActivity.call(context, billInfo);
        return true;
    }


    public static boolean succeed2(List<String> list, Context context) {
        if (list.size() < 5) return false;
        String money = BillTools.getMoney(list.get(2));
        String shopName = list.get(1);


        Cache data = Caches.getOne(TAG, BillInfo.TYPE_PAY);
        BillInfo billInfo;
        if (data == null) {
            return false;
        } else {
            billInfo = BillInfo.parse(data.cacheData);
        }

        billInfo.setMoney(money);
        billInfo.setShopAccount(shopName);


        Logs.d("Qianji_Analyze", billInfo.toString());
        if (billInfo.getShopRemark() == null) {
            billInfo.setRemark("付款给" + shopName);
            billInfo.setShopRemark("付款给" + shopName);
        }


        billInfo.setType(BillInfo.TYPE_PAY);

        if (billInfo.getAccountName() == null)
            billInfo.setAccountName("微信");


        Caches.del(TAG);

        Logs.d("Qianji_Analyze", "捕获的金额:" + money + ",捕获的商户名：" + shopName);

        billInfo.setSource(Wechat.PAYMENT);
        CallAutoActivity.call(context, billInfo);
        return true;
    }
}
