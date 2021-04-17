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

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import cn.dreamn.qianji_auto.database.Dao.AppDataDao;
import cn.dreamn.qianji_auto.database.Dao.Asset2Dao;
import cn.dreamn.qianji_auto.database.Dao.AssetDao;
import cn.dreamn.qianji_auto.database.Dao.AutoBillDao;
import cn.dreamn.qianji_auto.database.Dao.BookNameDao;
import cn.dreamn.qianji_auto.database.Dao.CacheDao;
import cn.dreamn.qianji_auto.database.Dao.CategoryNameDao;
import cn.dreamn.qianji_auto.database.Dao.IdentifyRegularDao;
import cn.dreamn.qianji_auto.database.Dao.LogDao;
import cn.dreamn.qianji_auto.database.Dao.RegularDao;
import cn.dreamn.qianji_auto.database.Table.AppData;
import cn.dreamn.qianji_auto.database.Table.Asset;
import cn.dreamn.qianji_auto.database.Table.Asset2;
import cn.dreamn.qianji_auto.database.Table.AutoBill;
import cn.dreamn.qianji_auto.database.Table.BookName;
import cn.dreamn.qianji_auto.database.Table.Cache;
import cn.dreamn.qianji_auto.database.Table.CategoryName;
import cn.dreamn.qianji_auto.database.Table.IdentifyRegular;
import cn.dreamn.qianji_auto.database.Table.Log;
import cn.dreamn.qianji_auto.database.Table.Regular;


@Database(entities = {
        Log.class,
        Regular.class,
        Cache.class,
        BookName.class,
        Asset.class,
        Asset2.class,
        AutoBill.class,
        CategoryName.class,
        AppData.class,
        IdentifyRegular.class
}, version = 8, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract LogDao LogDao();

    public abstract RegularDao RegularDao();

    public abstract CacheDao CacheDao();

    public abstract BookNameDao BookNameDao();

    public abstract AssetDao AssetDao();

    public abstract Asset2Dao Asset2Dao();

    public abstract AutoBillDao AutoBillDao();

    public abstract CategoryNameDao CategoryNameDao();


    public abstract AppDataDao AppDataDao();


    public abstract IdentifyRegularDao IdentifyRegularDao();


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
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Other ADD COLUMN  text TEXT ");
            database.execSQL("UPDATE  Other set text = \"\"");
            database.execSQL("ALTER TABLE Sms ADD COLUMN  text TEXT ");
            database.execSQL("UPDATE  Sms set text = \"\"");
        }
    };
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //修改AutoBill表
            database.execSQL("CREATE TABLE BillTemp "+
                    "( id INTEGER  PRIMARY KEY NOT NULL ,"+
                    " billInfo TEXT, "+
                    " date TEXT "+
                    " ) "
            );

            //2.将原来表中的数据复制过来，
            database.execSQL(" INSERT INTO BillTemp (id,billInfo,date) " +
                    "SELECT id,billInfo,'03.09'  FROM AutoBill "
            );

            //3. 将原表删除
            database.execSQL(" DROP TABLE AutoBill ");

            //4.将新建的表改名
            database.execSQL(" ALTER  TABLE BillTemp  RENAME to AutoBill");


            //修改Other表
            database.execSQL("CREATE TABLE IdentifyRegular" +
                    "( id INTEGER  PRIMARY KEY NOT NULL ," +
                    " regular TEXT, " +
                    " name TEXT, " +
                    " text TEXT, " +
                    " account1 TEXT, " +
                    " account2 TEXT, " +
                    " type TEXT, " +
                    " silent TEXT, " +
                    " money TEXT, " +
                    " fee TEXT ," +
                    " shopName TEXT, " +
                    " shopRemark TEXT, "+
                    " source TEXT, "+
                    " identify TEXT,"+
                    " fromApp TEXT,"+
                    " use int default 1, "+
                    " sort int default 0 "+
                    " ) "
            );

            //2.将原来表中的数据复制过来，
            database.execSQL(" INSERT INTO IdentifyRegular (regular,name,text,use,sort,identify,fromApp) " +
                    "SELECT regular,name,text,use,sort,'App','微信'  FROM Other "
            );

            //2.将原来表中的数据复制过来，
            database.execSQL(" INSERT INTO IdentifyRegular (regular,name,text,use,sort,identify,fromApp) " +
                    "SELECT regular,name,text,use,sort,'Sms','短信'  FROM Sms "
            );

            //3. 将原表删除
            database.execSQL(" DROP TABLE Other ");
            database.execSQL(" DROP TABLE Sms ");
            //创建APPData
            database.execSQL("CREATE TABLE AppData"+
                    "( id INTEGER " + " PRIMARY KEY NOT NULL ," +
                    " rawData TEXT, " +
                    " fromApp TEXT, " +
                    " identify TEXT, " +
                    " time TEXT " +
                    " ) "
            );

        }
    };
    static final Migration MIGRATION_7_8 = new Migration(7, 8) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //修改Regular表
            database.execSQL("CREATE TABLE TempTable " +
                    "( id INTEGER  PRIMARY KEY NOT NULL ," +
                    " regular TEXT, " +
                    " name TEXT ," +
                    " tableList TEXT ," +
                    " use INTEGER ," +
                    " sort INTEGER  default 0 " +
                    " ) "
            );

            //2.将原来表中的数据复制过来，
            database.execSQL(" INSERT INTO TempTable (id,regular,name,tableList,use,sort) " +
                    "SELECT id,regular,name,tableList,use,sort  FROM AutoBill "
            );

            //3. 将原表删除
            database.execSQL(" DROP TABLE Regular ");

            //4.将新建的表改名
            database.execSQL(" ALTER  TABLE TempTable  RENAME to Regular");

            //修改Regular表
            database.execSQL("CREATE TABLE TempTable " +
                    "( id INTEGER  PRIMARY KEY NOT NULL ," +
                    " regular TEXT, " +
                    " name TEXT ," +
                    " tableList TEXT ," +
                    " use INTEGER ," +
                    " sort INTEGER  default 0 " +
                    " ) "
            );


            //修改IdentifyRegular表
            database.execSQL("CREATE TABLE IdentifyRegularTemp" +
                    "( id INTEGER  PRIMARY KEY NOT NULL ," +
                    " regular TEXT, " +
                    " name TEXT, " +
                    " text TEXT, " +
                    " identify TEXT," +
                    " fromApp TEXT," +
                    " use int default 1, " +
                    " sort int default 0 " +
                    " ) "
            );

            //2.将原来表中的数据复制过来，
            database.execSQL(" INSERT INTO IdentifyRegular (regular,name,text,use,sort,identify,fromApp) " +
                    "SELECT regular,name,text,use,sort,identify,fromApp  FROM IdentifyRegular "
            );


            //3. 将原表删除
            database.execSQL(" DROP TABLE IdentifyRegular ");

            database.execSQL(" ALTER  TABLE IdentifyRegularTemp  RENAME to IdentifyRegular");
        }
    };
}
