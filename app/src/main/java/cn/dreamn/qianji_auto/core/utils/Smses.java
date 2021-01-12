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

package cn.dreamn.qianji_auto.core.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.eclipsesource.v8.V8;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Sms;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Smses extends ContentObserver {

    Cursor cursor;
    Context mcontext;
    Handler mhandler;

    /*
    重写一个构造方法，因为当数据库变化时要通知程序进行下一步操作，
    所以需要传入Handler对象*/
    public Smses(Context context,Handler handler) {
        super(handler);
        mcontext = context;
        mhandler=handler;


    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Logs.d(uri.toString());
        if (uri.toString().equals("content://sms/inbox-insert")) {
            cursor = mcontext.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            if (cursor.moveToFirst()) {

//                读取短信内容
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String data = getSms(body);

                String[] strings=data.split("\\|");
                if(strings.length!=6)return;
                BillInfo billInfo = new BillInfo();
                billInfo.setType(strings[2]);

                String account=strings[1];
                if(!strings[4].equals("undefined")){
                    account=account+"("+strings[4]+")";
                }

                billInfo.setBookName(BookNames.getDefault());
                if(!strings[5].equals("undefined")){
                    billInfo.setShopAccount(strings[5]);
                }else{
                    billInfo.setShopAccount("");
                }
                billInfo.setAccountName(account);
                billInfo.setTime();
                billInfo.setRemark(strings[0]);
                billInfo.setShopRemark(strings[0]);

                Category category = new Category();
                billInfo.setCateName(category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark()));

                if(billInfo.isAvaiable()){
                    CallAutoActivity.call(mcontext,billInfo);
                }
            }
        }
    }


    public String getSms(String smsBody){
        Logs.d(smsBody);
        Logs.d(getSmsRegularJs(smsBody));

        V8 runtime = V8.createV8Runtime();
        String result=runtime.executeStringScript(getSmsRegularJs(smsBody));

        Logs.d("Qianji_Sms","短信分析结果："+result);
        return result;

    }

    //获取所有的js
    public String getSmsRegularJs(String Body){
        StringBuilder smsList= new StringBuilder();
        Sms[] sms = DbManger.db.SmsDao().load();
        for (Sms value : sms) {
            smsList.append(value.regular);
        }

        // Sms 【招商银行】您账户1956于12月13日19:57银联扣款人民币20.00元（（特约）公交充值）

        String js="function getSms(body){ var remark,account,type,money,num,shopName; %s return remark+'|'+account+'|'+type+'|'+money+'|'+num };getSms('%s');";

        return String.format(js,smsList.toString(),Body);
    }


    /**
     *js demo
     * if(title.contents("123"))//标题 contents 、not contents、indexof、endof、regular（匹配到）
     *      * if(sub.contents("123"))//副标题
     *      * if(time>200 && time <100)//时间 < 、>、=
     *      * return "okk"
     */

    public void addSms(String js){
        DbManger.db.SmsDao().add(js);
    }

    public void changeSms(int id,String js){
        DbManger.db.SmsDao().update(id,js);
    }

}