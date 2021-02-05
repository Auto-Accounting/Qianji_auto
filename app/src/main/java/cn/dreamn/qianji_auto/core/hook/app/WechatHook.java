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

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.utils.tools.Task;
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
       // hookMsg2();
        hookSetting();
        hookLog();
        hookMsg();
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



    private void hookMsg(){
        Logi("微信hook启动",true);
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        //Logi("hook住了类 com.tencent.wcdb.database.SQLiteDatabase",true);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                super.afterHookedMethod(param);
                Task.onMain(100, () ->  {
                    Logi("微信hook after",true);
                    try {
                        ContentValues contentValues = (ContentValues) param.args[2];
                        String tableName = (String) param.args[0];
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

                        if (type == 318767153) {
                            String contentStr = contentValues.getAsString("content");
                            Logi( "XML消息："+contentStr,true);
                            try{
                                XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                                String xml= xmlToJson.toString();
                                JSONObject msg = JSONObject.parseObject(xml);
                                JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                                String title = mmreader.getJSONObject("template_header").getString("title");
                                String display_name = mmreader.getJSONObject("template_header").getString("display_name");
                                JSONObject jsonObject = mmreader.getJSONObject("line_content");
                                jsonObject.put("display_name",display_name);

                                Logi( "收到消息："+xml,true);
                                Logi( "-------消息开始解析-------");
                                Bundle bundle=new Bundle();
                                bundle.putString("type", Receive.WECHAT);
                                bundle.putString("data",jsonObject.toJSONString());
                                switch (title){
                                    case "微信支付凭证":
                                        Logi( "-------微信支付凭证-------");
                                        bundle.putString("from", Wechat.PAYMENT);break;
                                    case "收款到账通知":
                                        Logi( "-------收款到账通知-------");
                                        bundle.putString("from", Wechat.RECEIVED_QR);break;
                                    case "转账退款通知":
                                        Logi( "-------转账退款通知-------");
                                        bundle.putString("from", Wechat.PAYMENT_TRANSFER_REFUND);break;
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
                });

            }
        });
    }

    private void hookRedpackage(){}

    private void hookSetting(){
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                Activity activity = (Activity) param.thisObject;
                final String activityClzName = activity.getClass().getName();
                if (activityClzName.contains("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")) {
                    Task.onMain(100, () -> doSettingsMenuInject(activity));
                }
            }
        });

    }

    private void hookLog(){
        XposedHelpers.findAndHookMethod("com.tencent.mars.xlog.Xlog", mAppClassLoader, "logWrite2",
                int.class,String.class, String.class, String.class,int.class,int.class,long.class,long.class,String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        Task.onMain(100, () ->  {

                            int logType  = (int) param.args[0];
                            String str2  = (String) param.args[1];
                            String str3  = (String) param.args[2];
                            String str4 = (String) param.args[3];
                            int num4  = (int) param.args[4];
                            int num5  = (int) param.args[5];
                            long long6  = (long) param.args[6];
                            long long7  = (long) param.args[7];
                            String str8  = (String) param.args[8];
                            //根据值来过过滤日志级别
                            String wechatLogType = getWechatLogType(logType);

                            Logi(wechatLogType+"LogType==="+logType);
                            Logi(wechatLogType+str2);
                            Logi(wechatLogType+str3);
                            Logi(wechatLogType+ str4);
                            Logi(wechatLogType+ ""+num4);
                            Logi(wechatLogType+ ""+num5);
                            Logi(wechatLogType+ ""+long6);
                            Logi(wechatLogType+ ""+long7);
                            Logi(wechatLogType+ str8);
                       });

                        super.beforeHookedMethod(param);
                    }
                });

    }
    //微信日志级别
    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;
    public static final int LEVEL_FATAL = 5;
    public static final int LEVEL_NONE = 6;

    //微信日志过滤
    public static final String LOG_VERBOSE = "LOG_VERBOSE";
    public static final String LOG_DEBUG = "LOG_DEBUG";
    public static final String LOG_INFO= "LOG_INFO";
    public static final String LOG_WARNING = "LOG_WARNING";
    public static final String LOG_ERROR = "LOG_ERROR";
    public static final String LOG_FATAL = "LOG_FATAL";
    public static final String LOG_NONE = "LOG_NONE ";

    private String getWechatLogType(int logType) {
        String TAG=null;
        if(logType==LEVEL_VERBOSE){
            TAG=LOG_VERBOSE;
        }else if(logType==LEVEL_DEBUG){
            TAG=LOG_DEBUG;
        }else if(logType==LEVEL_INFO){
            TAG=LOG_INFO;
        }else if(logType==LEVEL_WARNING){
            TAG=LOG_WARNING;
        }else if(logType==LEVEL_ERROR){
            TAG=LOG_ERROR;
        }else if(logType==LEVEL_FATAL){
            TAG=LOG_FATAL;
        }else if(logType==LEVEL_NONE){
            TAG=LOG_NONE;
        }
        return TAG;
    }

}
