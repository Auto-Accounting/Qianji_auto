/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.hook.alipay;

import android.app.AndroidAppHelper;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.hook.tools;
import cn.dreamn.qianji_auto.utils.active.Api;
import cn.dreamn.qianji_auto.utils.active.Bill;
import cn.dreamn.qianji_auto.utils.active.Fun;
import cn.dreamn.qianji_auto.utils.active.Sort;
import cn.dreamn.qianji_auto.utils.file.Log;
import cn.dreamn.qianji_auto.utils.file.Storage;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static cn.dreamn.qianji_auto.utils.active.Api.getQianji;


public class hookAlipayRec implements IXposedHookLoadPackage {
    ClassLoader classLoader;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.eg.android.AlipayGphone")) return;
        classLoader = lpparam.classLoader;
        hookRec(lpparam);
        hookRecRed(lpparam);

    }

    /**
     * hook支付宝通知页面
     * @param lpparam
     * @throws Throwable
     */
    private void hookRec(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> MsgboxInfoServiceImpl = classLoader.loadClass("com.alipay.android.phone.messageboxstatic.biz.sync.d");
        Class<?> SyncMessage = classLoader.loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage");
        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl,"onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data=param.args[0].toString();
                data=data.substring(data.indexOf("msgData=[")+"msgData=[".length(),data.indexOf("], pushData=,"));
                JSONObject jsonObject= JSON.parseObject(data);
                if(!jsonObject.containsKey("pl"))return;
                String str=jsonObject.getString("pl");
                JSONObject jsonObject1= JSON.parseObject(str);

                if(!jsonObject1.containsKey("templateType"))return;
                if(!jsonObject1.containsKey("title"))return;
                if(!jsonObject1.containsKey("content"))return;

                Log.i("支付宝数据捕获",str);

                Context context = AndroidAppHelper.currentApplication();
                if(jsonObject1.getString("templateType").equals("BN")){
                    JSONObject content=jsonObject1.getJSONObject("content");
                    switch (jsonObject1.getString("title")){
                        case "转账收款到余额宝":
                        case "转账到账成功":return;
                        case "付款成功":doPay(content,context);break;
                        case "转账成功":doReturn(content,context);break;
                        case "退款通知":doRefund(content,context);break;
                        case "收到一笔转账":doTransfer(content,context);break;//转账收款或退款
                        default:doNothing(content);
                    }
                }else if(jsonObject1.getString("templateType").equals("S")){
                    JSONObject content=jsonObject1.getJSONObject("extraInfo");
                    switch (jsonObject1.getString("title")){
                        case "商家服务":
                            return;
                        case "商家服务·收款到账":
                            content.put("mainAmount", Fun.getMoney(jsonObject1.getString("content")));
                            doRec(content,context);break;
                        case "网商银行·余利宝":doMoney(content,context);break;
                        case "蚂蚁财富·我的余额宝":
                            content.put("mainAmount",Fun.getMoney(jsonObject1.getString("content")));
                            doAnt(content,context);break;
                        default:doNothing(content);
                    }
                }




            }
        });
    }

    //退款到账
    private void doRefund(JSONObject jsonObject, Context context) {
        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));
        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = Sort.getPayTools(jsonObject1.getString("payTool"));

        String sort=Sort.getAliTools("退款");
        tools.sendGain2QianJi(context,cost,product,payTool,sort,"支付宝收到退款信息");

    }

    //余额宝收益
    private void doAnt(JSONObject jsonObject, Context context) {
        String product=tools.getProduct("余额宝",jsonObject.getString("assistMsg1"));
        String cost = jsonObject.getString("mainAmount");
        String payTool = Sort.getPayTools("余额");//存储到余额资产

        String sort=Sort.getAliTools("余额宝");
        tools.sendGain2QianJi(context,cost,product,payTool,sort,"余额宝收益信息");

    }
    //余利宝收益
    private void doMoney(JSONObject jsonObject, Context context) {
        String product=tools.getProduct("余利宝",jsonObject.getString("assistMsg2")+"余利宝收益");
        String cost = Fun.getMoney(jsonObject.getString("assistMsg1"));
        String payTool = Sort.getPayTools("余额");

        String sort=Sort.getAliTools("余利宝");
        tools.sendGain2QianJi(context,cost,product,payTool,sort,"余利宝收益信息");
    }

    //支付宝收到转账进行处理
    private void doTransfer(JSONObject jsonObject, Context context){
        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));
        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = Sort.getPayTools(jsonObject1.getString("payTool"));


        String sort=Sort.getAliTools("转账");
        tools.sendGain2QianJi(context,cost,product,payTool,sort,"支付宝收到转账信息");
    }
    //收款
    private void doRec(JSONObject jsonObject, Context context){
        String product=tools.getProduct(jsonObject.getString("assistName1"),jsonObject.getString("assistMsg1"));
        String cost = jsonObject.getString("mainAmount");
        String payTool = Sort.getPayTools("余额");

        String sort=Sort.getAliTools("二维码收款");
        tools.sendGain2QianJi(context,cost,product,payTool,sort,"支付宝二维码收款信息");
    }
    private void hookRecRed(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        ClassLoader classLoader = lpparam.classLoader;
        Class<?> proguard = classLoader.loadClass("com.alipay.mobile.redenvelope.proguard.c.b");
        Class<?> SyncMessage = classLoader.loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage");
        XposedHelpers.findAndHookMethod(proguard,"onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data=param.args[0].toString();
                data=data.substring(data.indexOf("msgData=[")+"msgData=[".length(),data.indexOf("], pushData=,"));
                JSONObject jsonObject= JSON.parseObject(data);
                if(!jsonObject.containsKey("pl"))return;
                String str=jsonObject.getString("pl");
                JSONObject jsonObject1= JSON.parseObject(str);
                if(!jsonObject1.containsKey("templateJson"))return;
                String templateJson=jsonObject1.getString("templateJson");
                JSONObject jsonObject2= JSON.parseObject(templateJson);
                if(!jsonObject2.containsKey("statusLine1Text"))return;
                if(!jsonObject2.containsKey("title"))return;
                if(!jsonObject2.containsKey("subtitle"))return;
                if(!jsonObject2.getString("subtitle").contains("来自"))return;



                Context context = AndroidAppHelper.currentApplication();
                String user=jsonObject2.getString("subtitle").replaceAll("&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");

                String product =tools.getProduct(user,jsonObject2.getString("title"));
                String cost = Fun.getMoney(jsonObject2.getString("statusLine1Text"));
                String payTool = "支付宝";

                String sort=Sort.getAliTools("收红包");
                tools.sendGain2QianJi(context,cost,product,payTool,sort,"支付宝收到收红包信息");
            }
        });
    }

    private void doNothing(JSONObject content) {
        Log.i("未识别的账单或其他支付宝信息",content.toString());
    }



    //支付宝发生支付时，进行处理
    private void doPay(JSONObject jsonObject, Context context){

        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));

        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = Sort.getPayTools(jsonObject1.getString("payTool"));
        String shopId=jsonObject1.getString("shopId");
        String remark=jsonObject1.getString("remark");
        String sort=Sort.getLearnSort(shopId,remark);

        tools.sendPay2QianJi(context,cost,product,payTool,sort,"支付宝支付信息捕获");


    }
    //支付宝发生转账时，进行处理
    private void doReturn(JSONObject jsonObject, Context context){
        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));

        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = Sort.getPayTools(jsonObject1.getString("payTool"));

        //发出请求
        String shopId=jsonObject1.getString("shopId");
        String remark=jsonObject1.getString("remark");
        String sort=Sort.getLearnSort(shopId,remark);
        tools.sendPay2QianJi(context,cost,product,payTool,sort,"支付宝转账信息捕获");
    }



    private JSONObject getResult(JSONArray jsonArray){
        JSONObject jsonObject=new JSONObject();

        String shopName="";
        String remark="";
        String mPayTool="";
        for(int i=0;i<jsonArray.size();i++) {
            String title=jsonArray.getJSONObject(i).getString("title");
            String content=jsonArray.getJSONObject(i).getString("content");

            switch (title){
                case "退款去向：":
                case "付款方式：":jsonObject.put("payTool", content);break;
                case "已用优惠：":mPayTool="花呗支付";break;
                case "转账到：":
                case "付款方：":
                case "对方账户：":
                case "户号：":
                case "还款到：":
                case "交易对象：":shopName=content;break;

                case "充值说明：":
                case "转账说明：":
                case "转账备注：":
                case "缴费说明：":
                case "退款说明：":
                case "还款说明：":
                case "商品说明：":remark=content;break;
                default:Log.i("未定义标题名",title);
            }
        }
        if(!mPayTool.equals("")){
            jsonObject.put("payTool", mPayTool);
        }
        if(!jsonObject.containsKey("payTool"))
            jsonObject.put("payTool", "余额");
        if(remark.equals(""))
            remark=shopName;
        String product= tools.getProduct(shopName,remark);

        jsonObject.put("product",product);
        jsonObject.put("shopId",shopName);
        jsonObject.put("remark",remark);
        return jsonObject;
    }





}
