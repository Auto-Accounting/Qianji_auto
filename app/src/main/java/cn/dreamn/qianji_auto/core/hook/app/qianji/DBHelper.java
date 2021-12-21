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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.data.local.FileUtils;

public class DBHelper {

    private final Utils utils;
    boolean isObj = false;
    //SQLiteDatabase
    private SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    public DBHelper(Utils utils) {
        this.utils = utils;
        openDb();
    }

    public DBHelper(SQLiteDatabase db, Utils utils) {
        this.db = db;
        this.utils = utils;
        isObj = true;
    }

    private void openDb() {
        isObj = false;
        utils.log("Qianji-Copy 开始复制文件", false);
        @SuppressLint("SdCardPath") String NEW_PATH = "/data/data/com.mutangtech.qianji/qianjiapp_copy";
        FileUtils.del(NEW_PATH);//尝试删掉这里的文件
        @SuppressLint("SdCardPath") boolean copyed = FileUtils.copyFile("/data/data/com.mutangtech.qianji/databases/qianjiapp", NEW_PATH);
        if (copyed) {
            utils.log("Qianji-Copy 文件复制成功", false);
        } else {
            utils.log("Qianji-Copy 文件复制失败", false);
        }
        db = SQLiteDatabase.openOrCreateDatabase(NEW_PATH, null);
    }

    @SuppressLint("Range")
    public String getAllTables() {
        if (!db.isOpen()) {
            openDb();
        }
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

    @SuppressLint("Range")
    public JSONArray getCategory(String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from category where USER_ID='" + UserId + "'", null);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", cursor.getString(cursor.getColumnIndex("NAME")));
            jsonObject.put("type", cursor.getString(cursor.getColumnIndex("TYPE")));
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            jsonObject.put("book_id", cursor.getString(cursor.getColumnIndex("bookid")));
            jsonObject.put("parent", cursor.getString(cursor.getColumnIndex("PARENT_ID")));
            jsonObject.put("level", cursor.getString(cursor.getColumnIndex("LEVEL")));
            jsonObject.put("icon", cursor.getString(cursor.getColumnIndex("ICON")));
            jsonObject.put("sort", cursor.getString(cursor.getColumnIndex("SORT")));
            jsonArray.add(jsonObject);
        }
        cursor.close();
        //  db.close();
        return jsonArray;
    }

    @SuppressLint("Range")
    public JSONArray getAsset(String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from user_asset where STATUS = 0 and  USERID='" + UserId + "'", null);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sort", cursor.getInt(cursor.getColumnIndex("SORT")));
            jsonObject.put("id", cursor.getInt(cursor.getColumnIndex("_ID")));
            jsonObject.put("name", cursor.getString(cursor.getColumnIndex("NAME")));
            jsonObject.put("icon", cursor.getString(cursor.getColumnIndex("ICON")));
            jsonObject.put("type", cursor.getString(cursor.getColumnIndex("TYPE")));
            jsonObject.put("info", cursor.getString(cursor.getColumnIndex("LOAN_INFO")));
            jsonArray.add(jsonObject);
        }
        cursor.close();
        return jsonArray;
    }

    @SuppressLint("Range")
    public JSONArray getUserBook(boolean isVip, String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from user_book where  USERID='" + UserId + "' or MEMBER_ID='" + UserId + "'", null);
        JSONArray jsonArray = new JSONArray();
        boolean has = false;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("BOOK_ID")));
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            if (name.equals("默认账本")) {
                has = true;
            }
            jsonObject.put("name", name);
            jsonObject.put("userId", cursor.getString(cursor.getColumnIndex("USERID")));
            jsonObject.put("cover", cursor.getString(cursor.getColumnIndex("COVER")));
            jsonArray.add(jsonObject);
        }
        cursor.close();
        if (!isVip && !has) {//非vip并且没有默认账本会自动附加
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "-1");
            jsonObject.put("name", "默认账本");
            jsonObject.put("cover", "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @SuppressLint("Range")
    public JSONArray getBills(String type, String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from user_bill where TYPE='" + type + "' and USERID='" + UserId + "' and EXTRA IS NULL order by createtime desc", null);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("billid")));
            jsonObject.put("remark", cursor.getString(cursor.getColumnIndex("REMARK")));
            jsonObject.put("money", cursor.getString(cursor.getColumnIndex("MONEY")));
            jsonObject.put("descinfo", cursor.getString(cursor.getColumnIndex("DESCINFO")));
            jsonObject.put("createtime", cursor.getString(cursor.getColumnIndex("createtime")));
            jsonObject.put("type", cursor.getString(cursor.getColumnIndex("TYPE")));
            jsonArray.add(jsonObject);
        }
        cursor.close();
        return jsonArray;
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
            if (db != null && db.isOpen() && !isObj) {
                db.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}