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
import android.telephony.SmsMessage;

import cn.dreamn.qianji_auto.core.base.Other;
import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.base.alipay.BiBiZan;
import cn.dreamn.qianji_auto.core.base.alipay.ClientCash;
import cn.dreamn.qianji_auto.core.base.alipay.FundArrival;
import cn.dreamn.qianji_auto.core.base.alipay.MaYi;
import cn.dreamn.qianji_auto.core.base.alipay.PaymentSuccess;
import cn.dreamn.qianji_auto.core.base.alipay.QrCollection;
import cn.dreamn.qianji_auto.core.base.alipay.RedReceived;
import cn.dreamn.qianji_auto.core.base.alipay.Refund;
import cn.dreamn.qianji_auto.core.base.alipay.Repayment;
import cn.dreamn.qianji_auto.core.base.alipay.TransferIntoYuebao;
import cn.dreamn.qianji_auto.core.base.alipay.TransferReceived;
import cn.dreamn.qianji_auto.core.base.alipay.TransferSucceed;
import cn.dreamn.qianji_auto.core.base.alipay.TransferSucceedAccount;
import cn.dreamn.qianji_auto.core.base.alipay.YuEBao;
import cn.dreamn.qianji_auto.core.base.alipay.YuLiBao;
import cn.dreamn.qianji_auto.core.base.wechat.Payment;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.base.wechat.WechatIncomeMoney;
import cn.dreamn.qianji_auto.core.base.wechat.WechatIncomeShop;
import cn.dreamn.qianji_auto.core.base.wechat.WechatPaymentRefund;
import cn.dreamn.qianji_auto.core.base.wechat.WechatPaymentTransfer;
import cn.dreamn.qianji_auto.core.base.wechat.WechatQrCollection;
import cn.dreamn.qianji_auto.core.base.wechat.WechatRedPackage;
import cn.dreamn.qianji_auto.core.base.wechat.WechatRedPackageReceived;
import cn.dreamn.qianji_auto.core.base.wechat.WechatRedRefund;
import cn.dreamn.qianji_auto.core.base.wechat.WechatTransferRefund;
import cn.dreamn.qianji_auto.core.helper.SmsServer;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class ReceiveBroadcast extends BroadcastReceiver {


    //从意图获取短信对象的具体方法
    public static SmsMessage[] getMessageFromIntent(Intent intent) {
        SmsMessage[] retmeMessage = null;
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        retmeMessage = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            byte[] bytedata = (byte[]) pdus[i];
            retmeMessage[i] = SmsMessage.createFromPdu(bytedata);
        }
        return retmeMessage;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null) return;
        //开机启动
        switch (action) {
            case "android.provider.Telephony.SMS_RECEIVED":

                SmsMessage[] msgs = getMessageFromIntent(intent);
                //提取短信内容
                if (msgs != null && msgs.length > 0) {
                    StringBuilder msg2 = new StringBuilder();
                    String user = "";
                    for (SmsMessage msg : msgs) {
                        user = msg.getDisplayOriginatingAddress();
                        msg2.append(msg.getDisplayMessageBody());
                    }

                    Logs.i("--------收到短信-------\n" +
                            "发件人是：" + user +
                            "\n------短信内容-------\n" +
                            msg2.toString() +
                            "\n----------------------"
                    );
                    SmsServer.readSMS(msg2.toString(), context);
                }

                break;
            case "android.intent.action.BOOT_COMPLETED":
                Logs.i("开机启动检测");
                if (!Status.getActiveMode().equals("helper")) {
                    Logs.i("Xposed模式下后台服务不启动");
                    return;
                }//不是无障碍模式不启动
                Logs.i("无障碍模式服务启动");
                ServerManger.startAccessibility(context);
                ServerManger.startAutoNotify(context);


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
                Bundle extData = intent.getExtras();
                if (extData == null) return;
                String type = extData.getString("type");
                String from = extData.getString("from");
                String data = extData.getString("data");
                String title = extData.getString("title");
                if (title == null) title = "";
                if (from == null || data == null || type == null) return;
                String str =

                        "源APP: " + type + "\n" +
                                "源自类型: " + from + "\n" +
                                "源标题: " + title + "\n" +
                                "数据: " + data + "\n";
                Logs.i(str);
                BillInfo billInfo = null;
                switch (type) {
                    case Receive.ALIPAY:
                        switch (from) {
                            case Alipay.BIBIZAN:
                                //笔笔攒消息
                                billInfo = BiBiZan.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.REPAYMENT:
                                billInfo = Repayment.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.PAYMENT_SUCCESS:
                                billInfo = PaymentSuccess.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.QR_COLLECTION:
                                billInfo = QrCollection.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.REC_YUEBAO:
                                billInfo = YuEBao.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.REC_YULIBAO:
                                billInfo = YuLiBao.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.RED_RECEIVED:
                                billInfo = RedReceived.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.REFUND:
                                billInfo = Refund.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.FUNDS_ARRIVAL:
                                billInfo = FundArrival.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.TRANSFER_RECEIVED:
                                billInfo = TransferReceived.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.TRANSFER_SUCCESS:
                                billInfo = TransferSucceed.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.TRANSFER_SUCCESS_ACCOUNT:
                                billInfo = TransferSucceedAccount.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.MAYI:
                                billInfo = MaYi.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.CLIENT_CASH:
                                billInfo = ClientCash.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.TRANSFER_YUEBAO:
                            case Alipay.TRANSFER_INTO_YUEBAO:
                                billInfo = TransferIntoYuebao.getInstance().tryAnalyze(data, from);
                                break;
                            case Alipay.CANT_UNDERSTAND:
                                String body = Other.getTextWithWechat(data);
                                Logs.i("未识别信息文本", body);
                                Other.regular(title, data, context);
                                break;
                            default:
                                break;
                        }

                        break;
                    case Receive.WECHAT:
                        switch (from) {
                            case Wechat.PAYMENT:
                                billInfo = Payment.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.RECEIVED_QR:
                                billInfo = WechatQrCollection.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.PAYMENT_TRANSFER:
                                billInfo = WechatPaymentTransfer.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.PAYMENT_TRANSFER_RECEIVED:
                                //  WechatTransferReceived.getInstance().tryAnalyze(data,context);
                                // 和上面那个一样
                                break;
                            case Wechat.RED_PACKAGE:
                                billInfo = WechatRedPackage.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.RED_PACKAGE_RECEIVED:
                                billInfo = WechatRedPackageReceived.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.PAYMENT_TRANSFER_REFUND:
                                billInfo = WechatTransferRefund.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.PAYMENT_REFUND:
                                billInfo = WechatPaymentRefund.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.RED_REFUND:
                                billInfo = WechatRedRefund.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.INCOME_SHOP:
                                billInfo = WechatIncomeShop.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.MONEY_INCOME:
                                billInfo = WechatIncomeMoney.getInstance().tryAnalyze(data, from);
                                break;
                            case Wechat.CANT_UNDERSTAND:
                                String body = Other.getTextWithWechat(data);
                                Logs.i("未识别信息文本", body);
                                Other.regular(title, data, context);
                                break;
                            default:
                                break;
                        }

                        break;
                    default:
                        break;
                }
                // str += ">>>>>>>>>>>>>>>>>>账单分析完毕 \n";
                if (billInfo != null) {

                    CallAutoActivity.call(context, billInfo);
                }

                break;
            }
        }
    }


}
