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

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 付款给某人
 */
public class QrCollection extends Analyze {

    private static QrCollection paymentSuccess;

    public static QrCollection getInstance(){
        if(paymentSuccess!=null)return paymentSuccess;
        paymentSuccess=new QrCollection();
        return paymentSuccess;
    }


    @Override
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject=setContent(content);
        if(jsonObject==null)return ;

        BillInfo billInfo=new BillInfo();

        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("extra")));
        billInfo.setShopRemark(jsonObject.getString("assistMsg1"));
        billInfo.setShopAccount(jsonObject.getString("assistName1"));
        billInfo.setAccountName("余额");
        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setSilent(true);


        billInfo.setSource("支付宝二维码收钱成功");
        CallAutoActivity.call(context,billInfo);
    }

    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {


        return billInfo;
    }
}
