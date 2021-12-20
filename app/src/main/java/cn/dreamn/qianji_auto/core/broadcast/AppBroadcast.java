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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


public class AppBroadcast extends BroadcastReceiver {
    public static final int BROADCAST_ASYNC = 1;//同步信号
    public static final int BROADCAST_GET_REI = 2;//获取报销账单
    public static final int BROADCAST_GET_YEAR = 3;//获取同步账单
    public static final int BROADCAST_NOTHING = 0;//获取同步账单

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MMKV mmkv = MMKV.defaultMMKV();
        if (action == null) {
            mmkv.encode("AutoSignal", BROADCAST_ASYNC);
            Log.d("action 为空");
            return;
        }
        int single = mmkv.getInt("AutoSignal", BROADCAST_NOTHING);
        if (BROADCAST_NOTHING == single) {
            Log.d("不在接收同步的范围之内！");
            return;
        }


        mmkv.encode("AutoSignal", BROADCAST_NOTHING);
        Bundle extData = intent.getExtras();

        if (extData == null) {
            Log.d("数据为空？");
            return;
        }
        Log.d("收到数据");
        AppManager.AsyncEnd(context, extData, single);

        Log.i("钱迹数据同步完毕");
    }
}
