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

package cn.dreamn.qianji_auto.database;

import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_1_2;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_2_3;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_3_4;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_4_5;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_5_6;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_6_7;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_7_8;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_8_9;
import static cn.dreamn.qianji_auto.database.AppDatabase.MIGRATION_9_10;

import android.content.Context;

import androidx.room.Room;


public class DbManger {
    public static AppDatabase db;

    public static void init(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "ankio").
                addMigrations(MIGRATION_1_2).
                addMigrations(MIGRATION_2_3).
                addMigrations(MIGRATION_3_4).
                addMigrations(MIGRATION_4_5).
                addMigrations(MIGRATION_5_6).
                addMigrations(MIGRATION_6_7).
                addMigrations(MIGRATION_7_8).
                addMigrations(MIGRATION_8_9).
                addMigrations(MIGRATION_9_10).
                build();
    }
}
