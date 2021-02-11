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
 * 笔笔攒
 */
public class Repayment extends Analyze {

    private static Repayment paymentSuccess;

    public static Repayment getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new Repayment();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        billInfo.setShopRemark("支付宝还款");


        billInfo.setType(BillInfo.TYPE_CREDIT_CARD_PAYMENT);


        if (!billInfo.getShopRemark().startsWith("还款")) return null;
        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("extra")));
        billInfo.setShopAccount("网商银行");
        billInfo.setShopRemark(jsonObject.getString("assistMsg1"));
        billInfo.setAccountName("支付宝");//这个不准确
        billInfo.setAccountName2(jsonObject.getString("assistMsg2"));

        return billInfo;
    }
}
