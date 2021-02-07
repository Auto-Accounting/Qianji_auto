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

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.utils.tools.Logs;


/**
 * 微信支付成功
 */
public class WechatTransferReceived extends Analyze {

    private static WechatTransferReceived paymentSuccess;

    public static WechatTransferReceived getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new WechatTransferReceived();
        return paymentSuccess;
    }


    @Override
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject = setContent(content);
        if (jsonObject == null) return;

        BillInfo billInfo = new BillInfo();

        billInfo = getResult(jsonObject, billInfo);

        billInfo.setType(BillInfo.TYPE_INCOME);

        billInfo.setAccountName("零钱");


        billInfo.setSource("微信转账收款");
        CallAutoActivity.call(context, billInfo);

    }


    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {

        try {
            String raw = jsonObject.getJSONObject("lines").getJSONArray("line").getJSONObject(0).getJSONObject("value").getString("word");

            String[] raws = raw.split("向你转账");
            if (raws.length != 2) return billInfo;
            billInfo.setShopAccount(raws[0]);
            billInfo.setMoney(BillTools.getMoney(raws[1]));
            String money = jsonObject.getJSONObject("topline").getJSONObject("value").getString("word");
            billInfo.setShopRemark("今日收款总金额" + money);

        } catch (Exception e) {
            Logs.i("解析json出现了错误！\n json数据：" + jsonObject.toJSONString() + "\n 错误：" + e.toString());
        }


        return billInfo;
    }
}
