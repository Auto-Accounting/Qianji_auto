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
public class FundArrival extends Analyze {

    private static FundArrival paymentSuccess;

    public static FundArrival getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new FundArrival();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;

        billInfo.setShopRemark("商家付款");

        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setSilent(true);

        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("money")));
        JSONArray jsonArray = jsonObject.getJSONArray("content");
        billInfo.setAccountName("余额");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String name = jsonObject1.getString("title");
            String value = jsonObject1.getString("content");
            Logs.d("name ->" + name + "  value->" + value);
            switch (name) {

                case "付款方：":
                    billInfo.setShopAccount(value);
                    break;

                default:
                    break;
            }
        }
        return billInfo;
    }
}
