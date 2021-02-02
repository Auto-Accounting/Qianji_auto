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

package cn.dreamn.qianji_auto.core.hook.app;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class WechatHook extends HookBase {
    private static WechatHook wechatHook;
    public static synchronized WechatHook getInstance() {
        if (wechatHook == null) {
            wechatHook = new WechatHook();
        }
        return wechatHook;
    }
    @Override
    public void hookFirst() throws Error {
        Logi("微信hook启动");
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Logi("微信hook before");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Logi("微信hook after");
                super.afterHookedMethod(param);
                try {

                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];
                    String data = (String) param.args[1];
                    if(tableName==null)Logi("null");
                    else Logi("table:"+tableName+" data:"+data +" data2:"+contentValues.toString());
                    if (!tableName.equals("message") || TextUtils.isEmpty(tableName)) {
                        return;
                    }
                    Integer type = contentValues.getAsInteger("type");
                    if (null == type) {
                        return;
                    }

                    try{
                        Logi("当前Type值"+ type.toString());
                        String contentStr = contentValues.getAsString("content");
                        Logi("数据流数据"+contentStr);
                    }catch (Exception e){
                        Logi("数据流错误"+ e.toString());
                    }

                    Logi(contentValues.getAsString("content"));
                    if (type == 318767153) {
                        String contentStr = contentValues.getAsString("content");
                        Logi( "XML消息："+contentStr,true);
                        try{
                            XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                            String xml= xmlToJson.toString();
                            JSONObject msg = JSONObject.parseObject(xml);
                            Logi("微信支付JSON数据\n"+  xml);
                            JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                            String title = mmreader.getJSONObject("template_header").getString("title");
                            //String display_name = mmreader.getJSONObject("template_header").getString("display_name");
                            Logi( "收到消息："+mmreader.toJSONString(),true);
                            Logi( "-------消息开始解析-------");
                            Bundle bundle=new Bundle();
                            bundle.putString("type", Receive.WECHAT);
                            bundle.putString("data",xml);
                            switch (title){
                                case "微信支付凭证":
                                    Logi( "-------微信支付凭证-------");
                                    bundle.putString("from", Wechat.PAYMENT);break;
                                case "收款到账通知":
                                    Logi( "-------收款到账通知-------");
                                    bundle.putString("from", Wechat.RECEIVED_QR);break;
                                default:
                                    Logi( "-------未知数据结构-------",true);
                                    bundle.putString("from", Wechat.CANT_UNDERSTAND);break;
                            }
                            send(bundle);
                        }catch (Exception e){
                           // Log.i("JSON错误", e.toString());
                        }
                        /*JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                        //全日志信息
                        String des = msg.getJSONObject("msg").getJSONObject("appmsg").getString("des");
                        Log.i(TAG, "进入判断字段阶段des" + des);
                        String title = mmreader.getJSONObject("template_header").getString("title");
                        Log.i(TAG, "选择的账单==" + title);
                        if (title != null && !TextUtils.isEmpty(title)) {
                            JSONObject mmreader1 = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");
                            Log.i(TAG, title + mmreader1.toString());
                            if (title.contains("收款到账通知")) {
                                //Log.i(TAG, "收款信息" + mmreader1.toString());
                            } else if (title.contains("微信支付凭证")) {
                                //Log.i(TAG, "收款信息" + mmreader1.toString());
                            } else if (title.contains("转账到银行卡到账成功")) {
                                //Log.i(TAG, "收款信息" + mmreader1.toString());
                            } else if (title.contains("银行卡发起成功")) {
                                //Log.i(TAG, "收款信息" + mmreader1.toString());
                            }
                        }*/
                    }
                    /*if (type == 419430449){
                        //微信转账给对方
                        String contentStr = contentValues.getAsString("content");
                        Log.i("微信转账原始数据",  contentStr);
                        try{
                            String xml=XmlTool.xmlToJson(null,contentStr);
                            JSONObject msg = JSONObject.parseObject(xml);
                            Log.i("微信转账JSON数据",  xml);
                            JSONObject wcpayinfo = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

                            String cost = wcpayinfo.getString("feedesc");
                            cost=Fun.getMoney(cost);
                            String shopName="";
                            String remark=wcpayinfo.getString("pay_memo");
                            String payTool="零钱";

                        }catch (Exception e){
                            Log.i("JSON错误", e.toString());
                        }
                    }*/
                } catch (Exception e) {
                    Logi( "获取账单信息出错：" + e.getMessage());
                }
            }
        });


    }

    @Override
    public String getPackPageName() {
        return "com.tencent.mm";
    }

    @Override
    public String getAppName() {
        return "微信";
    }

    @Override
    public String[] getAppVer() {
        return new String[]{
                "8.0.0"
        };
    }
}
