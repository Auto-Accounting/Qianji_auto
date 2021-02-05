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

import cn.dreamn.qianji_auto.core.base.wechat.Analyze;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;

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
        billInfo.setTime();
        billInfo=getResult(jsonObject,billInfo);

        billInfo.setAccountName(Assets.getMap("零钱"));
        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setSilent(true);
        billInfo.setCateName(Category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark(),BillInfo.TYPE_INCOME));
        billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));

        billInfo.setSource("微信二维码收钱成功");
        CallAutoActivity.call(context,billInfo);
    }


    @Override
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo) {
        String money = jsonObject.getJSONObject("topline").getJSONObject("value").getString("word");
        billInfo.setMoney(BillTools.getMoney(money));

        JSONArray line=jsonObject.getJSONObject("lines").getJSONArray("line");
        for(int i=0;i<line.size();i++){
            JSONObject jsonObject1=line.getJSONObject(i);
            String key=jsonObject1.getJSONObject("key").getString("word");
            String value=jsonObject1.getJSONObject("value").getString("word");

            switch (key){
                case "付款方":billInfo.setShopAccount(value);break;
                case "付款方备注":
                case "汇总":
                    billInfo.setShopRemark(value);break;
            }
        }

        return billInfo;
    }
}
