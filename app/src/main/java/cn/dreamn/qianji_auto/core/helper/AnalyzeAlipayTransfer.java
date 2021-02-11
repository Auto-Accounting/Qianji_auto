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
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeAlipayTransfer {
    private final static String TAG = "alipay_transfer";

    //获取备注
    public static boolean remark(List<String> list, Context context) {
        String remark = null, money = null, account = null;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.equals("订单信息")) {
                remark = list.get(i + 1);
                money = BillTools.getMoney(list.get(i - 1));
                account = list.get(i + 3);
                break;
            }
        }
        if (remark == null || money == null || account == null) {
            return false;
        }

        BillInfo billInfo;

        int type;
        Cache shopName = Caches.getOne("alipayShopName", "0");
        Cache cache = Caches.getOne(AnalyzeAlipayRedPackage.TAG, BillInfo.TYPE_PAY);
        Cache cache2 = Caches.getOne(AnalyzeAlipayPayPerson.TAG, BillInfo.TYPE_PAY);

        if (shopName != null) {
            if (cache != null && remark.contains("发普通红包")) {
                billInfo = BillInfo.parse(cache.cacheData);
                billInfo.setRemark("发普通红包");
                billInfo.setShopRemark("发普通红包");
                type = 1;
            } else if (remark.contains("发一字千金红包")) {
                billInfo = new BillInfo();
                billInfo.setRemark("发一字千金红包");
                billInfo.setShopRemark("发一字千金红包");
                billInfo.setShopAccount(shopName.cacheData);
                type = 2;
            } else {
                billInfo = new BillInfo();
                type = 0;
            }
            Caches.del("alipayShopName");
        } else if (cache2 != null) {
            type = 4;
            billInfo = new BillInfo();
        } else {
            type = 3;
            billInfo = new BillInfo();
        }


        Caches.add(TAG, billInfo.toString(), BillInfo.TYPE_PAY);

        billInfo.setMoney(money);
        billInfo.setAccountName(account);

        billInfo.setShopRemark(remark);


        if (type == 0 || type == 4) {
            Caches.update(TAG, billInfo.toString());
            Caches.update(AnalyzeAlipayPayPerson.TAG, billInfo.toString());
        } else if (type == 1 || type == 2 || type == 3) {


            billInfo.setType(BillInfo.TYPE_PAY);

            if (billInfo.getAccountName() == null)
                billInfo.setAccountName("支付宝");
            if (type == 3) {

            } else {

            }
            billInfo.dump();


        }

        Logs.d("Qianji_Analyze", billInfo.getQianJi());
        Logs.d("Qianji_Analyze", "转账说明：" + remark + "转账金额：" + money + "转账账户：" + account);
        return true;
    }


    public static boolean succeed(List<String> list, Context context) {
        String money = BillTools.getMoney(list.get(1));
        String shopName = list.get(5);

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
            billInfo.setRemark("转账给" + shopName);
            billInfo.setShopRemark("转账给" + shopName);
        }


        billInfo.setType(BillInfo.TYPE_PAY);

        if (billInfo.getAccountName() == null)
            billInfo.setAccountName("支付宝");


        billInfo.dump();


        Caches.del(TAG);

        Logs.d("Qianji_Analyze", "捕获的金额:" + money + ",捕获的商户名：" + shopName);
        return true;
    }


}
