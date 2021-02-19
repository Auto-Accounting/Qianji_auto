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

/**
 * 转账给某人
 */
public class TransferIntoYuebao extends Analyze {

    private static TransferIntoYuebao transferIntoYuebao;

    public static TransferIntoYuebao getInstance() {
        if (transferIntoYuebao != null) return transferIntoYuebao;
        transferIntoYuebao = new TransferIntoYuebao();
        return transferIntoYuebao;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;

        billInfo.setSilent(true);
        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
        if (billInfo.getAccountName() == null || billInfo.getAccountName().equals(""))
            billInfo.setAccountName("余额");
        //余额转入余额宝
        billInfo.setAccountName2("余额宝");
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
            switch (name) {
                case "付款方式：":
                    billInfo.setAccountName(value);
                    break;
                case "交易对象：":
                    billInfo.setShopAccount(value);
                    break;
                case "商品说明：":
                    billInfo.setShopRemark(value);
                    break;
                default:
                    break;
            }
        }
        return billInfo;
    }
}
