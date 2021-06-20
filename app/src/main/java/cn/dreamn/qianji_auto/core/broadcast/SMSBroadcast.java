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

package cn.dreamn.qianji_auto.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SMSBroadcast extends BroadcastReceiver {
    //从意图获取短信对象的具体方法
    public static SmsMessage[] getMessageFromIntent(Intent intent) {
        SmsMessage[] retmeMessage;
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        retmeMessage = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            byte[] bytedata = (byte[]) pdus[i];
            retmeMessage[i] = SmsMessage.createFromPdu(bytedata);
        }
        return retmeMessage;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        // android.provider.Telephony.SMS_RECEIVED
        SmsMessage[] msgs = getMessageFromIntent(intent);
        //提取短信内容
        /*if (msgs.length > 0) {
            StringBuilder msg2 = new StringBuilder();
            String user = "";
            for (SmsMessage msg : msgs) {
                user = msg.getDisplayOriginatingAddress();
                msg2.append(msg.getDisplayMessageBody());
            }

            Logs.i("短信", "--------收到短信-------\n" +
                    "发件人是：" + user +
                    "\n------短信内容-------\n" +
                    msg2.toString() +
                    "\n----------------------"
            );
            SmsServer.readSMS(msg2.toString(), context);
        }*/

    }
}
