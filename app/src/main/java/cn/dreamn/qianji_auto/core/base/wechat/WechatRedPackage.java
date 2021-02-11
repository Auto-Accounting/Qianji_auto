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

import cn.dreamn.qianji_auto.core.base.Analyze;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;


/**
 * 微信转账成功成功
 */
public class WechatRedPackage extends Analyze {

    private static WechatRedPackage paymentSuccess;

    public static WechatRedPackage getInstance() {
        if (paymentSuccess != null) return paymentSuccess;
        paymentSuccess = new WechatRedPackage();
        return paymentSuccess;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals(""))
            billInfo.setShopRemark("微信红包");
        billInfo.setAccountName("零钱");
        billInfo.setType(BillInfo.TYPE_PAY);

        if (!jsonObject.getString("payTools").equals(""))
            billInfo.setAccountName(jsonObject.getString("payTools"));


        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("money")));

        //抓的很准

        billInfo.setShopAccount(jsonObject.getString("nickName"));
        billInfo.setShopRemark(jsonObject.getString("receivertitle"));


        return billInfo;
    }

    @Override
    public BillInfo getResult(BillInfo billInfo) {


        return billInfo;
    }
}
