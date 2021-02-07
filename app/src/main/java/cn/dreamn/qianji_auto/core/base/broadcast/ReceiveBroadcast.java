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

package cn.dreamn.qianji_auto.core.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.base.alipay.BiBiZan;
import cn.dreamn.qianji_auto.core.base.alipay.FundArrival;
import cn.dreamn.qianji_auto.core.base.alipay.PaymentSuccess;
import cn.dreamn.qianji_auto.core.base.alipay.QrCollection;
import cn.dreamn.qianji_auto.core.base.alipay.RedReceived;
import cn.dreamn.qianji_auto.core.base.alipay.Refund;
import cn.dreamn.qianji_auto.core.base.alipay.TransferIntoYuebao;
import cn.dreamn.qianji_auto.core.base.alipay.TransferReceived;
import cn.dreamn.qianji_auto.core.base.alipay.TransferSucceed;
import cn.dreamn.qianji_auto.core.base.alipay.YuEBao;
import cn.dreamn.qianji_auto.core.base.alipay.YuLiBao;
import cn.dreamn.qianji_auto.core.base.wechat.Payment;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.base.wechat.WechatPaymentTransfer;
import cn.dreamn.qianji_auto.core.base.wechat.WechatTransferReceived;
import cn.dreamn.qianji_auto.core.helper.SmsServer;
import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class ReceiveBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action==null)return;
        //开机启动
        switch (action) {
            case "android.intent.action.BOOT_COMPLETED":
                Logs.i("开机启动检测");
                if (!Status.getActiveMode().equals("helper")) {
                    Logs.i("Xposed模式下后台服务不启动");
                    return;
                }//不是无障碍模式不启动
                Logs.i("无障碍模式服务启动");
                ServerManger.startAccessibility(context);
                ServerManger.startAutoNotify(context);
                ServerManger.startNotice(context);
                ServerManger.startSms(context);
                break;
            case "cn.dreamn.qianji_auto.XPOSED_LOG": {
                Bundle extData = intent.getExtras();
                if (extData == null) return;
                String tag = extData.getString("tag");
                String msg = extData.getString("msg");
                Logs.i(tag, msg);
                break;
            }
            case "cn.dreamn.qianji_auto.XPOSED": {
                Logs.d("收到消息");
                //xp处理消息
                Bundle extData = intent.getExtras();
                if (extData == null) return;
                String type = extData.getString("type");
                String from = extData.getString("from");
                String data = extData.getString("data");
                if (from == null || data == null || type == null) return;
                Logs.i("---------收到广播消息------- \n" +
                        "源APP: " + type + "\n" +
                        "源自类型: " + from + "\n" +
                        "数据: " + data + "\n" +
                        "--------------------------------  \n");

                switch (type) {
                    case Receive.ALIPAY:
                        switch (from) {
                            case Alipay.BIBIZAN:
                                //笔笔攒消息
                                BiBiZan.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.PAYMENT_SUCCESS:
                                PaymentSuccess.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.QR_COLLECTION:
                                QrCollection.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.REC_YUEBAO:
                                YuEBao.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.REC_YULIBAO:
                                YuLiBao.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.RED_RECEIVED:
                                RedReceived.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.REFUND:
                                Refund.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.FUNDS_ARRIVAL:
                                FundArrival.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.TRANSFER_RECEIVED:
                                TransferReceived.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.TRANSFER_SUCCESS:
                                TransferSucceed.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.TRANSFER_SUCCESS_ACCOUNT:
                                break;
                            case Alipay.TRANSFER_YUEBAO:
                            case Alipay.TRANSFER_INTO_YUEBAO:
                                TransferIntoYuebao.getInstance().tryAnalyze(data, context);
                                break;
                            case Alipay.CANT_UNDERSTAND:
                                break;
                            default:
                                break;
                        }

                        break;
                    case Receive.WECHAT:
                        switch (from) {
                            case Wechat.PAYMENT:
                                Payment.getInstance().tryAnalyze(data, context);
                                break;
                            case Wechat.RECEIVED_QR:
                                cn.dreamn.qianji_auto.core.base.wechat.QrCollection.getInstance().tryAnalyze(data,context);
                                break;
                            case Wechat.PAYMENT_TRANSFER:
                                WechatPaymentTransfer.getInstance().tryAnalyze(data, context);
                                break;
                            case Wechat.PAYMENT_TRANSFER_RECEIVED:
                                //  WechatTransferReceived.getInstance().tryAnalyze(data,context);
                                break;
                            case Wechat.RED_PACKAGE:

                                break;
                            case Wechat.RED_PACKAGE_RECEIVED:

                                break;
                            case Wechat.PAYMENT_TRANSFER_REFUND:

                                break;
                            case Wechat.RED_REFUND:

                                break;
                            case Wechat.CANT_UNDERSTAND:

                                break;
                            default:
                                break;
                        }

                        break;
                    case Receive.SMS:
                        SmsServer.readSMS(data,context);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }


}
