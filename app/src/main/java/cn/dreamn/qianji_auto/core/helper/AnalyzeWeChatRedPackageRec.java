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

package cn.dreamn.qianji_auto.core.helper;

import android.content.Context;

import java.util.List;

import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

class AnalyzeWeChatRedPackageRec {
    private final static String TAG = "wechat_redpackage_rec";
    //获取备注


    public static boolean succeed(List<String> list, Context context){

        String money=null,remark=null,shopName=null;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if(str.contains("的红包")){
                shopName=str.replace("的红包","");
                money=BillTools.getMoney(list.get(i+2));
                remark=list.get(i+1);
                break;
            }
        }
        if(money==null||remark==null||shopName==null)return false;

        BillInfo billInfo = new BillInfo();

        billInfo.setMoney(money);
        billInfo.setRemark(remark);
        billInfo.setShopRemark(remark);
        billInfo.setShopAccount(shopName);


        billInfo.setMoney(money);


        Logs.d("Qianji_Analyze",billInfo.toString());


        billInfo.setType(BillInfo.TYPE_INCOME);

        billInfo.setAccountName("零钱");

        billInfo.setSource("微信红包收款捕获");
        billInfo.dump();
        CallAutoActivity.call(context, billInfo);



        Logs.d("Qianji_Analyze","捕获的金额:"+money+",捕获的商户名：无");
        return true;
    }


}
