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

package cn.dreamn.qianji_auto.utils.runUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;



/**
 * Created by Jason on 2017/9/10.
 */

public class Task {

    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void onMain(long msec, final Runnable runnable) {
        Runnable run = () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.d("task",e.toString());
            }
        };
        sMainHandler.postDelayed(run, msec);
    }

    public static void onMain(final Runnable runnable) {
        Runnable run = () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.d("task",e.toString());
            }
        };
        sMainHandler.post(run);
    }

    public static void onThread(final Runnable runnable){
        new Thread(runnable).start();
    }
}
