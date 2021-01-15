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

import android.annotation.SuppressLint;

import com.xuexiang.xutil.data.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.MyApp;
import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Log;
import cn.dreamn.qianji_auto.core.utils.Tools;

public class Logs {

    public static int timeout=24*60*60;

    public static void d(String msg)  {
        String defaultTag = "Qianji-Auto";
        android.util.Log.i(defaultTag, msg);
    }
    public static void d(String TAG,String msg)  {
        android.util.Log.i(TAG, msg);
    }

    public static void i(String msg)  {
        String defaultTag = "Qianji-Auto";
        android.util.Log.i(defaultTag, msg);
        DbManger.db.LogDao().add(msg,"自动记账");
        DbManger.db.LogDao().deleteTimeout(timeout);
    }
    public static void i(String TAG,String msg)  {
        android.util.Log.i(TAG, msg);
        DbManger.db.LogDao().add(msg,TAG);
        DbManger.db.LogDao().deleteTimeout(timeout);
    }


    @SuppressLint("SimpleDateFormat")
    private static String getTime(){
        return Tools.getTime("yyyy-MM-dd HH:mm:ss");

    }


    public static void del(Integer pos) {
        DbManger.db.LogDao().del(pos);
    }

    public static void delAll(){
        DbManger.db.LogDao().delAll();
    }

    public static Log[] getAll(){
        return DbManger.db.LogDao().loadAll();
    }
}

