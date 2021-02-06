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

import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeAlipayRedPackageRec {
    public final static String TAG = "alipay_redpackage_rec";


    public static boolean succeed(List<String> list, Context applicationContext) {

        BillInfo billInfo=new BillInfo();

        billInfo.setShopAccount(list.get(1));
        billInfo.setRemark(list.get(2));
        billInfo.setShopRemark(list.get(2));
        billInfo.setMoney(BillTools.getMoney(list.get(3)));


        billInfo.setType(BillInfo.TYPE_INCOME);

        if(billInfo.getAccountName()==null)
            billInfo.setAccountName("支付宝");

        billInfo.setAccountName(billInfo.getAccountName());
        billInfo.setSource("支付宝收款红包捕获");
        billInfo.dump();
        CallAutoActivity.call(applicationContext, billInfo);

        Caches.del(TAG);

        Logs.d("Qianji_Analyze","捕获红包");
        return false;
    }
}
