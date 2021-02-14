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
import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeWeChatTransferRec {
    private final static String TAG = "wechat_transfer_rec";

    public static boolean succeed(List<String> list, Context context) {
        if (list.size() < 8) return false;
        String money = BillTools.getMoney(list.get(1));
        BillInfo billInfo = new BillInfo();

        Cache data2 = Caches.getOne("shopName", "0");

        if (data2 == null) return false;

        billInfo.setShopAccount(data2.cacheData);


        billInfo.setMoney(money);


        Logs.d("Qianji_Analyze", billInfo.toString());


        billInfo.setType(BillInfo.TYPE_INCOME);

        billInfo.setAccountName("零钱");


        Logs.d("Qianji_Analyze", "捕获的金额:" + money + ",捕获的商户名：无");

        billInfo.setSource(Wechat.PAYMENT_TRANSFER_RECEIVED);
        CallAutoActivity.call(context, billInfo);

        return true;
    }


}
