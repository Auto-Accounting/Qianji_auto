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

import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.Smses;
import cn.dreamn.qianji_auto.utils.tools.JsEngine;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/*import com.eclipsesource.v8.V8;*/

public class SmsServer {


    public static void readSMS(String sms, Context context) {
        String data = getSms(sms);

        String[] strings = data.split("\\|", -1);
        if (strings.length != 5) return;
        BillInfo billInfo = new BillInfo();
        if (strings[3].equals("undefined") || strings[2].equals("undefined") || strings[1].equals("undefined")) {
            Logs.i("未能匹配到有效短信");
            return;
        }

        billInfo.setType(BillInfo.getTypeId(strings[2]));

        billInfo.setMoney(BillTools.getMoney(strings[3]));

        String account = strings[1];

        billInfo.setShopAccount(strings[1]);

        billInfo.setSilent(true);

        billInfo.setSource("短信");

        if (!strings[4].equals("undefined")) {
            account = account + "(" + strings[4] + ")";
        }

        billInfo.setAccountName(account);

        billInfo.setRemark(strings[0]);
        billInfo.setShopRemark(strings[0]);


        CallAutoActivity.call(context, billInfo);
    }


    public static String getSms(String smsBody) {


        try {
         /*  V8 runtime = V8.createV8Runtime();
           String result=runtime.executeStringScript(Smses.getSmsRegularJs(smsBody));*/
            String result = JsEngine.run(Smses.getSmsRegularJs(smsBody));
            Logs.d("Qianji_Sms", "短信分析结果：" + result);
            return result;
        } catch (Exception e) {

            Logs.i("短信识别出错", e.toString());
            return "";
        }


    }


}