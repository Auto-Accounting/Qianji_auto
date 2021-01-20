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

package cn.dreamn.qianji_auto.core.helper;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;

import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class AutoNotificationService extends NotificationListenerService {


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String pkg = sbn.getPackageName();
        if (notification != null) {
            Bundle extras = notification.extras;
            if (extras != null) {
                String title = extras.getString(NotificationCompat.EXTRA_TITLE, "");
                String content = extras.getString(NotificationCompat.EXTRA_TEXT, "");
                Logs.d( "**********************");
                Logs.d( "包名:" + pkg);
                Logs.d( "标题:" + title);
                Logs.d( "内容:" + content);
                Logs.d( "**********************");


                switch (pkg) {
                    case "com.eg.android.AlipayGphone":
                        if (content != null && !content.equals("")) {
                            if (content.contains("通过扫码向你付款") || content.contains("成功收款") || title.contains("你已成功收款")) {
                                String money = BillTools.getMoney(content);
                                if (money.equals("0")) {
                                    money = BillTools.getMoney(title);
                                }
                                if(!money.equals("0")){
                                    BillInfo billInfo = new BillInfo();
                                    billInfo.setMoney(money);
                                    billInfo.setShopRemark(content);
                                    billInfo.setTime();
                                    billInfo.setBookName(BookNames.getDefault());
                                    billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));
                                    billInfo.setType(BillInfo.TYPE_INCOME);
                                    billInfo.setAccountName("余额");
                                    billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));
                                    billInfo.setSource("支付宝二维码收款捕获");
                                    billInfo.setCateName(Category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark(),BillInfo.TYPE_PAY));
                                    billInfo.dump();
                                    CallAutoActivity.call(getApplicationContext(), billInfo);
                                }
                            }


                        }

                        break;
                    case "com.tencent.mm":

                        if (content != null && !content.equals("")) {
                            if (title.equals("微信收款助手") && content.contains("微信支付收款")) {
                                String money = BillTools.getMoney(content);
                                if (!money.equals("0")) {
                                    BillInfo billInfo = new BillInfo();
                                    billInfo.setMoney(money);
                                    billInfo.setShopRemark(content);
                                    billInfo.setTime();
                                    billInfo.setBookName(BookNames.getDefault());
                                    billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(),billInfo.getShopRemark()));
                                    billInfo.setType(BillInfo.TYPE_INCOME);
                                    billInfo.setAccountName("零钱");
                                    billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));
                                    billInfo.setSource("微信二维码收款捕获");
                                    billInfo.setCateName(Category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark(),BillInfo.TYPE_PAY));
                                    billInfo.dump();
                                    CallAutoActivity.call(getApplicationContext(), billInfo);
                                }

                            }
                        }

                        break;
                }



            }
        }
    }


}
