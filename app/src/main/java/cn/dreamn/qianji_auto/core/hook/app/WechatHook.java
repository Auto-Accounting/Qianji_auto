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
        hookMsg2();
        hookMsgInsertWithOnConflict();
        hookRedpackage();


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

    private void hookMsg2(){
        Logi("微信hook com.tencent.mm.sdk.storage.MAutoStorage",true);
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.mm.sdk.storage.MAutoStorage", mAppClassLoader);
        //Logi("hook住了类 com.tencent.wcdb.database.SQLiteDatabase",true);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", ContentValues.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Logi("调用insert方法",true);
                super.beforeHookedMethod(param);


            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Logi("微信hook after",true);
                super.afterHookedMethod(param);
            }
        });
    }

    private void hookMsg(){
        Logi("微信hook启动",true);
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        //Logi("hook住了类 com.tencent.wcdb.database.SQLiteDatabase",true);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Logi("调用insert方法",true);
                super.beforeHookedMethod(param);


            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Logi("微信hook after",true);
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
                            Logi("JSON错误"+e.toString(),true);
                        }

                    }

                } catch (Exception e) {
                    Logi( "获取账单信息出错：" + e.getMessage(),true);
                }
            }
        });
    }

    private void hookMsgInsertWithOnConflict(){
        Logi("微信hook insertWithOnConflict",true);
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insertWithOnConflict", String.class, String.class, ContentValues.class,int.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String str1=param.args[0].toString();
                String str2=param.args[1].toString();
                String str3=param.args[2].toString();
                Logi("BEFORE\n[PARAM 1]"+ str1+"\n"+"[PARAM 2]"+ str2+"\n"+"[PARAM 3]"+ str3+"\n",true);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String str1=param.args[0].toString();
                String str2=param.args[1].toString();
                String str3=param.args[2].toString();
                Logi("AFTER\n[PARAM 1]"+ str1+"\n"+"[PARAM 2]"+ str2+"\n"+"[PARAM 3]"+ str3+"\n",true);
            }
        });
    }
    private void hookRedpackage(){}
}
