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

package cn.dreamn.qianji_auto.core.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;


import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.base.alipay.RedPackageRec;
import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class ReceiveBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action==null)return;
        //开机启动
        if(action.equals("android.intent.action.BOOT_COMPLETED"))
        {
            Logs.i("开机启动检测");
            if(!Status.getActiveMode().equals("helper")){
                Logs.i("后台服务不启动");
                return;
            }//不是无障碍模式不启动
            Logs.i("无障碍模式启动服务");
            ServerManger.startAccessibility(context);
            ServerManger.startAutoNotify(context);
            ServerManger.startNotice(context);
            ServerManger.startSms(context);
        }else if(action.equals("cn.dreamn.qianji_auto.XPOSED")){
            //xp处理消息
            Bundle extData=intent.getExtras();
            if(extData==null)return;
            String type=extData.getString("type");
            String from=extData.getString("from");
            String data=extData.getString("data");
            if(from==null||data==null||type==null)return;
            Logs.d("---------收到广播消息------- ");
            Logs.d("源APP: "+type);
            Logs.d("源自类型: "+from);
            Logs.d("数据: "+data);
            Logs.d("-------------------------------- ");

            switch (type){
                case Receive.ALIPAY:
                    switch (from){
                        case Alipay.PAYMENT_SUCCESS:break;
                        case Alipay.QR_COLLECTION:break;
                        case Alipay.REC_YUEBAO:break;
                        case Alipay.REC_YULIBAO:break;
                        case Alipay.RED_RECEIVED:
                            RedPackageRec.getInstance().tryAnalyze(data,context);
                            break;
                        case Alipay.REFUND:break;
                        case Alipay.TRANSFER_RECEIVED:break;
                        case Alipay.TRANSFER_SUCCESS:break;
                        case Alipay.TRANSFER_SUCCESS_ACCOUNT:break;
                        case Alipay.TRANSFER_YUEBAO:break;
                        case Alipay.CANT_UNDERSTAND:break;
                        default: break;
                    }

                    break;
                case Receive.WECHAT:break;
                case Receive.SMS:break;
                default:break;
            }
        }
    }


}
