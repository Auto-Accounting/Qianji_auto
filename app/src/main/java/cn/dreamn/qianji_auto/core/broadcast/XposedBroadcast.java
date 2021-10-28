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

package cn.dreamn.qianji_auto.core.broadcast;

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


public class XposedBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (action.equals("cn.dreamn.qianji_auto.XPOSED_LOG")) {
            Bundle extData = intent.getExtras();
            if (extData == null) return;
            String tag = extData.getString("tag");
            String msg = extData.getString("msg");
            Log.i("Xposed日志-" + tag, msg);
        } else if (action.equals("cn.dreamn.qianji_auto.XPOSED")) {
            Bundle extData = intent.getExtras();
            if (extData == null) return;
            String data = extData.getString("data").replace("\t", "").replace("\n", "n");
            String identify = extData.getString("app_identify");
            String app = extData.getString("app_package");
            String appName = extData.getString("app_name");
            Log.i("Xposed - 自动记账", "自动记账收到数据：AppName:" + appName + ",源自:" + app + ",数据：" + data);
            TaskThread.onThread(() -> {
                Db.db.AppDataDao().add(data, identify, app);
            });
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    BillInfo billInfo = (BillInfo) msg.obj;
                    billInfo.setFromApp(app);
                    SendDataToApp.call(billInfo);
                }
            };

            RegularCenter.getInstance("app").run(app, data, null, new TaskThread.TaskResult() {
                @Override
                public void onEnd(Object obj) {
                    BillInfo billInfo = (BillInfo) obj;
                    if (billInfo != null) {
                        HandlerUtil.send(mHandler, billInfo, HANDLE_OK);
                    }
                }
            });

        }
    }
}
