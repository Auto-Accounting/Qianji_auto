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

import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;

public class RedPackageRec extends Analyze {

    private static RedPackageRec redPackageRec;

    public static RedPackageRec getInstance(){
        if(redPackageRec!=null)return redPackageRec;
        redPackageRec=new RedPackageRec();
        return redPackageRec;
    }


    @Override
    public void tryAnalyze(String content, Context context) {

        JSONObject jsonObject=setContent(content);
        if(jsonObject==null)return ;

        BillInfo billInfo=new BillInfo();
        billInfo.setTime();
        String shopName=jsonObject.getString("subtitle").replaceAll("&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");

        String shopRemark =jsonObject.getString("title");
        String money = BillTools.getMoney(jsonObject.getString("statusLine1Text"));
        String payTool = "支付宝";
        billInfo.setAccountName(payTool);
        billInfo.setType(BillInfo.TYPE_INCOME);
        billInfo.setCateName(Category.getCategory(shopName,shopRemark,BillInfo.TYPE_INCOME));
        billInfo.setRemark(Remark.getRemark(shopName,shopRemark));
        billInfo.setMoney(money);
        billInfo.setShopRemark(shopRemark);
        billInfo.setShopAccount(shopName);
        billInfo.setSource("支付宝收到红包");
        CallAutoActivity.call(context,billInfo);
    }

    @Override
    BillInfo getResult(JSONArray jsonArray, BillInfo billInfo) {
        //红包
        return billInfo;
    }
}
