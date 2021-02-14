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

import android.content.Context;
import android.content.Intent;

import cn.dreamn.qianji_auto.core.helper.AutoBillService;
import cn.dreamn.qianji_auto.core.helper.AutoReadAccessibilityService;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

public class ServerManger {
    public static void startAccessibility(Context context) {

        Permission.getInstance().grant(context, Permission.Assist);
        //context.startService(new Intent(context, AutoAccessibilityService.class));
    }

    public static void startAutoNotify(Context context) {
        Logs.i("通知已启动");
        context.startService(new Intent(context, AutoBillService.class));
    }


}
