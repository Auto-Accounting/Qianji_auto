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
 * 笔笔攒
 */
public class BiBiZan extends Analyze {

    private static BiBiZan paymentSuccess;

    public static BiBiZan getInstance(){
        if(paymentSuccess!=null)return paymentSuccess;
        paymentSuccess=new BiBiZan();
        return paymentSuccess;
    }


    @Override
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject = setContent(content);
        if (jsonObject == null) return;

        BillInfo billInfo = new BillInfo();

        billInfo.setShopRemark("笔笔攒");

        billInfo = getResult(jsonObject, billInfo);

        billInfo.setSilent(true);
        billInfo.setType(BillInfo.TYPE_TRANSFER_ACCOUNTS);
        billInfo.setAccountName2("余额宝");


        billInfo.setSource("支付宝笔笔攒");
        CallAutoActivity.call(context, billInfo);

    }

    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {
        billInfo.setMoney(BillTools.getMoney(jsonObject.getString("money")));
        JSONArray jsonArray=jsonObject.getJSONArray("content");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            String name=jsonObject1.getString("title");
            String value=jsonObject1.getString("content");
            Logs.d("name ->"+name+"  value->"+value);
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
