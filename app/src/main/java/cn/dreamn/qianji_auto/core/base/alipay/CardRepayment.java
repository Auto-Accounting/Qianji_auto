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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Analyze;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 笔笔攒
 */
public class CardRepayment extends Analyze {

    private static CardRepayment paymentSuccess;

    public static CardRepayment getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new CardRepayment();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals(""))
            billInfo.setShopRemark("支付宝还款");


        billInfo.setType(BillInfo.TYPE_CREDIT_CARD_PAYMENT);


        if (!billInfo.getShopRemark().contains("还款")) return null;
        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
        billInfo.setSilent(true);
        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("money")));
        JSONArray jsonArray = jsonObject.getJSONArray("content");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String name = jsonObject1.getString("title");
            String value = jsonObject1.getString("content");
            Logs.d("name ->" + name + "  value->" + value);
            switch (name) {
                case "付款方式：":
                    billInfo.setAccountName(value);
                    break;

                case "还款到：":
                    billInfo.setShopAccount(value);
                    billInfo.setAccountName2(value);
                    break;

                case "还款说明：":

                    billInfo.setShopRemark(value);
                    break;
                default:
                    break;
            }
        }

        return billInfo;
    }
}
