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
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

/*import com.eclipsesource.v8.V8;*/

import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Smses;
import cn.dreamn.qianji_auto.utils.tools.JsEngine;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class SmsServer extends ContentObserver {

    Cursor cursor;
    Context mcontext;
    Handler mhandler;

    /*
    重写一个构造方法，因为当数据库变化时要通知程序进行下一步操作，
    所以需要传入Handler对象*/
    public SmsServer(Context context, Handler handler) {
        super(handler);
        mcontext = context;
        mhandler=handler;


    }

    public static void readSMS(String sms,Context context){
        String data = getSms(sms);

        String[] strings = data.split("\\|");
        if (strings.length != 5) return;
        BillInfo billInfo = new BillInfo();


        billInfo.setType(BillInfo.getTypeId(strings[2]));


        String account = strings[1];
        if (!strings[4].equals("undefined")) {
            account = account + "(" + strings[4] + ")";
        }


               /* if(!strings[5].equals("undefined")){
                    billInfo.setShopAccount(strings[5]);
                }else{
                    billInfo.setShopAccount("");
                }*/
        billInfo.setAccountName(account);

        billInfo.setRemark(strings[0]);
        billInfo.setShopRemark(strings[0]);


        CallAutoActivity.call(context, billInfo);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Logs.d(uri.toString());
        if (uri.toString().equals("content://sms/inbox-insert")) {
            cursor = mcontext.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            assert cursor != null;
            if (cursor.moveToFirst()) {
                String body = cursor.getString(cursor.getColumnIndex("body"));
                readSMS(body,mcontext);
//
            }
        }
    }


    public static String getSms(String smsBody){


       try{
         /*  V8 runtime = V8.createV8Runtime();
           String result=runtime.executeStringScript(Smses.getSmsRegularJs(smsBody));*/
           String result = JsEngine.run(Smses.getSmsRegularJs(smsBody));
           Logs.d("Qianji_Sms","短信分析结果："+result);
           return result;
       }catch (Exception e){

           Logs.i("短信识别出错",e.toString());
           return "";
       }



    }

    





}