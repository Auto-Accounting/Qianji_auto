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

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import cn.dreamn.qianji_auto.core.db.Dao.Asset2Dao;
import cn.dreamn.qianji_auto.core.db.Dao.AssetDao;
import cn.dreamn.qianji_auto.core.db.Dao.AutoBillDao;
import cn.dreamn.qianji_auto.core.db.Dao.BookNameDao;
import cn.dreamn.qianji_auto.core.db.Dao.CacheDao;
import cn.dreamn.qianji_auto.core.db.Dao.CategoryNameDao;
import cn.dreamn.qianji_auto.core.db.Dao.LogDao;
import cn.dreamn.qianji_auto.core.db.Dao.OtherDao;
import cn.dreamn.qianji_auto.core.db.Dao.RegularDao;
import cn.dreamn.qianji_auto.core.db.Dao.SmsDao;
import cn.dreamn.qianji_auto.core.db.Table.Asset;
import cn.dreamn.qianji_auto.core.db.Table.Asset2;
import cn.dreamn.qianji_auto.core.db.Table.AutoBill;
import cn.dreamn.qianji_auto.core.db.Table.BookName;
import cn.dreamn.qianji_auto.core.db.Table.Cache;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;
import cn.dreamn.qianji_auto.core.db.Table.Log;
import cn.dreamn.qianji_auto.core.db.Table.Other;
import cn.dreamn.qianji_auto.core.db.Table.Regular;
import cn.dreamn.qianji_auto.core.db.Table.Sms;

@Database(entities = {
        Log.class,
        Regular.class,
        Sms.class,
        Cache.class,
        BookName.class,
        Asset.class,
        Asset2.class,
        AutoBill.class,
        Other.class,
        CategoryName.class
}, version = 5, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract LogDao LogDao();

    public abstract RegularDao RegularDao();

    public abstract SmsDao SmsDao();

    public abstract CacheDao CacheDao();

    public abstract BookNameDao BookNameDao();

    public abstract AssetDao AssetDao();

    public abstract Asset2Dao Asset2Dao();

    public abstract AutoBillDao AutoBillDao();

    public abstract OtherDao OtherDao();

    public abstract CategoryNameDao CategoryNameDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE `Other` (`id` INTEGER NOT NULL ,`regular` TEXT,`num` TEXT,`name` TEXT,`use` INTEGER NOT NULL DEFAULT 1, `sort` INTEGER NOT NULL DEFAULT 0,PRIMARY KEY(`id`))");
            database.execSQL("ALTER TABLE Regular  ADD COLUMN sort INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Sms  ADD COLUMN sort INTEGER NOT NULL DEFAULT 0");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE `CategoryName` (`id` INTEGER NOT NULL , `name` TEXT,`icon` TEXT,`level` TEXT ,`type` TEXT,`self_id` TEXT,`parent_id` TEXT,PRIMARY KEY(`id`))");
            database.execSQL("ALTER TABLE Asset2 ADD COLUMN icon TEXT");
            database.execSQL("ALTER TABLE BookName ADD COLUMN icon TEXT");

        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE CategoryName ADD COLUMN  book_id TEXT  ");
            database.execSQL("ALTER TABLE BookName ADD COLUMN book_id TEXT   ");
            database.execSQL("ALTER TABLE Asset2 ADD COLUMN sort INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE  BookName set book_id = \"-1\" ");
            database.execSQL("UPDATE  CategoryName set book_id = \"-1\" ");
        }
    };
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE CategoryName ADD COLUMN  sort TEXT  ");
            database.execSQL("UPDATE  CategoryName set sort = \"500\" ");
        }
    };
}
