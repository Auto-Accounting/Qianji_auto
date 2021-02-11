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

import cn.dreamn.qianji_auto.core.base.Analyze;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;

public class RedReceived extends Analyze {

    private static RedReceived redPackageRec;

    public static RedReceived getInstance() {
        if (redPackageRec != null) return redPackageRec;
        redPackageRec = new RedReceived();
        return redPackageRec;
    }


    @Override
    public BillInfo tryAnalyze(String content, String source) {
        BillInfo billInfo = super.tryAnalyze(content, source);

        if (billInfo == null) return null;
        billInfo.setShopRemark("收红包");
        String shopName = jsonObject.getString("subtitle").replaceAll("&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");

        String shopRemark = jsonObject.getString("title");
        String money = BillTools.getMoney(jsonObject.getString("statusLine1Text"));
        String payTool = "支付宝";
        billInfo.setAccountName(payTool);
        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setMoney(money);
        billInfo.setShopRemark(shopRemark);
        billInfo.setShopAccount(shopName);
        return billInfo;

    }


}
