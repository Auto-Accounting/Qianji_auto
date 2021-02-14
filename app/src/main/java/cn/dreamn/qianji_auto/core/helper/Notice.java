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

import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;

public class Notice {
    public static void tryAnalyze(Context context, String notice) {
        if (notice.startsWith("[微信收款助手: 微信支付收款")) {
            BillInfo billInfo = new BillInfo();
            billInfo.setMoney(BillTools.getMoney(notice));

            billInfo.setShopRemark(notice.replace("微信收款助手:", ""));
            billInfo.setShopAccount("微信支付");
            billInfo.setSource(Wechat.RECEIVED_QR);
            billInfo.setType(BillInfo.TYPE_INCOME);
            billInfo.setAccountName("零钱");
            CallAutoActivity.call(context, billInfo);
        } else {
            if (notice.contains("你已成功收款")) {
                BillInfo billInfo = new BillInfo();
                billInfo.setMoney(BillTools.getMoney(notice));

                billInfo.setShopRemark(notice);
                billInfo.setShopAccount("支付宝支付");
                billInfo.setSource(Alipay.QR_COLLECTION);
                billInfo.setType(BillInfo.TYPE_INCOME);
                billInfo.setAccountName("余额");
                CallAutoActivity.call(context, billInfo);
            }
        }
    }
}
