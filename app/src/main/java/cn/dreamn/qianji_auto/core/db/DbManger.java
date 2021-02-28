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

import static cn.dreamn.qianji_auto.core.db.AppDatabase.MIGRATION_1_2;
import static cn.dreamn.qianji_auto.core.db.AppDatabase.MIGRATION_2_3;
import static cn.dreamn.qianji_auto.core.db.AppDatabase.MIGRATION_3_4;
import static cn.dreamn.qianji_auto.core.db.AppDatabase.MIGRATION_4_5;

public class DbManger {
    public static AppDatabase db;

    public static void init(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "ankio").
                allowMainThreadQueries().
                addMigrations(MIGRATION_1_2).
                addMigrations(MIGRATION_2_3).
                addMigrations(MIGRATION_3_4).
                addMigrations(MIGRATION_4_5).
                build();
    }
}
