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
public class TransferSucceedAccount extends Analyze {

    private static TransferSucceedAccount transferReceived;

    public static TransferSucceedAccount getInstance() {
        if (transferReceived != null) return transferReceived;
        transferReceived = new TransferSucceedAccount();
        return transferReceived;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (!jsonObject.getString("status").equals("余额宝转出申请提交")) return null;


        billInfo.setShopRemark("余额宝转出银行卡");

        billInfo.setAccountName("余额宝");

        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
        billInfo.setShopAccount("余额宝");
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

                case "转入账户：":
                    billInfo.setAccountName2(value);
                    break;
                case "转出说明：":
                    billInfo.setShopRemark(value);
                    break;
                default:
                    break;
            }
        }
        return billInfo;
    }
}
