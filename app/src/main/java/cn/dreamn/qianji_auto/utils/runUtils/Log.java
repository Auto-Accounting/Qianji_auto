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

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.setting.AppStatus;


public class Log {

    public static int timeout = 24 * 60 * 60;
    public static int MODE_CLOSE = 0;//关闭日志
    public static int MODE_SIMPLE = 1;//简单记录
    public static int MODE_MORE = 2;//详细记录
    public static String TAG = "自动记账";

    public static void init(String tag) {
        TAG = tag;
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (msg == null) return;
        android.util.Log.d(TAG, msg);
        MMKV mmkv = MMKV.defaultMMKV();
        int mode = mmkv.getInt("log_mode", 1);

        if (!AppStatus.isDebug() && (mode == MODE_CLOSE || mode == MODE_MORE)) return;//不记录
        addToDB(TAG, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (msg == null) return;
        android.util.Log.i(TAG, msg);
        MMKV mmkv = MMKV.defaultMMKV();
        int mode = mmkv.getInt("log_mode", 1);
        if (mode == MODE_CLOSE && !AppStatus.isDebug()) return;//不记录
        addToDB(TAG, msg);
    }


    public static void addToDB(String TAG, String msg) {
        TaskThread.onThread(() -> {
            Db.db.LogDao().del(getLimit());
            Db.db.LogDao().add(msg, TAG);
        });
    }


    public static int getLimit() {
        MMKV mmkv = MMKV.defaultMMKV();
        int mode = mmkv.getInt("log_mode", 1);
        return (mode == MODE_MORE || AppStatus.isDebug()) ? 1000 : 500;
    }


    public static void delLimit(int limit) {
        TaskThread.onThread(() -> Db.db.LogDao().del(limit));
    }




}

