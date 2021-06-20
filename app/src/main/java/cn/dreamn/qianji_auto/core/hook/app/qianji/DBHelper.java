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

package cn.dreamn.qianji_auto.core.hook.app.qianji;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.files.FileUtils;

public class DBHelper {

    private static final String DB_NAME = "qianjiapp";
    //SQLiteDatabase
    private final SQLiteDatabase db;
    private final String NEW_PATH = "/data/data/com.mutangtech.qianji/qianjiapp_copy";
    public String err = "";

    @SuppressLint("SdCardPath")
    public DBHelper(Utils utils) {
        utils.log("Qianji-Copy 开始复制文件", false);
        FileUtils.del(NEW_PATH);//尝试删掉这里的文件
        Boolean copyed = FileUtils.copyFile("/data/data/com.mutangtech.qianji/databases/qianjiapp", NEW_PATH);
        if (copyed) {
            utils.log("Qianji-Copy 文件复制成功", false);
        } else {
            utils.log("Qianji-Copy 文件复制失败", false);
        }
        db = SQLiteDatabase.openOrCreateDatabase(NEW_PATH, null);
    }

    public DBHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public String getAllTables() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;", null);
        StringBuilder str = new StringBuilder();
        while (cursor.moveToNext()) {
            String str1 = cursor.getString(cursor.getColumnIndex("name"));
            Cursor cursor2 = db.rawQuery(" PRAGMA TABLE_INFO (" + str1 + ")", null);
            StringBuilder str2 = new StringBuilder();
            while (cursor2.moveToNext()) {
                str2.append(" ").append(cursor.getString(cursor.getColumnIndex("name")));
            }
            cursor2.close();
            str.append("[").append(str1).append("](").append(str2).append(")\n");
        }
        cursor.close();
        return str.toString();
    }

    public ArrayList<Data> getCategory() {
        Cursor cursor = db.rawQuery("select * from category", null);
        ArrayList<Data> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Bundle bundle = new Bundle();
            bundle.putString("name", cursor.getString(cursor.getColumnIndex("NAME")));
            bundle.putString("type", cursor.getString(cursor.getColumnIndex("TYPE")));
            bundle.putString("id", cursor.getString(cursor.getColumnIndex("_id")));
            bundle.putString("book_id", cursor.getString(cursor.getColumnIndex("bookid")));
            bundle.putString("parent", cursor.getString(cursor.getColumnIndex("PARENT_ID")));
            bundle.putString("level", cursor.getString(cursor.getColumnIndex("LEVEL")));
            bundle.putString("icon", cursor.getString(cursor.getColumnIndex("ICON")));
            bundle.putString("sort", cursor.getString(cursor.getColumnIndex("SORT")));
            Data data1 = new Data();
            data1.set(bundle);
            data.add(data1);
        }
        cursor.close();
        //  db.close();
        return data;
    }

    public ArrayList<Data> getAsset() {
        Cursor cursor = db.rawQuery("select * from user_asset where TYPE <> 5 and STATUS = 0", null);
        ArrayList<Data> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Bundle bundle = new Bundle();
            bundle.putInt("sort", cursor.getInt(cursor.getColumnIndex("SORT")));
            bundle.putString("name", cursor.getString(cursor.getColumnIndex("NAME")));
            bundle.putString("icon", cursor.getString(cursor.getColumnIndex("ICON")));
            Data data1 = new Data();
            data1.set(bundle);
            data.add(data1);
        }
        cursor.close();
        // db.close();
        return data;
    }

    public ArrayList<Data> getUserBook() {
        Cursor cursor = db.rawQuery("select * from user_book ", null);
        ArrayList<Data> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Bundle bundle = new Bundle();
            bundle.putString("id", cursor.getString(cursor.getColumnIndex("BOOK_ID")));
            bundle.putString("name", cursor.getString(cursor.getColumnIndex("NAME")));
            bundle.putString("cover", cursor.getString(cursor.getColumnIndex("COVER")));
            Data data1 = new Data();
            data1.set(bundle);
            data.add(data1);
        }
        cursor.close();
        //  db.close();
        return data;
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            if (db != null) {
                db.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}