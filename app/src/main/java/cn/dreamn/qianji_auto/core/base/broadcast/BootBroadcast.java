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

import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class BootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        //android.intent.action.BOOT_COMPLETED
        Logs.i("开机启动检测");
        if (!Status.getActiveMode().equals("helper")) {
            Logs.i("Xposed模式下不启动后台服务");
            return;
        }//不是无障碍模式不启动
        Logs.i("无障碍模式下后台服务启动");
        ServerManger.startAccessibility(context);
        ServerManger.startAutoNotify(context);

    }
}
