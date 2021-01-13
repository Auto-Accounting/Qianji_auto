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

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.dreamn.qianji_auto.core.utils.Assets;

@Database(entities = {
        Log.class,
        Regular.class,
        Sms.class,
        Cache.class,
        BookName.class,
        Asset.class,
        Asset2.class,
        AutoBill.class
}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract LogDao LogDao();
    public abstract RegularDao RegularDao();
    public abstract SmsDao SmsDao();
    public abstract CacheDao CacheDao();
    public abstract BookNameDao BookNameDao();
    public abstract AssetDao AssetDao();
    public abstract Asset2Dao Asset2Dao();
    public abstract AutoBillDao AutoBillDao();
}
