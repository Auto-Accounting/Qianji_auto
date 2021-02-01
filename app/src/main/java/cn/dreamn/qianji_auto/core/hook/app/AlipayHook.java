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

import android.content.Context;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


public class AlipayHook extends HookBase {
    private static AlipayHook alipayHook;
    public static synchronized AlipayHook getInstance() {
        if (alipayHook == null) {
            alipayHook = new AlipayHook();
        }
        return alipayHook;
    }
    @Override
    public void hookFirst() throws Error {

        hookSafe();
        hookRed();
        hookReceive();

    }

    @Override
    public String getPackPageName() {
        return "com.eg.android.AlipayGphone";
    }

    @Override
    public String getAppName() {
        return "支付宝";
    }

    @Override
    public String[] getAppVer() {
        return new String[]{
                "10.2.13.9020",
        };
    }

    /**
     * hook掉安全校验
     */
    private void hookSafe() {
        try {

            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.alipay.apmobilesecuritysdk.scanattack.common.ScanAttack", mAppClassLoader), "getScanAttackInfo", Context.class, int.class, int.class, boolean.class, int.class, int.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(param.getResult()!=null)
                        Logi("支付宝原本监测到的安全信息："+param.getResult().toString(),true);
                    param.setResult(null);
                    Logi( "hook 支付宝安全监测 succeed！",true);
                }

            });

        } catch (Error | Exception e) {
            Logi( "hook 支付宝安全监测 error :"+e.toString(),true);
        }
    }
    private void hookRed(){

        Class<?> proguard = XposedHelpers.findClass("com.alipay.mobile.redenvelope.proguard.c.b", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);


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
                Logi( "hook 支付宝收到红包 succeed！",true);
                Logi("红包数据："+data);

                Bundle bundle=new Bundle();
                bundle.putString("from",Alipay.RED_RECEIVED);
                bundle.putString("type",Receive.ALIPAY);
                bundle.putString("data",jsonObject2.toJSONString());
                send(bundle);


            }
        });
    }
    private void hookReceive(){

        Class<?> MsgboxInfoServiceImpl = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.sync.d", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);

        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl,"onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data=param.args[0].toString();
                data=data.substring(data.indexOf("msgData=[")+"msgData=[".length(),data.indexOf("], pushData=,"));
                Logi( "收到消息："+data,true);
                JSONObject jsonObject= JSON.parseObject(data);
                if(!jsonObject.containsKey("pl"))return;
                String str=jsonObject.getString("pl");
                JSONObject jsonObject1= JSON.parseObject(str);

                if(!jsonObject1.containsKey("templateType"))return;
                if(!jsonObject1.containsKey("title"))return;
                if(!jsonObject1.containsKey("content"))return;

                Logi( "-------消息开始解析-------");
                Bundle bundle=new Bundle();
                bundle.putString("type",Receive.ALIPAY);
                if(jsonObject1.getString("templateType").equals("BN")){
                    JSONObject content=jsonObject1.getJSONObject("content");
                    bundle.putString("data",content.toJSONString());
                    switch (jsonObject1.getString("title")){
                        case "转账收款到余额宝":
                            Logi( "-------转账收款到余额宝-------");
                            bundle.putString("from", Alipay.TRANSFER_YUEBAO);break;
                        case "转账到账成功":
                            Logi( "-------转账到账成功-------");
                            bundle.putString("from", Alipay.TRANSFER_SUCCESS_ACCOUNT);break;
                        case "余额宝-笔笔攒-单笔攒入":
                            Logi( "-------余额宝-笔笔攒-单笔攒入-------");
                            bundle.putString("from", Alipay.BIBIZAN);break;
                        case "资金到账通知":
                            Logi( "-------资金到账通知-------");
                            bundle.putString("from", Alipay.FUNDS_ARRIVAL);break;
                        case "付款成功":
                            Logi( "-------付款成功-------");
                            bundle.putString("from", Alipay.PAYMENT_SUCCESS);break;
                        case "转账成功":
                            Logi( "-------转账成功-------");
                            bundle.putString("from", Alipay.TRANSFER_SUCCESS);break;
                        case "退款通知":
                            Logi( "-------退款通知-------");
                            bundle.putString("from", Alipay.REFUND);break;
                        case "收到一笔转账":
                            Logi( "-------收到一笔转账-------");
                            bundle.putString("from", Alipay.TRANSFER_RECEIVED);break;
                        case "余额宝-自动转入":
                            Logi( "-------余额宝自动转入------");
                            bundle.putString("from", Alipay.TRANSFER_INTO_YUEBAO);break;
                        default:
                            Logi( "-------未知数据结构-------",true);
                            bundle.putString("from", Alipay.CANT_UNDERSTAND);break;
                    }
                    send(bundle);
                }else if(jsonObject1.getString("templateType").equals("S")){
                    JSONObject content=jsonObject1.getJSONObject("extraInfo");
                    content.put("extra",jsonObject1.getString("content"));
                    bundle.putString("data",content.toJSONString());
                    switch (jsonObject1.getString("title")){
                        case "商家服务":
                            return;//没什么卵用
                        case "商家服务·收款到账":
                            Logi( "-------商家服务·收款到账-------");
                            bundle.putString("from", Alipay.QR_COLLECTION);break;
                        case "网商银行·余利宝":
                            Logi( "-------网商银行·余利宝-------");
                            bundle.putString("from", Alipay.REC_YULIBAO);break;
                        case "蚂蚁财富·我的余额宝":
                            Logi( "-------蚂蚁财富·我的余额宝-------");
                            bundle.putString("from", Alipay.REC_YUEBAO);break;
                        default:
                            Logi( "-------未知数据结构-------",true);
                            bundle.putString("from", Alipay.CANT_UNDERSTAND);break;
                    }
                    send(bundle);
                }
            }
        });
    }
}
