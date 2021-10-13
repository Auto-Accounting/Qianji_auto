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
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.database.DbManger;


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
        MMKV mmkv = MMKV.defaultMMKV();
        int mode = mmkv.getInt("log_mode", 1);
        Task.onThread(() -> {
            android.util.Log.i(TAG, msg);
            if (mode != 0 && mode != 1) {
                DbManger.db.LogDao().add(msg, TAG, getTime());
                DbManger.db.LogDao().deleteTimeout(timeout);
            }
        });
    }

    public static void i(String msg) {

        i(TAG, msg);

    }

    public static void i(String TAG, String msg) {
        if (msg == null) return;
        MMKV mmkv = MMKV.defaultMMKV();
        int mode = mmkv.getInt("log_mode", 1);
        Task.onThread(() -> {
            android.util.Log.i(TAG, msg);
            if (mode != 0) {
                DbManger.db.LogDao().add(msg, TAG, getTime());
                DbManger.db.LogDao().deleteTimeout(timeout);
            }
        });
    }

    public static void m(String msg) {
        m(TAG, msg);
    }

    public static void m(String TAG, String msg) {
        if (msg == null) return;
        Task.onThread(() -> {
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
        Task.onThread(() -> DbManger.db.LogDao().del(pos));
    }

    public static void delAll(onDelOk ok) {
        Task.onThread(() -> {
            DbManger.db.LogDao().delAll();
            ok.ok();
        });
    }

    public static void getAll(onResult ret) {
        Task.onThread(()-> {

            cn.dreamn.qianji_auto.database.Table.Log[] logs = DbManger.db.LogDao().loadLimit();
            if (logs == null || logs.length <= 0) {
                ret.getLog(null);
                return;
            }

            ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            for (cn.dreamn.qianji_auto.database.Table.Log log : logs) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", log.pos);
                bundle.putString("time", log.time2);
                bundle.putString("title", log.title);
                bundle.putString("sub", log.sub);
                bundleArrayList.add(bundle);
            }
            ret.getLog(bundleArrayList.toArray(new Bundle[0]));
        });

    }

    public interface onDelOk {
        void ok();
    }

    public interface onResult {
        void getLog(Bundle[] logs);
    }
}

