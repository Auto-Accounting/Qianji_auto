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

package cn.dreamn.qianji_auto.utils.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.List;

import cn.dreamn.qianji_auto.utils.XToastUtils;

public class Android {
    public static void hideTaskbar(Activity activity) {
        MMKV mmkv = MMKV.defaultMMKV();
        boolean show = mmkv.getBoolean("tasker_show", true);
        mmkv.encode("tasker_show", !show);
        XToastUtils.success("多任务状态：" + (show ? "显示" : "隐藏"));
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.AppTask> tasks;
            tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                tasks.get(0).setExcludeFromRecents(!show);
            }
        }

    }
}
