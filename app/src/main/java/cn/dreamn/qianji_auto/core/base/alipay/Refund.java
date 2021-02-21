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
public class Refund extends Analyze {

    private static Refund paymentSuccess;

    public static Refund getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new Refund();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals(""))
            billInfo.setShopRemark("支付宝退款");


        billInfo.setShopAccount("支付宝");
        billInfo.setType(BillInfo.TYPE_INCOME);


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

                case "退款去向：":
                    billInfo.setAccountName(value);
                    break;
                case "退款说明：":
                    billInfo.setShopRemark(value);
                    break;

                default:
                    break;
            }
        }
        return billInfo;
    }
}
