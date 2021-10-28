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

package cn.dreamn.qianji_auto.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.dreamn.qianji_auto.data.database.Dao.AppDataDao;
import cn.dreamn.qianji_auto.data.database.Dao.AssetDao;
import cn.dreamn.qianji_auto.data.database.Dao.AssetMapDao;
import cn.dreamn.qianji_auto.data.database.Dao.AutoBillDao;
import cn.dreamn.qianji_auto.data.database.Dao.BookNameDao;
import cn.dreamn.qianji_auto.data.database.Dao.CategoryDao;
import cn.dreamn.qianji_auto.data.database.Dao.LogDao;
import cn.dreamn.qianji_auto.data.database.Dao.RegularDao;
import cn.dreamn.qianji_auto.data.database.Table.AppData;
import cn.dreamn.qianji_auto.data.database.Table.Asset;
import cn.dreamn.qianji_auto.data.database.Table.AssetMap;
import cn.dreamn.qianji_auto.data.database.Table.AutoBill;
import cn.dreamn.qianji_auto.data.database.Table.BookName;
import cn.dreamn.qianji_auto.data.database.Table.Category;
import cn.dreamn.qianji_auto.data.database.Table.Log;
import cn.dreamn.qianji_auto.data.database.Table.Regular;


@Database(entities = {
        Log.class,
        Regular.class,
        BookName.class,
        AssetMap.class,
        Asset.class,
        AutoBill.class,
        Category.class,
        AppData.class,
}, version = 14, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract LogDao LogDao();
    public abstract RegularDao RegularDao();

    public abstract BookNameDao BookNameDao();

    public abstract AssetMapDao AssetMapDao();

    public abstract AssetDao AssetDao();

    public abstract AutoBillDao AutoBillDao();

    public abstract CategoryDao CategoryDao();

    public abstract AppDataDao AppDataDao();

}
