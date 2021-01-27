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
 *//*


package cn.dreamn.qianji_auto.core.hook;

import android.app.AndroidAppHelper;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class wechat implements IXposedHookLoadPackage {
    private static final String TAG ="wechat" ;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tencent.mm")) {
           return;
        }

        //微信支付渠道确定的支付
        hookPay(lpparam);
    }
    */
/**
     * hookLog
     * @param lpparam
     *//*

    private void hookLog(XC_LoadPackage.LoadPackageParam lpparam,String method){
        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ac", lpparam.classLoader, method,String.class,String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
               // Log.i("wechat",param.args[0].toString()+"   -   " +param.args[1].toString());
            }
        });
    }
    */
/**
     * hook支付函数
     * @param lpparam
     *//*

    private void hookPay(XC_LoadPackage.LoadPackageParam lpparam){
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                try {

                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];
                    //Log.i(TAG, tableName);
                    if (!tableName.equals("message") || TextUtils.isEmpty(tableName)) {
                        return;
                    }
                    Integer type = contentValues.getAsInteger("type");
                    if (null == type) {
                        return;
                    }

                 */
/*   try{
                        Log.i("当前Type值", type.toString());
                        String contentStr = contentValues.getAsString("content");
                        Log.i("数据流数据",  contentStr);
                    }catch (Exception e){
                        Log.i("数据流错误", e.toString());
                    }*//*


                    */
/**
                     * Log.i(TAG, contentValues.getAsString("content"));
                     *//*

                    if (type == 318767153) {
                       String contentStr = contentValues.getAsString("content");
                        try{
                            String xml= XmlTool.xmlToJson(null,contentStr);
                            JSONObject msg = JSONObject.parseObject(xml);
                            Log.i("微信支付JSON数据",  xml);
                            JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                            String title = mmreader.getJSONObject("template_header").getString("title");
                            String display_name = mmreader.getJSONObject("template_header").getString("display_name");
                            switch (title){
                                case "微信支付凭证":doPay(mmreader.getJSONObject("template_detail"),display_name);break;
                                case "收款到账通知":doRec(mmreader.getJSONObject("template_detail"));break;
                            }
                        }catch (Exception e){
                            Log.i("JSON错误", e.toString());
                        }
                        */
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
                        }*//*

                    }
                    */
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
                    }*//*

                } catch (Exception e) {
                   Log.i(TAG, "获取账单信息出错：" + e.getMessage());
                }
            }
        });
    }

    private void doRec(JSONObject template_detail) {
        JSONObject line_content=template_detail.getJSONObject("line_content");
        String cost=line_content.getJSONObject("topline").getJSONObject("value").getString("word");
        cost= Fun.getMoney(cost);
        StringBuilder remark= new StringBuilder();
        String payTool="零钱";
        JSONArray line=line_content.getJSONObject("lines").getJSONArray("line");
        for(int i=0;i<line.size();i++){
            JSONObject jsonObject1=line.getJSONObject(i);
            String key=jsonObject1.getJSONObject("key").getString("word");
            String value=jsonObject1.getJSONObject("value").getString("word");

            switch (key){
                case "付款方备注":
                case "汇总":
                    remark.append(value);break;
            }
        }
        Context context = AndroidAppHelper.currentApplication();
        //获取所有的支付信息
        payTool= Sort.getPayTools(payTool);

        String sort=Sort.getWeTools("二维码收款");;
        tools.sendGain2QianJi(context,cost,tools.getProduct("二维码收款", remark.toString()),payTool,sort,"微信二维码收款账单");
    }


    private void doPay(JSONObject jsonObject,String title){
        JSONObject line_content=jsonObject.getJSONObject("line_content");
        String cost=line_content.getJSONObject("topline").getJSONObject("value").getString("word");
        cost= Fun.getMoney(cost);
        if(title.equals("")){
            scan(line_content,cost);
        }else{
            qr(line_content,cost,title);
        }

    }
    private void qr(JSONObject line_content,String cost,String title){
        StringBuilder remark= new StringBuilder();
        String payTool="";
        if(line_content.getJSONObject("lines").size()==1){
            JSONObject line=line_content.getJSONObject("lines").getJSONObject("line");
            payTool=line.getJSONObject("value").getString("word");
        }else{
            JSONArray line=line_content.getJSONObject("lines").getJSONArray("line");
            for(int i=0;i<line.size();i++){
                JSONObject jsonObject1=line.getJSONObject(i);
                String key=jsonObject1.getJSONObject("key").getString("word");
                String value=jsonObject1.getJSONObject("value").getString("word");

                switch (key){
                    case "支付方式":payTool=value;break;
                    case "扣费项目":
                    case "备注":
                        remark.append(value);break;
                }
            }
        }



        Context context = AndroidAppHelper.currentApplication();
        //获取所有的支付信息
        payTool= Sort.getPayTools(payTool);

        String sort=Sort.getLearnSort(title,remark.toString());;
        tools.sendPay2QianJi(context,cost,tools.getProduct(title, remark.toString()),payTool,sort,"微信支付账单");

    }
    private void scan(JSONObject line_content,String cost){

        JSONArray line=line_content.getJSONObject("lines").getJSONArray("line");

        String shopName="";
        String remark="";
        String payTool="";

        for(int i=0;i<line.size();i++){
            JSONObject jsonObject1=line.getJSONObject(i);
            String key=jsonObject1.getJSONObject("key").getString("word");
            String value=jsonObject1.getJSONObject("value").getString("word");

            switch (key){
                case "收款方":
                case "商家名称":
                    shopName=value;break;
                case "支付方式":payTool=value;break;
                case "付款留言":
                case "商品详情":
                    remark=value;break;
            }
        }
        String product= tools.getProduct(shopName,remark);

        //获取所有的支付信息
        payTool= Sort.getPayTools(payTool);

        String sort=Sort.getLearnSort(shopName,remark);
        Context context = AndroidAppHelper.currentApplication();
        tools.sendPay2QianJi(context,cost,product,payTool,sort,"微信扫码支付账单");

    }
}
*/
