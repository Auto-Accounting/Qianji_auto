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

package cn.dreamn.qianji_auto.core.hook.app.wechat;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.hook.Task;
import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class hookMsg {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Context mContext = utils.getContext();
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    ContentValues contentValues = (ContentValues) param.args[2];
                    String tableName = (String) param.args[0];
                    String arg = (String) param.args[1];

                    if (!tableName.equals("message") || TextUtils.isEmpty(tableName)) {
                        return;
                    }
                    Integer type = contentValues.getAsInteger("type");
                    if (null == type) {
                        return;
                    }
                    String contentStr = contentValues.getAsString("content");

                    //转账消息
                    if (type == 419430449) {
                        Integer isSend = contentValues.getAsInteger("isSend");
                        //我发出去的转账
                        String talker = contentValues.getAsString("talker");
                        XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                        String xml = xmlToJson.toString();
                        JSONObject msg = JSONObject.parseObject(xml);

                        utils.log("-------转账信息-------\n" +
                                "微信JSON消息：" + xml, true);

                        JSONObject wcpayinfo = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");
                        wcpayinfo.put("talker", talker);
                        if (utils.readData("userName").equals("false") || utils.readData("cache_wechat_paytool").equals("false")) {
                            utils.log("转账消息被捕获，但无法获得有效支付信息。", false);
                            return;
                        }
                        wcpayinfo.put("nickName", utils.readData("userName"));
                        wcpayinfo.put("payTools", utils.readData("cache_wechat_paytool"));//缓存的支付方式
                        wcpayinfo.put("isSend", String.valueOf(isSend));//缓存的支付方式
                                /*writeData("userName", "false");
                                writeData("cache_wechat_paytool", "false");*/

                        Bundle bundle = new Bundle();
                        bundle.putString("type", Receive.WECHAT);
                        bundle.putString("data", wcpayinfo.toJSONString());
                        bundle.putString("title", "微信转账");
                        bundle.putString("from", Wechat.PAYMENT_TRANSFER);


                        utils.send(bundle);
                    } else if (type == 436207665) {
                        Integer isSend = contentValues.getAsInteger("isSend");
                        if (isSend == 1) {
                            //我发出去的转账
                            String talker = contentValues.getAsString("talker");
                            XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                            String xml = xmlToJson.toString();
                            JSONObject msg = JSONObject.parseObject(xml);

                            utils.log("-------红包信息-------", true);
                            utils.log("微信JSON消息：" + xml, true);

                            JSONObject wcpayinfo = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

                            Task.onMain(100, () -> {

                                if (utils.readData("userName").equals("false") || utils.readData("cache_wechat_paytool").equals("false") || utils.readData("cache_wechat_payMoney").equals("false")) {
                                    utils.log("红包消息被捕获，但无法获得有效支付信息。", false);
                                    return;
                                }

                                wcpayinfo.put("talker", talker);
                                wcpayinfo.put("nickName", utils.readData("userName"));
                                wcpayinfo.put("payTools", utils.readData("cache_wechat_paytool"));//缓存的支付方式
                                wcpayinfo.put("isSend", String.valueOf(isSend));//缓存的支付方式
                                wcpayinfo.put("money", utils.readData("cache_wechat_payMoney"));//缓存的金额

                               /* writeData("userName", "false");
                                writeData("cache_wechat_paytool", "false");
                                writeData("cache_wechat_payMoney", "false");*/

                                Bundle bundle = new Bundle();
                                bundle.putString("type", Receive.WECHAT);
                                bundle.putString("data", wcpayinfo.toJSONString());
                                bundle.putString("title", "微信红包");
                                bundle.putString("from", Wechat.RED_PACKAGE);


                                utils.send(bundle);
                            });
                        }

                    } else if (type == 318767153) {
                        utils.log("微信XML消息：" + contentStr, true);
                        XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                        String xml = xmlToJson.toString();
                        try {


                            JSONObject msg = JSONObject.parseObject(xml);
                            JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                            String title = mmreader.getJSONObject("template_header").getString("title");
                            String display_name = mmreader.getJSONObject("template_header").getString("display_name");
                            JSONObject jsonObject = mmreader.getJSONObject("line_content");
                            if (jsonObject == null) {
                                try {
                                    jsonObject = mmreader.getJSONObject("template_detail").getJSONObject("line_content");
                                } catch (Exception e) {
                                    //没有获取到
                                }
                            }
                            if (jsonObject == null) return;
                            jsonObject.put("display_name", display_name);
                            jsonObject.put("payTools", utils.readData("cache_wechat_paytool"));
                            utils.log("原始json数据：" + jsonObject.toJSONString(), true);

                            utils.log("收到消息：" + xml, true);
                            utils.log("-------消息开始解析-------");
                            Bundle bundle = new Bundle();
                            bundle.putString("type", Receive.WECHAT);
                            bundle.putString("data", jsonObject.toJSONString());
                            bundle.putString("title", title);
                            utils.log(title);
                            switch (title) {
                                case "扣费凭证":
                                case "微信支付凭证":
                                    utils.log("-------微信支付凭证/扣费凭证-------");
                                    bundle.putString("from", Wechat.PAYMENT);
                                    break;
                                case "收款到账通知":
                                    utils.log("-------收款到账通知-------");
                                    bundle.putString("from", Wechat.RECEIVED_QR);
                                    break;
                                case "转账收款汇总通知":
                                    utils.log("-------转账收款汇总通知-------");
                                    bundle.putString("from", Wechat.PAYMENT_TRANSFER_RECEIVED);
                                    break;
                                case "转账过期退还通知":
                                case "转账退款到账通知":
                                    utils.log("-------转账过期退还/退款到账通知-------");
                                    bundle.putString("from", Wechat.PAYMENT_TRANSFER_REFUND);
                                    break;
                                case "零钱提现到账":
                                    utils.log("-------零钱提现到账-------");
                                    bundle.putString("from", Wechat.MONEY_INCOME);
                                    break;
                                case "红包退款到账通知":
                                    utils.log("-------红包退款到账通知-------");
                                    bundle.putString("from", Wechat.RED_REFUND);
                                case "退款到账通知":
                                    utils.log("-------退款到账通知-------");
                                    bundle.putString("from", Wechat.PAYMENT_REFUND);
                                    break;
                                case "商家转账入账通知":
                                    utils.log("-------商家转账入账通知-------");
                                    bundle.putString("from", Wechat.INCOME_SHOP);
                                    break;
                                default:
                                    utils.log("-------未知数据结构-------", true);
                                    bundle.putString("from", Wechat.CANT_UNDERSTAND);
                                    break;

                            }
                            utils.send(bundle);
                        } catch (Exception e) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", Receive.WECHAT);
                            bundle.putString("data", xml);
                            bundle.putString("title", Wechat.CANT_UNDERSTAND);
                            bundle.putString("from", Wechat.CANT_UNDERSTAND);
                            utils.send(bundle);
                            utils.log("JSON错误" + e.toString(), true);
                        }

                    }

                } catch (Exception e) {
                    utils.log("获取账单信息出错：" + e.getMessage(), true);
                }

            }
        });
    }


}
