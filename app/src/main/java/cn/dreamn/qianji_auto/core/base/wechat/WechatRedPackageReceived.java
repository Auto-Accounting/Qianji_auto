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

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;


/**
 * 微信转账成功成功
 */
public class WechatRedPackageReceived extends Analyze {

    private static WechatRedPackageReceived paymentSuccess;

    public static WechatRedPackageReceived getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new WechatRedPackageReceived();
        return paymentSuccess;
    }


    @Override
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject = setContent(content);
        if (jsonObject == null) return;

        BillInfo billInfo = new BillInfo();
        billInfo.setShopRemark("微信红包收入");
        billInfo.setAccountName("零钱");
        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setSource("微信收红包");

        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("money")));

        //抓的很准

        billInfo.setShopAccount(jsonObject.getString("shopAccount"));
        billInfo.setShopRemark(jsonObject.getString("remark"));


        CallAutoActivity.call(context, billInfo);

    }


    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {


        return billInfo;
    }
}
