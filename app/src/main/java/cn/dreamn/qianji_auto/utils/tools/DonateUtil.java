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

package cn.dreamn.qianji_auto.utils.tools;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.net.URLEncoder;

public class DonateUtil {

    public static final String DONATE_ID_ALIPAY = "https://qr.alipay.com/fkx18202qn4k2xono9fjk2f";
    public static final String DONATE_ID_WECHAT = "wxp://f2f0r6MaJqCzWMorhVHqdjFX8IiYl3SQ2TBY";
    public static final String AUTHOR_QQ = "573658513";
    public static final String AUTHOR_QQ_NAME = "Ankio";

    public static boolean openAlipayPayPage(Context context) {
        return openAlipayPayPage(context, DONATE_ID_ALIPAY);
    }

    public static boolean openAlipayPayPage(Context context, String qrcode) {
        try {
            qrcode = URLEncoder.encode(qrcode, "utf-8");
        } catch (Exception ignored) {
        }
        try {
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            openUri(context, alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            Logs.d(e.toString());
        }
        return false;
    }

    public static boolean openWeChatPay(Context context) {
        try {
            Intent donateIntent = new Intent();
            donateIntent.setClassName(context, "com.tencent.mm.plugin.remittance.ui.RemittanceAdapterUI");
            donateIntent.putExtra("scene", 1);
            donateIntent.putExtra("pay_channel", 13);
            donateIntent.putExtra("receiver_name", DONATE_ID_WECHAT);
            context.startActivity(donateIntent);
            return true;
        } catch (Exception e) {
            Logs.d(e.toString());
        }
        return false;
    }

    public static boolean openQQPay(Context context) {
        Intent donateIntent = new Intent();
        donateIntent.putExtra("come_from", 5);
        JSONObject localJSONObject = new JSONObject();
        try {
            localJSONObject.put("targetUin", AUTHOR_QQ);
            localJSONObject.put("targetNickname", AUTHOR_QQ_NAME);
            localJSONObject.put("sign", "");
            localJSONObject.put("trans_fee", "");
            localJSONObject.put("source", "1");
            localJSONObject.put("desc", "");
            donateIntent.putExtra("extra_data", localJSONObject.toString());
            donateIntent.putExtra("app_info", "appid#20000001|bargainor_id#1000026901|channel#wallet");
            donateIntent.putExtra("callbackSn", "0");
            donateIntent.setClassName(context, "com.tencent.mobileqq.activity.qwallet.TransactionActivity");
            context.startActivity(donateIntent);
            return true;
        } catch (Exception e) {
            Logs.d(e.toString());
        }
        return false;
    }

    private static void openUri(Context context, String s) throws Exception {
        Intent intent = Intent.parseUri(s, Intent.URI_INTENT_SCHEME);
        context.startActivity(intent);
    }
}
