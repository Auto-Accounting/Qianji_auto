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

package cn.dreamn.qianji_auto.core.base;

import android.content.Context;
import android.webkit.URLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.dreamn.qianji_auto.core.db.Helper.Others;
import cn.dreamn.qianji_auto.core.db.Helper.Smses;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.utils.tools.JsEngine;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 其他未定义类型处理解决方案
 */
public class Other {
    public static String getTextWithWechat(String str) {
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(str);

        StringBuilder ret = new StringBuilder();
        while (m.find()) {
            String data = m.group();
            data = data.replace("\"", "").replace("\t", ",").replace("\n", ",").replace("\\n", "").replace("'", "");
            if (!data.contains("//") && !data.equals("") && !data.equals("\\/\\/"))
                ret.append(",").append(data);

        }
        String s = ret.toString();
        if (s.contains(",")) {
            s = s.substring(s.indexOf(","));
        }
        return s;
    }

    public static String getTextWithAlipay(String str) {
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(str);
        //  boolean next = false;
        StringBuilder ret = new StringBuilder();
        while (m.find()) {
            String data = m.group();
            data = data.replace("\"", "").replace("\t", ",").replace("\n", ",").replace("\\n", "").replace("'", "");
            if (!data.contains("//") && !data.equals(""))
                ret.append(",").append(data);
            //next = data.equals("word");
        }
        String s = ret.toString();
        if (s.contains(",")) {
            s = s.substring(s.indexOf(","));
        }
        return s;
    }


    public static void regular(String title, String body, Context context) {


        String data = getData(body);

        String[] strings = Others.getNum(data);

        BillInfo billInfo = new BillInfo();
        if (strings[3].equals("") || strings[2].equals("") || strings[1].equals("")) {
            Logs.i("未能匹配到有效数据");
            return;
        }

        billInfo.setType(BillInfo.getTypeId(strings[2]));

        billInfo.setMoney(BillTools.getMoney(strings[3]));

        String account = strings[1];

        billInfo.setShopAccount(strings[5]);

        billInfo.setSilent(strings[7].equals("是"));

        billInfo.setSource(title);

        if (!strings[4].equals("")) {
            account = account + "(" + strings[4] + ")";
        }

        billInfo.setAccountName(account);
        billInfo.setAccountName2(strings[6]);
        billInfo.setShopRemark(strings[0]);


        CallAutoActivity.call(context, billInfo);
    }

    public static String getData(String body) {


        try {
         /*  V8 runtime = V8.createV8Runtime();
           String result=runtime.executeStringScript(Smses.getSmsRegularJs(smsBody));*/
            String result = JsEngine.run(Others.getOtherRegularJs(body));
            Logs.d("Qianji_Text", "文本分析结果：" + result);
            return result;
        } catch (Exception e) {
            Logs.i("短信识别出错", e.toString());
            return "";
        }


    }

}
