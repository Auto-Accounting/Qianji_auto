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

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.utils.tools.Logs;

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
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject = setContent(content);
        if (jsonObject == null) return;

        BillInfo billInfo = new BillInfo();
        billInfo.setShopRemark("支付宝还款");
        billInfo = getResult(jsonObject, billInfo);

        billInfo.setType(BillInfo.TYPE_CREDIT_CARD_PAYMENT);


        billInfo.setSource("支付宝还款");
        if (!billInfo.getShopRemark().startsWith("还款")) return;
        CallAutoActivity.call(context, billInfo);

    }

    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {
        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("extra")));
        billInfo.setShopAccount("网商银行");
        billInfo.setShopRemark(jsonObject.getString("assistMsg1"));
        billInfo.setAccountName("支付宝");//这个不准确
        billInfo.setAccountName2(jsonObject.getString("assistMsg2"));

        return billInfo;
    }
}
