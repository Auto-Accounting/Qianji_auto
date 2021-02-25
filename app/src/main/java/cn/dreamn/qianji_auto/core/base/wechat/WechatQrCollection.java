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

package cn.dreamn.qianji_auto.core.base.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Analyze;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;

/**
 * 付款给某人
 */
public class WechatQrCollection extends Analyze {

    private static WechatQrCollection paymentSuccess;

    public static WechatQrCollection getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new WechatQrCollection();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;


        billInfo.setAccountName("零钱");
        billInfo.setType(BillInfo.TYPE_INCOME);


        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
        billInfo.setSilent(true);
        String money = BillTools.getMoney(jsonObject.getJSONObject("topline").getJSONObject("value").getString("word"));
        billInfo.setMoney(BillTools.getMoney(money));
        String payTools = jsonObject.getString("payTools");
        if (payTools != null && !payTools.equals("")) {
            billInfo.setAccountName(payTools);
        }
        JSONArray line = jsonObject.getJSONObject("lines").getJSONArray("line");
        for (int i = 0; i < line.size(); i++) {
            JSONObject jsonObject1 = line.getJSONObject(i);
            String key = jsonObject1.getJSONObject("key").getString("word");
            String value = jsonObject1.getJSONObject("value").getString("word");

            switch (key) {
                case "付款方":
                    billInfo.setShopAccount(value);
                    break;
                case "付款方备注":
                case "汇总":
                    billInfo.setShopRemark(value);
                    break;
            }
        }

        return billInfo;
    }
}
