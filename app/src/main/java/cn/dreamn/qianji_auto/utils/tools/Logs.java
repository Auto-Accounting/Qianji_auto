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

import java.util.Calendar;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.MyApp;
import cn.dreamn.qianji_auto.core.db.DbManger;

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
        DbManger.db.LogDao().add(msg,getTime());
        DbManger.db.LogDao().deleteTimeout(timeout);
    }
    public static void i(String TAG,String msg)  {
        android.util.Log.i(TAG, msg);
        DbManger.db.LogDao().add(msg,getTime());
        DbManger.db.LogDao().deleteTimeout(timeout);
    }


    @SuppressLint("DefaultLocale")
    private static String getTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}

