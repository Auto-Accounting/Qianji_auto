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
 * 付款给某人
 */
public class PaymentSuccess extends Analyze {

    private static PaymentSuccess paymentSuccess;

    public static PaymentSuccess getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new PaymentSuccess();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals(""))
            billInfo.setShopRemark("支付宝支付");


        if (billInfo.getShopAccount() != null && billInfo.getShopAccount().equals("花呗")) {
            billInfo.setType(BillInfo.TYPE_CREDIT_CARD_PAYMENT);
            billInfo.setAccountName2("花呗");
        } else if (billInfo.getShopRemark().equals("普通充值")) {
            billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
            billInfo.setAccountName2("余额");
        } else {
            billInfo.setType(BillInfo.TYPE_PAY);


        }
        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
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
                case "交易对象：":
                case "还款到：":
                case "户号：":
                    billInfo.setShopAccount(value);
                    break;
                case "商品说明：":
                case "充值说明：":
                case "还款说明：":
                case "缴费说明：":
                    billInfo.setShopRemark(value);
                    break;
                default:
                    break;
            }
        }
        return billInfo;
    }
}
