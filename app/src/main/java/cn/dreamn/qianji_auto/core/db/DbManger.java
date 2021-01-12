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

package cn.dreamn.qianji_auto.core.db;

import android.content.Context;

import androidx.room.Room;

public class DbManger {
    public static AppDatabase db;

    public static void init(Context context) {
        db = Room.databaseBuilder(context,AppDatabase.class, "ankio").allowMainThreadQueries().build();
    }

    public static void initDb(){

    }

    private static void initAsset(){}
    private static void initAutoBill(){}
    private static void initBookName(){}
    private static void initCache(){}
    private static void initLog(){}
    private static void initRegular(){}
    private static void initSms(){}
}
