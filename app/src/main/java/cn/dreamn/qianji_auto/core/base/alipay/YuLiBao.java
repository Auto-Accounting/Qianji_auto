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

package cn.dreamn.qianji_auto.core.base.alipay;

import cn.dreamn.qianji_auto.core.base.Analyze;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;

/**
 * 付款给某人
 */
public class YuLiBao extends Analyze {

    private static YuLiBao paymentSuccess;

    public static YuLiBao getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new YuLiBao();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;

        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("assistMsg1")));
        billInfo.setShopAccount("余利宝");
        billInfo.setShopRemark(jsonObject.getString("assistMsg2") + "余利宝收益");
        billInfo.setAccountName("余利宝");
        billInfo.setType(BillInfo.TYPE_INCOME);


        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {

        billInfo.setSilent(true);
        return billInfo;
    }
}
