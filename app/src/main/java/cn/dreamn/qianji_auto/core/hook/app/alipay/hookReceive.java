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

package cn.dreamn.qianji_auto.core.hook.app.alipay;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookReceive {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> MsgboxInfoServiceImpl = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.sync.d", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);

        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl, "onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = param.args[0].toString();
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length(), data.indexOf("], pushData=,"));

                try {
                    data = "[" + data + "]";
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        analyze(utils, jsonArray.get(i).toString());
                    }
                } catch (Exception e) {
                    try {
                        analyze(utils, data);
                    } catch (Exception e2) {
                        utils.log("AlipayErr" + e2.toString(), false);
                    }
                }

            }
        });
    }

    private static void analyze(Utils utils, String data) {
        utils.log("收到消息：" + data, true);
        JSONObject jsonObject = JSON.parseObject(data);
        if (!jsonObject.containsKey("pl")) return;
        String str = jsonObject.getString("pl");
        JSONObject jsonObject1 = JSON.parseObject(str);

        if (!jsonObject1.containsKey("templateType")) return;
        if (!jsonObject1.containsKey("templateName")) return;
        if (!jsonObject1.containsKey("title")) return;
        if (!jsonObject1.containsKey("content")) return;

        utils.log("-------消息开始解析-------");
        Bundle bundle = new Bundle();
        bundle.putString("type", Receive.ALIPAY);
        String title = jsonObject1.getString("title");
        String templateName = jsonObject1.getString("templateName");
        if (title.equals("其他")) title = templateName;

        if (jsonObject1.getString("templateType").equals("BN")) {
            JSONObject content = jsonObject1.getJSONObject("content");
            content.put("alipay_cache_shopremark", utils.readData("alipay_cache_shopremark"));
            content.put("alipay_cache_money", utils.readData("alipay_cache_money"));
            content.put("alipay_cache_payTool", utils.readData("alipay_cache_payTool"));
            bundle.putString("data", content.toJSONString());
            // String title = jsonObject1.getString("title");
            utils.log(title);
            bundle.putString("title", title);
            switch (title) {
                case "转账收款到余额宝":
                    utils.log("-------转账收款到余额宝-------");
                    bundle.putString("from", Alipay.TRANSFER_YUEBAO);
                    break;
                case "转账到账成功":
                    utils.log("-------转账到账成功-------");
                    bundle.putString("from", Alipay.TRANSFER_SUCCESS_ACCOUNT);
                    break;
                case "余额宝-笔笔攒-单笔攒入":
                case "笔笔攒自动存入消息":
                    utils.log("-------余额宝-笔笔攒-单笔攒入-------");
                    bundle.putString("from", Alipay.BIBIZAN);
                    break;
                case "资金到账通知":
                    utils.log("-------资金到账通知-------");
                    bundle.putString("from", Alipay.FUNDS_ARRIVAL);
                    break;
                case "充值-普通充值":
                    utils.log("-------充值-普通充值-------");
                    bundle.putString("from", Alipay.PAYMENT_ORDING);
                    break;
                case "付款成功":
                    content.put("cache", true);
                    utils.log("-------付款成功-------");
                    bundle.putString("from", Alipay.PAYMENT_SUCCESS);
                    break;
                case "余额宝-蚂蚁星愿自动攒入":
                    utils.log("-------余额宝-蚂蚁星愿自动攒入-------");
                    bundle.putString("from", Alipay.MAYI);
                    break;
                case " 红包到账通知":
                    utils.log("------- 红包到账通知-------");
                    bundle.putString("from", Alipay.CLIENT_CASH);
                    break;
                case "转账成功":
                    utils.log("-------转账成功-------");
                    bundle.putString("from", Alipay.TRANSFER_SUCCESS);
                    break;
                case "退款通知":
                    utils.log("-------退款通知-------");
                    bundle.putString("from", Alipay.REFUND);
                    break;
                case "收到一笔转账":
                    utils.log("-------收到一笔转账-------");
                    bundle.putString("from", Alipay.TRANSFER_RECEIVED);
                    break;
                case "余额宝-自动转入":
                case "余额宝-单次转入":
                    utils.log("-------余额宝自动、单次转入------");
                    bundle.putString("from", Alipay.TRANSFER_INTO_YUEBAO);
                    break;
                case "还款到账成功":
                    utils.log("-------还款到账成功------");
                    bundle.putString("from", Alipay.CARD_REPAYMENT);
                    break;
                default:
                    utils.log("-------未知数据结构-------", true);
                    bundle.putString("from", Alipay.CANT_UNDERSTAND);
                    break;
            }
            utils.send(bundle);
        } else if (jsonObject1.getString("templateType").equals("S")) {
            JSONObject content = jsonObject1.getJSONObject("extraInfo");
            content.put("extra", jsonObject1.getString("content"));
            content.put("alipay_cache_shopremark", utils.readData("alipay_cache_shopremark"));
            content.put("alipay_cache_money", utils.readData("alipay_cache_money"));
            content.put("alipay_cache_payTool", utils.readData("alipay_cache_payTool"));
            bundle.putString("data", content.toJSONString());
            //String title = jsonObject1.getString("title");
            utils.log(title);
            bundle.putString("title", title);
            switch (title) {
                case "商家服务":
                    return;//没什么卵用
                case "商家服务·收款到账":
                    utils.log("-------商家服务·收款到账-------");
                    bundle.putString("from", Alipay.QR_COLLECTION);
                    break;
                case "网商银行·余利宝":
                    utils.log("-------网商银行·余利宝-------");
                    bundle.putString("from", Alipay.REC_YULIBAO);
                    break;
                case "网商银行":
                    utils.log("-------网商银行-------");
                    bundle.putString("from", Alipay.REPAYMENT);
                    break;
                case "蚂蚁财富·我的余额宝":
                    utils.log("-------蚂蚁财富·我的余额宝-------");
                    bundle.putString("from", Alipay.REC_YUEBAO);
                    break;
                default:
                    utils.log("-------未知数据结构-------", true);
                    bundle.putString("from", Alipay.CANT_UNDERSTAND);
                    break;
            }
            utils.send(bundle);
        }

    }
}
