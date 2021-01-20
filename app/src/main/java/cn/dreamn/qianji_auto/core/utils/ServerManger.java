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

package cn.dreamn.qianji_auto.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import cn.dreamn.qianji_auto.core.helper.AutoAccessibilityService;
import cn.dreamn.qianji_auto.core.helper.AutoBillService;
import cn.dreamn.qianji_auto.core.helper.AutoNotificationService;
import cn.dreamn.qianji_auto.core.helper.SmsServer;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

import static com.xuexiang.xutil.XUtil.getContentResolver;

public class ServerManger {
    public static void startAccessibility(Context context){

        if(!AutoAccessibilityService.isStart()){
            Permission.getInstance().grant(context,Permission.Assist);
        }
        //context.startService(new Intent(context, AutoAccessibilityService.class));
    }

    public static void startAutoNotify(Context context){
        Logs.i("通知已启动");
        context.startService(new Intent(context, AutoBillService.class));
    }

    public static void startSms(Context context){
        Logs.i("短信监听已启动");
        SmsServer smsServer = new SmsServer(context,null);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, smsServer);
    }

    public static void startNotice(Context context){
        Logs.i("通知监听已启动");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, AutoNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

}
