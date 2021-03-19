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

import android.annotation.SuppressLint;

import cn.dreamn.qianji_auto.database.DbManger;


public class Log {

    public static int timeout = 24 * 60 * 60;

    public static void d(String msg) {
        String defaultTag = "Qianji-Auto";
        Task.onThread(()-> android.util.Log.i(defaultTag, msg));
    }

    public static void d(String TAG, String msg) {
        Task.onThread(()-> android.util.Log.i(TAG, msg));
    }

    public static void i(String msg) {
        String defaultTag = "Qianji-Auto";

        Task.onThread(()->{
            android.util.Log.i(defaultTag, msg);
            DbManger.db.LogDao().add(msg, "自动记账", getTime());
            DbManger.db.LogDao().deleteTimeout(timeout);
        });

    }

    public static void i(String TAG, String msg) {

        Task.onThread(()->{
            android.util.Log.i(TAG, msg);
            DbManger.db.LogDao().add(msg, TAG, getTime());
            DbManger.db.LogDao().deleteTimeout(timeout);
        });
    }


    @SuppressLint("SimpleDateFormat")
    private static String getTime() {
        return Tool.getTime("yyyy-MM-dd HH:mm:ss");

    }


    public static void del(Integer pos) {
        Task.onThread(()-> {
            DbManger.db.LogDao().del(pos);
        });
    }


    public static void delAll() {
        Task.onThread(()-> {
            DbManger.db.LogDao().delAll();
        });
    }

    interface onResult{
        void getLog(cn.dreamn.qianji_auto.database.Table.Log[] logs);
    }

    public static void getAll(onResult ret) {
        Task.onThread(()->{
            ret.getLog(DbManger.db.LogDao().loadAll());
        });
    }
}

