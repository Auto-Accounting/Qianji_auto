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
import cn.dreamn.qianji_auto.utils.tools.Logs;


/**
 * 微信支付成功
 */
public class WechatIncomeShop extends Analyze {

    private static WechatIncomeShop paymentSuccess;

    public static WechatIncomeShop getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new WechatIncomeShop();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals(""))
            billInfo.setShopRemark("微信支付付款成功");


        billInfo.setSilent(true);
        billInfo.setType(BillInfo.TYPE_INCOME);


        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {
        String money = BillTools.getMoney(jsonObject.getJSONObject("topline").getJSONObject("value").getString("word"));
        String shop = jsonObject.getString("display_name");
        billInfo.setMoney(BillTools.getMoney(money));
        try {
            JSONObject lines = jsonObject.getJSONObject("lines");

            if (!shop.equals("")) {
                billInfo.setShopAccount(shop);
            }

            JSONArray line = lines.getJSONArray("line");
            for (int i = 0; i < line.size(); i++) {
                JSONObject jsonObject1 = line.getJSONObject(i);
                String key = jsonObject1.getJSONObject("key").getString("word");
                String value = jsonObject1.getJSONObject("value").getString("word");

                switch (key) {
                    case "入账账户":
                        billInfo.setAccountName(value);
                        break;
                    case "转账备注":
                        billInfo.setShopRemark(value);
                        break;
                }
            }
        } catch (Exception e) {
            Logs.i("解析json出现了错误！\n json数据：" + jsonObject.toJSONString() + "\n 错误：" + e.toString());
        }

        return billInfo;
    }
}
